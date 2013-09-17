/**
 * 2D Cutting stock
 * Copyright (C) 2013 DuncanvR
 * Main.java
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

public class Main {

   public static void main(String[] args) {
      if(args.length == 1)
         new Solver(new Problem(new java.io.File(args[0]), true)).solve();
      else {
         System.out.println("Cutting stock optimiser");
         System.out.println("Usage: java -jar cuttingstock.jar PROBLEMFILE");
      }
   }
}
