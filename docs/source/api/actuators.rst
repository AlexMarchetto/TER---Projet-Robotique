Actuators API
=============

Overview
--------

The ``api.actuators`` package contains the classes used to control the mechanical parts of the robot.

In this project, the robot has two main actuators:

* an arm;
* a gripper.

The arm is used to raise or lower the gripper.

The gripper is used to grab or release a puck.

This package makes it possible to write simple commands such as:

.. code-block:: java

   bot.arm().lower();
   bot.gripper().close();
   bot.arm().lift();

Instead of directly manipulating Webots motors in the behavior code.

Package location
----------------

The actuators API is located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── actuators/
               ├── Arm.java
               └── Gripper.java

Package declaration
-------------------

Each file in this package starts with:

.. code-block:: java

   package api.actuators;

This means that the files must be located in:

.. code-block:: text

   api/actuators/

If the package declaration and the folder path do not match, Java will not compile the project correctly.

Main classes
------------

The package contains two main classes:

.. list-table::
   :header-rows: 1

   * - Class
     - Role
   * - ``Arm``
     - Controls the vertical movement of the robot arm.
   * - ``Gripper``
     - Controls the opening and closing of the robot gripper.

General organization
--------------------

The actuator system is organized like this:

.. code-block:: text

   TERBot
      |
      ├── Arm
      │    ├── Webots Motor "arm_motor"
      │    └── Webots PositionSensor "arm_sensor"
      |
      └── Gripper
           ├── Webots Motor "gripper_left_motor"
           └── Webots Motor "gripper_right_motor"

The ``TERBot`` class creates one ``Arm`` object and one ``Gripper`` object.

These objects can then be used by the robot behaviors.

Example:

.. code-block:: java

   bot.arm().lower();
   bot.gripper().close();
   bot.arm().lift();

Arm
---

Overview
~~~~~~~~

The ``Arm`` class controls the robot arm.

The arm is used to move the gripper up or down.

It is mainly used during puck collection:

1. the robot reaches a puck;
2. the arm lowers the gripper;
3. the gripper closes;
4. the arm lifts the puck.

Class code
~~~~~~~~~~

.. code-block:: java

   package api.actuators;

   import com.cyberbotics.webots.controller.Motor;
   import com.cyberbotics.webots.controller.PositionSensor;
   import com.cyberbotics.webots.controller.Supervisor;

   public class Arm {
     private final Motor armMotor;
     private final PositionSensor armSensor;
     private double upPosition = -0.65;
     private double downPosition = 0.35;

     public Arm(Supervisor robot, int timeStep) {
       this.armMotor = robot.getMotor("arm_motor");
       this.armSensor = robot.getPositionSensor("arm_sensor");
       if (armSensor != null) { armSensor.enable(timeStep); }
     }

     public void lift() { moveTo(upPosition); }
     public void lower() { moveTo(downPosition); }
     public void moveTo(double position) { if (armMotor != null) { armMotor.setPosition(position); } }
     public double getPosition() { return armSensor == null ? 0.0 : armSensor.getValue(); }
     public void setUpPosition(double upPosition) { this.upPosition = upPosition; }
     public void setDownPosition(double downPosition) { this.downPosition = downPosition; }
     public boolean exists() { return armMotor != null; }
   }

Attributes
~~~~~~~~~~

The ``Arm`` class contains four attributes:

.. code-block:: java

   private final Motor armMotor;
   private final PositionSensor armSensor;
   private double upPosition = -0.65;
   private double downPosition = 0.35;

``armMotor``
^^^^^^^^^^^^

The ``armMotor`` attribute stores the Webots motor used to move the arm.

It is retrieved with:

.. code-block:: java

   robot.getMotor("arm_motor");

This means that the robot PROTO must contain a motor named:

.. code-block:: text

   arm_motor

``armSensor``
^^^^^^^^^^^^^

The ``armSensor`` attribute stores the Webots position sensor associated with the arm.

It is retrieved with:

.. code-block:: java

   robot.getPositionSensor("arm_sensor");

This sensor can be used to read the current position of the arm.

The robot PROTO must contain a position sensor named:

.. code-block:: text

   arm_sensor

``upPosition``
^^^^^^^^^^^^^^

.. code-block:: java

   private double upPosition = -0.65;

This value represents the position of the arm when it is lifted.

By default, the lifted position is:

.. code-block:: text

   -0.65

