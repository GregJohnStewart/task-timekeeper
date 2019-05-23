#Task Timekeeper Desktop App

This is the desktop app for Task Timekeeper.

## Workflow

In general, this is how one works with Task timekeeper:

 - Make tasks to associate with timespans
 - Make a new work period to add spans to
 - Add spans to the work period
   - You can add spans without specifying end time, you can complete the span later, when you finish working
 - repeat as you do work
 - review the time you spent on what tasks for either a certain period or over a few periods 


##Configuration

Main configuration for the app. Command line arguments override environment variables.

| Name        | Description                                                           | Value(s)               | Default Value            | Environment                 | Command Line Arg        |
| ----------- | --------------------------------------------------------------------- | ---------------------- | ------------------------ | --------------------------- | ----------------------- |
| Config File | Where to put configuration, and some configuration is saved.          | Writable file location | ~/.timeKeeper/config.cfg | TKPR_CONFIG_FILE=(location) | --configFile (location) |
| Save file   | Where time data is saved.                                             | Writable file location | ~/.timeKeeper/save.tks   | TKPR_SAVE_FILE=(location)   | --saveFile (location)   |
| Mode        | How to open the program; GUI, Management, or single command (default) | GUI / MANAGE / SINGLE  | SINGLE                   | TKPR_MODE=(mode)            | --mode (mode)           |

## Command line interaction

Whether working in single command mode or management mode, the same arguments are used.

It should be noted that in single mode, one can only operate on the latest work period (it is automatically selected and cannot be changed).

### Commands

TODO:: table detailing different commands

#### Special Commands

TODO:: list available special commands

#### Time inputs

TODO:: list available time input formats