# Task Timekeeper Desktop App

This is the desktop app for Task Timekeeper. Requires Java 11 to run.

## Workflow

In general, this is how one works with Task Timekeeper:

 - Make tasks to associate with timespans
 - Make a new work period to add spans to
 - Add spans to the work period
   - You can add spans without specifying end time, you can complete the span later, when you finish working
 - repeat as you do work
 - review the time you spent on what tasks for either a certain period or over a few periods


## Configuration

Main configuration for the app. Command line arguments override environment variables.

| Name        | Description                                                           | Value(s)               | Default Value            | Environment                 | Command Line Arg        |
| ----------- | --------------------------------------------------------------------- | ---------------------- | ------------------------ | --------------------------- | ----------------------- |
| Config File | Where to put configuration, and some configuration is saved.          | Writable file location | ~/.timeKeeper/config.cfg | TKPR_CONFIG_FILE=(location) | --configFile (location) |
| Save file   | Where time data is saved.                                             | Writable file location | ~/.timeKeeper/save.tks   | TKPR_SAVE_FILE=(location)   | --saveFile (location)   |
| Mode        | How to open the program; GUI, Management, or single command (default) | GUI / MANAGE / SINGLE  | SINGLE                   | TKPR_MODE=(mode)            | --mode (mode)           |

## Command line interaction

To learn how to interact with the program from the command line, look [here](Command%20Line.md)

It should be noted that in single mode, one can only operate on the latest work period (it is automatically selected and cannot be changed).

## Distributing/ Making Distributions

To learn how to build this for yourself, look [here](BuildingDistrobutions.md)

## Running from Gradle

To run this with Gradle, use as with the following example:

`-PappArgs=[\"--mode\",\"GUI_SWING\"]`

## TODOS:

- make stats processing use the new allstats processor