Utils API
=========

Overview
--------

The ``api.utils`` package contains utility classes used by several parts of the robot API.

A utility class is a class that does not represent a physical part of the robot.  
Instead, it provides helper methods that can be reused in different places.

In this project, the main utility class is:

.. code-block:: text

   MathUtils.java

This class contains mathematical helper methods used for navigation, angle correction, distance computation, and value limitation.

The methods in ``MathUtils`` are static.  
This means they can be used without creating a ``MathUtils`` object.

Example:

.. code-block:: java

   double angle = MathUtils.normalizeAngle(targetAngle - robotAngle);

Package location
----------------

The utils API is located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── utils/
               └── MathUtils.java

Package declaration
-------------------

The file starts with:

.. code-block:: java

   package api.utils;

This means that the file must be located in:

.. code-block:: text

   api/utils/

If the package declaration and the folder path do not match, Java will not compile the project correctly.

MathUtils
---------

Overview
~~~~~~~~

``MathUtils`` is a utility class.

It provides common mathematical functions used by the robot.

These functions are useful for:

* keeping angles in a valid range;
* getting the robot orientation;
* computing the distance between two positions;
* limiting a value between a minimum and a maximum.

Class code
~~~~~~~~~~

.. code-block:: java

   package api.utils;

   import com.cyberbotics.webots.controller.Supervisor;

   public class MathUtils {
     private MathUtils() {}

     public static double normalizeAngle(double angle) {
       while (angle > Math.PI) {
         angle -= 2.0 * Math.PI;
       }

       while (angle < -Math.PI) {
         angle += 2.0 * Math.PI;
       }

       return angle;
     }

     public static double getRobotYaw(Supervisor robot) {
       double[] orientation = robot.getSelf().getOrientation();
       return Math.atan2(orientation[3], orientation[0]);
     }

     public static double distance2D(double[] a, double[] b) {
       double dx = b[0] - a[0];
       double dy = b[1] - a[1];

       return Math.sqrt(dx * dx + dy * dy);
     }

     public static double clamp(double value, double min, double max) {
       if (value < min) {
         return min;
       }

       if (value > max) {
         return max;
       }

       return value;
     }
   }

Why the constructor is private
------------------------------

The class contains a private constructor:

.. code-block:: java

   private MathUtils() {}

This means that no object of type ``MathUtils`` can be created.

For example, this is not allowed:

.. code-block:: java

   MathUtils utils = new MathUtils();

This is intentional.

``MathUtils`` only contains static methods, so it is not necessary to create an object.

The methods can be called directly with the class name:

.. code-block:: java

   MathUtils.distance2D(a, b);

This makes the class simple and clear.

Static methods
--------------

All methods in ``MathUtils`` are static.

A static method belongs to the class itself, not to an object.

Example:

.. code-block:: java

   double result = MathUtils.clamp(1.5, 0.0, 1.0);

Here, no ``MathUtils`` object is created.

This is useful for utility functions that do not need to store any internal state.

normalizeAngle
--------------

.. code-block:: java

   public static double normalizeAngle(double angle)

This method normalizes an angle.

It keeps the angle between:

.. code-block:: text

   -PI and PI

In radians, this corresponds approximately to:

.. code-block:: text

   -3.14159 and 3.14159

Why normalize an angle?
~~~~~~~~~~~~~~~~~~~~~~~

When the robot turns toward a target, the controller often computes the difference between two angles:

.. code-block:: java

   double angleError = targetAngle - robotAngle;

However, angles can sometimes become greater than ``PI`` or lower than ``-PI``.

This can cause the robot to turn in the wrong direction or make a very large unnecessary rotation.

The goal of ``normalizeAngle`` is to keep the angle error in a predictable range.

Code explanation
~~~~~~~~~~~~~~~~

.. code-block:: java

   while (angle > Math.PI) {
     angle -= 2.0 * Math.PI;
   }

If the angle is greater than ``PI``, the method subtracts a full turn.

A full turn is:

.. code-block:: java

   2.0 * Math.PI

Then:

.. code-block:: java

   while (angle < -Math.PI) {
     angle += 2.0 * Math.PI;
   }

If the angle is lower than ``-PI``, the method adds a full turn.

Finally, the corrected angle is returned:

.. code-block:: java

   return angle;

Example
~~~~~~~

If the angle is too large:

.. code-block:: java

   double angle = MathUtils.normalizeAngle(4.0);

The returned value will be brought back into the range ``[-PI, PI]``.

Typical use in the robot
~~~~~~~~~~~~~~~~~~~~~~~~

This method is used when the robot needs to face a puck or the drop zone.