This value can be adjusted if the arm is not high enough or if it moves in the wrong direction.

``downPosition``
^^^^^^^^^^^^^^^^

.. code-block:: java

   private double downPosition = 0.35;

This value represents the position of the arm when it is lowered.

By default, the lowered position is:

.. code-block:: text

   0.35

This value can be adjusted if the gripper does not reach the puck correctly.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public Arm(Supervisor robot, int timeStep) {
     this.armMotor = robot.getMotor("arm_motor");
     this.armSensor = robot.getPositionSensor("arm_sensor");
     if (armSensor != null) { armSensor.enable(timeStep); }
   }

The constructor receives:

* the Webots ``Supervisor``;
* the simulation ``timeStep``.

It retrieves:

* the arm motor;
* the arm position sensor.

Then it enables the position sensor if it exists.

The condition:

.. code-block:: java

   if (armSensor != null)

prevents the program from crashing if the sensor is missing from the robot model.

lift
~~~~

.. code-block:: java

   public void lift() {
     moveTo(upPosition);
   }

This method raises the arm.

It sends the arm to the predefined ``upPosition``.

Example:

.. code-block:: java

   bot.arm().lift();

This is usually called:

* at the beginning of the simulation;
* after grabbing a puck;
* after dropping a puck.

lower
~~~~~

.. code-block:: java

   public void lower() {
     moveTo(downPosition);
   }

This method lowers the arm.

It sends the arm to the predefined ``downPosition``.

Example:

.. code-block:: java

   bot.arm().lower();

This is usually called before closing the gripper on a puck.

moveTo
~~~~~~

.. code-block:: java

   public void moveTo(double position) {
     if (armMotor != null) {
       armMotor.setPosition(position);
     }
   }

This method moves the arm to a custom position.

It directly sets the target position of the Webots motor.

Example:

.. code-block:: java

   bot.arm().moveTo(0.1);

The ``if (armMotor != null)`` check prevents a crash if the arm motor was not found.

getPosition
~~~~~~~~~~~

.. code-block:: java

   public double getPosition() {
     return armSensor == null ? 0.0 : armSensor.getValue();
   }

This method returns the current arm position.

If the position sensor does not exist, it returns ``0.0``.

Example:

.. code-block:: java

   double position = bot.arm().getPosition();
   System.out.println("Arm position: " + position);

This can be useful for debugging or for checking whether the arm has reached a target position.

setUpPosition
~~~~~~~~~~~~~

.. code-block:: java

   public void setUpPosition(double upPosition) {
     this.upPosition = upPosition;
   }

This method changes the lifted position of the arm.

Example:

.. code-block:: java

   bot.arm().setUpPosition(-0.8);

This can be useful if the robot model changes or if the default position is not adapted.

setDownPosition
~~~~~~~~~~~~~~~

.. code-block:: java

   public void setDownPosition(double downPosition) {
     this.downPosition = downPosition;
   }

This method changes the lowered position of the arm.

Example:

.. code-block:: java

   bot.arm().setDownPosition(0.45);

This can be useful if the gripper does not go low enough to reach a puck.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return armMotor != null;
   }

This method checks whether the arm motor was correctly found.

Example:

.. code-block:: java

   if (!bot.arm().exists()) {
     System.out.println("Arm motor not found");
   }

This is useful for debugging the robot PROTO.

Gripper
-------

Overview
~~~~~~~~

The ``Gripper`` class controls the robot gripper.

The gripper is composed of two motors:

* one motor for the left part;
* one motor for the right part.

The gripper can be opened or closed.

It is used to grab and release pucks.

Class code
~~~~~~~~~~

.. code-block:: java

   package api.actuators;

   import com.cyberbotics.webots.controller.Motor;
   import com.cyberbotics.webots.controller.Supervisor;

   public class Gripper {
     private final Motor leftMotor;
     private final Motor rightMotor;
     private double openLeftPosition = 0.2;
     private double openRightPosition = -0.2;
     private double closedLeftPosition = -0.55;
     private double closedRightPosition = 0.55;
     private boolean open;

     public Gripper(Supervisor robot) {
       this.leftMotor = robot.getMotor("gripper_left_motor");
       this.rightMotor = robot.getMotor("gripper_right_motor");
       this.open = false;
     }

     public void open() {
       if (leftMotor != null) { leftMotor.setPosition(openLeftPosition); }
       if (rightMotor != null) { rightMotor.setPosition(openRightPosition); }
       open = true;
     }

     public void close() {
       if (leftMotor != null) { leftMotor.setPosition(closedLeftPosition); }
       if (rightMotor != null) { rightMotor.setPosition(closedRightPosition); }
       open = false;
     }

     public boolean isOpen() { return open; }
     public void setOpenPositions(double left, double right) { this.openLeftPosition = left; this.openRightPosition = right; }
     public void setClosedPositions(double left, double right) { this.closedLeftPosition = left; this.closedRightPosition = right; }
     public boolean exists() { return leftMotor != null || rightMotor != null; }
   }

