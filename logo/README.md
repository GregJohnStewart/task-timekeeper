# Task Timekeeper Logo Management

In general, the file `logo-main.png` is the main logo that will be used to make the rest of the logo files.

Files in the `generated` folder that are directly referenced by parts of the project and should not be renamed, moved or deleted:

 * `desktop-icon.icns` - Used in DesktopApp for the icon used when installed on an OS
 * `gui-icon.png` - Used in DesktopApp for the icon used in GUI mode

## Generating needed filed from `logo-main.png`

To generate the files actually used as logos, you simply need to run the following file:

#### `generate-logos.sh`

Requisites:

 * Have imagemagick installed
 * This script is for running on Linux systems