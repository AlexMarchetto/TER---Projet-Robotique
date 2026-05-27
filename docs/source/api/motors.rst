Motors API
==========

Overview
--------

The ``api.motors`` package contains the classes used to control the robot movement.

In this project, the robot has four wheels:

* front-left wheel;
* front-right wheel;
* rear-left wheel;
* rear-right wheel.

Each wheel is controlled by one Webots motor.

The goal of this package is to make movement commands easy to understand.

For example, instead of manually controlling four motors, the behavior can simply write:

.. code-block:: java

   robot.motors().forward(4.0);
   robot.motors().turnLeft(3.0);
   robot.motors().stop();

Why this package exists
-----------------------

Without this API, the behavior code would need to directly control every wheel motor.

For example:

.. code-block:: java

   wheel1.setVelocity(4.0);
   wheel2.setVelocity(4.0);
   wheel3.setVelocity(4.0);
   wheel4.setVelocity(4.0);

With the API, the same action becomes:

.. code-block:: java

   robot.motors().forward(4.0);

This makes the code easier to read, easier to debug, and easier to reuse in practical sessions.

Package location
----------------

The files are located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── motors/
               ├── DriveBase.java
               ├── MotorGroup.java
               └── Wheel.java

Package declaration
-------------------

Each file starts with:

.. code-block:: java

   package api.motors;

This means that these files must be placed in:

.. code-block:: text

   api/motors/

If the folder and package name do not match, Java will not compile the project.

General organization
--------------------

The motor system is organized in three levels:

.. code-block:: text

   DriveBase
      |
      ├── MotorGroup leftWheels
      │      ├── Wheel frontLeft
      │      └── Wheel rearLeft
      |
      └── MotorGroup rightWheels
             ├── Wheel frontRight
             └── Wheel rearRight

Each class has a different role:

.. list-table::
   :header-rows: 1

   * - Class
     - Role
   * - ``Wheel``
     - Controls one Webots motor.
   * - ``MotorGroup``
     - Controls several wheels at the same time.
   * - ``DriveBase``
     - Controls the complete robot movement.

Wheel mapping
-------------

The robot uses four Webots motors.

The API maps them like this:

.. list-table::
   :header-rows: 1

   * - Webots motor name
     - API name
     - Position on robot
   * - ``wheel1``
     - ``frontLeft``
     - Front-left wheel
   * - ``wheel2``
     - ``frontRight``
     - Front-right wheel
   * - ``wheel3``
     - ``rearLeft``
     - Rear-left wheel
   * - ``wheel4``
     - ``rearRight``
     - Rear-right wheel

These names are important.

If the motor names in Webots are different, the API will not be able to retrieve the motors correctly.

Wheel
-----

Role of the Wheel class
~~~~~~~~~~~~~~~~~~~~~~~

The ``Wheel`` class represents one wheel of the robot.

It wraps one Webots ``Motor`` object and provides simple methods to:

* set the wheel speed;
* move the wheel forward;
* move the wheel backward;
* stop the wheel;
* check if the motor exists.

Wheel source code
~~~~~~~~~~~~~~~~~

.. code-block:: java

   package api.motors;

   import com.cyberbotics.webots.controller.Motor;

   public class Wheel {
     private final Motor motor;
     private double currentSpeed;

     public Wheel(Motor motor) {
       this.motor = motor;
       this.currentSpeed = 0.0;
       if (this.motor != null) {
         this.motor.setPosition(Double.POSITIVE_INFINITY);
         this.motor.setVelocity(0.0);
       }
     }

     public void setSpeed(double speed) {
       currentSpeed = speed;
       if (motor != null) { motor.setVelocity(speed); }
     }

     public void forward(double speed) { setSpeed(Math.abs(speed)); }
     public void backward(double speed) { setSpeed(-Math.abs(speed)); }
     public void stop() { setSpeed(0.0); }
     public double getCurrentSpeed() { return currentSpeed; }
     public boolean exists() { return motor != null; }
   }

Attributes
~~~~~~~~~~

The ``Wheel`` class contains two attributes:

.. code-block:: java

   private final Motor motor;
   private double currentSpeed;

