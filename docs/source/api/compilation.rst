Controller Compilation
======================

Overview
--------

The API version of the controller is divided into several Java files and packages.

Because of this, compiling only the main Java file is not enough. The controller must compile:

* the main controller file;
* the API core classes;
* the behavior classes;
* the motor classes;
* the sensor classes;
* the actuator classes;
* the world management classes;
* the task classes;
* the state classes;
* the utility classes.

To make this easier, the project provides a Windows batch script named:

.. code-block:: text

   controller.bat

This script can clean, build, or rebuild the Java controller.

Expected location
-----------------

The file ``controller.bat`` must be placed at the root of the controller folder.

Expected structure:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       ├── FourWheelsCollisionAvoidanceAPI.java
       ├── controller.bat
       └── api/
           ├── actuators/
           ├── behavior/
           ├── core/
           ├── motors/
           ├── sensors/
           ├── state/
           ├── tasks/
           ├── utils/
           └── world/

The script must be launched from this folder:

.. code-block:: text

   controllers/FourWheelsCollisionAvoidanceAPI/

Purpose of the script
---------------------

The script provides three commands:

.. list-table::
   :header-rows: 1

   * - Command
     - Role
   * - ``controller.bat clean``
     - Deletes all generated ``.class`` files.
   * - ``controller.bat build``
     - Compiles the Java controller and all API classes.
   * - ``controller.bat rebuild``
     - Cleans the project, then builds it again.

This avoids manually writing a long ``javac`` command every time the controller must be compiled.

Complete controller.bat file
----------------------------

The compilation script is:

.. code-block:: bat

   @echo off
   setlocal

   REM ============================================================
   REM Build / clean script for a Webots Java controller
   REM This file must be placed at the root of the controller folder.
   REM Example:
   REM controllers/FourWheelsCollisionAvoidanceAPI/controller.bat
   REM ============================================================

   set WEBOTS_HOME=C:\Program Files\Webots
   set CONTROLLER_JAR=%WEBOTS_HOME%\lib\controller\java\Controller.jar

   if not exist "%CONTROLLER_JAR%" (
       echo ERROR: Controller.jar not found.
       echo Tested path:
       echo "%CONTROLLER_JAR%"
       echo.
       echo Check that Webots is installed in:
       echo "%WEBOTS_HOME%"
       pause
       exit /b 1
   )

   if "%1"=="clean" goto clean
   if "%1"=="build" goto build
   if "%1"=="rebuild" goto rebuild

   echo Usage:
   echo   controller.bat clean
   echo   controller.bat build
   echo   controller.bat rebuild
   echo.
   pause
   exit /b 0

   :clean
   echo Deleting .class files...
   del /S /Q *.class 2>nul
   echo Clean completed.
   exit /b 0

   :build
   echo Compiling Java controller...
   javac -encoding UTF-8 -classpath "%CONTROLLER_JAR%" -d . *.java api\core\*.java api\behavior\*.java api\motors\*.java api\sensors\*.java api\actuators\*.java api\world\*.java api\tasks\*.java api\state\*.java api\utils\*.java

   if errorlevel 1 (
       echo.
       echo ERROR: Compilation failed.
       pause
       exit /b 1
   )

   echo.
   echo Build completed successfully.
   exit /b 0

   :rebuild
   call "%~f0" clean
   call "%~f0" build
   exit /b %errorlevel%

How the script works
--------------------

Webots path
~~~~~~~~~~~

The script first defines the Webots installation path:

.. code-block:: bat

   set WEBOTS_HOME=C:\Program Files\Webots

Then it defines the path to the Java controller library:

.. code-block:: bat

   set CONTROLLER_JAR=%WEBOTS_HOME%\lib\controller\java\Controller.jar

The file ``Controller.jar`` is required to compile a Java controller using the Webots API.

If Webots is installed somewhere else, this line must be modified.

For example:

.. code-block:: bat

   set WEBOTS_HOME=D:\Webots

Controller.jar verification
~~~~~~~~~~~~~~~~~~~~~~~~~~~

The script checks whether ``Controller.jar`` exists:

.. code-block:: bat

   if not exist "%CONTROLLER_JAR%" (
       echo ERROR: Controller.jar not found.
       ...
       exit /b 1
   )

If the file is not found, the script stops and displays an error message.

This usually means that:

* Webots is not installed in the expected folder;
* the ``WEBOTS_HOME`` variable is wrong;
* the Webots installation is incomplete.

Available commands
~~~~~~~~~~~~~~~~~~

The script checks the first argument passed to it:

.. code-block:: bat

   if "%1"=="clean" goto clean
   if "%1"=="build" goto build
   if "%1"=="rebuild" goto rebuild

This means that the user must call the script with one of these commands:

.. code-block:: powershell

   .\controller.bat clean
   .\controller.bat build
   .\controller.bat rebuild

If no command is provided, the script displays the usage instructions.

Clean command
-------------

The clean command removes all generated ``.class`` files.

Command:

.. code-block:: powershell

   .\controller.bat clean

Executed section:

.. code-block:: bat

   :clean
   echo Deleting .class files...
   del /S /Q *.class 2>nul
   echo Clean completed.
   exit /b 0

The option ``/S`` deletes ``.class`` files in subfolders.

