/**
 * 2D Cutting stock
 * Copyright (C) 2013 DuncanvR
 * Shape.java
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

public class Shape {
   protected final int    id, width, height;
   protected final String name;

   public Shape(int id, int width, int height, String name) {
      this.id     = id;
      this.width  = width;
      this.height = height;
      this.name   = name;
   }

   @Override
   public int hashCode() {
      return 893 + 949363 * id + 90 * width + 19 * height;
   }

   @Override
   public String toString() {
      return "Shape#" + id + ": " + width + "x" + height;
   }
}
