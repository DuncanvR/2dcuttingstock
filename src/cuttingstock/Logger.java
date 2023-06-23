/**
 * 2D Cutting stock
 * Copyright (C) 2023 DuncanvR
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

import dvrlib.generic.UnixColorCode;
import java.util.function.BiConsumer;

public class Logger {
   public enum Level {
      ERROR,
      WARNING,
      INFO,
      DEBUG,
      TRACE,
   }

   public static class Message {
      protected       UnixColorCode back  = null;
      protected       UnixColorCode front = null;
      protected final Level         lvl;
      protected final String        msg;

      protected Message(String msg, Level lvl) {
         this.msg = msg;
         this.lvl = lvl;
      }

      public Message onBlack() {
         this.back = UnixColorCode.Background.Black;
         return this;
      }

      public Message onRed() {
         this.back = UnixColorCode.Background.Red;
         return this;
      }

      public Message onGreen() {
         this.back = UnixColorCode.Background.Green;
         return this;
      }

      public Message onYellow() {
         this.back = UnixColorCode.Background.Yellow;
         return this;
      }

      public Message onBlue() {
         this.back = UnixColorCode.Background.Blue;
         return this;
      }

      public Message onPurple() {
         this.back = UnixColorCode.Background.Purple;
         return this;
      }

      public Message onCyan() {
         this.back = UnixColorCode.Background.Cyan;
         return this;
      }

      public Message onWhite() {
         this.back = UnixColorCode.Background.White;
         return this;
      }

      public Message inBlack() {
         this.front = UnixColorCode.Regular.Black;
         return this;
      }

      public Message inRed() {
         this.front = UnixColorCode.Regular.Red;
         return this;
      }

      public Message inGreen() {
         this.front = UnixColorCode.Regular.Green;
         return this;
      }

      public Message inYellow() {
         this.front = UnixColorCode.Regular.Yellow;
         return this;
      }

      public Message inBlue() {
         this.front = UnixColorCode.Regular.Blue;
         return this;
      }

      public Message inPurple() {
         this.front = UnixColorCode.Regular.Purple;
         return this;
      }

      public Message inCyan() {
         this.front = UnixColorCode.Regular.Cyan;
         return this;
      }

      public Message inWhite() {
         this.front = UnixColorCode.Regular.White;
         return this;
      }

      public Message inBoldBlack() {
         this.front = UnixColorCode.Bold.Black;
         return this;
      }

      public Message inBoldRed() {
         this.front = UnixColorCode.Bold.Red;
         return this;
      }

      public Message inBoldGreen() {
         this.front = UnixColorCode.Bold.Green;
         return this;
      }

      public Message inBoldYellow() {
         this.front = UnixColorCode.Bold.Yellow;
         return this;
      }

      public Message inBoldBlue() {
         this.front = UnixColorCode.Bold.Blue;
         return this;
      }

      public Message inBoldPurple() {
         this.front = UnixColorCode.Bold.Purple;
         return this;
      }

      public Message inBoldCyan() {
         this.front = UnixColorCode.Bold.Cyan;
         return this;
      }

      public Message inBoldWhite() {
         this.front = UnixColorCode.Bold.White;
         return this;
      }

      public Message inUnderlinedBlack() {
         this.front = UnixColorCode.Underline.Black;
         return this;
      }

      public Message inUnderlinedRed() {
         this.front = UnixColorCode.Underline.Red;
         return this;
      }

      public Message inUnderlinedGreen() {
         this.front = UnixColorCode.Underline.Green;
         return this;
      }

      public Message inUnderlinedYellow() {
         this.front = UnixColorCode.Underline.Yellow;
         return this;
      }

      public Message inUnderlinedBlue() {
         this.front = UnixColorCode.Underline.Blue;
         return this;
      }

      public Message inUnderlinedPurple() {
         this.front = UnixColorCode.Underline.Purple;
         return this;
      }

      public Message inUnderlinedCyan() {
         this.front = UnixColorCode.Underline.Cyan;
         return this;
      }

      public Message inUnderlinedWhite() {
         this.front = UnixColorCode.Underline.White;
         return this;
      }

      public void print() {
         if(MinimumLevel.compareTo(lvl) >= 0)
            System.out.print(printableMessage());
      }

      public void println() {
         if(MinimumLevel.compareTo(lvl) >= 0)
            System.out.println(printableMessage());
      }

      protected String printableMessage() {
         if(SupportsColour)
           return (back == null ? "" : back.toString()) + (front == null ? "" : front.toString()) + msg + (back == null && front == null ? "" : UnixColorCode.Reset.toString());
        return msg;
      }
   }

   public static Level   MinimumLevel   = Level.INFO;
   public static boolean SupportsColour = true;

   public static Message error(Object msg) {
      return new Message(msg.toString(), Level.ERROR).onRed();
   }

   public static Message warning(Object msg) {
      return new Message(msg.toString(), Level.WARNING).onYellow();
   }

   public static Message info(Object msg) {
      return new Message(msg.toString(), Level.INFO);
   }

   public static Message debug(Object msg) {
      return new Message(msg.toString(), Level.DEBUG);
   }

   public static Message trace(Object msg) {
      return new Message(msg.toString(), Level.TRACE);
   }
}