Attributes
~~~~~~~~~~

The ``Gripper`` class contains several attributes:

.. code-block:: java

   private final Motor leftMotor;
   private final Motor rightMotor;
   private double openLeftPosition = 0.2;
   private double openRightPosition = -0.2;
   private double closedLeftPosition = -0.55;
   private double closedRightPosition = 0.55;
   private boolean open;

``leftMotor``
^^^^^^^^^^^^^

The ``leftMotor`` attribute stores the Webots motor controlling the left side of the gripper.

It is retrieved with:

.. code-block:: java

   robot.getMotor("gripper_left_motor");

The robot PROTO must contain a motor named:

.. code-block:: text

   gripper_left_motor

``rightMotor``
^^^^^^^^^^^^^^

The ``rightMotor`` attribute stores the Webots motor controlling the right side of the gripper.

It is retrieved with:

.. code-block:: java

   robot.getMotor("gripper_right_motor");

The robot PROTO must contain a motor named:

.. code-block:: text

   gripper_right_motor

Open positions
^^^^^^^^^^^^^^

.. code-block:: java

   private double openLeftPosition = 0.2;
   private double openRightPosition = -0.2;

These values represent the motor positions used to open the gripper.

By default:

.. code-block:: text

   left gripper open position  =  0.2
   right gripper open position = -0.2

Closed positions
^^^^^^^^^^^^^^^^

.. code-block:: java

   private double closedLeftPosition = -0.55;
   private double closedRightPosition = 0.55;

These values represent the motor positions used to close the gripper.

By default:

.. code-block:: text

   left gripper closed position  = -0.55
   right gripper closed position =  0.55

The signs are opposite because the two sides of the gripper rotate in opposite directions.

``open``
^^^^^^^^

.. code-block:: java

   private boolean open;

This boolean stores the current logical state of the gripper.

If ``open`` is ``true``, the API considers the gripper open.

If ``open`` is ``false``, the API considers the gripper closed.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public Gripper(Supervisor robot) {
     this.leftMotor = robot.getMotor("gripper_left_motor");
     this.rightMotor = robot.getMotor("gripper_right_motor");
     this.open = false;
   }

The constructor retrieves both gripper motors from Webots.

At the beginning, the internal state is set to:

.. code-block:: java

   this.open = false;

However, in the ``TERBot`` constructor, the gripper is opened immediately with:

.. code-block:: java

   gripper.open();

So the robot starts the simulation with the gripper open.

open
~~~~

.. code-block:: java

   public void open() {
     if (leftMotor != null) { leftMotor.setPosition(openLeftPosition); }
     if (rightMotor != null) { rightMotor.setPosition(openRightPosition); }
     open = true;
   }

This method opens the gripper.

It sends the left and right motors to their open positions.

Example:

.. code-block:: java

   bot.gripper().open();

This is usually called:

* at the beginning of the simulation;
* before trying to grab a puck;
* when dropping a puck.

close
~~~~~

.. code-block:: java

   public void close() {
     if (leftMotor != null) { leftMotor.setPosition(closedLeftPosition); }
     if (rightMotor != null) { rightMotor.setPosition(closedRightPosition); }
     open = false;
   }

This method closes the gripper.

It sends the left and right motors to their closed positions.

Example:

.. code-block:: java

   bot.gripper().close();

This is usually called when the robot is positioned around a puck.

isOpen
~~~~~~

.. code-block:: java

   public boolean isOpen() {
     return open;
   }

This method returns the logical state of the gripper.

Example:

.. code-block:: java

   if (bot.gripper().isOpen()) {
     System.out.println("The gripper is open");
   }

Important note: this method does not measure the real physical position of the motors.  
It only returns the state stored by the API after calling ``open`` or ``close``.

setOpenPositions
~~~~~~~~~~~~~~~~

