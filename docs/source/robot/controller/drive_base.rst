################
DriveBase
################

*****************
Role of the class
*****************

The ``DriveBase`` class represents the robot drive base. It groups together
the four wheels of the robot and allows movement control using a left speed
and a right speed.

****************
Responsibilities
****************

- Create the four robot wheels.
- Associate Webots motors with ``Wheel`` objects.
- Apply a speed to the left wheels.
- Apply a speed to the right wheels.
- Stop the robot.

*************
Encapsulation
*************

The wheels are stored in private attributes: ``frontLeft``,
``frontRight``, ``rearLeft`` and ``rearRight``.

As a result, ``TERBot`` does not directly manipulate Webots motors.

**************************
Ownership and composition
**************************

``DriveBase`` owns four ``Wheel`` objects.

This relationship corresponds to composition: the drive base is composed
of wheels.

*****************************
Relations with other classes
*****************************

``DriveBase`` is owned by ``TERBot``.

It uses ``Wheel`` and depends on the Webots ``Supervisor`` to retrieve
motors by their name.

*********
Functions
*********

``DriveBase(Supervisor robot)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Class constructor.

It retrieves the ``wheel1``, ``wheel2``, ``wheel3`` and ``wheel4`` motors
from Webots, then creates the four corresponding ``Wheel`` objects.

``setSpeed(double leftSpeed, double rightSpeed)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Applies speeds to the wheels.

``leftSpeed`` is applied to the left wheels and ``rightSpeed`` to the
right wheels.

This method allows the robot to move forward, move backward or turn
depending on the difference between the two speeds.

``stop()``
~~~~~~~~~~

Stops the drive base by calling ``setSpeed(0.0, 0.0)``.