``motor`` is the Webots motor controlled by the wheel.

``currentSpeed`` stores the last speed value sent to the motor.

This is useful for debugging because it allows the program to remember the last command sent to the wheel.

Constructor explanation
~~~~~~~~~~~~~~~~~~~~~~~

The constructor receives a Webots motor:

.. code-block:: java

   public Wheel(Motor motor) {
     this.motor = motor;
     this.currentSpeed = 0.0;
     if (this.motor != null) {
       this.motor.setPosition(Double.POSITIVE_INFINITY);
       this.motor.setVelocity(0.0);
     }
   }

It does three things:

1. It stores the motor.
2. It initializes the speed to ``0.0``.
3. It configures the motor in velocity mode.

Velocity mode
~~~~~~~~~~~~~

This line is very important:

.. code-block:: java

   this.motor.setPosition(Double.POSITIVE_INFINITY);

In Webots, a motor can be controlled in two main ways:

* position control;
* velocity control.

For the wheels, we do not want the motor to move to a fixed angle.

We want the wheel to rotate continuously.

That is why the position is set to:

.. code-block:: java

   Double.POSITIVE_INFINITY

This switches the motor to velocity control mode.

Then the wheel is stopped at the beginning:

.. code-block:: java

   this.motor.setVelocity(0.0);

setSpeed
~~~~~~~~

.. code-block:: java

   public void setSpeed(double speed) {
     currentSpeed = speed;
     if (motor != null) {
       motor.setVelocity(speed);
     }
   }

The ``setSpeed`` method sends a velocity command to the wheel.

A positive speed makes the wheel rotate forward.

A negative speed makes the wheel rotate backward.

The method also stores the value in ``currentSpeed``.

The condition:

.. code-block:: java

   if (motor != null)

prevents the program from crashing if the motor was not found in Webots.

forward
~~~~~~~

.. code-block:: java

   public void forward(double speed) {
     setSpeed(Math.abs(speed));
   }

The ``forward`` method makes the wheel rotate forward.

It uses ``Math.abs`` to make sure the speed is positive.

Example:

.. code-block:: java

   wheel.forward(3.0);

Even if the user writes:

.. code-block:: java

   wheel.forward(-3.0);

the wheel still receives a positive speed.

backward
~~~~~~~~

.. code-block:: java

   public void backward(double speed) {
     setSpeed(-Math.abs(speed));
   }

The ``backward`` method makes the wheel rotate backward.

It forces the speed to be negative.

Example:

.. code-block:: java

   wheel.backward(3.0);

This sends:

.. code-block:: text

   -3.0

to the motor.

stop
~~~~

.. code-block:: java

   public void stop() {
     setSpeed(0.0);
   }

The ``stop`` method stops the wheel.

getCurrentSpeed
~~~~~~~~~~~~~~~

.. code-block:: java

   public double getCurrentSpeed() {
     return currentSpeed;
   }

This method returns the last speed sent to the wheel.

Important: it does not measure the real physical wheel speed.

It only returns the last command stored by the API.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return motor != null;
   }

The ``exists`` method checks if the Webots motor was found.

Example:

.. code-block:: java

   if (!robot.motors().frontLeft().exists()) {
     System.out.println("Front-left wheel motor not found");
   }

This is useful for debugging motor names in Webots.

MotorGroup
----------

Role of the MotorGroup class
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``MotorGroup`` class controls several wheels at the same time.

This is useful because the robot has two wheels on each side.

For example:

* the left group contains the front-left and rear-left wheels;
* the right group contains the front-right and rear-right wheels.

Instead of setting the same speed twice, the group does it automatically.

MotorGroup source code
~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   package api.motors;

   public class MotorGroup {
     private final Wheel[] wheels;

     public MotorGroup(Wheel... wheels) {
       this.wheels = wheels;
     }

     public void setSpeed(double speed) {
       for (Wheel wheel : wheels) {
         if (wheel != null) {
           wheel.setSpeed(speed);
         }
       }
     }

     public void forward(double speed) { setSpeed(Math.abs(speed)); }
     public void backward(double speed) { setSpeed(-Math.abs(speed)); }
     public void stop() { setSpeed(0.0); }
   }