.. code-block:: java

   public void setOpenPositions(double left, double right) {
     this.openLeftPosition = left;
     this.openRightPosition = right;
   }

This method changes the motor positions used when opening the gripper.

Example:

.. code-block:: java

   bot.gripper().setOpenPositions(0.3, -0.3);

This is useful if the gripper does not open enough or opens too much.

setClosedPositions
~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public void setClosedPositions(double left, double right) {
     this.closedLeftPosition = left;
     this.closedRightPosition = right;
   }

This method changes the motor positions used when closing the gripper.

Example:

.. code-block:: java

   bot.gripper().setClosedPositions(-0.6, 0.6);

This is useful if the gripper does not close enough around the puck.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return leftMotor != null || rightMotor != null;
   }

This method checks whether at least one of the two gripper motors exists.

It returns ``true`` if the left motor or the right motor was found.

Example:

.. code-block:: java

   if (!bot.gripper().exists()) {
     System.out.println("No gripper motor found");
   }

Naming convention
-----------------

The actuator API depends on the names of the motors and sensors in the robot PROTO.

The robot must contain:

.. list-table::
   :header-rows: 1

   * - Webots device name
     - Used by
     - Role
   * - ``arm_motor``
     - ``Arm``
     - Moves the arm up and down.
   * - ``arm_sensor``
     - ``Arm``
     - Reads the arm position.
   * - ``gripper_left_motor``
     - ``Gripper``
     - Moves the left part of the gripper.
   * - ``gripper_right_motor``
     - ``Gripper``
     - Moves the right part of the gripper.

If these names are changed in the robot PROTO, the Java API must be changed too.

Example of correct motor names:

.. code-block:: text

   RotationalMotor {
     name "arm_motor"
   }

   PositionSensor {
     name "arm_sensor"
   }

   RotationalMotor {
     name "gripper_left_motor"
   }

   RotationalMotor {
     name "gripper_right_motor"
   }

Typical pickup sequence
-----------------------

A simple pickup sequence can be written like this:

.. code-block:: java

   bot.motors().stop();
   bot.arm().lower();
   bot.gripper().close();
   bot.arm().lift();

However, in a real behavior, these actions should not all be executed instantly.

The arm and gripper need time to move.

For this reason, the final behavior usually uses timed tasks.

Example logical sequence:

.. code-block:: text

   1. Stop the robot.
   2. Lower the arm.
   3. Wait a few simulation steps.
   4. Close the gripper.
   5. Wait a few simulation steps.
   6. Lift the arm.
   7. Continue the mission.

This is why the ``api.tasks`` package is useful.

Typical drop sequence
---------------------

A simple drop sequence can be written like this:

.. code-block:: java

   bot.motors().stop();
   bot.arm().lower();
   bot.gripper().open();
   bot.arm().lift();

This sequence releases the puck and prepares the robot to continue searching.

Debugging
---------

If the arm does not move, check:

* the robot PROTO contains a motor named ``arm_motor``;
* the arm motor is inside a ``HingeJoint`` device field;
* the controller has been recompiled;
* ``bot.arm().exists()`` returns ``true``;
* the target positions are valid for the motor limits.

Example:

.. code-block:: java

   if (!bot.arm().exists()) {
     System.out.println("Arm motor not found");
   }

If the gripper does not move, check:

* the robot PROTO contains ``gripper_left_motor``;
* the robot PROTO contains ``gripper_right_motor``;
* both motors are inside ``HingeJoint`` device fields;
* the controller has been recompiled;
* ``bot.gripper().exists()`` returns ``true``;
* the open and closed positions are compatible with the motor limits.

If the arm moves in the wrong direction, try changing:

.. code-block:: java

   bot.arm().setUpPosition(...);
   bot.arm().setDownPosition(...);

If the gripper opens when it should close, try changing:

.. code-block:: java

   bot.gripper().setOpenPositions(..., ...);
   bot.gripper().setClosedPositions(..., ...);

Summary
-------

The ``api.actuators`` package provides a simple API for the robot mechanical parts.

``Arm`` controls the arm using the Webots motor ``arm_motor`` and the position sensor ``arm_sensor``.

``Gripper`` controls the two gripper motors:

* ``gripper_left_motor``;
* ``gripper_right_motor``.

The rest of the controller can use simple commands such as:

.. code-block:: java

   bot.arm().lower();
   bot.gripper().close();
   bot.arm().lift();

This makes the robot behavior easier to read and easier to understand.