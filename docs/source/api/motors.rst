Motors API
==========

Overview
--------

The ``api.motors`` package contains the classes used to control the robot wheels.

This package is responsible for all basic movements of the robot:

* moving forward;
* moving backward;
* turning left;
* turning right;
* making curved movements;
* stopping the robot;
* controlling individual wheels.

The goal of this package is to hide the low-level Webots motor code behind a simpler API.

Instead of directly manipulating each Webots motor, the rest of the controller can use simple methods such as:

.. code-block:: java

   bot.motors().forward(2.0);
   bot.motors().turnLeft(2.0);
   bot.motors().stop();

Package location
----------------

The motors API is located in:

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

Each file in this package starts with:

.. code-block:: java

   package api.motors;

This means that the files must be located in:

.. code-block:: text

   api/motors/

If the package declaration and the folder path do not match, Java will not compile the project correctly.

Main classes
------------

The package contains three main classes:

.. list-table::
   :header-rows: 1

   * - Class
     - Role
   * - ``Wheel``
     - Represents one robot wheel and controls one Webots motor.
   * - ``MotorGroup``
     - Groups several wheels and applies the same speed to all of them.
   * - ``DriveBase``
     - Controls the whole robot movement using the four wheels.

General organization
--------------------

The movement system is organized like this:

.. code-block:: text

   DriveBase
      |
      ├── frontLeft  -> Wheel -> Webots Motor "wheel1"
      ├── frontRight -> Wheel -> Webots Motor "wheel2"
      ├── rearLeft   -> Wheel -> Webots Motor "wheel3"
      └── rearRight  -> Wheel -> Webots Motor "wheel4"

The left wheels and right wheels are also grouped:

.. code-block:: text

   leftWheels  = frontLeft + rearLeft
   rightWheels = frontRight + rearRight

This makes it easy to control the robot like a differential-drive robot.

Wheel
-----

Overview
~~~~~~~~

The ``Wheel`` class represents one wheel of the robot.

It wraps a Webots ``Motor`` object and provides simple methods to control the wheel speed.

Class code
~~~~~~~~~~

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

The class contains two attributes:

.. code-block:: java

   private final Motor motor;
   private double currentSpeed;

``motor``
^^^^^^^^^

The ``motor`` attribute stores the Webots motor linked to the wheel.

For example, the front-left wheel is linked to the Webots motor named:

.. code-block:: text

   wheel1

``currentSpeed``
^^^^^^^^^^^^^^^^

The ``currentSpeed`` attribute stores the last speed value applied to the wheel.

This is useful to know the current command given to the wheel.

Constructor
~~~~~~~~~~~

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

The constructor does three things:

1. It stores the motor.
2. It initializes the current speed to ``0.0``.
3. It configures the motor in velocity mode.

Velocity mode
~~~~~~~~~~~~~

This line is very important:

.. code-block:: java

   this.motor.setPosition(Double.POSITIVE_INFINITY);

In Webots, a motor can be controlled in position mode or velocity mode.

By setting the position to ``Double.POSITIVE_INFINITY``, the motor is switched to velocity control mode.

This means that the wheel can rotate continuously.

Then, the wheel is stopped at the beginning:

.. code-block:: java

   this.motor.setVelocity(0.0);

This prevents the robot from moving before the controller explicitly gives a speed.

setSpeed
~~~~~~~~

.. code-block:: java

   public void setSpeed(double speed) {
     currentSpeed = speed;
     if (motor != null) { motor.setVelocity(speed); }
   }

This method sets the wheel speed.

It also stores the speed in ``currentSpeed``.

The condition:

.. code-block:: java

   if (motor != null)

prevents the program from crashing if the motor was not found in Webots.

For example, if the motor name is wrong in the PROTO file, ``robot.getMotor(...)`` may return ``null``.  
This check makes the API more robust.

forward
~~~~~~~

.. code-block:: java

   public void forward(double speed) {
     setSpeed(Math.abs(speed));
   }

This method makes the wheel rotate forward.

