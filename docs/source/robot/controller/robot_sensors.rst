################
RobotSensors
################

*****************
Role of the class
*****************

The ``RobotSensors`` class groups together all sensors used by the robot.

It centralizes access to Webots sensors so that ``TERBot`` does not directly
manipulate ``DistanceSensor``, ``Camera`` or ``TouchSensor`` objects.

****************
Responsibilities
****************

- Retrieve Webots sensors.
- Enable sensors using the simulation time step.
- Read distances on the left, right and front sides.
- Read the state of the touch sensor.
- Read the average color detected by the camera.
- Detect whether the observed color is red.

*************
Encapsulation
*************

The sensors are stored in private attributes: ``dsRight``, ``dsLeft``,
``dsFront``, ``colorSensor`` and ``touchFront``.

*****************************
Relations with other classes
*****************************

``RobotSensors`` is owned by ``TERBot``.

It depends on ``DistanceSensor``, ``Camera``, ``TouchSensor`` and
``Supervisor``.

*********
Functions
*********

``RobotSensors(Supervisor robot, int timeStep)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Constructor.

It retrieves the ``ds_right``, ``ds_left``, ``ds_front``,
``color_sensor`` and ``touch_front`` sensors.

Each detected sensor is enabled using ``enable(timeStep)``.

``getRightDistance()``
~~~~~~~~~~~~~~~~~~~~~~

Returns the value of the right distance sensor.

If the sensor does not exist, it returns ``0.0``.

``getLeftDistance()``
~~~~~~~~~~~~~~~~~~~~~

Returns the value of the left distance sensor.

If the sensor does not exist, it returns ``0.0``.

``getFrontDistance()``
~~~~~~~~~~~~~~~~~~~~~~

Returns the value of the front distance sensor.

This sensor is used to detect an object or a puck in front of the robot.

``isTouched()``
~~~~~~~~~~~~~~~

Returns ``true`` if the front touch sensor detects a collision.

``getAverageColor()``
~~~~~~~~~~~~~~~~~~~~~

Computes the average color detected by the camera.

The method returns an array ``{red, green, blue}``.

If the camera does not exist or if the image is invalid, it returns
``{0, 0, 0}``.

``isRedDetected()``
~~~~~~~~~~~~~~~~~~~

Indicates whether the detected average color corresponds to red.

The method checks that the red component is greater than 150, the green
component is lower than 100, and the blue component is lower than 100.