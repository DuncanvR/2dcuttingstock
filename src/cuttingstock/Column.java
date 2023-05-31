/**
 * 2D Cutting stock
 * Copyright (C) 2013 DuncanvR
 * Column.java
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class Column {
   public static class Loc {
      protected boolean turn;
      protected int     x, y;

      public Loc(int x, int y, boolean turn) {
         this.x    = x;
         this.y    = y;
         this.turn = turn;
      }

      @Override
      public int hashCode() {
         return 472 * x + 849 * y + (turn ? 84934 : 21038);
      }

      @Override
      public String toString() {
         return (turn ? "t" : "") + x + "," + y;
      }
   }

   protected final Problem problem;

   protected HashMap<Loc, Shape> map = new HashMap<Loc, Shape>();

   public Column(Problem problem) {
      this.problem = problem;
   }

   public Column(Column that) {
      this.problem = that.problem;
      map.putAll(that.map);
   }

   public boolean add(Loc l, Shape s) {
      if(check(l, s)) {
         map.put(l, s);
         return true;
      }
      return false;
   }

   protected boolean check(Loc l, Shape s) {
      // Check location
      if(l.x < 0 || l.y < 0)
         return false;
      // Check fit within resources
      if(l.x + (l.turn ? s.height : s.width) > problem.resourceWidth || l.y + (l.turn ? s.width : s.height) > problem.resourceHeight)
         return false;
      // Check overlap with already included shapes
      for(java.util.Map.Entry<Loc, Shape> e : map.entrySet()) {
         Loc l2 = e.getKey();
         Shape s2 = e.getValue();
         if(l2.x - l.x  - (l.turn  ? s.height  : s.width)   < problem.cuttingLoss &&
            l.x  - l2.x - (l2.turn ? s2.height : s2.width)  < problem.cuttingLoss &&
            l2.y - l.y  - (l.turn  ? s.width   : s.height)  < problem.cuttingLoss &&
            l.y  - l2.y - (l2.turn ? s2.width  : s2.height) < problem.cuttingLoss)
            return false;
      }
      return true;
   }

   /**
    * Tries to arrange all the given shapes into a new column and returns it, or <code>null</code> if no column can be made.
    */
   public static Column construct(Problem problem, Shape shapes[]) {
      for(Shape[] ss : new dvrlib.generic.RandomOrder<Shape[]>(dvrlib.algorithm.SubSets.permutations(shapes))) {
         Column c = new Column(problem);
         if(!construct(c, ss, 0))
            continue;

         for(Shape s : ss) {
            if(!c.map.containsValue(s)) {
               System.out.print("Shapes: [");
               for(Shape s2 : ss) {
                  System.out.print(" " + s2);
               }
               System.out.println(" ]");
               System.out.println("Constructed " + c);
               throw new RuntimeException("Missing " + s);
            }
         }

         return c;
      }
      return null;
   }

   protected static boolean construct(Column c, Shape ss[], int i) {
      if(i < ss.length) {
         HashSet<Loc> locs = c.possibleLocs(ss[i]);
         for(Loc l : locs) {
            c.map.put(l, ss[i]);
            if(construct(c, ss, i + 1))
               return true;
            else
               c.map.remove(l);
         }
         return false;
      }
      return true;
   }

   /**
    * Returns all possible locations the given shape could be placed in this column.
    */
   protected HashSet<Loc> possibleLocs(Shape s) {
      HashSet<Loc> locs = new HashSet<Loc>();
      // Check resource bounds
      int xmax  = problem.resourceWidth - s.width,
          xmaxt = problem.resourceWidth - s.height,
          ymax  = problem.resourceHeight - s.height,
          ymaxt = problem.resourceHeight - s.width;
      possibleLocs(new Loc(0,     0,     false), s, locs);
      possibleLocs(new Loc(0,     0,     true),  s, locs);
      possibleLocs(new Loc(xmax,  0,     false), s, locs);
      possibleLocs(new Loc(xmaxt, 0,     true),  s, locs);
      possibleLocs(new Loc(0,     ymax,  false), s, locs);
      possibleLocs(new Loc(0,     ymaxt, true),  s, locs);
      possibleLocs(new Loc(xmax,  ymax,  false), s, locs);
      possibleLocs(new Loc(xmaxt, ymaxt, true),  s, locs);
      // Check sides of already present shapes
      for(Entry<Loc, Shape> e : map.entrySet()) {
         Loc l = e.getKey();
         int otherWidth  = (l.turn ? e.getValue().height : e.getValue().width),
             otherHeight = (l.turn ? e.getValue().width  : e.getValue().height);
         /*
          * Try all possible locations around the other shape:
          *   b|c             d|e   y1
          *   =/===============\=
          *   a|               |f   y2
          *    |               |
          *    |               |
          *   l|               |g   y3
          *   =\===============/=
          *   k|j             i|h   y4
          *
          *  x1 x2           x3 x4
          */
         int x1  = l.x - s.width  - problem.cuttingLoss,
             x1t = l.x - s.height - problem.cuttingLoss,
             x2  = l.x,
             x3  = l.x + otherWidth - s.width,
             x3t = l.x + otherWidth - s.height,
             x4  = l.x + otherWidth + problem.cuttingLoss,
             y1  = l.y - s.height - problem.cuttingLoss,
             y1t = l.y - s.width  - problem.cuttingLoss,
             y2  = l.y,
             y3  = l.y + otherHeight - s.height,
             y3t = l.y + otherHeight - s.width,
             y4  = l.y + otherHeight + problem.cuttingLoss;
         possibleLocs(new Loc(x1,  y2,  false), s, locs); // a
         possibleLocs(new Loc(x1t, y2,  true),  s, locs); // a
         possibleLocs(new Loc(x1,  y1,  false), s, locs); // b
         possibleLocs(new Loc(x1t, y1t, true),  s, locs); // b
         possibleLocs(new Loc(x2,  y1,  false), s, locs); // c
         possibleLocs(new Loc(x2,  y1t, true),  s, locs); // c
         possibleLocs(new Loc(x3,  y1,  false), s, locs); // d
         possibleLocs(new Loc(x3t, y1t, true),  s, locs); // d
         possibleLocs(new Loc(x4,  y1,  false), s, locs); // e
         possibleLocs(new Loc(x4,  y1t, true),  s, locs); // e
         possibleLocs(new Loc(x4,  y2,  false), s, locs); // f
         possibleLocs(new Loc(x4,  y2,  true),  s, locs); // f
         possibleLocs(new Loc(x4,  y3,  false), s, locs); // g
         possibleLocs(new Loc(x4,  y3t, true),  s, locs); // g
         possibleLocs(new Loc(x4,  y4,  false), s, locs); // h
         possibleLocs(new Loc(x4,  y4,  true),  s, locs); // h
         possibleLocs(new Loc(x3,  y4,  false), s, locs); // i
         possibleLocs(new Loc(x3t, y4,  true),  s, locs); // i
         possibleLocs(new Loc(x2,  y4,  false), s, locs); // j
         possibleLocs(new Loc(x2,  y4,  true),  s, locs); // j
         possibleLocs(new Loc(x1,  y4,  false), s, locs); // k
         possibleLocs(new Loc(x1t, y4,  true),  s, locs); // k
         possibleLocs(new Loc(x1,  y3,  false), s, locs); // l
         possibleLocs(new Loc(x1t, y3t, true),  s, locs); // l

         possibleLocs(new Loc(0,     y1,    false), s, locs);
         possibleLocs(new Loc(0,     y1t,   true),  s, locs);
         possibleLocs(new Loc(0,     y4,    false), s, locs);
         possibleLocs(new Loc(0,     y4,    true),  s, locs);
         possibleLocs(new Loc(xmax,  y1,    false), s, locs);
         possibleLocs(new Loc(xmaxt, y1t,   true),  s, locs);
         possibleLocs(new Loc(xmax,  y4,    false), s, locs);
         possibleLocs(new Loc(xmaxt, y4,    true),  s, locs);
         possibleLocs(new Loc(x1,    0,     false), s, locs);
         possibleLocs(new Loc(x1t,   0,     true),  s, locs);
         possibleLocs(new Loc(x4,    0,     false), s, locs);
         possibleLocs(new Loc(x4,    0,     true),  s, locs);
         possibleLocs(new Loc(x1,    ymax,  false), s, locs);
         possibleLocs(new Loc(x1t,   ymaxt, true),  s, locs);
         possibleLocs(new Loc(x4,    ymax,  false), s, locs);
         possibleLocs(new Loc(x4,    ymaxt, true),  s, locs);
      }
      return locs;
   }

   protected void possibleLocs(Loc l, Shape s, HashSet<Loc> locs) {
      if(check(l, s))
         locs.add(l);
   }

   @Override
   public boolean equals(Object o) {
      return (o instanceof Column && ((Column) o).map.values().containsAll(map.values()) && map.values().containsAll(((Column) o).map.values()));
   }

   @Override
   public int hashCode() {
      int hc = 489343;
      for(Shape s : map.values()) {
         hc += 42 * s.hashCode();
      }
      return hc;
   }

   @Override
   public String toString() {
      return "Column " + map;
   }
}