It uses ``Math.abs(speed)`` so that the speed is always positive, even if the user passes a negative value.

Example:

.. code-block:: java

   wheel.forward(2.0);

backward
~~~~~~~~

.. code-block:: java

   public void backward(double speed) {
     setSpeed(-Math.abs(speed));
   }

This method makes the wheel rotate backward.

It forces the speed to be negative.

Example:

.. code-block:: java

   wheel.backward(2.0);

This is equivalent to:

.. code-block:: java

   wheel.setSpeed(-2.0);

stop
~~~~

.. code-block:: java

   public void stop() {
     setSpeed(0.0);
   }

This method stops the wheel.

getCurrentSpeed
~~~~~~~~~~~~~~~

.. code-block:: java

   public double getCurrentSpeed() {
     return currentSpeed;
   }

This method returns the last speed assigned to the wheel.

It does not read the real physical speed from Webots.  
It only returns the last command sent by the API.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return motor != null;
   }

This method checks whether the Webots motor was correctly found.

It can be useful for debugging.

Example:

.. code-block:: java

   if (!bot.motors().frontLeft().exists()) {
     System.out.println("Front-left wheel motor not found");
   }

MotorGroup
----------

Overview
~~~~~~~~

The ``MotorGroup`` class groups several wheels together.

It allows the same speed to be applied to multiple wheels at once.

This is useful because the robot has:

* two wheels on the left side;
* two wheels on the right side.

Instead of setting each wheel speed manually, ``MotorGroup`` can control several wheels with one method call.

Class code
~~~~~~~~~~

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

Attribute
~~~~~~~~~

The class stores a list of wheels:

.. code-block:: java

   private final Wheel[] wheels;

The array can contain one or more wheels.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public MotorGroup(Wheel... wheels) {
     this.wheels = wheels;
   }

The ``...`` syntax means that the constructor accepts a variable number of wheels.

For example:

.. code-block:: java

   MotorGroup leftWheels = new MotorGroup(frontLeft, rearLeft);

This creates a group containing the front-left and rear-left wheels.

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

This method applies the same speed to all wheels in the group.

The ``if (wheel != null)`` condition prevents errors if one wheel is missing.

forward
~~~~~~~

.. code-block:: java

   public void forward(double speed) {
     setSpeed(Math.abs(speed));
   }

This method makes all wheels in the group move forward.

backward
~~~~~~~~

.. code-block:: java

   public void backward(double speed) {
     setSpeed(-Math.abs(speed));
   }

This method makes all wheels in the group move backward.

stop
~~~~

.. code-block:: java

   public void stop() {
     setSpeed(0.0);
   }

This method stops all wheels in the group.

Example
~~~~~~~

Example with the left wheels:

.. code-block:: java

   MotorGroup leftWheels = new MotorGroup(frontLeft, rearLeft);
   leftWheels.forward(2.0);

Both left wheels receive the same speed.

DriveBase
---------

Overview
~~~~~~~~

The ``DriveBase`` class controls the complete movement of the robot.

It creates the four wheels, groups the left and right wheels, and provides high-level movement methods.

This is the class most frequently used by behaviors.

Example:

.. code-block:: java

   bot.motors().forward(2.0);
   bot.motors().turnRight(2.0);
   bot.motors().stop();

Class code
~~~~~~~~~~

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

Attributes
~~~~~~~~~~

The class stores the four wheels:

.. code-block:: java

   private final Wheel frontLeft;
   private final Wheel frontRight;
   private final Wheel rearLeft;
   private final Wheel rearRight;

It also stores two motor groups:

.. code-block:: java

   private final MotorGroup leftWheels;
   private final MotorGroup rightWheels;

These groups are used to control the left and right sides of the robot.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public DriveBase(Supervisor robot) {
     this.frontLeft = new Wheel(robot.getMotor("wheel1"));
     this.frontRight = new Wheel(robot.getMotor("wheel2"));
     this.rearLeft = new Wheel(robot.getMotor("wheel3"));
     this.rearRight = new Wheel(robot.getMotor("wheel4"));
     this.leftWheels = new MotorGroup(frontLeft, rearLeft);
     this.rightWheels = new MotorGroup(frontRight, rearRight);
   }