Constructor explanation
~~~~~~~~~~~~~~~~~~~~~~~

The constructor is:

.. code-block:: java

   public MotorGroup(Wheel... wheels) {
     this.wheels = wheels;
   }

The ``...`` syntax means that the constructor can receive several wheels.

Example:

.. code-block:: java

   MotorGroup leftWheels = new MotorGroup(frontLeft, rearLeft);

This creates a group with two wheels.

setSpeed
~~~~~~~~

.. code-block:: java

   public void setSpeed(double speed) {
     for (Wheel wheel : wheels) {
       if (wheel != null) {
         wheel.setSpeed(speed);
       }
     }
   }

This method applies the same speed to every wheel in the group.

The ``if`` condition prevents errors if a wheel is missing.

forward, backward, stop
~~~~~~~~~~~~~~~~~~~~~~~

These methods work like the methods in ``Wheel``, but they apply to all wheels in the group.

Example:

.. code-block:: java

   leftWheels.forward(4.0);

This sends speed ``4.0`` to all wheels in the left group.

DriveBase
---------

Role of the DriveBase class
~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``DriveBase`` class controls the complete movement of the robot.

It is the class used by the rest of the API when the robot needs to move.

For example:

.. code-block:: java

   robot.motors().forward(4.0);
   robot.motors().turnRight(3.0);
   robot.motors().curveLeft(3.0, 0.5);
   robot.motors().stop();

The ``DriveBase`` does not directly represent one motor.

It represents the whole moving base of the robot.

DriveBase source code
~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   package api.motors;

   import com.cyberbotics.webots.controller.Supervisor;

   import api.utils.MathUtils;

   public class DriveBase {
     private final Wheel frontLeft;
     private final Wheel frontRight;
     private final Wheel rearLeft;
     private final Wheel rearRight;
     private final MotorGroup leftWheels;
     private final MotorGroup rightWheels;

     public DriveBase(Supervisor robot) {
       this.frontLeft = new Wheel(robot.getMotor("wheel1"));
       this.frontRight = new Wheel(robot.getMotor("wheel2"));
       this.rearLeft = new Wheel(robot.getMotor("wheel3"));
       this.rearRight = new Wheel(robot.getMotor("wheel4"));
       this.leftWheels = new MotorGroup(frontLeft, rearLeft);
       this.rightWheels = new MotorGroup(frontRight, rearRight);
     }

     public void setSpeed(double leftSpeed, double rightSpeed) {
       leftWheels.setSpeed(leftSpeed);
       rightWheels.setSpeed(rightSpeed);
     }

     public void forward(double speed) { setSpeed(Math.abs(speed), Math.abs(speed)); }
     public void backward(double speed) { setSpeed(-Math.abs(speed), -Math.abs(speed)); }
     public void turnLeft(double speed) { double v = Math.abs(speed); setSpeed(-v, v); }
     public void turnRight(double speed) { double v = Math.abs(speed); setSpeed(v, -v); }
     public void curveLeft(double speed, double factor) { double v = Math.abs(speed); setSpeed(v * MathUtils.clamp(factor, 0.0, 1.0), v); }
     public void curveRight(double speed, double factor) { double v = Math.abs(speed); setSpeed(v, v * MathUtils.clamp(factor, 0.0, 1.0)); }
     public void stop() { setSpeed(0.0, 0.0); }

     public Wheel frontLeft() { return frontLeft; }
     public Wheel frontRight() { return frontRight; }
     public Wheel rearLeft() { return rearLeft; }
     public Wheel rearRight() { return rearRight; }
   }

Constructor explanation
~~~~~~~~~~~~~~~~~~~~~~~

The constructor retrieves the four wheel motors from Webots:

.. code-block:: java

   this.frontLeft = new Wheel(robot.getMotor("wheel1"));
   this.frontRight = new Wheel(robot.getMotor("wheel2"));
   this.rearLeft = new Wheel(robot.getMotor("wheel3"));
   this.rearRight = new Wheel(robot.getMotor("wheel4"));

Then it creates two groups:

