################
MathUtils
################

*****************
Role of the class
*****************

The ``MathUtils`` class is a utility class. It does not represent a physical
component of the robot. It groups together mathematical functions used by
several classes of the controller.

****************
Responsibilities
****************

- Normalize an angle.
- Retrieve the robot orientation.
- Compute a two-dimensional distance.

*************
Encapsulation
*************

``MathUtils`` has no internal state. Its constructor is private in order to
prevent the creation of ``MathUtils`` objects.

The methods are static and can be called directly from the class.

*****************************
Relations with other classes
*****************************

``MathUtils`` is used by ``TERBot`` and ``PuckManager``.

*********
Functions
*********

``normalizeAngle(double angle)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Normalizes an angle so that it remains between ``-PI`` and ``PI``.

This function is useful for comparing two orientations and computing a
proper angular error.

``getRobotYaw(Supervisor robot)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Returns the robot orientation angle on the horizontal plane.

The method retrieves the robot orientation matrix using
``getOrientation()``, then uses ``Math.atan2``.

``distance2D(double[] a, double[] b)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Computes the distance between two points on the horizontal plane using only
the ``x`` and ``y`` coordinates.

Formula used:

.. code-block:: text

   distance = sqrt((b.x - a.x)^2 + (b.y - a.y)^2)