The constructor receives the Webots ``Supervisor``.

It retrieves the four wheel motors by name:

.. code-block:: text

   wheel1
   wheel2
   wheel3
   wheel4

These names must match the motor names defined in the robot PROTO file.

The wheel mapping is:

.. list-table::
   :header-rows: 1

   * - Webots motor name
     - API attribute
     - Position
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

Then it creates two groups:

.. code-block:: java

   this.leftWheels = new MotorGroup(frontLeft, rearLeft);
   this.rightWheels = new MotorGroup(frontRight, rearRight);

The left group contains:

* ``frontLeft``;
* ``rearLeft``.

The right group contains:

* ``frontRight``;
* ``rearRight``.

setSpeed
~~~~~~~~

.. code-block:: java

   public void setSpeed(double leftSpeed, double rightSpeed) {
     leftWheels.setSpeed(leftSpeed);
     rightWheels.setSpeed(rightSpeed);
   }

This method sets the speed of the left wheels and the right wheels independently.

This is the base method used by all other movement methods.

Example:

.. code-block:: java

   bot.motors().setSpeed(2.0, 1.0);

This makes the left wheels move faster than the right wheels, so the robot starts to curve.

forward
~~~~~~~

.. code-block:: java

   public void forward(double speed) {
     setSpeed(Math.abs(speed), Math.abs(speed));
   }

This method makes the robot move forward.

Both sides receive the same positive speed.

Example:

.. code-block:: java

   bot.motors().forward(2.0);

Even if the user gives a negative value, ``Math.abs`` turns it into a positive value.

backward
~~~~~~~~

.. code-block:: java

   public void backward(double speed) {
     setSpeed(-Math.abs(speed), -Math.abs(speed));
   }

This method makes the robot move backward.

Both sides receive the same negative speed.

Example:

.. code-block:: java

   bot.motors().backward(2.0);

turnLeft
~~~~~~~~

.. code-block:: java

   public void turnLeft(double speed) {
     double v = Math.abs(speed);
     setSpeed(-v, v);
   }

This method makes the robot rotate to the left.

The left wheels move backward and the right wheels move forward.

Example:

.. code-block:: java

   bot.motors().turnLeft(2.0);

Wheel speeds:

.. code-block:: text

   left wheels  = -2.0
   right wheels =  2.0

turnRight
~~~~~~~~~

.. code-block:: java

   public void turnRight(double speed) {
     double v = Math.abs(speed);
     setSpeed(v, -v);
   }

This method makes the robot rotate to the right.

The left wheels move forward and the right wheels move backward.

Example:

.. code-block:: java

   bot.motors().turnRight(2.0);

Wheel speeds:

.. code-block:: text

   left wheels  =  2.0
   right wheels = -2.0

curveLeft
~~~~~~~~~

.. code-block:: java

   public void curveLeft(double speed, double factor) {
     double v = Math.abs(speed);
     setSpeed(v * MathUtils.clamp(factor, 0.0, 1.0), v);
   }

This method makes the robot move forward while curving left.

The right wheels keep the full speed, while the left wheels are slowed down.

The ``factor`` controls how strong the curve is.

The value is clamped between ``0.0`` and ``1.0`` using:

.. code-block:: java

   MathUtils.clamp(factor, 0.0, 1.0)

Meaning of the factor:

.. list-table::
   :header-rows: 1

   * - Factor
     - Effect
   * - ``1.0``
     - Both sides have the same speed, so the robot moves almost straight.
   * - ``0.5``
     - The left side is slower, so the robot curves left.
   * - ``0.0``
     - The left side stops, so the robot turns more sharply left.

Example:

.. code-block:: java

   bot.motors().curveLeft(2.0, 0.5);

Wheel speeds:

.. code-block:: text

   left wheels  = 1.0
   right wheels = 2.0

