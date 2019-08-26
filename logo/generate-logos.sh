#!/usr/bin/env bash
# This script automatically generates the logos needed for the project
# Must have inkscape installed

mainFile="logo-main.png";
destinationDir="generated/desktop-app/";

# Generate the gui icon. Used as the icon for the Desktop GUI and taskbar icon.
guiIconSize="128"
inkscape -z -e ${destinationDir}gui-icon.png -w $guiIconSize -h $guiIconSize $mainFile

# generate the desktop icon, or the icon of the executable when installed.
desktopIconSize="500"
inkscape -z -e ${destinationDir}desktop-icon.icn -w $desktopIconSize -h $desktopIconSize $mainFile