################
Arm
################

*****************
Role of the class
*****************

The ``Arm`` class represents the robot arm and gripper. It groups together
the motors used to move the arm and open or close the gripper.

****************
Responsibilities
****************

- Retrieve the arm motor.
- Retrieve the two gripper motors.
- Retrieve the arm position sensor.
- Raise the arm.
- Lower the arm.
- Open the gripper.
- Close the gripper.

*************
Encapsulation
*************

The motors and the sensor are private: ``armMotor``,
``gripperLeftMotor``, ``gripperRightMotor`` and ``armSensor``.
The arm and gripper positions are stored in private constants.

*****************************
Relations with other classes
*****************************

``Arm`` is owned by ``TERBot``.
It depends on ``Motor``, ``PositionSensor`` and ``Supervisor``.

*********
Functions
*********

``Arm(Supervisor robot, int timeStep)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Constructor. It retrieves ``arm_motor``, ``gripper_left_motor``,
``gripper_right_motor`` and ``arm_sensor``.
If the position sensor exists, it is enabled.
The constructor then moves the arm to the raised position and opens the
gripper.

``lift()``
~~~~~~~~~~

Raises the arm by setting the arm motor to the ``ARM_UP`` position.

``lower()``
~~~~~~~~~~~

Lowers the arm by setting the arm motor to the ``ARM_DOWN`` position.

``openGripper()``
~~~~~~~~~~~~~~~~~

Opens the gripper by applying the open position to the left and right
gripper motors.

``closeGripper()``
~~~~~~~~~~~~~~~~~~

Closes the gripper by applying the closed position to the left and right
gripper motors.