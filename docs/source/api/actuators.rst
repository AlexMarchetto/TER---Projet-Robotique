Actuators API
=============

Overview
--------

The ``api.actuators`` package contains the classes used to control the mechanical parts of the robot.

In this project, the robot has two main actuators:

* an arm;
* a gripper.

The arm is used to move the gripper up and down.

The gripper is used to open and close around a puck.

Together, these two parts allow the robot to pick up and drop pucks during the mission.

Why this package exists
-----------------------

Without this API, the behavior code would have to directly manipulate Webots motors every time the robot wants to move the arm or the gripper.

For example, without the API, the code would look like this:

.. code-block:: java

   Motor armMotor = robot.getMotor("arm_motor");
   armMotor.setPosition(0.35);

With the API, the same action becomes easier to understand:

.. code-block:: java

   robot.arm().lower();

This is the main goal of the actuators API: making the robot code easier to read.

Package location
----------------

The files are located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── actuators/
               ├── Arm.java
               └── Gripper.java

Package declaration
-------------------

Both files start with:

.. code-block:: java

   package api.actuators;

This means that the files must be placed in the folder:

.. code-block:: text

   api/actuators/

If the folder and package name do not match, Java will not compile the project.

General idea
------------

The actuator system can be represented like this:

.. code-block:: text

   TERBot
      |
      ├── Arm
      │    ├── arm_motor
      │    └── arm_sensor
      |
      └── Gripper
           ├── gripper_left_motor
           └── gripper_right_motor

The ``TERBot`` class creates the arm and the gripper when the robot starts.

Then, the behavior can use them with simple instructions:

.. code-block:: java

   robot.arm().lower();
   robot.gripper().close();
   robot.arm().lift();

Arm
---

Role of the Arm class
~~~~~~~~~~~~~~~~~~~~~

The ``Arm`` class controls the vertical movement of the robot arm.

The arm has two main positions:

.. list-table::
   :header-rows: 1

   * - Position
     - Meaning
     - Default value
   * - ``upPosition``
     - The arm is lifted.
     - ``-0.65``
   * - ``downPosition``
     - The arm is lowered.
     - ``0.35``

The lifted position is used when the robot moves.

The lowered position is used when the robot wants to grab or release a puck.

Arm source code
~~~~~~~~~~~~~~~

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

Important Webots devices
~~~~~~~~~~~~~~~~~~~~~~~~

The ``Arm`` class uses two Webots devices:

.. list-table::
   :header-rows: 1

   * - Webots device
     - Name in Webots
     - Role
   * - Motor
     - ``arm_motor``
     - Moves the arm.
   * - PositionSensor
     - ``arm_sensor``
     - Reads the arm position.

These names are very important.

If the motor is not named ``arm_motor`` in Webots, this line will not find it:

.. code-block:: java

   robot.getMotor("arm_motor");

If the sensor is not named ``arm_sensor``, this line will not find it:

.. code-block:: java

   robot.getPositionSensor("arm_sensor");

Constructor explanation
~~~~~~~~~~~~~~~~~~~~~~~

The constructor is called when the ``TERBot`` object is created.

.. code-block:: java

   public Arm(Supervisor robot, int timeStep) {
     this.armMotor = robot.getMotor("arm_motor");
     this.armSensor = robot.getPositionSensor("arm_sensor");
     if (armSensor != null) { armSensor.enable(timeStep); }
   }

It does three things:

1. It retrieves the arm motor from Webots.
2. It retrieves the arm position sensor from Webots.
3. It enables the position sensor.

In Webots, a sensor must be enabled before it can be used.

This is why the code contains:

.. code-block:: java

   armSensor.enable(timeStep);

The ``if`` condition avoids an error if the sensor does not exist.

lift
~~~~

.. code-block:: java

   public void lift() {
     moveTo(upPosition);
   }

The ``lift`` method raises the arm.

It sends the arm to the ``upPosition``.

Example:

.. code-block:: java

   robot.arm().lift();

This method is usually used:

* at the beginning of the simulation;
* after grabbing a puck;
* after dropping a puck;
* when the mission is finished.

lower
~~~~~

.. code-block:: java

   public void lower() {
     moveTo(downPosition);
   }

The ``lower`` method lowers the arm.

It sends the arm to the ``downPosition``.

Example:

.. code-block:: java

   robot.arm().lower();

This method is usually used before grabbing or dropping a puck.

moveTo
~~~~~~

.. code-block:: java

   public void moveTo(double position) {
     if (armMotor != null) {
       armMotor.setPosition(position);
     }
   }

The ``moveTo`` method moves the arm to a custom position.

This method is more general than ``lift`` and ``lower``.

For example:

