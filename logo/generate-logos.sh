#!/usr/bin/env bash
# This script automatically generates the logos needed for the project from logo-main.png
# Must have ImageMagick installed
# https://imagemagick.org/script/convert.php

mainFile="logo-main.png";
destinationDir="generated/";

convert $mainFile -resize 250x250 ${destinationDir}gui-icon.png
convert $mainFile -resize 250x250 ${destinationDir}desktop-icon.icn