# Task Timekeeper Command Line User Guide

This is a guide for using Task Timekeeper in either `MANAGE` or `SINGLE` mode.

Configuration arguments in the README of the desktop app are not explained here, as they are independent of telling the app of what to do. To learn about configuration, go to the [main readme](README.md)

[Skip to examples](#workflow-examples-cheat-sheet)

### `MANAGE` vs `SINGLE` Mode:

`MANAGE` mode is simply a continuous version of `SINGLE`. In single mode, you enter your command in the initial call to the program, and only that one command is run. In Manage mode, you simply run the program in this mode and are entered into a new command prompt to enter in multiple commands (one at a time).

## Usage

### Workflow

### Breakdown of a command

### Working with Tasks

### Working with Periods

### Working with Spans

### Inputting time/dates

### Special Commands

 * *newspan* - finishes remaining timespans and starts a new one in the selected period. Need to have period selected and task specified.
 * *selectnewest* - selects the newest period held
 * *finish* - Finishes remaining timespans in selected period.
 * *newperiod* - Finishes remaining timespans in selected period, creates a new period and selects it.

## Workflow Examples (cheat sheet)

Examples of working through common tasks. Listed are arguments to pass, which can be given when using either single or managed mode.

This section is meant to be a quick reference guide, view the sections above for more details and explanations.

### Adding Tasks

Adding a new task:

`-a add -o task -n TaskOne`

Verifying it was created:

`-a view -o task`

### Adding a work period

Adding a new work period:

`-s newperiod`

Verifying it was created:

`-a view -o period`

#### Notes:

This finishes timespans in the currently selected period, adds a new period and selects it.

### Removing a work period

Removing a work period:

`-a remove -o period -i <index of period in view>`

Verifying it was removed:

`-a view -o period`

### Selecting a work period

**NOTE::** Selecting specific periods is only supported in manage mode. In single mode, only the newest/ most recent period is selected.

Selecting a work period:

 `-a view -o period -se -i <index of period in view>`

Verifying it was selected (will have an asterisk in the "S" column):

`-a view -o period`

### Starting a new timespan

Starting a new timespan (and finishing the others):

`-s newspan -tn <task name>`

Verifying it was created:

`-a view -o span`

### Finish unfinished timespans

Finishing all unfinished spans:

`-s finish`

Verifying they were finished:

`-a view -o span`

### Review timespans

Viewing all spans in period:

`-a view -o span`

Reviewing time spent on tasks:

`<unimplemented/ TODO>`

### Cleaning up

Removing periods before a certain date:

`-a remove -o period -bf <datetime>`