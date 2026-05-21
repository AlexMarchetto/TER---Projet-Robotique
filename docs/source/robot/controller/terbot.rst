################
TERBot
################

*****************
Role of the class
*****************

The ``TERBot`` class is the central class of the controller.

It represents the robot from a software perspective and coordinates all
robot subsystems.

The main program loop and the robot state machine are implemented in this
class.

****************
Responsibilities
****************

- Manage the main robot loop.
- Read information from the sensors.
- Decide which action to perform according to the current state.
- Control the wheels through ``DriveBase``.
- Control the arm and gripper through ``Arm``.
- Manage pucks through ``PuckManager``.
- Manage transitions between the different robot states.
- Apply searching, approaching, avoidance, pickup and drop behaviors.

*************
Encapsulation
*************

The ``TERBot`` class encapsulates the global robot logic.

Its main attributes are private: ``supervisor``, ``driveBase``,
``sensors``, ``arm``, ``puckManager``, ``mode``,
``currentPuckIndex`` and ``puckAttached``.

The only important public method is ``run()``.

The other methods are private because they correspond to internal robot
behaviors.

**************************
Ownership and composition
**************************

``TERBot`` owns a ``DriveBase``, a ``RobotSensors``, an ``Arm`` and a
``PuckManager``.

This relationship corresponds to composition.

The software robot is composed of several specialized subsystems.

*************
State machine
*************

The main states are:

``SEARCH``,
``APPROACH_PUCK``,
``LOWER_ARM``,
``CLOSE_GRIPPER``,
``LIFT_ARM``,
``GO_TO_DROP_ZONE``,
``DROP_PUCK``,
``TOUCH_AVOID``,
``LIFT_ARM_AFTER_DROP``
and
``BACK_AND_TURN_AFTER_DROP``.

*********
Functions
*********

``TERBot(Supervisor supervisor)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Class constructor.

It initializes the software robot using the Webots ``Supervisor``.

It stores the ``Supervisor``, retrieves the Webots time step, creates
``DriveBase``, ``RobotSensors``, ``Arm`` and ``PuckManager``, then
defines the list of pucks to manage.

``run()``
~~~~~~~~~

Starts the main robot loop.

This method calls ``supervisor.step(timeStep)`` at each iteration.

As long as the simulation is running, the robot continues executing its
logic.

If a puck is attached, its position is updated so that it follows the
robot, then ``update()`` is called.

``update()``
~~~~~~~~~~~~

Updates the robot behavior at each simulation step.

This method reads the touch sensor state, handles contacts, executes the
behavior corresponding to the current mode, applies wheel speeds,
updates the previous touch state and displays debug information.

``handleContact(boolean touched)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Handles contacts detected by the front sensor.

The method distinguishes between contact with a nearby puck and contact
with an obstacle.

If the contact corresponds to a puck, the robot switches to
``LOWER_ARM`` mode.

Otherwise, it switches to ``TOUCH_AVOID`` mode.

``updateSearch()``
~~~~~~~~~~~~~~~~~~

Handles the puck search behavior.

The robot moves forward, monitors the side and front sensors, avoids
walls and searches for the nearest available puck.

If a puck is detected, the robot switches to ``APPROACH_PUCK`` mode.

The method returns the left and right wheel speeds.

``updateTouchAvoid()``
~~~~~~~~~~~~~~~~~~~~~~

Handles obstacle avoidance after contact with an obstacle.

The robot moves backward and then turns.

Once the sequence is completed, it returns to ``SEARCH`` mode.

``updateApproachPuck()``
~~~~~~~~~~~~~~~~~~~~~~~~

Handles the approach toward the targeted puck.

The method computes the direction toward the puck, the current robot
orientation and the angular error.

It then applies a trajectory correction.

If the puck is lost or if the approach lasts too long, the robot
returns to ``SEARCH`` mode.

``updateGoToDropZone(boolean touched)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Handles movement toward the drop zone.

The method orients the robot toward the drop position and then moves it
forward.

If the robot reaches the drop zone while carrying an attached puck, it
switches to ``DROP_PUCK`` mode.

``updateBackAndTurnAfterDrop()``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Handles the post-drop behavior.

The robot moves backward and then turns before returning to
``SEARCH`` mode.

``resetSearch()``
~~~~~~~~~~~~~~~~~

Resets the internal robot state in order to return to search mode.

It resets the mode, counters, current puck and detection indicators.

``printDebug(boolean touched)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Displays useful debug information in the console:

- current mode;
- sensor values;
- average color;
- touch sensor state;
- attached puck;
- current puck;
- blocking counter.