curveRight
~~~~~~~~~~

.. code-block:: java

   public void curveRight(double speed, double factor) {
     double v = Math.abs(speed);
     setSpeed(v, v * MathUtils.clamp(factor, 0.0, 1.0));
   }

This method makes the robot move forward while curving right.

The left wheels keep the full speed, while the right wheels are slowed down.

Example:

.. code-block:: java

   bot.motors().curveRight(2.0, 0.5);

Wheel speeds:

.. code-block:: text

   left wheels  = 2.0
   right wheels = 1.0

stop
~~~~

.. code-block:: java

   public void stop() {
     setSpeed(0.0, 0.0);
   }

This method stops the robot.

Example:

.. code-block:: java

   bot.motors().stop();

Wheel accessors
~~~~~~~~~~~~~~~

The class also provides access to each individual wheel:

.. code-block:: java

   public Wheel frontLeft() { return frontLeft; }
   public Wheel frontRight() { return frontRight; }
   public Wheel rearLeft() { return rearLeft; }
   public Wheel rearRight() { return rearRight; }

These methods are mainly useful for debugging or advanced control.

Example:

.. code-block:: java

   System.out.println(bot.motors().frontLeft().getCurrentSpeed());

How movements work
------------------

The robot uses differential drive logic.

This means that movement depends on the speed difference between the left and right wheels.

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

Example usages
--------------

Move forward
~~~~~~~~~~~~

.. code-block:: java

   bot.motors().forward(2.0);

Move backward
~~~~~~~~~~~~~

.. code-block:: java

   bot.motors().backward(2.0);

Turn left
~~~~~~~~~

.. code-block:: java

   bot.motors().turnLeft(2.0);

Turn right
~~~~~~~~~~

.. code-block:: java

   bot.motors().turnRight(2.0);

Curve left
~~~~~~~~~~

.. code-block:: java

   bot.motors().curveLeft(2.0, 0.5);

Curve right
~~~~~~~~~~~

.. code-block:: java

   bot.motors().curveRight(2.0, 0.5);

Stop
~~~~

.. code-block:: java

   bot.motors().stop();

Example in a behavior
---------------------

The motors API is usually used inside a robot behavior.

Example:

.. code-block:: java

   if (bot.sensors().front().isObjectDetected(350.0)) {
     bot.motors().turnLeft(2.0);
   } else {
     bot.motors().forward(2.0);
   }

This means:

* if an object is detected in front of the robot, the robot turns left;
* otherwise, the robot moves forward.

Important naming convention
---------------------------

The motors API depends on the names of the motors in the Webots robot.

The robot PROTO must contain motors named exactly:

.. code-block:: text

   wheel1
   wheel2
   wheel3
   wheel4

If one name is different, the corresponding motor may not be found.

For example, this is correct:

.. code-block:: text

   RotationalMotor {
     name "wheel1"
   }

But this is not compatible with the API:

.. code-block:: text

   RotationalMotor {
     name "front_left_wheel"
   }

Unless the Java code is modified accordingly.

Debugging
---------

If the robot does not move, check the following points:

* the motors are correctly named in the PROTO file;
* the controller is correctly compiled;
* the controller is correctly assigned to the robot;
* the wheels have a ``HingeJoint``;
* the motors are inside the ``device`` field of the ``HingeJoint``;
* the wheel ``Solid`` has physics;
* the ``Wheel`` objects are not linked to ``null`` motors.

You can use:

.. code-block:: java

   bot.motors().frontLeft().exists();

to check whether a wheel motor was found.

Summary
-------

The ``api.motors`` package provides a clean API to control the robot movement.

``Wheel`` controls one Webots motor.

``MotorGroup`` controls several wheels at the same time.

``DriveBase`` controls the full robot movement.

This package allows the rest of the project to use simple commands such as:

.. code-block:: java

   bot.motors().forward(2.0);
   bot.motors().turnLeft(2.0);
   bot.motors().stop();

instead of directly manipulating the four Webots motors.