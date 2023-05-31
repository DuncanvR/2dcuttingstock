#!/bin/sh

# 2D Cutting stock - Compile script
# Copyright (C) DuncanvR, 2012-2013
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.

# Change directory to where the script is located
cd "$(dirname "$0")"
SCRIPT="$(basename "$0")"
while([ -L $SCRIPT ]) do
   SCRIPT="$(readlink "$SCRIPT")"
   cd "$(dirname "$SCRIPT")"
   SCRIPT="$(basename "$SCRIPT")"
done
DIR="$(pwd)"
LIBDIR=$DIR/lib/

# Build
echo " *** Building source..."
if [ -d $DIR/build ] ; then
   rm -R $DIR/build 2>/dev/null
fi
mkdir -p $DIR/build
cd $DIR/src/
javac -g -cp $LIBDIR/lpsolve55j.jar:$LIBDIR/DvRlib.jar -d ../build/ cuttingstock/* -Xlint:deprecation -Xlint:unchecked
if [ "$?" != "0" ] ; then
   echo " *** Error(s) while compiling source; Aborting..."
   exit
fi

if [ -d $DIR/test-src ] ; then
   # Build tests
   echo " *** Building tests..."
   if [ -d $DIR/test-build ] ; then
      rm -R $DIR/test-build/* 2>/dev/null
   else
      mkdir -p $DIR/test-build
   fi
   cd $DIR/test-src
   javac -cp $LIBDIR/junit.jar:$LIBDIR/lpsolve55j.jar:$LIBDIR/DvRlib.jar:../build/ -d ../test-build/ *
   if [ "$?" != "0" ] ; then
      echo " *** Error(s) while compiling tests; Aborting..."
      exit
   fi

   # Run tests
   echo " *** Running tests..."
   cd $DIR/test-build
   java -cp $LIBDIR/junit.jar:$LIBDIR/hamcrest-core.jar:$LIBDIR/lpsolve55j.jar:$LIBDIR/DvRlib.jar:../build/:./ -ea org.junit.runner.JUnitCore $(/usr/bin/find . -iname \*.class | /usr/bin/grep -v '\$' | sed 's:\./\(.*\)\.class:\1:' | tr '/' '.')
   if [ "$?" != "0" ] ; then
      echo " *** Error(s) while running tests; Aborting..."
      exit
   fi
fi

# Package
echo " *** Packaging..."
mkdir -p $DIR/dist
cd $DIR/dist
jar cfm cuttingstock.jar manifest -C ../build/ cuttingstock
if [ "$?" != "0" ] ; then
   echo " *** Error(s) while packaging; Aborting"
   exit
fi

# Done
echo " *** Done"
