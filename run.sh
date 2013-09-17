#!/usr/bin/bash

# 2D Cutting stock - Run script
# Copyright (C) DuncanvR, 2013
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

# Run the program
LD_LIBRARY_PATH=./lib java -jar ./dist/cuttingstock.jar "$@"
