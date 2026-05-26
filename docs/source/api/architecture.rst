API Architecture
================

Overview
--------

The API controller is divided into several packages. Each package has a specific role in the robot control system.

The goal of this architecture is to avoid having all the code inside one large controller file.

Instead, the robot is controlled through reusable classes that manage specific parts of the robot, such as motors, sensors, actuators, behaviors, tasks, and world objects.

General structure
-----------------

The API is organized as follows:

.. code-block:: text

   api/
   ├── actuators/
   ├── behavior/
   ├── core/
   ├── motors/
   ├── sensors/
   ├── state/
   ├── tasks/
   ├── utils/
   └── world/

Each package contains classes related to one responsibility.

Global architecture
-------------------

The main controller file is:

.. code-block:: text

   FourWheelsCollisionAvoidanceAPI.java

This file is the entry point of the controller.

It creates the main robot object and starts the selected behavior.

The global architecture can be represented as follows:

.. code-block:: text

   FourWheelsCollisionAvoidanceAPI
                |
                v
              TERBot
                |
      ┌─────────┼─────────┐
      v         v         v
   Motors    Sensors   Actuators
      |
      v
   Behaviors
      |
      v
   Tasks / World / Utils

Main controller
---------------

The main controller is responsible for launching the program.

Its role is not to contain all the robot logic.

It should mainly:

* create the robot API;
* initialize the robot components;
* create the behavior to execute;
* run the main simulation loop.

Example structure:

.. code-block:: java

   public class FourWheelsCollisionAvoidanceAPI {
     public static void main(String[] args) {
       TERBot bot = new TERBot();

       RobotBehavior behavior = new CollectPucksBehavior(bot);

       while (bot.step()) {
         behavior.update();
       }
     }
   }

This structure keeps the main file short and readable.

Core package
------------

The ``api.core`` package contains the main robot abstraction.

Main class:

.. code-block:: text

   TERBot.java

The ``TERBot`` class gives access to the main parts of the robot:

* the drive base;
* the sensors;
* the arm;
* the gripper;
* the simulation step;
* the Webots robot or supervisor object.

Example:

.. code-block:: java

   bot.driveBase().forward(2.0);
   bot.sensors().front().getValue();
   bot.arm().up();
   bot.gripper().close();

Motors package
--------------

The ``api.motors`` package contains the classes used to control the wheels.

Main classes:

.. code-block:: text

   DriveBase.java
   MotorGroup.java
   Wheel.java

This package allows the robot to perform simple movements:

* move forward;
* move backward;
* turn left;
* turn right;
* stop;
* set different speeds for the left and right sides.

Example:

.. code-block:: java

   bot.driveBase().forward(2.0);
   bot.driveBase().turnLeft(1.5);
   bot.driveBase().stop();

Sensors package
---------------

The ``api.sensors`` package contains wrapper classes around Webots sensors.

Main classes:

.. code-block:: text

   SensorManager.java
   DistanceSensorWrapper.java
   TouchSensorWrapper.java
   ColorSensorWrapper.java
   RGBColor.java

This package provides simplified access to:

* front distance sensor;
* left distance sensor;
* right distance sensor;
* front touch sensor;
* color sensor.

Example:

.. code-block:: java

   if (bot.sensors().front().isObjectDetected(350.0)) {
     bot.driveBase().stop();
   }

Actuators package
-----------------

The ``api.actuators`` package controls the mechanical parts of the robot.

Main classes:

.. code-block:: text

   Arm.java
   Gripper.java

The arm is used to move the gripper up and down.

The gripper is used to grab or release a puck.

Example:

.. code-block:: java

   bot.arm().down();
   bot.gripper().close();
   bot.arm().up();

Behavior package
----------------

The ``api.behavior`` package contains high-level robot behaviors.

Main classes:

.. code-block:: text

   RobotBehavior.java
   SimpleAvoidObstacleBehavior.java
   CollectPucksBehavior.java

A behavior describes what the robot does during the simulation.

For example:

* avoiding obstacles;
* searching for pucks;
* collecting pucks;
* going to the drop zone;
* dropping a puck.

Example:

.. code-block:: java

   RobotBehavior behavior = new CollectPucksBehavior(bot);

   while (bot.step()) {
     behavior.update();
   }

State package
-------------

The ``api.state`` package contains the possible robot modes.

Main class:

.. code-block:: text

   RobotMode.java

The robot mode is used to know what the robot is currently doing.

Example modes:

.. code-block:: text

   SEARCH
   APPROACH_PUCK
   LOWER_ARM
   CLOSE_GRIPPER
   LIFT_ARM
   GO_TO_DROP_ZONE
   DROP_PUCK
   TOUCH_AVOID

Using states makes the behavior easier to understand and debug.

Tasks package
-------------

The ``api.tasks`` package manages actions that last several simulation steps.

Main classes:

.. code-block:: text

   RobotTask.java
   TaskScheduler.java
   TimedTask.java

This is useful for actions such as:

* lowering the arm;
* closing the gripper;
* raising the arm;
* moving backward for a short time;
* turning for a fixed duration.

Instead of writing many counters directly in the behavior, tasks can be used to organize timed actions.

World package
-------------

The ``api.world`` package manages objects in the simulation world.

Main class:

.. code-block:: text

   PuckManager.java

This package is mainly used to manage pucks.

It can be responsible for:

* finding pucks in the Webots world;
* finding the nearest available puck;
* checking if a puck has already been delivered;
* attaching a puck to the robot;
* dropping a puck in the drop zone.

Utils package
-------------

The ``api.utils`` package contains utility functions.

Main class:

.. code-block:: text

   MathUtils.java

This package can contain functions such as:

* angle normalization;
* distance computation;
* value clamping.

These functions are used by several parts of the API.

Communication between packages
------------------------------

The packages communicate through the ``TERBot`` class.

The behavior does not directly manipulate Webots motors and sensors.

Instead, it uses the API:

.. code-block:: java

   bot.driveBase().forward(2.0);
   bot.sensors().touchFront().isTouched();
   bot.gripper().open();

This makes the code easier to read and avoids repeating Webots-specific code everywhere.

Advantages of this architecture
-------------------------------

This architecture provides several advantages:

* the code is easier to read;
* each class has a clear responsibility;
* the main controller is shorter;
* the behavior is easier to understand;
* the project is easier to debug;
* students can work on one package at a time;
* new behaviors can be added more easily.

Summary
-------

The API architecture separates the robot into several logical parts.

The main controller starts the program.

``TERBot`` gives access to the robot components.

The packages ``motors``, ``sensors``, ``actuators``, ``behavior``, ``tasks``, ``world``, ``utils`` and ``state`` each handle a specific responsibility.

This makes the project more modular, easier to teach, and easier to extend.