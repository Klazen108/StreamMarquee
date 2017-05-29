# StreamMarquee
* By Klazen108
* Written 5/28/2017

Displays messages on a nice, easily-capturable window for OBS/XSplit/etc.

##Text Inputs
Text is loaded from the associated file, `text.txt`. Messages are placed one to a line.

##Properties
The properties file, `sm.properties`, defines some common properties for the program. If any properties are changed during runtime, these values are immediately saved. If you change properties in the file during runtime and want to reload them, press `ctrl+R`. See the hotkeys section for more information.

The properties file has the following fields:
* **height**       height of the window, including the title bar
* **width**        width of the window
* **bgcolor**      background color, in 24bit, decimal format (0=black, 16777215=white)
* **fgcolor**      foreground color, in 24bit, decimal format (0=black, 16777215=white)
* **font**         name of the font to use
* **fontSize**     size of the font to use
* **scrollSpeed**  scroll speed, 30 is maximum
* **marqueeDelay** a delay, in milliseconds, to wait before the next message is shown after the current message finishes
* **mode**         `RANDOM` or `SEQUENTIAL`

##Hotkeys
* **Space**        Skips to the next message
* **Ctrl+R**       Reloads all properties from the file (except the window size)
* **Ctrl+Up**      Increase font size by one
* **Ctrl+Down**    Decrease font size by one

Based on the Winamp Stream Display project, also by me