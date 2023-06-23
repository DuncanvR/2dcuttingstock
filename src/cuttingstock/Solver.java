/**
 * 2D Cutting stock
 * Copyright (C) 2013 DuncanvR
 * Solver.java
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package cuttingstock;

import dvrlib.generic.RandomOrder;
import lpsolve.*;
import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Vector;

public class Solver implements AutoCloseable {
   protected final ColumnBuilder          columnBuilder = new ColumnBuilder();
   protected final HashSet<Column>        columns       = new HashSet<Column>();
   protected final LpSolve                lp;
   protected final Problem                problem;
   protected final RandomOrder<Integer[]> shapeOrderings;

   protected double[] duals    = new double[0];
   protected boolean  integral = false;
   protected boolean  closed   = false;

   public Solver(Problem problem) {
      LpSolve lp = null;
      try {
         // Construct the lp
         lp = LpSolve.makeLp(problem.shapes.size(), 0);
         lp.setVerbose(LpSolve.IMPORTANT);
         lp.setMinim();
      }
      catch(LpSolveException e) {
         e.printStackTrace();
      }
      this.lp             = lp;
      this.problem        = problem;
      this.shapeOrderings = new RandomOrder<Integer[]>(dvrlib.algorithm.SubSets.subSets(IntStream.range(0, problem.shapes.size()).boxed().toArray(Integer[]::new)));

      try {
         // Add constraints
         for(int i = 0; i < problem.shapes.size(); i++) {
            lp.setConstrType(i + 1, LpSolve.EQ);
            lp.setRh(i + 1, problem.shapes.get(i).count() * 1d);
            lp.setRowName(i + 1, "" + (i + 1));
         }

         // Add initial columns
         for(int i = 0; i < problem.shapes.size(); i++) {
            Column[] cs = columnBuilder.buildColumns(problem, new RequiredShape[]{ problem.shapes.get(i) });
            if(cs.length == 0)
               throw new RuntimeException("No way to fit " + problem.shapes.get(i) + " inside a resource");
            addColumns(cs);
         }

         // Solve the problem
         lp.solve();

         Logger.info("Initial solution:").inBoldYellow().println();
         printSolution();
      }
      catch(LpSolveException e) {
         e.printStackTrace();
      }
   }

   protected boolean addColumns(Column cs[]) throws LpSolveException {
      boolean added = false;
      for (Column c : cs) {
         added |= addColumn(c);
      }
      return added;
   }

   protected boolean addColumn(Column c) throws LpSolveException {
      if(c == null || columns.contains(c))
         return false;

      double col[] = new double[lp.getNrows() + 1];
      col[0] = 1d; // cost of this column, i.e. one resource

      for(Shape s : c.map.values()) {
         col[s.id] += 1d;
      }

      lp.addColumn(col);
      lp.setBinary(lp.getNcolumns(), integral);
      columns.add(c);

      return true;
   }

   protected boolean price() throws LpSolveException {
      shapeOrderings.randomise();
      for(Integer[] is : shapeOrderings) {
         double score = 0d;
         for(int i : is) {
            score += duals[i];
         }
         if(score < 1d)
            continue;

         RequiredShape[] ss = new RequiredShape[is.length];
         for(int j = 0; j < is.length; j++) {
            ss[j] = problem.shapes.get(is[j]);
         }
         Column[] cs = columnBuilder.buildColumns(problem, ss);
         if(cs.length == 0)
            continue;

         if(!addColumns(cs))
            continue;

         if(solveLP())
            return true;
      }
      return false;
   }

   public void printSolution() throws LpSolveException {
      Logger.info("   Number of needed sheets: ").print();
      Logger.info(integral ? Long.toString(Math.round(lp.getObjective())) : Double.toString(lp.getObjective())).inGreen().println();
      double[] var = lp.getPtrVariables();
      for(int i = 0, c = 1; i < var.length; i++) {
         if(var[i] > 0d) {
            double col[] = lp.getPtrColumn(i + 1);
            Logger.info("   Cutting plan for sheet " + c++ + ": " + (integral ? Long.toString(Math.round(var[i])) : Double.toString(var[i])) + " * ").print();
            assert(col[0] == 1d); // col[0] holds the cost of the column, which should be one resource
            HashMap<String, Long> shapes = new HashMap<String, Long>();
            for(int j = 1; j < col.length; j++) {
               if(col[j] > 0d) {
                  shapes.put(problem.shapes.get(Integer.parseInt(lp.getRowName(j)) - 1).shape().name, Math.round(col[j]));
               }
            }
            Logger.info(shapes).println();
         }
      }
   }

   public void solve() {
      try {
         Logger.info("Solving as fractional problem...").inYellow().println();
         solveLP();
         while(price()) ;

         Logger.info("Fractional solution:").inBoldYellow().println();
         printSolution();

         Logger.info("Solving as integral problem...").inYellow().println();
         integral = true;
         for(int i = 1; i <= lp.getNcolumns(); i++) {
            lp.setBinary(i, true);
         }
         solveLP();
         while(price()) ;

         Logger.info("Integral solution:").inBoldYellow().println();
         printSolution();
      }
      catch(LpSolveException e) {
         e.printStackTrace();
      }
   }

   protected boolean solveLP() throws LpSolveException {
      lp.solve();
      // Retrieve shadow prices
      double dualsArr[] = lp.getPtrDualSolution();
      double[] dualsOld = duals;
      duals = new double[problem.shapes.size()];
      for(int i = 0; i < lp.getNrows(); i++) {
         duals[i] = dualsArr[i + 1] > 0d ? dualsArr[i] : 0d;
      }
      // Return whether the duals have changed, and therefore whether the solution has changed
      return !Arrays.equals(duals, dualsOld);
   }

   //--- java.lang.AutoCloseable
   @Override
   public void close() {
      if(!closed && lp != null) {
         lp.deleteLp();
         closed = true;
      }
   }
}
