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

import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.Vector;

public class Problem {
   public final static Pattern Comment = Pattern.compile("^#.*");

   protected int cuttingLoss    = 0,
                 resourceWidth  = 0,
                 resourceHeight = 0;
   protected final Vector<Shape> shapes = new Vector<Shape>();

   public Problem(java.io.File file, boolean print) {
      try {
         if(print)
            System.out.println("Problem:");

         Scanner in = new Scanner(file), line = new Scanner(in.nextLine());
         // Skip comments
         while(line.hasNext(Comment))
            line = new Scanner(in.nextLine());
         // Read resource size
         cuttingLoss = line.nextInt();
         if(print)
            System.out.println("   Cutting loss: " + cuttingLoss);

         resourceWidth = line.nextInt();
         resourceHeight = line.nextInt();
         if(print)
            System.out.println("   Resources: " + resourceWidth + "x" + resourceHeight);

         int id = 1;
         if(print)
            System.out.println("   Shapes:");
         while(in.hasNext()) {
            line = new Scanner(in.nextLine());
            if(!line.hasNext(Comment)) {
               int c = line.nextInt(),
                   w = line.nextInt(),
                   h = line.nextInt();
               String n = line.hasNext() ? line.nextLine().trim() : "";
               if(print)
                  System.out.println("      " + c + " times " + w + "x" + h + ":\t" + n);
               for(int i = 0; i < c; i++) {
                  shapes.add(new Shape(id++, w, h, n));
               }
            }
         }
      }
      catch(java.io.FileNotFoundException ex) {
         System.out.println("Indicated problem file not found");
      }
   }
}
