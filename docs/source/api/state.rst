State API
=========

Overview
--------

The ``api.state`` package contains the different modes used by the robot behavior.

In this project, the robot uses a state machine to organize its actions.

Instead of writing all decisions in one large block, the robot switches between several states.

Each state represents one step of the robot mission.

For example, the robot can be:

* searching for a puck;
* avoiding an obstacle;
* approaching a puck;
* lowering the arm;
* closing the gripper;
* lifting the arm;
* moving to the drop zone;
* dropping the puck;
* backing up after the drop.

Package location
----------------

The state API is located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── state/
               └── RobotMode.java

Package declaration
-------------------

The file starts with:

.. code-block:: java

   package api.state;

This means that the file must be located in:

.. code-block:: text

   api/state/

RobotMode
---------

Overview
~~~~~~~~

``RobotMode`` is an enum.

An enum is a Java type used to define a fixed list of possible values.

Here, ``RobotMode`` defines all possible states of the robot.

Class code
~~~~~~~~~~

.. code-block:: java

   package api.state;

   public enum RobotMode {
     SEARCH,
     TOUCH_AVOID,
     APPROACH_PUCK,
     LOWER_ARM,
     CLOSE_GRIPPER,
     LIFT_ARM,
     GO_TO_DROP_ZONE,
     DROP_PUCK,
     LIFT_ARM_AFTER_DROP,
     BACK_AND_TURN_AFTER_DROP
   }

Why use an enum?
~~~~~~~~~~~~~~~~

Using an enum makes the robot behavior easier to read and safer.

Instead of writing states as text:

.. code-block:: java

   String mode = "SEARCH";

The code can use:

.. code-block:: java

   RobotMode mode = RobotMode.SEARCH;

This is better because Java can check that the value exists.

For example, with strings, a spelling mistake could create a bug:

.. code-block:: java

   mode = "SERACH";

This mistake would compile, but the robot behavior would not work correctly.

With an enum, this mistake is detected by Java.

List of robot modes
-------------------

The robot has the following modes:

.. list-table::
   :header-rows: 1

   * - Mode
     - Role
   * - ``SEARCH``
     - The robot searches for an available puck.
   * - ``TOUCH_AVOID``
     - The robot avoids an obstacle after a physical contact.
   * - ``APPROACH_PUCK``
     - The robot moves toward a detected puck.
   * - ``LOWER_ARM``
     - The robot lowers its arm before grabbing the puck.
   * - ``CLOSE_GRIPPER``
     - The robot closes the gripper around the puck.
   * - ``LIFT_ARM``
     - The robot lifts the arm after grabbing the puck.
   * - ``GO_TO_DROP_ZONE``
     - The robot moves toward the drop zone.
   * - ``DROP_PUCK``
     - The robot releases the puck in the drop zone.
   * - ``LIFT_ARM_AFTER_DROP``
     - The robot lifts the arm again after dropping the puck.
   * - ``BACK_AND_TURN_AFTER_DROP``
     - The robot moves backward and turns to leave the drop zone.

Detailed mode explanation
-------------------------

SEARCH
~~~~~~

.. code-block:: java

   SEARCH

The ``SEARCH`` mode is the default mode of the robot.

In this state, the robot looks for a puck in the arena.

The robot can use:

* distance sensors;
* color detection;
* puck position through the ``PuckManager``;
* obstacle avoidance logic.

Typical behavior:

.. code-block:: text

   If a puck is detected:
       switch to APPROACH_PUCK
   Else:
       continue searching

TOUCH_AVOID
~~~~~~~~~~~

.. code-block:: java

   TOUCH_AVOID

The ``TOUCH_AVOID`` mode is used when the robot touches an obstacle but is not carrying a puck.

For example, this can happen when the robot touches:

* a wall;
* the drop zone without carrying a puck;
* an obstacle.

Typical behavior:

.. code-block:: text

   Move backward for a short time
   Turn for a short time
   Return to SEARCH

This prevents the robot from staying blocked against an obstacle.

APPROACH_PUCK
~~~~~~~~~~~~~

.. code-block:: java

   APPROACH_PUCK

The ``APPROACH_PUCK`` mode is used when a puck has been detected.

In this state, the robot moves toward the selected puck.

The robot can correct its direction using:

* the puck position;
* the robot orientation;
* the angle error between the robot and the puck.

Typical behavior:

.. code-block:: text

   Compute direction to puck
   Adjust wheel speeds
   Move toward the puck
   If contact is detected:
       switch to LOWER_ARM

LOWER_ARM
~~~~~~~~~

.. code-block:: java

   LOWER_ARM

The ``LOWER_ARM`` mode is used before grabbing the puck.

The robot stops moving and lowers its arm.

Typical behavior:

.. code-block:: java

   bot.motors().stop();
   bot.arm().lower();

This action usually needs to last several simulation steps.  
For this reason, it can be managed using a ``TimedTask``.

CLOSE_GRIPPER
~~~~~~~~~~~~~

.. code-block:: java

   CLOSE_GRIPPER

The ``CLOSE_GRIPPER`` mode is used to close the gripper around the puck.

