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

import lpsolve.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Solver {
   protected final LpSolve         lp;
   protected final Problem         problem;
   protected final HashSet<Column> columns = new HashSet<Column>();

   protected HashMap<Shape, Double> duals    = new HashMap<Shape, Double>();
   protected boolean                integral = false;

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
      this.lp      = lp;
      this.problem = problem;

      try {
         // Add constraints
         for(int i = 1; i <= problem.shapes.size(); i++) {
            lp.setConstrType(i, LpSolve.EQ);
            lp.setRh(i, 1d);
            lp.setRowName(i, "" + i);
         }

         // Add initial columns
         for(int i = 0; i < problem.shapes.size(); i++) {
            Column c = Column.construct(problem, new Shape[]{problem.shapes.get(i)});
            if(c == null)
               throw new RuntimeException("No way to fit " + problem.shapes.get(i) + " inside a resource");
            addColumn(c);
         }

         // Solve the problem
         lp.solve();
      }
      catch(LpSolveException e) {
         e.printStackTrace();
      }

   }

   protected boolean addColumn(Column c) throws LpSolveException {
      if(c == null || columns.contains(c))
         return false;
      double col[] = new double[lp.getNrows() + 1];
      col[0] = 1d;
      for(Shape s : c.map.values()) {
         col[s.id] += 1d;
      }
      lp.addColumn(col);
      columns.add(c);

      if(integral)
         lp.setBinary(lp.getNcolumns(), true);
      return true;
   }

   protected boolean price() throws LpSolveException {
      for(Shape[] ss : new dvrlib.generic.RandomOrder<Shape[]>(dvrlib.algorithm.SubSets.subSets(duals.keySet().toArray(new Shape[0])))) {
         double score = 0d;
         for(Shape s : ss) {
            score += duals.get(s);
         }
         if(score < 1d)
            continue;

         Column c = Column.construct(problem, ss);
         if(c == null)
            continue;

         if(!addColumn(c))
            continue;

         if(solveLP())
            return true;
      }
      return false;
   }

   public void printSolution() throws LpSolveException {
      System.out.println("   Number of needed sheets: " + lp.getObjective());
      double[] var = lp.getPtrVariables();
      for(int i = 0, c = 1; i < var.length; i++) {
         if(var[i] > 0d) {
            System.out.print("   Cutting plan for sheet " + c++ + ":" + (integral ? "" : " " + var[i] + " *") + " ");
            double col[] = lp.getPtrColumn(i + 1); // col[0] holds its cost, which should be 1d
            HashMap<String, Integer> shapes = new HashMap<String, Integer>();
            for(int j = 1; j < col.length; j++) {
               if(col[j] != 0d) {
                  String s = problem.shapes.get(Integer.parseInt(lp.getRowName(j)) - 1).name;
                  if(shapes.containsKey(s))
                     shapes.put(s, shapes.get(s) + 1);
                  else
                     shapes.put(s, 1);
               }
            }
            System.out.println(shapes);
         }
      }
   }

   public void solve() {
      try {
         System.out.println("Solving...");
         solveLP();
         while(price()) ;

         System.out.println("Fractional solution:");
         printSolution();

         integral = true;
         for(int i = 1; i <= lp.getNcolumns(); i++) {
            lp.setBinary(i, true);
         }
         solveLP();
         while(price()) ;

         System.out.println("Integral solution:");
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
      HashMap<Shape, Double> dualsOld = duals;
      duals = new HashMap<Shape, Double>();
      for(int i = 1; i < lp.getNrows() + 1; i++) {
         if(dualsArr[i] > 0d)
            duals.put(problem.shapes.get(i - 1), dualsArr[i]);
      }
      // Return whether the duals have changed, and therefore whether the solution has changed
      return !dualsOld.equals(duals);
   }

   //--- java.lang.Object methods
   @Override
   protected void finalize() throws Throwable {
      if(lp != null)
         lp.deleteLp();
   }
}