.. code-block:: java

   this.leftWheels = new MotorGroup(frontLeft, rearLeft);
   this.rightWheels = new MotorGroup(frontRight, rearRight);

This makes it possible to control the robot using left and right speeds.

Movement principle
------------------

The robot uses a differential drive logic.

This means that the movement depends on the speed difference between the left wheels and the right wheels.

.. list-table::
   :header-rows: 1

   * - Left wheels
     - Right wheels
     - Robot movement
   * - Positive
     - Positive
     - Moves forward
   * - Negative
     - Negative
     - Moves backward
   * - Negative
     - Positive
     - Turns left
   * - Positive
     - Negative
     - Turns right
   * - Slower
     - Faster
     - Curves left
   * - Faster
     - Slower
     - Curves right
   * - Zero
     - Zero
     - Stops

This is the main idea behind all movement methods.

setSpeed
~~~~~~~~

.. code-block:: java

   public void setSpeed(double leftSpeed, double rightSpeed) {
     leftWheels.setSpeed(leftSpeed);
     rightWheels.setSpeed(rightSpeed);
   }

The ``setSpeed`` method is the base movement method.

It controls the left and right sides independently.

Example:

.. code-block:: java

   robot.motors().setSpeed(4.0, 2.0);

In this example, the left wheels are faster than the right wheels.

The robot will curve to the right.

forward
~~~~~~~

.. code-block:: java

   public void forward(double speed) {
     setSpeed(Math.abs(speed), Math.abs(speed));
   }

The ``forward`` method moves the robot forward.

Both sides receive the same positive speed.

Example:

.. code-block:: java

   robot.motors().forward(4.0);

backward
~~~~~~~~

.. code-block:: java

   public void backward(double speed) {
     setSpeed(-Math.abs(speed), -Math.abs(speed));
   }

The ``backward`` method moves the robot backward.

Both sides receive the same negative speed.

Example:

.. code-block:: java

   robot.motors().backward(2.0);

turnLeft
~~~~~~~~

.. code-block:: java

   public void turnLeft(double speed) {
     double v = Math.abs(speed);
     setSpeed(-v, v);
   }

The ``turnLeft`` method rotates the robot to the left.

The left wheels move backward.

The right wheels move forward.

Example:

.. code-block:: java

   robot.motors().turnLeft(3.0);

This is a rotation on the spot.

turnRight
~~~~~~~~~

.. code-block:: java

   public void turnRight(double speed) {
     double v = Math.abs(speed);
     setSpeed(v, -v);
   }

The ``turnRight`` method rotates the robot to the right.

The left wheels move forward.

The right wheels move backward.

Example:

.. code-block:: java

   robot.motors().turnRight(3.0);

curveLeft
~~~~~~~~~

.. code-block:: java

   public void curveLeft(double speed, double factor) {
     double v = Math.abs(speed);
     setSpeed(v * MathUtils.clamp(factor, 0.0, 1.0), v);
   }

The ``curveLeft`` method makes the robot move forward while turning left.

The right wheels keep the full speed.

The left wheels are slowed down.

The ``factor`` controls how strong the curve is.

.. list-table::
   :header-rows: 1

   * - Factor
     - Effect
   * - ``1.0``
     - Almost straight movement.
   * - ``0.5``
     - Medium left curve.
   * - ``0.0``
     - Very strong left curve.

Example:

.. code-block:: java

   robot.motors().curveLeft(4.0, 0.5);

This means:

.. code-block:: text

   left wheels  = 2.0
   right wheels = 4.0

The robot moves forward while curving left.

curveRight
~~~~~~~~~~

.. code-block:: java

   public void curveRight(double speed, double factor) {
     double v = Math.abs(speed);
     setSpeed(v, v * MathUtils.clamp(factor, 0.0, 1.0));
   }

The ``curveRight`` method makes the robot move forward while turning right.

The left wheels keep the full speed.

The right wheels are slowed down.

Example:

.. code-block:: java

   robot.motors().curveRight(4.0, 0.5);

This means:

.. code-block:: text

   left wheels  = 4.0
   right wheels = 2.0

The robot moves forward while curving right.

Why clamp is used
~~~~~~~~~~~~~~~~~

