/**
 * 2D Cutting stock
 * Copyright (C) 2013 DuncanvR
 * Problem.java
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

import dvrlib.generic.Pair;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Vector;

public class Problem {
   public final static Pattern Comment = Pattern.compile("^#.*");

   protected int cuttingLoss    = 0,
                 resourceWidth  = 0,
                 resourceHeight = 0,
                 resourceSize   = 0;
   protected final Vector<RequiredShape> shapes = new Vector<RequiredShape>();

   public Problem(java.io.File file) {
      try {
         Logger.info("Problem:").inBoldYellow().println();

         Scanner in = new Scanner(file), line = new Scanner(in.nextLine());
         // Skip comments
         while(line.hasNext(Comment))
            line = new Scanner(in.nextLine());
         // Read resource size
         cuttingLoss = line.nextInt();
         Logger.info("   Cutting loss: " + cuttingLoss).println();

         resourceWidth  = line.nextInt();
         resourceHeight = line.nextInt();
         resourceSize   = resourceWidth * resourceHeight;
         Logger.info("   Resources: " + resourceWidth + "x" + resourceHeight).println();

         int id = 1;
         Logger.info("   Shapes:").println();
         while(in.hasNext()) {
            line = new Scanner(in.nextLine());
            if(!line.hasNext(Comment)) {
               int c = line.nextInt(),
                   w = line.nextInt(),
                   h = line.nextInt();
               String n = line.hasNext() ? line.nextLine().trim() : "";
               Logger.info("      " + c + " * " + w + "x" + h + ":\t" + n).println();
               shapes.add(new RequiredShape(new Shape(id++, w, h, n), c));
            }
         }
      }
      catch(java.io.FileNotFoundException ex) {
         Logger.error("Indicated problem file not found").println();
      }
   }
}
