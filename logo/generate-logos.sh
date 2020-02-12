#!/usr/bin/env bash
# This script automatically generates the logos needed for the project
# Must have inkscape installed

mainFile="logo-main.svg";
destinationDir="generated/";

# Generate logo-main png from the main svg
mainSize="1000"
inkscape -z -e ${destinationDir}logo-main.png -w $mainSize -h $mainSize $mainFile

# Generate the gui icon. Used as the icon for the Desktop GUI and taskbar icon.
guiIconSize="128"
inkscape -z -e ${destinationDir}desktop-app/gui-icon.png -w $guiIconSize -h $guiIconSize $mainFile

# generate the desktop icon, or the icon of the executable when installed.
desktopIconSize="500"
inkscape -z -e ${destinationDir}desktop-app/desktop-icon.icn -w $desktopIconSize -h $desktopIconSize $mainFile

# copy main into server's web directory
cp $mainFile ../WebServer/Server/src/main/resources/META-INF/resources/res/media/logo.svg