Example:

.. code-block:: java

   double targetAngle = Math.atan2(dy, dx);
   double robotAngle = MathUtils.getRobotYaw(robot);
   double angleError = MathUtils.normalizeAngle(targetAngle - robotAngle);

If ``angleError`` is positive, the robot can turn left.

If ``angleError`` is negative, the robot can turn right.

This allows the robot to correct its direction.

getRobotYaw
-----------

.. code-block:: java

   public static double getRobotYaw(Supervisor robot)

This method returns the orientation of the robot on the horizontal plane.

This orientation is usually called the yaw angle.

What is yaw?
~~~~~~~~~~~~

In robotics, the yaw angle represents the direction the robot is facing.

For a mobile robot moving on the ground:

* yaw tells where the robot is looking;
* yaw is used to turn toward a target;
* yaw is used to compute angle errors.

How it works
~~~~~~~~~~~~

The method first retrieves the orientation matrix of the robot:

.. code-block:: java

   double[] orientation = robot.getSelf().getOrientation();

In Webots, ``getOrientation`` returns a 3x3 orientation matrix stored as an array of 9 values.

Then the yaw angle is computed with:

.. code-block:: java

   return Math.atan2(orientation[3], orientation[0]);

The ``atan2`` function returns an angle in radians.

Typical use
~~~~~~~~~~~

This method is used when the robot needs to compare its current direction with the direction of a target.

Example with a puck:

.. code-block:: java

   double robotYaw = MathUtils.getRobotYaw(robot);

Example in a behavior:

.. code-block:: java

   double targetAngle = Math.atan2(dy, dx);
   double robotAngle = MathUtils.getRobotYaw(bot.supervisor());
   double angleError = MathUtils.normalizeAngle(targetAngle - robotAngle);

This angle error can then be used to decide whether the robot should turn left or right.

distance2D
----------

.. code-block:: java

   public static double distance2D(double[] a, double[] b)

This method computes the distance between two positions on the ground.

It only uses the ``x`` and ``y`` coordinates.

The ``z`` coordinate is ignored.

Why ignore z?
~~~~~~~~~~~~~

The robot moves on a flat surface.

For navigation, the robot usually only needs to know the horizontal distance between two points.

Therefore, only these coordinates are used:

.. code-block:: text

   x
   y

The height coordinate ``z`` is not important for navigation on the floor.

Code explanation
~~~~~~~~~~~~~~~~

.. code-block:: java

   double dx = b[0] - a[0];
   double dy = b[1] - a[1];

The method computes the difference between both points on the x-axis and y-axis.

Then it computes the Euclidean distance:

.. code-block:: java

   return Math.sqrt(dx * dx + dy * dy);

This corresponds to the formula:

.. code-block:: text

   distance = sqrt(dx² + dy²)

Example
~~~~~~~

.. code-block:: java

   double[] robotPosition = bot.supervisor().getSelf().getPosition();
   double[] puckPosition = bot.pucks().getPuckPosition(0);

   double distance = MathUtils.distance2D(robotPosition, puckPosition);

This returns the 2D distance between the robot and the puck.

Typical use in the robot
~~~~~~~~~~~~~~~~~~~~~~~~

This method is used by ``PuckManager`` to find the nearest puck.

Example:

.. code-block:: java

   double distance = MathUtils.distance2D(robotPosition, puckNode.getPosition());

It can also be used to check if the robot is close enough to a target.

clamp
-----

.. code-block:: java

   public static double clamp(double value, double min, double max)

This method limits a value between a minimum and a maximum.

If the value is lower than the minimum, the method returns the minimum.

If the value is greater than the maximum, the method returns the maximum.

Otherwise, it returns the original value.

Code explanation
~~~~~~~~~~~~~~~~

.. code-block:: java

   if (value < min) {
     return min;
   }

If ``value`` is too small, the method returns ``min``.

.. code-block:: java

   if (value > max) {
     return max;
   }

If ``value`` is too large, the method returns ``max``.

.. code-block:: java

   return value;

If the value is already inside the valid range, it is returned unchanged.

Example
~~~~~~~

.. code-block:: java

   double result = MathUtils.clamp(1.5, 0.0, 1.0);

The result will be:

.. code-block:: text

   1.0

Because ``1.5`` is greater than the maximum value ``1.0``.

Another example:

.. code-block:: java

   double result = MathUtils.clamp(-0.5, 0.0, 1.0);

The result will be:

.. code-block:: text

   0.0

Because ``-0.5`` is lower than the minimum value ``0.0``.

Typical use in the robot
~~~~~~~~~~~~~~~~~~~~~~~~

