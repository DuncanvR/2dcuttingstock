2dcuttingstock
===

This is a simple solver for 2 dimensional cutting stock problems.
All shapes are expected to be rectangular, and a predefined cutting loss is taken into account.
It uses a two-step approach: first it applies column generation to solve the LP relaxation of the problem to optimality; then it transforms the variables of all found columns to integrals and solves the problem again, giving a solution very close to the optimum.
Please note that the pricing problem will explore every possible combination and configuration of shapes in order to create new columns, which might take an awful lot of time on larger instances.

Problem instances
---
Instances are given as plain text files, in the following format.
The first line holds the cutting loss, width of the resource and height of the resource respectively.
The remaining lines list the shapes that have to be cut out of the resources, defined by the number of times that shape is to be included, its width and height and optionally a name.
All numbers are expected to be integers, and separated by whitespace.
Lines starting with a `#` are considered comments and will be ignored.
An example problem looks like this:

    # Cutting_loss Resource_width Resource_height
    2 2440 1220
    # Count Shape_width Shape_height Name
    2 800  450 A
    2 2250 450 B
    1 2250 800 C
    2 2100 450 D
    1 2100 800 E

Libraries
---
The program is written in Java and makes use of the [lp_solve](http://lpsolve.sourceforge.net/5.5/) and [DvRlib](https://github.com/duncanvr/DvRlib) libraries.
To compile, place the libraries --- i.e. `lpsolve55j.jar`, `liblpsolve55j.so` (or `liblpsolve55j.dll` on Windows) and `DvRlib.jar` --- in the `lib/` directory, and run the `compile.sh` script.
It can then be executed using the `run.sh` script, by calling `run.sh PROBLEMFILE` from your favourite shell.
