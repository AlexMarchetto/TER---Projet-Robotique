Presentation
============

Overview
--------

The API version of the controller is designed to make the robot easier to program and understand.

Instead of writing all the Webot code in a single large controller file, the project is divided into several packages. Each package has a specific responsibility.

The goal of this API is to provide simple methods to control the robot.

For exemple, instead of directly controlling each Webots motor, the user can write:

.. code-block:: java

    bot.driveBase().forward(2.0);

This make the controller more readable, more reusable, and easier to use in pravtival sessions.

Project structure
-----------------

The controller is organized as follows:

.. code-block:: text

    controllers/
    └── FourWheelsCollisionAvoidanceAPI/
        ├── FourWheelsCollisionAvoidanceAPI.java
        ├── controller.bat
        └── api/
           ├── actuators/
           │   ├── Arm.java
           │   └── Gripper.java
           ├── behavior/
           │   ├── CollectPucksBehavior.java
           │   ├── RobotBehavior.java
           │   └── SimpleAvoidObstacleBehavior.java
           ├── core/
           │   └── TERBot.java
           ├── motors/
           │   ├── DriveBase.java
           │   ├── MotorGroup.java
           │   └── Wheel.java
           ├── sensors/
           │   ├── ColorSensorWrapper.java
           │   ├── DistanceSensorWrapper.java
           │   ├── RGBColor.java
           │   ├── SensorManager.java
           │   └── TouchSensorWrapper.java
           ├── state/
           │   └── RobotMode.java
           ├── tasks/
           │   ├── RobotTask.java
           │   ├── TaskScheduler.java
           │   └── TimedTask.java
           ├── utils/
           │   └── MathUtils.java
           └── world/
               └── PuckManager.java

Main controller
---------------

The main controller file is:

.. code-block:: text

    FourWheelsCollisionAvoidanceAPI.java

This file is the entry point of the controller. It creates the robot API, initializes the robot components, and starts the main behavior.

Main packages
-------------

The API is divided into several packages:

.. list-table::
    :header-rows: 1

    * - Package
      - Role
    * - ``api.actuators``
      - Controls the arm and the gripper.
    * - ``api.behavior``
      - Contains the high-level robot behaviors.
    * - ``api.core``
        - Contains the main robot class, ``TERBot``.
    * - ``api.motors``
        - Controls the wheels and the drive base.
    * - ``api.sensors``
        - Provides access to the distance sensors, touch sensor, and color sensor.
    * - ``api.state``
        - Defines the different robot modes.
    * - ``api.tasks``
        - Manages timed actions.
    * - ``api.utils``
        - Contains utility functions.
    * - ``api.world``
        - Manages the pucks and world objects.  

Why use an API ?
----------------

Using an API has several advantages:

* the code is easier to read;
* each class has a clear responsibility;
* the controlelr is easier to maintain;
* the project is easier to split into practical sessions;
* students can focus on the robot behavior instead of low-level Webots code;
* new behaviors can be added more easily.

Exemple without the API
-----------------------

Without the API, moving the robot requires direct access to the Webots motors:

.. code-block:: java

   Motor wheel1 = robot.getMotor("wheel1");
   wheel1.setPosition(Double.POSITIVE_INFINITY);
   wheel1.setVelocity(2.0);

Example with the API
--------------------

With the API, the same action can be written more simply:

.. code-block:: java

   bot.driveBase().forward(2.0);

This version is shorter and easier to understand.

Summary
-------

The API transforms the controller into a set of reusable components.

It makes the project cleaner, more modular, and more suitable for learning robotics step by step.