The curve methods use:

.. code-block:: java

   MathUtils.clamp(factor, 0.0, 1.0)

This ensures that the factor always stays between ``0.0`` and ``1.0``.

For example:

* if the factor is ``-0.5``, it becomes ``0.0``;
* if the factor is ``1.5``, it becomes ``1.0``.

This prevents invalid wheel speeds.

stop
~~~~

.. code-block:: java

   public void stop() {
     setSpeed(0.0, 0.0);
   }

The ``stop`` method stops the robot.

Example:

.. code-block:: java

   robot.motors().stop();

Wheel accessors
~~~~~~~~~~~~~~~

The ``DriveBase`` also gives access to each wheel:

.. code-block:: java

   public Wheel frontLeft() { return frontLeft; }
   public Wheel frontRight() { return frontRight; }
   public Wheel rearLeft() { return rearLeft; }
   public Wheel rearRight() { return rearRight; }

These methods are mainly useful for debugging.

Example:

.. code-block:: java

   System.out.println(robot.motors().frontLeft().getCurrentSpeed());

Use in the full robot behavior
------------------------------

The motors API is used everywhere in the robot behavior.

For example, in the collection mission:

.. list-table::
   :header-rows: 1

   * - Situation
     - Motor action
   * - The robot searches for pucks.
     - ``forward`` or obstacle avoidance turns.
   * - The robot approaches a puck.
     - ``setSpeed`` with angle correction.
   * - The robot touches an obstacle.
     - ``backward`` then ``turnLeft``.
   * - The robot carries a puck to the drop zone.
     - ``forward`` or ``curveLeft`` / ``curveRight``.
   * - The robot drops a puck.
     - ``stop``.

This package is therefore essential for all robot movements.

Examples
--------

Move forward
~~~~~~~~~~~~

.. code-block:: java

   robot.motors().forward(4.0);

Move backward
~~~~~~~~~~~~~

.. code-block:: java

   robot.motors().backward(2.0);

Turn left
~~~~~~~~~

.. code-block:: java

   robot.motors().turnLeft(3.0);

Turn right
~~~~~~~~~~

.. code-block:: java

   robot.motors().turnRight(3.0);

Curve left
~~~~~~~~~~

.. code-block:: java

   robot.motors().curveLeft(4.0, 0.5);

Curve right
~~~~~~~~~~~

.. code-block:: java

   robot.motors().curveRight(4.0, 0.5);

Stop
~~~~

.. code-block:: java

   robot.motors().stop();

Naming convention
-----------------

The motors API depends on the names of the motors in Webots.

The robot must contain:

.. code-block:: text

   wheel1
   wheel2
   wheel3
   wheel4

If one of these names is wrong, the corresponding wheel will not work.

Debugging
---------

If the robot does not move, check:

* the controller has been compiled;
* the controller is assigned to the robot in Webots;
* the motors are named ``wheel1``, ``wheel2``, ``wheel3`` and ``wheel4``;
* the wheels are connected to motors in the robot model;
* the wheel joints are correctly configured;
* the wheels have a physical shape and collision object;
* the motor exists in the API.

You can test each wheel with:

.. code-block:: java

   System.out.println(robot.motors().frontLeft().exists());
   System.out.println(robot.motors().frontRight().exists());
   System.out.println(robot.motors().rearLeft().exists());
   System.out.println(robot.motors().rearRight().exists());

If the robot turns when it should move forward, check the wheel mapping.

If the robot moves backward when it should move forward, the wheel direction or motor orientation may need to be adjusted in Webots.

Summary
-------

The ``api.motors`` package controls the robot movement.

It contains three classes:

* ``Wheel`` controls one Webots motor.
* ``MotorGroup`` controls several wheels together.
* ``DriveBase`` controls the complete robot movement.

The most important class for behaviors is ``DriveBase``.

It allows simple movement commands such as:

.. code-block:: java

   robot.motors().forward(4.0);
   robot.motors().turnLeft(3.0);
   robot.motors().curveRight(4.0, 0.5);
   robot.motors().stop();

This makes the behavior code easier to understand because it describes the robot actions directly.