.. code-block:: java

   robot.arm().moveTo(0.1);

This sends the arm to position ``0.1``.

The condition:

.. code-block:: java

   if (armMotor != null)

prevents the program from crashing if the motor is missing.

getPosition
~~~~~~~~~~~

.. code-block:: java

   public double getPosition() {
     return armSensor == null ? 0.0 : armSensor.getValue();
   }

The ``getPosition`` method returns the current arm position.

If the sensor does not exist, it returns ``0.0``.

Example:

.. code-block:: java

   System.out.println(robot.arm().getPosition());

This is mainly useful for debugging.

setUpPosition and setDownPosition
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

These methods are used to change the default arm positions.

.. code-block:: java

   public void setUpPosition(double upPosition) {
     this.upPosition = upPosition;
   }

   public void setDownPosition(double downPosition) {
     this.downPosition = downPosition;
   }

Example:

.. code-block:: java

   robot.arm().setUpPosition(-0.75);
   robot.arm().setDownPosition(0.40);

This can be useful if the robot model changes or if the arm does not move enough.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return armMotor != null;
   }

The ``exists`` method checks whether the arm motor was found.

Example:

.. code-block:: java

   if (!robot.arm().exists()) {
     System.out.println("Arm motor not found");
   }

This method is useful when debugging the Webots robot model.

Gripper
-------

Role of the Gripper class
~~~~~~~~~~~~~~~~~~~~~~~~~

The ``Gripper`` class controls the robot gripper.

The gripper has two sides:

* the left side;
* the right side.

Each side is controlled by one motor.

The gripper can be:

* opened;
* closed.

When the robot wants to take a puck, it closes the gripper.

When the robot wants to release a puck, it opens the gripper.

Gripper source code
~~~~~~~~~~~~~~~~~~~

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

Important Webots devices
~~~~~~~~~~~~~~~~~~~~~~~~

The ``Gripper`` class uses two Webots motors:

.. list-table::
   :header-rows: 1

   * - Webots device
     - Name in Webots
     - Role
   * - Motor
     - ``gripper_left_motor``
     - Moves the left part of the gripper.
   * - Motor
     - ``gripper_right_motor``
     - Moves the right part of the gripper.

The names must be exactly the same in Webots and in Java.

Constructor explanation
~~~~~~~~~~~~~~~~~~~~~~~

The constructor is:

.. code-block:: java

   public Gripper(Supervisor robot) {
     this.leftMotor = robot.getMotor("gripper_left_motor");
     this.rightMotor = robot.getMotor("gripper_right_motor");
     this.open = false;
   }

It retrieves the two gripper motors.

At creation, the internal ``open`` variable is set to ``false``.

However, in the ``TERBot`` constructor, the gripper is opened immediately:

.. code-block:: java

   gripper.open();

So when the simulation starts, the gripper is open and ready to grab a puck.

Open and closed positions
~~~~~~~~~~~~~~~~~~~~~~~~~

The gripper uses four position values:

.. list-table::
   :header-rows: 1

   * - Variable
     - Meaning
     - Default value
   * - ``openLeftPosition``
     - Open position for the left motor.
     - ``0.2``
   * - ``openRightPosition``
     - Open position for the right motor.
     - ``-0.2``
   * - ``closedLeftPosition``
     - Closed position for the left motor.
     - ``-0.55``
   * - ``closedRightPosition``
     - Closed position for the right motor.
     - ``0.55``

The left and right values often have opposite signs because both sides of the gripper rotate in opposite directions.

open
~~~~

.. code-block:: java

   public void open() {
     if (leftMotor != null) { leftMotor.setPosition(openLeftPosition); }
     if (rightMotor != null) { rightMotor.setPosition(openRightPosition); }
     open = true;
   }

The ``open`` method opens the gripper.

It sends each motor to its open position.

Example:

.. code-block:: java

   robot.gripper().open();

This is used:

* at the start of the simulation;
* before grabbing a puck;
* when releasing a puck;
* when the robot has finished the mission.

close
~~~~~

.. code-block:: java

   public void close() {
     if (leftMotor != null) { leftMotor.setPosition(closedLeftPosition); }
     if (rightMotor != null) { rightMotor.setPosition(closedRightPosition); }
     open = false;
   }

The ``close`` method closes the gripper.

Example:

.. code-block:: java

   robot.gripper().close();

This is used when the robot is positioned around a puck.

isOpen
~~~~~~

.. code-block:: java

   public boolean isOpen() {
     return open;
   }

The ``isOpen`` method returns the logical state of the gripper.

Important: this value does not come from a physical sensor.  
It only remembers whether the API last called ``open`` or ``close``.

Example:

