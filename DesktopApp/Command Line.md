# Task Timekeeper Command Line User Guide

This is a guide for using Task Timekeeper in either `MANAGE` or `SINGLE` mode.

Configuration arguments in the README of the desktop app are not explained here, as they are independent of telling the app of what to do. To learn about configuration, go to the [main readme](README.md)

[Skip to examples](#workflow-examples-cheat-sheet)

### `MANAGE` vs `SINGLE` Mode:

`MANAGE` mode is simply a continuous version of `SINGLE`. In single mode, you enter your command in the initial call to the program, and only that one command is run. In Manage mode, you simply run the program in this mode and are entered into a new command prompt to enter in multiple commands (one at a time).

## Usage

**Why the backslashes between words?**

In this guide you will see things like the following: `-n Task\ Name`

This is meant to express how you will need to enter in strings with spaces in managed mode. When processing in single mode, you can simply throw quotes around the value (I.e., `"Task Name"`, as the shell knows how to interpret that.

In managed mode, however, the parser is not quite as sophisticated. It splits your input on spaces, ignoring escaped spaces (`\ `). It does not know to look for quoted strings as one value. Therefore, in managed mode, spaces in string inputs require spaces to be escaped.

This guide has all example strings with escaped spaces because doing this would work for both managed and single mode, whereas quoted strings would only be valid for single mode. 


### Workflow

### Breakdown of a command

### Working with Tasks

#### Viewing tasks

TODO

#### Adding a task

The syntax for adding a task is simple.

`-a create -o task -n Task\ Name`

Here, `-a create -o task` specifies we want to create an object of type task, and `-n Task\ Name` specifies that we are giving it the name "Task Name".

You can also specify a custom attribute when creating:

`-a create -o task -n TaskName -att charge\ code -atv 12345`

Where `-att chargeCode` specifies we are giving it an attribute named "charge code", and `-atv 12345` specifies that we give that attribute a value of "12345".

#### Modifying a task

TODO:: explain index method of selecting task to modify

##### Changing the task's name

`-a edit -o task -n Task\ Name -nn New\ Task\ Name`

This changes the "Task Name" task to have the new name "New Task Name". `-n Task\ Name` specifies the name of the task to change, while `-nn New\ Task\ Name` specifies the new name to give it.

##### Changing a task's attributes

###### Adding/ Updating an attribute

`-a edit -o task -n Task\ Name -att charge\ code -atv 1234567`

This either adds or updates the attribute "charge code", giving it the value "1234567". `-at charge\ code` specifies the "charge code" attribute, while `-atv 12334567` supplies the value to give it. 

###### Removing an attribute

`-a edit -o task -n Task\ Name -att charge\ code`

This removes the attribute "charge code" from the "Task Name" task; simply not specifying the value to give the attribute tells the program to remove it.

#### Removing a task

TODO:: explain index method of selecting task to remove

`-a remove -o task -n Task\ Name`

This removes the task "Task Name" from the list of tasks.

This can fail if there are timespans that use this task.

### Working with Periods

#### Viewing Work Periods

TODO

#### Adding a work period

`-a add -o period`

This adds a new work period. The created period should be the first one in a viewing of periods. Note this alone does not select the newly created period (unless in single mode) nor does it finish any tasks in previously created periods.

You can specify to select the new period by specifying to do so with the command:

`-a add -o period -se`

You can specify a key/val attribute pair when adding:

`-a add -o period -att key -atv value`

#### Updating a work period

All modifications on a period affect the selected period. In single mode, this is the most recent period.

##### Changing a periods's attributes

###### Adding/ Updating an attribute

`-a edit -o period -att charge\ code -atv 1234567`

This either adds or updates the attribute "charge code", giving it the value "1234567". `-at charge\ code` specifies the "charge code" attribute, while `-atv 12334567` supplies the value to give it. 

###### Removing an attribute

`-a edit -o period -att charge\ code`

This removes the attribute "charge code" from the selected period; simply not specifying the value to give the attribute tells the program to remove it.

#### Removing a work period

##### Removing by index

TODO

##### Removing based on datetime

You can remove work periods based on datetimes.

Note: in the following examples, replace <datetime> with a valid datetime format. See the [inputting date/times](#inputting-timedates) section.

###### Removing periods before a datetime

`-a remove -o period -bf <datetime>`

The `-bf <datetime>` parameter specifies to remove periods before the given datetime. 

###### Removing periods before a datetime

`-a remove -o period -af <datetime>`

The `-af <datetime>` parameter specifies to remove periods after the given datetime.

###### Removing periods between two datetimes

`-a remove -o period -bf <datetime> -af <datetime>`

The `-bf <datetime>` parameter specifies to remove periods before the given datetime. 

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