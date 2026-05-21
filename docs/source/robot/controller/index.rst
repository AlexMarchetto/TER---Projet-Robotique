################
Robot Controller
################

This section documents the software architecture of the robot Webots
controller.

The objective of this refactoring is to replace a single file containing
all the logic with several specialized classes. Each class has a specific
responsibility, making the code easier to read, maintain and extend.

*****************
General Principle
*****************

The controller is mainly based on composition.

The ``FourWheelsCollisionAvoidance`` class is the entry point of the
program. It creates a ``TERBot`` object, which represents the software
robot.

``TERBot`` then owns several subsystems:

- ``DriveBase`` for movement.
- ``RobotSensors`` for sensors.
- ``Arm`` for the arm and gripper.
- ``PuckManager`` for puck management.
- ``RobotMode`` for robot states.
- ``MathUtils`` for utility mathematical functions.

There is no business inheritance between the project classes. The model
mainly relies on ownership relationships, also called composition
relationships.

.. toctree::
   :maxdepth: 1
   :caption: Classes

   four_wheels_collision_avoidance
   terbot
   robot_mode
   wheel
   drive_base
   robot_sensors
   arm
   puck_manager
   math_utils
   uml