#Task Timekeeper Desktop App

This is the desktop app for Task Timekeeper.

##Configuration

Main configuration for the app. Command line arguments override environment variables.

| Name        | Description                                                           | Value(s)               | Default Value            | Environment                 | Command Line Arg        |
| ----------- | --------------------------------------------------------------------- | ---------------------- | ------------------------ | --------------------------- | ----------------------- |
| Config File | Where to put configuration, and some configuration is saved.          | Writable file location | ~/.timeKeeper/config.cfg | TKPR_CONFIG_FILE=(location) | --configFile (location) |
| Save file   | Where time data is saved.                                             | Writable file location | ~/.timeKeeper/save.tks   | TKPR_SAVE_FILE=(location)   | --saveFile (location)   |
| Mode        | How to open the program; GUI, Management, or single command (default) | GUI / MANAGE / SINGLE  | SINGLE                   | TKPR_MODE=(mode)            | --mode (mode)           |

##Command line args

These are arguments for interacting with SINGLE mode.

| Argument    | Description              | Valid Values |
| ----------- | ------------------------ | ------------ |
| --help / -h | Displays the help output | (no values)  | 


##Management Mode

TODO  