Typical behavior:

.. code-block:: java

   bot.gripper().close();

After this step, the robot can consider that the puck is collected.

LIFT_ARM
~~~~~~~~

.. code-block:: java

   LIFT_ARM

The ``LIFT_ARM`` mode is used after the gripper has closed.

The robot lifts the arm to carry the puck.

Typical behavior:

.. code-block:: java

   bot.arm().lift();

After this state, the robot can move toward the drop zone.

GO_TO_DROP_ZONE
~~~~~~~~~~~~~~~

.. code-block:: java

   GO_TO_DROP_ZONE

The ``GO_TO_DROP_ZONE`` mode is used when the robot is carrying a puck.

In this state, the robot moves toward the drop zone.

The behavior can use:

* the robot position;
* the drop zone position;
* the robot orientation;
* angle correction.

Typical behavior:

.. code-block:: text

   Compute direction to the drop zone
   Turn if needed
   Move forward
   If the drop zone is touched:
       switch to DROP_PUCK

DROP_PUCK
~~~~~~~~~

.. code-block:: java

   DROP_PUCK

The ``DROP_PUCK`` mode is used when the robot reaches the drop zone.

In this state, the robot releases the puck.

Typical behavior:

.. code-block:: java

   bot.motors().stop();
   bot.arm().lower();
   bot.gripper().open();

The ``PuckManager`` can also mark the puck as delivered.

LIFT_ARM_AFTER_DROP
~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   LIFT_ARM_AFTER_DROP

The ``LIFT_ARM_AFTER_DROP`` mode is used after the puck has been released.

The robot lifts its arm again to avoid dragging the gripper on the ground.

Typical behavior:

.. code-block:: java

   bot.arm().lift();

After this step, the robot prepares to leave the drop zone.

BACK_AND_TURN_AFTER_DROP
~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   BACK_AND_TURN_AFTER_DROP

The ``BACK_AND_TURN_AFTER_DROP`` mode is used after the puck has been dropped.

The robot moves backward and turns to avoid staying blocked against the drop zone.

Typical behavior:

.. code-block:: text

   Move backward
   Turn
   Return to SEARCH

This allows the robot to continue searching for other pucks.

How the state machine works
---------------------------

A state machine works by storing the current state of the robot.

Example:

.. code-block:: java

   private RobotMode mode = RobotMode.SEARCH;

Then the behavior checks the current mode and executes the corresponding logic.

Example:

.. code-block:: java

   switch (mode) {
     case SEARCH:
       // Search for a puck
       break;

     case APPROACH_PUCK:
       // Move toward the puck
       break;

     case LOWER_ARM:
       // Lower the arm
       break;

     default:
       break;
   }

Each state can decide when to switch to another state.

Example:

.. code-block:: java

   mode = RobotMode.APPROACH_PUCK;

Example mission sequence
------------------------

A normal successful mission can follow this sequence:

.. code-block:: text

   SEARCH
      |
      v
   APPROACH_PUCK
      |
      v
   LOWER_ARM
      |
      v
   CLOSE_GRIPPER
      |
      v
   LIFT_ARM
      |
      v
   GO_TO_DROP_ZONE
      |
      v
   DROP_PUCK
      |
      v
   LIFT_ARM_AFTER_DROP
      |
      v
   BACK_AND_TURN_AFTER_DROP
      |
      v
   SEARCH

This sequence represents one full collection cycle.

Obstacle sequence
-----------------

If the robot touches an obstacle while searching, it can use this sequence:

.. code-block:: text

   SEARCH
      |
      v
   TOUCH_AVOID
      |
      v
   SEARCH

This allows the robot to recover from a collision and continue searching.

Why this is useful
------------------

Using robot modes makes the behavior easier to understand.

Without modes, the robot behavior could become a large block of nested ``if`` statements.

With modes, each step is separated clearly:

.. code-block:: text

   SEARCH handles searching.
   APPROACH_PUCK handles movement to a puck.
   LOWER_ARM handles lowering the arm.
   CLOSE_GRIPPER handles closing the gripper.
   GO_TO_DROP_ZONE handles navigation to the base.
   DROP_PUCK handles releasing the puck.

This makes the code:

* easier to read;
* easier to debug;
* easier to modify;
* easier to explain during practical sessions.

Debugging with RobotMode
------------------------

The current mode can be printed in the console to understand what the robot is doing.

Example:

.. code-block:: java

   System.out.println("Current mode: " + mode);

This is useful when the robot does not behave as expected.

For example:

* if the robot never leaves ``SEARCH``, the puck detection condition may be wrong;
* if the robot stays in ``APPROACH_PUCK``, the contact detection may not work;
* if the robot stays in ``GO_TO_DROP_ZONE``, the drop zone detection may not work;
* if the robot repeats ``TOUCH_AVOID``, it may be stuck against an obstacle.

Summary
-------

The ``api.state`` package contains the ``RobotMode`` enum.

This enum defines all possible high-level states of the robot.

The robot uses these states to organize its behavior as a state machine.

This makes the controller easier to read and makes the full mission easier to understand.