This method is used in the motors API for curved movements.

Example:

.. code-block:: java

   MathUtils.clamp(factor, 0.0, 1.0)

The curve factor must stay between ``0.0`` and ``1.0``.

This prevents invalid values from producing unexpected wheel speeds.

Example in ``DriveBase``:

.. code-block:: java

   public void curveLeft(double speed, double factor) {
     double v = Math.abs(speed);
     setSpeed(v * MathUtils.clamp(factor, 0.0, 1.0), v);
   }

If the user gives a factor greater than ``1.0``, it will be limited to ``1.0``.

If the user gives a factor lower than ``0.0``, it will be limited to ``0.0``.

This makes the method safer.

How MathUtils is used in the project
------------------------------------

``MathUtils`` is used by several API modules.

In ``PuckManager``
~~~~~~~~~~~~~~~~~~

``PuckManager`` uses ``MathUtils`` to compute distances and angles to pucks.

Example:

.. code-block:: java

   double distance = MathUtils.distance2D(robotPosition, puckNodes[i].getPosition());

It also uses:

.. code-block:: java

   double robotYaw = MathUtils.getRobotYaw(robot);

and:

.. code-block:: java

   double angleError = MathUtils.normalizeAngle(targetAngle - robotYaw);

This helps the robot decide which puck to target.

In ``DriveBase``
~~~~~~~~~~~~~~~~

``DriveBase`` uses ``clamp`` to limit curve factors.

Example:

.. code-block:: java

   MathUtils.clamp(factor, 0.0, 1.0)

This prevents invalid values from being used for curved movement.

In robot behaviors
~~~~~~~~~~~~~~~~~~

A behavior can use ``MathUtils`` to compute how the robot should turn.

Example:

.. code-block:: java

   double angleError = bot.pucks().getAngleErrorToPuck(currentPuckIndex);

   if (angleError > 0) {
     bot.motors().turnLeft(2.0);
   } else {
     bot.motors().turnRight(2.0);
   }

Even if ``MathUtils`` is not called directly here, it is used inside ``PuckManager``.

Summary of methods
------------------

.. list-table::
   :header-rows: 1

   * - Method
     - Role
   * - ``normalizeAngle(angle)``
     - Keeps an angle between ``-PI`` and ``PI``.
   * - ``getRobotYaw(robot)``
     - Returns the direction the robot is facing.
   * - ``distance2D(a, b)``
     - Computes the distance between two positions using only x and y.
   * - ``clamp(value, min, max)``
     - Limits a value between a minimum and a maximum.

Debugging
---------

If the robot turns in the wrong direction, check:

* the value returned by ``getRobotYaw``;
* the target angle computed with ``Math.atan2``;
* the angle error after ``normalizeAngle``;
* the sign of the angle error.

Example debug print:

.. code-block:: java

   System.out.println("angleError = " + angleError);

If the robot selects a strange puck, check:

* the distance computed by ``distance2D``;
* the angle error to each puck;
* the score used by ``findBestAvailablePuck``.

If a curved movement is too strong or too weak, check:

* the curve factor;
* the result of ``clamp``;
* the left and right wheel speeds.

Important notes
---------------

Angles are in radians
~~~~~~~~~~~~~~~~~~~~~

All angles used by ``MathUtils`` are in radians, not degrees.

For reference:

.. code-block:: text

   PI radians      = 180 degrees
   PI / 2 radians  = 90 degrees
   0 radians       = 0 degrees

This is important because Java methods such as ``Math.atan2`` also return radians.

Positions are arrays
~~~~~~~~~~~~~~~~~~~~

Webots positions are usually stored as arrays:

.. code-block:: java

   double[] position = node.getPosition();

The coordinates are accessed like this:

.. code-block:: text

   position[0] -> x
   position[1] -> y
   position[2] -> z

The ``distance2D`` method only uses:

.. code-block:: text

   position[0]
   position[1]

because the robot moves on the ground.

Limitations
-----------

``MathUtils`` only contains simple helper methods.

It does not perform advanced path planning.

For example, it does not compute:

* obstacle-free paths;
* shortest paths around walls;
* full navigation maps;
* trajectory optimization.

Its role is to provide basic mathematical tools used by the robot API.

Summary
-------

The ``api.utils`` package contains helper methods used by the rest of the API.

The ``MathUtils`` class provides four main methods:

.. code-block:: java

   normalizeAngle(angle)
   getRobotYaw(robot)
   distance2D(a, b)
   clamp(value, min, max)

These methods make the robot behavior easier to write and easier to understand.

They are used for navigation, puck selection, angle correction, curved movement, and safety checks.