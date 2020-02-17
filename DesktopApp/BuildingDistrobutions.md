# Task Timekeeper Desktop App- Building Distributions

This is a guide for building task timekeeper in the various ways for different operating systems. In general, it is required to build these on one of their respective operating systems.

The desktop app uses [SetupBuilder](https://github.com/i-net-software/SetupBuilder) to make the setup distributions, see their page for more information on how it works.

In general:

 * The generated packaged installers will be placed in: `task-timekeeper/DesktopApp/build/distributions/`
 * Command to run to get most of the files: (minus windows and Mac): `./gradlew clean test :DesktopApp:build :DesktopApp:deb :DesktopApp:rpm`

## Preparation

These steps should be done in preparation of the actual packaging:

 * Clean build: `:task-timekeeper$ ./gradlew clean build`

## Linux

### .deb (Debian (Ubuntu))

Simply run the following: `:task-timekeeper$ ./gradlew :DesktopApp:deb`

Requisites:

 * Need to have FakeRoot installed: `sudo apt install lintian fakeroot`
 * Need to have dpkg installed: `sudo apt install dpkg`
 
### .rpm (RHEL, Fedora, SUSE)

Simply run the following: `:task-timekeeper$ ./gradlew :DesktopApp:rpm`

Requisites:

 * Need to have FakeRoot installed: `sudo apt install lintian fakeroot`
 * Need to have rpm installed: `sudo apt install rpm`


## .msi (Windows) UNTESTED

Simply run the following: `:task-timekeeper$ ./gradlew :DesktopApp:msi`

Requisites:

 * Run on a windows system
 * Wix Toolset or [WixEdit](https://wixedit.github.io/) must be installed

## .dmg (MacOS) UNTESTED

Simply run the following: `:task-timekeeper$ ./gradlew :DesktopApp:dmg`

Requisites:

 * Run on a Mac
