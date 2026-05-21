################
Wheel
################

*****************
Role of the class
*****************

The ``Wheel`` class represents a motorized wheel of the robot. It
encapsulates a ``Motor`` object provided by Webots.

****************
Responsibilities
****************

- Store a Webots motor.
- Configure the motor for continuous rotation.
- Apply a speed to the wheel.
- Stop the wheel.

*************
Encapsulation
*************

The class encapsulates the Webots ``Motor`` object in a private attribute:

.. code-block:: java

   private final Motor motor;

The other classes do not directly manipulate the Webots motor. They use
the public methods of ``Wheel``.

*****************************
Relations with other classes
*****************************

``Wheel`` is used by ``DriveBase``. ``DriveBase`` owns four ``Wheel``
objects. ``Wheel`` also depends on the Webots ``Motor`` class.

*********
Functions
*********

``Wheel(Motor motor)``
~~~~~~~~~~~~~~~~~~~~~~

Class constructor. It receives a Webots ``Motor`` object and stores it.
If the motor exists, it is configured for continuous rotation using
``setPosition(Double.POSITIVE_INFINITY)`` and its speed is initialized to
``0.0``.

``setVelocity(double velocity)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Applies a speed to the wheel. The method checks that the motor is not
``null`` before calling ``setVelocity``.

``stop()``
~~~~~~~~~~

Stops the wheel by calling ``setVelocity(0.0)``.