# task-timekeeper
All-in one tool for keeping track of time spent on tasks

The base code behind all the projects is located in the [BaseCode](BaseCode) project.

Implementations can be found in [DesktopApp](DesktopApp) and [WebServer](WebServer)

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

## Known Issues

This is a list of known issues, if any.

### Webserver

 - The webserver is very much in development. If you see test errors in the project, just ignore them. Tentatively waiting for GraalVm to support Java11 to continue.

## Desktop App

The desktop app is located [here](DesktopApp). You can run it either as a cli or gui, and lets you jump into using the tool.

## Misc notes

### Versioning

The versioning in this project follows the following loose guidelines:

`<major>.<minor>.<micro>.<build>`

#### Major

This version describes a large set of changes; this is bumped when backwards incompatible changes are made and when enough changes warrants it

#### Minor 

This version describes minor but significant changes, such as in small behaviors and improvement tweaks

#### Micro

This version describes tiny changes, such as tweaks in output wording, spelling, or format. These changes would not affect behavior (at least significantly)

#### Build

This version is automatically incremented at build time, simply a counter for the number of builds.