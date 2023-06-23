/**
 * 2D Cutting stock
 * Copyright (C) 2023 DuncanvR
 * RequiredShape.java
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

import dvrlib.generic.IterableOnce;
import dvrlib.generic.Pair;
import dvrlib.generic.RandomOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

public class ColumnBuilder {
   public enum Feasibility {
      FEASIBLE,
      INFEASIBLE,
      UNKNOWN,
   }

   public class Creation {
      protected final HashMap<Shape, Pair<Feasibility, Creation>> nodes = new HashMap<Shape, Pair<Feasibility, Creation>>();

      public Feasibility checkFeasibility(Shape[] shapes, int i) {
         Pair<Feasibility, Creation> node = nodes.get(shapes[i]);
         if(node == null)
            return Feasibility.UNKNOWN;
         if(i == shapes.length - 1)
            return node.a;
         if(node.a == Feasibility.INFEASIBLE)
            return Feasibility.INFEASIBLE;
         return node.b.checkFeasibility(shapes, i + 1);
      }
   }

   protected final Creation emptyCreation = new Creation();

   /**
    * Tries to arrange all the given shapes into new columns and returns those.
    */
   public Column[] buildColumns(Problem problem, RequiredShape shapes[]) {
      Logger.debug("   Attempting to construct columns for:").println();
      for(RequiredShape s : shapes) {
         Logger.debug("      " + s).println();
      }

      ArrayList<Column> cs = new ArrayList<Column>();
      for(Shape[] ss : explodeRequiredShapes(shapes)) {
         Feasibility f = emptyCreation.checkFeasibility(ss, 0);
         if(f != Feasibility.INFEASIBLE) {
            Logger.debug("      Attempting with #" + ss[0].id).print();
            for(int i = 1; i < ss.length; i++) {
               Logger.debug("," + ss[i].id).print();
            }
            Logger.debug("").println();

            Column c = new Column(problem);
            if(buildColumn(emptyCreation, c, ss, 0))
               cs.add(c);
         }
      }
      Logger.debug("      Created " + cs.size() + " column" + (cs.size() == 1 ? "" : "s")).println();
      return cs.toArray(new Column[cs.size()]);
   }

   protected IterableOnce<Shape[]> explodeRequiredShapes(RequiredShape shapes[]) {
      ArrayList<ArrayList<Shape>> sets = new ArrayList<ArrayList<Shape>>();
      sets.add(new ArrayList<Shape>());

      for(int i = 0; i < shapes.length; i++) {
         for(ArrayList<Shape> set : sets) {
            set.add(shapes[i].shape());
         }

         if(shapes[i].count() > 1) {
            ArrayList<ArrayList<Shape>> extraSets = new ArrayList<ArrayList<Shape>>();
            for(int j = 1; j < shapes[i].count(); j++) {
               for(ArrayList<Shape> s : sets) {
                  ArrayList<Shape> clone = new ArrayList<Shape>();
                  clone.addAll(s);
                  for(int k = 0; k < j; k++) {
                     clone.add(shapes[i].shape());
                  }
                  extraSets.add(clone);
               }
            }
            sets.addAll(extraSets);
         }
      }

      return new IterableOnce<Shape[]>(sets.stream()
                                           .map((ArrayList<Shape> ss) -> { return ss.stream()
                                                                                    .sorted(Comparator.comparing(Shape::getSize).reversed())
                                                                                    .toArray(Shape[]::new); })
                                           .sorted(Comparator.comparing(java.lang.reflect.Array::getLength).reversed())
                                           .iterator());
   }

   protected boolean buildColumn(Creation creation, Column column, Shape shapes[], int i) {
      if(i >= shapes.length)
         return true;

      Pair<Feasibility, Creation> node = creation.nodes.get(shapes[i]);
      if(node == null || node.a != Feasibility.INFEASIBLE) {
         Collection<Column.Loc> locs = column.possibleLocs(shapes[i]);
         if(locs.size() > 0) {
            if(node == null || node.a == Feasibility.UNKNOWN) {
               node = new Pair<>(Feasibility.FEASIBLE, new Creation());
               creation.nodes.put(shapes[i], node);
            }
            for(Column.Loc l : new RandomOrder<Column.Loc>(locs)) {
               column.add(l, shapes[i]);
               if(buildColumn(node.b, column, shapes, i + 1))
                  return true;
               else
                  column.map.remove(l);
            }
         }
         else {
            creation.nodes.put(shapes[i], new Pair<>(Feasibility.INFEASIBLE, null));
         }
      }
      return false;
   }
}