The option ``/Q`` runs the deletion quietly.

This is useful when you want to remove old compiled files before rebuilding the controller.

Build command
-------------

The build command compiles the Java controller.

Command:

.. code-block:: powershell

   .\controller.bat build

Executed section:

.. code-block:: bat

   :build
   echo Compiling Java controller...
   javac -encoding UTF-8 -classpath "%CONTROLLER_JAR%" -d . *.java api\core\*.java api\behavior\*.java api\motors\*.java api\sensors\*.java api\actuators\*.java api\world\*.java api\tasks\*.java api\state\*.java api\utils\*.java

The ``javac`` command compiles:

.. code-block:: text

   *.java
   api/core/*.java
   api/behavior/*.java
   api/motors/*.java
   api/sensors/*.java
   api/actuators/*.java
   api/world/*.java
   api/tasks/*.java
   api/state/*.java
   api/utils/*.java

The option ``-encoding UTF-8`` ensures that Java files are read using UTF-8 encoding.

The option ``-classpath`` adds the Webots Java library.

The option ``-d .`` tells Java to place the compiled ``.class`` files in the current directory while respecting the package structure.

Rebuild command
---------------

The rebuild command cleans and builds the controller again.

Command:

.. code-block:: powershell

   .\controller.bat rebuild

Executed section:

.. code-block:: bat

   :rebuild
   call "%~f0" clean
   call "%~f0" build
   exit /b %errorlevel%

This command is useful when you want to make sure that no old compiled files remain.

In most cases, this is the safest command to use before launching Webots.

Recommended command
-------------------

During development, the recommended command is:

.. code-block:: powershell

   .\controller.bat rebuild

This ensures that the controller is fully cleaned and compiled again.

Running the script
------------------

Open a terminal in the controller folder:

.. code-block:: powershell

   cd controllers\FourWheelsCollisionAvoidanceAPI

Then run:

.. code-block:: powershell

   .\controller.bat rebuild

If the compilation succeeds, the terminal should display:

.. code-block:: text

   Build completed successfully.

After that, the Webots simulation can be launched.

Controller name in Webots
-------------------------

The controller name used in Webots must match the controller folder and the main Java class.

Expected names:

.. code-block:: text

   Folder:
   controllers/FourWheelsCollisionAvoidanceAPI/

   Main file:
   FourWheelsCollisionAvoidanceAPI.java

   Main class:
   public class FourWheelsCollisionAvoidanceAPI

   Webots controller field:
   controller "FourWheelsCollisionAvoidanceAPI"

In the robot PROTO file, the controller field must be:

.. code-block:: text

   controller "FourWheelsCollisionAvoidanceAPI"

If this name is wrong, Webots may compile the files correctly but fail to launch the controller.

Common errors
-------------

Controller.jar not found
~~~~~~~~~~~~~~~~~~~~~~~~

Example error:

.. code-block:: text

   ERROR: Controller.jar not found.

This means the script cannot find the Webots Java library.

Check this line:

.. code-block:: bat

   set WEBOTS_HOME=C:\Program Files\Webots

If Webots is installed somewhere else, update the path.

Compilation failed
~~~~~~~~~~~~~~~~~~

Example error:

.. code-block:: text

   ERROR: Compilation failed.

This means that one or more Java files contain an error.

Common causes:

* missing semicolon;
* wrong package name;
* wrong import;
* class name different from file name;
* missing Java file in one of the API packages.

Package does not exist
~~~~~~~~~~~~~~~~~~~~~~

If Java displays an error such as:

.. code-block:: text

   package api.motors does not exist

Check that:

* the file exists in the correct folder;
* the package declaration matches the folder path;
* the file is included in the ``javac`` command.

Example:

.. code-block:: java

   package api.motors;

The file must be located in:

.. code-block:: text

   api/motors/

Main class not found
~~~~~~~~~~~~~~~~~~~~

If Webots cannot launch the controller, check that:

* the controller field is correct in the robot PROTO;
* the controller folder has the correct name;
* the main Java file has the correct name;
* the public class has the correct name.

Expected main class:

.. code-block:: java

   public class FourWheelsCollisionAvoidanceAPI

Expected file:

.. code-block:: text

   FourWheelsCollisionAvoidanceAPI.java

Old compiled files
~~~~~~~~~~~~~~~~~~

Sometimes old ``.class`` files can cause confusing behavior.

In that case, run:

.. code-block:: powershell

   .\controller.bat rebuild

This deletes all old class files and recompiles the project.

Important notes
---------------

The script currently compiles a fixed list of packages:

.. code-block:: text

   api\core
   api\behavior
   api\motors
   api\sensors
   api\actuators
   api\world
   api\tasks
   api\state
   api\utils

If a new package is added later, it must also be added to the ``javac`` command.

For example, if a new package ``api/navigation`` is created, the build command must include:

.. code-block:: bat

   api\navigation\*.java

Summary
-------

The ``controller.bat`` script is used to manage the compilation of the API controller.

The most useful commands are:

.. code-block:: powershell

   .\controller.bat clean
   .\controller.bat build
   .\controller.bat rebuild

The recommended command before launching Webots is:

.. code-block:: powershell

   .\controller.bat rebuild

This ensures that the controller is clean, fully compiled, and ready to run in Webots.