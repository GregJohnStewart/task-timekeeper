# task-timekeeper
All-in one tool for keeping track of time spent on tasks

The base code behind all the projects is located in the BaseCode project.

Implementations can be found in desktop-app

## How it works

The Manager works off of several objects:

 - task - A task you spend time on.
   - Made up of the name of the task and any number of custom attributes.
   - Example: Project name, with charge code as an attribute
 - Timespan - A span of time spent on a task
   - Made up of the task being worked, and a start/end datetime
 - Work period - A period of time in which work takes place. 
   - Made up of timespans, and can have any number of custom attributes associated with it.
   - timespans are added to a period to describe how long certain tasks were worked for that period 
   - Example: a workday where I worked on a project from 1:00 to 2:00

