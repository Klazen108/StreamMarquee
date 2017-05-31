StreamMarquee
=============
* By **Klazen108**
* Written 5/28/2017
* Version 0.5.0

Displays messages on a nice, easily-capturable window for OBS/XSplit/etc.

[Project Repository, on GitHub](https://github.com/Klazen108/StreamMarquee)

Getting Started
---------------

###Quick Start###
To run the program, check out [Running the Project](#running-the-project). This is all you have to do to get started.

For a list of configurable settings, see [Properties](#properties). You don't need to modify this file, and if it's not present, a default one is generated.

For a list of hotkeys you can use to control the program while it runs, see [Hotkeys](#hotkeys).

### Dependencies ###
This program is written in Java, so obviously, you'll need to [install Java](https://www.java.com/en/download/). It was written with JavaSE 1.8 in mind, so you will need a compatible version, which should be any distribution of Java 1.8 (aka Java 8) or higher. If you've downloaded the precompiled binary, then you only need the [Java Runtime Environment (JRE)](https://www.java.com/en/download/whatis_java.jsp). If you want to build the project yourself, you will need a [Java Development Kit (JDK)](https://www.java.com/en/download/faq/develop.xml).

Speaking of building the project yourself, this project is managed using the [Apache Maven](https://maven.apache.org/) project management tool. The easiest way to build this project will be through Maven, so you should have that installed as well.

### Building the Project ###
If you downloaded the compiled binary, you can skip this section and go to the section titled [Running the Project](#running-the-project).

**StreamMarquee** is managed using the Apache Maven project management tool. You will need to install Maven to build the project. Alternatively, you can manage the dependencies and build the project yourself, but it is assumed that you have enough knowledge to figure everything out if so.

To build the project, open a console/terminal and navigate your current working directory to the directory containing the pom.xml file (the root directory of the project). Here, execute the command `mvn install`, and when the build has completed, there should be a **StreamMarquee-[version].jar** file output in the root directory of the project.

### Running the Project ###
To run the program, open a console/terminal and navigate your current working directory to the directory containing the jar. Then, execute the following command, where `[version]`  is replaced by the actual version number on the jar:

    java -jar StreamMarquee-[version].jar

You could also double-click the jar to run it if your operating system has been set up to do so (which is usually the case if you've installed Java). However, if you do so, no console window will be displayed, and you will miss out on any debug information output while the program runs. However you want to do it is up to you, it will work either way!

Text Inputs
-----------
Text is loaded from the associated file, `text.txt`. Messages are placed one to a line. You can edit this file in real time and the lines will be loaded as they are reached. You could even write your own script to modify this file to have the program display dynamic messages!

Properties
----------
The properties file, `sm.properties`, defines some common properties for the program. If any properties are changed during runtime, these values are immediately saved and will be kept after the program closes. If you change properties in the file during runtime and want to reload them, press `ctrl+R`. See the [hotkeys](#hotkeys) section for more information.

The properties file has the following fields:

| key              | description |
|------------------|-------------|
| **fontSize**     | size of the font to use |
| **scrollSpeed**  | scroll speed, 30 is maximum |
| **marqueeDelay** |a delay, in milliseconds, to wait before the next message is shown after the current message finishes |
| **width**        | width of the window |
| **height**       | height of the window, including the title bar |
| **curLine**      | the current line to be displayed, in sequential mode (or the last line displayed in random mode) |
| **bgcolor**      | background color, in 24bit, decimal format (0=black, 16777215=white) |
| **fgcolor**      | foreground color, in 24bit, decimal format (0=black, 16777215=white) |
| **font**         | name of the font to use |
| **mode**         | `RANDOM` or `SEQUENTIAL` - If random, then lines are chosen at random from the file. If sequential, then lines are chosen one after the other. |

Hotkeys
-------

| Key           | Description |
|---------------|-------------|
| **Space**     | Skips to the next message |
| **Ctrl+R**    | Reloads all properties from the file (except the window size) |
| **Ctrl+Up**   | Increase font size by one |
| **Ctrl+Down** | Decrease font size by one |

Footnote
--------

Based on the Winamp Stream Display project, also by me