#!/usr/bin/env bash
# This script automatically generates the logos needed for the project from logo-main.png
# Must have ImageMagick installed
# https://imagemagick.org/script/convert.php

mainFile="logo-main.png";
destinationDir="generated/";

iconDefaultSize="500x500"

convert $mainFile -resize $iconDefaultSize ${destinationDir}gui-icon.png
convert $mainFile -resize $iconDefaultSize ${destinationDir}desktop-icon.icn