.. code-block:: java

   if (robot.gripper().isOpen()) {
     System.out.println("The gripper is open.");
   }

setOpenPositions
~~~~~~~~~~~~~~~~

.. code-block:: java

   public void setOpenPositions(double left, double right) {
     this.openLeftPosition = left;
     this.openRightPosition = right;
   }

This method changes the open positions.

Example:

.. code-block:: java

   robot.gripper().setOpenPositions(0.3, -0.3);

This can be useful if the gripper does not open enough.

setClosedPositions
~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public void setClosedPositions(double left, double right) {
     this.closedLeftPosition = left;
     this.closedRightPosition = right;
   }

This method changes the closed positions.

Example:

.. code-block:: java

   robot.gripper().setClosedPositions(-0.6, 0.6);

This can be useful if the gripper does not close enough around the puck.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return leftMotor != null || rightMotor != null;
   }

The ``exists`` method checks whether at least one of the two gripper motors exists.

Example:

.. code-block:: java

   if (!robot.gripper().exists()) {
     System.out.println("Gripper motors not found");
   }

This is useful when debugging the robot model.

How the arm and gripper work together
-------------------------------------

The arm and gripper are usually used together.

To pick up a puck, the robot follows this logic:

.. code-block:: text

   1. Stop the robot.
   2. Lower the arm.
   3. Open the gripper.
   4. Close the gripper around the puck.
   5. Lift the arm.

In Java, the idea is:

.. code-block:: java

   robot.motors().stop();
   robot.arm().lower();
   robot.gripper().open();
   robot.gripper().close();
   robot.arm().lift();

In the real behavior, these actions are not executed all at once.  
The controller waits several simulation steps between them, because motors need time to move.

To drop a puck, the robot follows this logic:

.. code-block:: text

   1. Stop near the drop zone.
   2. Lower the arm.
   3. Open the gripper.
   4. Mark the puck as delivered.
   5. Lift the arm.

In Java, the idea is:

.. code-block:: java

   robot.motors().stop();
   robot.arm().lower();
   robot.gripper().open();
   robot.arm().lift();

Use in the full behavior
------------------------

In ``CollectPucksBehavior``, the arm and gripper are used during several modes.

.. list-table::
   :header-rows: 1

   * - Mode
     - Action
   * - ``LOWER_ARM``
     - The robot lowers the arm and opens the gripper.
   * - ``CLOSE_GRIPPER``
     - The robot closes the gripper to grab the puck.
   * - ``LIFT_ARM``
     - The robot lifts the arm after grabbing the puck.
   * - ``DROP_PUCK``
     - The robot opens the gripper to release the puck.
   * - ``LIFT_ARM_AFTER_DROP``
     - The robot lifts the arm after dropping the puck.
   * - ``FINISHED``
     - The robot lifts the arm and opens the gripper.

This makes the mission easier to understand as a sequence of steps.

Naming convention
-----------------

The actuator API depends on the names of the devices in Webots.

The robot must contain:

.. code-block:: text

   arm_motor
   arm_sensor
   gripper_left_motor
   gripper_right_motor

If one of these names is wrong, the corresponding part of the API will not work.

Debugging
---------

If the arm does not move, check:

* the motor is named ``arm_motor`` in Webots;
* the position sensor is named ``arm_sensor`` in Webots;
* the motor is correctly connected to the arm joint;
* the controller has been recompiled;
* ``robot.arm().exists()`` returns ``true``;
* the target positions are compatible with the joint limits.

If the gripper does not move, check:

* the left motor is named ``gripper_left_motor``;
* the right motor is named ``gripper_right_motor``;
* both motors are correctly connected to the gripper joints;
* the controller has been recompiled;
* ``robot.gripper().exists()`` returns ``true``;
* the open and closed positions are compatible with the joint limits.

If the arm moves in the wrong direction, change the default positions:

.. code-block:: java

   robot.arm().setUpPosition(...);
   robot.arm().setDownPosition(...);

If the gripper opens when it should close, change the gripper positions:

.. code-block:: java

   robot.gripper().setOpenPositions(..., ...);
   robot.gripper().setClosedPositions(..., ...);

Summary
-------

The ``api.actuators`` package controls the mechanical parts of the robot.

The ``Arm`` class controls the arm using:

* ``arm_motor``;
* ``arm_sensor``.

The ``Gripper`` class controls the gripper using:

* ``gripper_left_motor``;
* ``gripper_right_motor``.

This package makes the robot behavior easier to write and easier to understand.

Instead of manipulating Webots motors directly, the behavior can use simple commands:

.. code-block:: java

   robot.arm().lower();
   robot.gripper().close();
   robot.arm().lift();

These commands clearly describe what the robot is doing during the puck collection mission.