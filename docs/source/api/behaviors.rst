Behaviors API
=============

Overview
--------

The ``api.behavior`` package contains the classes that define the robot behavior.

A behavior describes what the robot does during the simulation.

For example, a behavior can make the robot:

* avoid obstacles;
* search for pucks;
* approach a puck;
* grab a puck;
* go to a drop zone;
* drop the puck;
* stop when the mission is finished.

The behavior package is the part of the controller where the robot becomes autonomous.

Package location
----------------

The files are located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── behavior/
               ├── RobotBehavior.java
               ├── SimpleAvoidObstacleBehavior.java
               └── CollectPucksBehavior.java

Package declaration
-------------------

Each file starts with:

.. code-block:: java

   package api.behavior;

This means that the files must be placed in:

.. code-block:: text

   api/behavior/

If the folder and package name do not match, Java will not compile the project.

Main idea
---------

The behavior package separates the robot control logic from the technical API.

The other packages provide tools:

* ``api.motors`` moves the robot;
* ``api.sensors`` reads the sensors;
* ``api.actuators`` controls the arm and gripper;
* ``api.world`` manages the pucks;
* ``api.state`` defines the robot modes.

The behavior package uses all these tools to decide what the robot should do.

The global structure is:

.. code-block:: text

   TERBot
      |
      └── RobotBehavior
             |
             ├── SimpleAvoidObstacleBehavior
             └── CollectPucksBehavior

RobotBehavior
-------------

Role of the interface
~~~~~~~~~~~~~~~~~~~~~

``RobotBehavior`` is an interface.

It defines the common structure for all robot behaviors.

A behavior must provide an ``update`` method.

It can also provide an ``init`` method.

Source code
~~~~~~~~~~~

.. code-block:: java

   package api.behavior;

   public interface RobotBehavior {
     default void init() {}
     void update();
   }

Why use an interface?
~~~~~~~~~~~~~~~~~~~~~

The interface allows the project to use different behaviors with the same robot.

For example, the robot can use:

* a simple obstacle avoidance behavior;
* a complete puck collection behavior;
* a test behavior for sensors;
* a test behavior for motors.

All these behaviors can be used by ``TERBot`` because they follow the same interface.

Example:

.. code-block:: java

   TERBot robot = new TERBot();

   robot.setBehavior(new CollectPucksBehavior(robot));

   robot.run();

The ``TERBot`` class does not need to know the exact behavior class.

It only knows that the behavior has:

* an ``init`` method;
* an ``update`` method.

init
~~~~

.. code-block:: java

   default void init() {}

The ``init`` method is called once before the simulation loop starts.

It is optional because it has a default empty implementation.

A behavior can use this method to prepare the robot.

Example:

.. code-block:: java

   @Override
   public void init() {
     robot.arm().lift();
     robot.gripper().open();
   }

update
~~~~~~

.. code-block:: java

   void update();

The ``update`` method is called at each simulation step.

This is where the robot makes decisions.

For example, the robot can:

* read sensors;
* move forward;
* turn;
* stop;
* lower the arm;
* close the gripper;
* change state.

This method is required for every behavior.

SimpleAvoidObstacleBehavior
---------------------------

Role of the class
~~~~~~~~~~~~~~~~~

``SimpleAvoidObstacleBehavior`` is a simple behavior used to test the robot movement and sensors.

It makes the robot move forward until it detects an obstacle.

If an obstacle is detected, the robot turns to avoid it.

This behavior is useful for early tests because it checks that:

* the motors work;
* the distance sensors work;
* the robot can react to obstacles.

Source code
~~~~~~~~~~~

.. code-block:: java

   package api.behavior;

   import api.core.TERBot;

   public class SimpleAvoidObstacleBehavior implements RobotBehavior {
     private final TERBot robot;

     private static final double SPEED = 4.0;
     private static final double TURN_SPEED = 3.0;
     private static final double FRONT_THRESHOLD = 350.0;
     private static final double SIDE_THRESHOLD = 900.0;

     public SimpleAvoidObstacleBehavior(TERBot robot) {
       this.robot = robot;
     }

     @Override
     public void init() {
       robot.arm().lift();
       robot.gripper().open();
     }

     @Override
     public void update() {
       robot.sensors().update();

       if (robot.sensors().frontDetectsObject(FRONT_THRESHOLD)) {
         robot.motors().turnLeft(TURN_SPEED);
       } else if (robot.sensors().leftDetectsObject(SIDE_THRESHOLD)) {
         robot.motors().turnRight(TURN_SPEED);
       } else if (robot.sensors().rightDetectsObject(SIDE_THRESHOLD)) {
         robot.motors().turnLeft(TURN_SPEED);
       } else {
         robot.motors().forward(SPEED);
       }
     }
   }

Attributes and constants
~~~~~~~~~~~~~~~~~~~~~~~~

The class stores the robot:

.. code-block:: java

   private final TERBot robot;

This gives the behavior access to:

* motors;
* sensors;
* arm;
* gripper.

The class also defines constants:

.. code-block:: java

   private static final double SPEED = 4.0;
   private static final double TURN_SPEED = 3.0;
   private static final double FRONT_THRESHOLD = 350.0;
   private static final double SIDE_THRESHOLD = 900.0;

``SPEED`` is used when the robot moves forward.

``TURN_SPEED`` is used when the robot turns.

``FRONT_THRESHOLD`` is used for the front distance sensor.

``SIDE_THRESHOLD`` is used for the left and right distance sensors.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public SimpleAvoidObstacleBehavior(TERBot robot) {
     this.robot = robot;
   }

The constructor receives the ``TERBot`` object.

This allows the behavior to control the robot.

init
~~~~

.. code-block:: java

   @Override
   public void init() {
     robot.arm().lift();
     robot.gripper().open();
   }

At the beginning of the simulation:

* the arm is lifted;
* the gripper is opened.

This avoids dragging the gripper on the ground while testing movement.

update
~~~~~~

.. code-block:: java

   @Override
   public void update() {
     robot.sensors().update();

     if (robot.sensors().frontDetectsObject(FRONT_THRESHOLD)) {
       robot.motors().turnLeft(TURN_SPEED);
     } else if (robot.sensors().leftDetectsObject(SIDE_THRESHOLD)) {
       robot.motors().turnRight(TURN_SPEED);
     } else if (robot.sensors().rightDetectsObject(SIDE_THRESHOLD)) {
       robot.motors().turnLeft(TURN_SPEED);
     } else {
       robot.motors().forward(SPEED);
     }
   }

At each simulation step, the robot:

1. updates its sensors;
2. checks if something is in front;
3. checks if something is on the left;
4. checks if something is on the right;
5. moves forward if no obstacle is detected.

The logic can be summarized as:

.. code-block:: text

   If an obstacle is in front:
       turn left
   Else if an obstacle is on the left:
       turn right
   Else if an obstacle is on the right:
       turn left
   Else:
       move forward

This behavior is simple, but it is useful to verify that the robot can react to the environment.

CollectPucksBehavior
--------------------

Role of the class
~~~~~~~~~~~~~~~~~

``CollectPucksBehavior`` is the main behavior of the project.

It controls the complete mission of the robot.

The mission is:

.. code-block:: text

   1. Search for a puck.
   2. Move toward the puck.
   3. Touch and grab the puck.
   4. Lift the puck.
   5. Go to the nearest drop zone.
   6. Drop the puck behind a white line.
   7. Move away from the drop zone.
   8. Repeat until all pucks are delivered.
   9. Stop the robot.

This behavior uses almost every part of the API:

* ``DriveBase`` to move the robot;
* ``SensorManager`` to read sensors;
* ``Arm`` to lower and lift the arm;
* ``Gripper`` to grab and release pucks;
* ``PuckManager`` to find, attach and drop pucks;
* ``RobotMode`` to organize the mission as a state machine;
* ``MathUtils`` to compute angles, distances and limited values.

State machine
~~~~~~~~~~~~~

The behavior is organized as a state machine.

A state machine means that the robot is always in one mode.

Each mode represents one step of the mission.

The current mode is stored in:

.. code-block:: java

   private RobotMode mode = RobotMode.SEARCH;

The robot changes mode when an action is finished.

Main modes
~~~~~~~~~~

The current behavior uses these modes:

.. list-table::
   :header-rows: 1

   * - Mode
     - Role
   * - ``SEARCH``
     - The robot searches for an available puck.
   * - ``TOUCH_AVOID``
     - The robot avoids an obstacle after contact.
   * - ``APPROACH_PUCK``
     - The robot moves toward the selected puck.
   * - ``LOWER_ARM``
     - The robot lowers the arm before grabbing.
   * - ``CLOSE_GRIPPER``
     - The robot closes the gripper around the puck.
   * - ``LIFT_ARM``
     - The robot lifts the arm after grabbing.
   * - ``GO_TO_DROP_ZONE``
     - The robot moves toward the selected drop zone.
   * - ``DROP_PUCK``
     - The robot releases the puck.
   * - ``LIFT_ARM_AFTER_DROP``
     - The robot lifts the arm after dropping.
   * - ``BACK_AND_TURN_AFTER_DROP``
     - The robot moves away from the drop zone.
   * - ``FINISHED``
     - The robot stops because all pucks are delivered.

The ``FINISHED`` mode is important because it gives the robot a clear mission ending.

Global mission sequence
~~~~~~~~~~~~~~~~~~~~~~~

A successful collection cycle follows this sequence:

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

When all pucks are delivered, the sequence becomes:

.. code-block:: text

   LIFT_ARM_AFTER_DROP
      |
      v
   FINISHED

The robot then stops.

Important attributes
~~~~~~~~~~~~~~~~~~~~

The behavior stores several values to remember what is happening.

.. code-block:: java

   private int currentPuckIndex = -1;

``currentPuckIndex`` stores the index of the puck currently targeted or carried.

If it is ``-1``, no puck is currently selected.

.. code-block:: java

   private boolean puckAttached = false;

``puckAttached`` indicates whether the robot is currently carrying a puck.

.. code-block:: java

   private boolean finishedMessagePrinted = false;

``finishedMessagePrinted`` ensures that the final mission message is printed only once.

Counters
~~~~~~~~

Several counters are used to control actions over time.

For example:

.. code-block:: java

   private int counter = 0;
   private int stepCounter = 0;
   private int wallStuckCounter = 0;

These counters are used to:

* wait while the arm moves;
* wait while the gripper closes;
* count how long the robot has been avoiding an obstacle;
* avoid reacting too early at the beginning of the simulation.

Main constants
~~~~~~~~~~~~~~

The behavior uses constants for speeds, distances and thresholds.

Examples:

.. code-block:: java

   private static final double SEARCH_SPEED = 4.0;
   private static final double APPROACH_SPEED = 0.8;
   private static final double GO_DROP_SPEED = 3.2;
   private static final double TURN_SPEED = 4.0;
   private static final double BACK_SPEED = -2.0;

These values control the movement speed of the robot.

Other constants define sensor thresholds:

.. code-block:: java

   private static final double FRONT_OBJECT_THRESHOLD = 350.0;
   private static final double SIDE_OBJECT_THRESHOLD = 900.0;
   private static final double WALL_STUCK_THRESHOLD = 970.0;

These values decide when the robot considers that an obstacle is detected.

Drop zone constants
~~~~~~~~~~~~~~~~~~~

The robot must drop pucks behind the white lines.

The two white lines are represented by:

.. code-block:: java

   private static final double LEFT_WHITE_LINE_X = -1.0;
   private static final double RIGHT_WHITE_LINE_X = 1.0;

The puck is dropped outside the selected white line using an offset:

.. code-block:: java

   private static final double DROP_OUTSIDE_OFFSET = 0.25;

This means:

.. code-block:: text

   left drop zone  -> x = -1.25
   right drop zone -> x =  1.25

The robot chooses the closest drop zone according to the puck position.

update method
~~~~~~~~~~~~~

The ``update`` method is the main method of the behavior.

It is called at each simulation step.

Its general structure is:

.. code-block:: java

   @Override
   public void update() {
     stepCounter++;

     robot.sensors().update();

     if (robot.pucks().allPucksDelivered()) {
       mode = RobotMode.FINISHED;
     }

     if (mode == RobotMode.FINISHED) {
       updateFinished();
       printDebug();
       return;
     }

     if (puckAttached && currentPuckIndex != -1) {
       robot.pucks().attachPuckToRobot(currentPuckIndex);
     }

     handleContact();

     switch (mode) {
       case SEARCH: updateSearch(); break;
       case TOUCH_AVOID: updateTouchAvoid(); break;
       case APPROACH_PUCK: updateApproachPuck(); break;
       case LOWER_ARM: updateLowerArm(); break;
       case CLOSE_GRIPPER: updateCloseGripper(); break;
       case LIFT_ARM: updateLiftArm(); break;
       case GO_TO_DROP_ZONE: updateGoToDropZone(); break;
       case DROP_PUCK: updateDropPuck(); break;
       case LIFT_ARM_AFTER_DROP: updateLiftArmAfterDrop(); break;
       case BACK_AND_TURN_AFTER_DROP: updateBackAndTurnAfterDrop(); break;
       case FINISHED: updateFinished(); break;
     }

     printDebug();
   }

At each step, the behavior:

1. updates the sensors;
2. checks if all pucks are delivered;
3. attaches the carried puck to the robot if needed;
4. handles contact;
5. updates the current mode;
6. prints debug information.

Mission completion
~~~~~~~~~~~~~~~~~~

The behavior checks if all pucks have been delivered:

.. code-block:: java

   if (robot.pucks().allPucksDelivered()) {
     mode = RobotMode.FINISHED;
   }

When the robot enters ``FINISHED`` mode, it calls:

.. code-block:: java

   updateFinished();

This method stops the robot and puts the arm and gripper in a safe position.

.. code-block:: java

   private void updateFinished() {
     robot.motors().stop();
     robot.arm().lift();
     robot.gripper().open();

     puckAttached = false;
     currentPuckIndex = -1;
     carryAvoidCounter = 0;
     carryAvoidDirection = 1;

     if (!finishedMessagePrinted) {
       System.out.println("All pucks have been delivered. Robot stopped.");
       finishedMessagePrinted = true;
     }
   }

This prevents the robot from continuing to move after the mission is complete.

Searching for a puck
~~~~~~~~~~~~~~~~~~~~

In ``SEARCH`` mode, the robot moves while looking for a puck.

It uses:

* the front distance sensor;
* the side distance sensors;
* the ``PuckManager``;
* angle and distance calculations.

The robot selects a puck with:

.. code-block:: java

   int bestPuckIndex = robot.pucks().findBestAvailablePuck(0.35);

This method chooses a puck using both distance and angle.

If a puck is close enough and in front of the robot, the mode becomes:

.. code-block:: java

   mode = RobotMode.APPROACH_PUCK;

Approaching a puck
~~~~~~~~~~~~~~~~~~

In ``APPROACH_PUCK`` mode, the robot moves toward the selected puck.

It computes:

* the distance to the puck;
* the angle error to the puck;
* a correction value.

.. code-block:: java

   double distanceToPuck = robot.pucks().getDistanceToPuck(currentPuckIndex);
   double angleError = robot.pucks().getAngleErrorToPuck(currentPuckIndex);

   double correction = APPROACH_TURN_GAIN * angleError;
   correction = MathUtils.clamp(correction, -TURN_SPEED, TURN_SPEED);

The correction is used to adjust the left and right wheel speeds:

.. code-block:: java

   double leftSpeed = APPROACH_SPEED - correction;
   double rightSpeed = APPROACH_SPEED + correction;

   robot.motors().setSpeed(leftSpeed, rightSpeed);

This allows the robot to steer toward the puck.

Contact handling
~~~~~~~~~~~~~~~~

The robot uses the front touch sensor to detect contact.

When a contact occurs, the behavior checks whether the contact is probably with a puck.

It does this by checking the distance to the nearest available puck.

If the nearest puck is close enough, the robot starts the pickup sequence.

If not, it considers the contact as an obstacle and switches to ``TOUCH_AVOID``.

Pickup sequence
~~~~~~~~~~~~~~~

The pickup sequence uses three modes:

.. code-block:: text

   LOWER_ARM
   CLOSE_GRIPPER
   LIFT_ARM

In ``LOWER_ARM`` mode:

.. code-block:: java

   robot.arm().lower();
   robot.gripper().open();

In ``CLOSE_GRIPPER`` mode:

.. code-block:: java

   robot.gripper().close();

Then the puck is considered attached:

.. code-block:: java

   puckAttached = true;

In ``LIFT_ARM`` mode:

.. code-block:: java

   robot.arm().lift();

After this, the robot goes to the drop zone.

Carrying a puck
~~~~~~~~~~~~~~~

While the robot carries a puck, the puck is attached to the robot using:

.. code-block:: java

   robot.pucks().attachPuckToRobot(currentPuckIndex);

This is called at each update step while ``puckAttached`` is ``true``.

This makes the puck follow the robot during the transport.

Choosing the drop zone
~~~~~~~~~~~~~~~~~~~~~~

After the gripper closes, the robot chooses the nearest drop zone.

This is done in:

.. code-block:: java

   chooseNearestDropZone();

The robot compares the puck position with the two white lines:

.. code-block:: java

   double distanceToLeftLine = Math.abs(referenceX - LEFT_WHITE_LINE_X);
   double distanceToRightLine = Math.abs(referenceX - RIGHT_WHITE_LINE_X);

If the left line is closer, the target is placed behind the left line.

If the right line is closer, the target is placed behind the right line.

The Y coordinate is limited with:

.. code-block:: java

   targetDropY = MathUtils.clamp(referenceY, DROP_MIN_Y, DROP_MAX_Y);

This prevents the robot from choosing a drop position too close to the top or bottom wall.

Going to the drop zone
~~~~~~~~~~~~~~~~~~~~~~

In ``GO_TO_DROP_ZONE`` mode, the robot moves toward the selected drop zone.

It first aligns itself with the target.

If the angle error is too large, it turns:

.. code-block:: java

   if (Math.abs(angleError) > DROP_ALIGNMENT_THRESHOLD) {
     if (angleError > 0.0) {
       robot.motors().turnLeft(TURN_SPEED);
     } else {
       robot.motors().turnRight(TURN_SPEED);
     }
     return;
   }

If it is aligned, it moves forward.

Avoiding pucks while carrying one
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When the robot is carrying a puck, other pucks are not targets anymore.

They become obstacles.

The robot must avoid pushing them.

The method:

.. code-block:: java

   findBlockingPuckWhileCarrying();

checks if another puck is on the path to the drop zone.

It uses:

* forward distance;
* lateral distance;
* delivery status;
* current carried puck index.

If a puck is on the path and far enough, the robot curves around it.

If the puck is too close, the robot performs an emergency avoidance.

This logic prevents the robot from pushing other pucks while carrying one.

Dropping a puck
~~~~~~~~~~~~~~~

When the robot reaches the drop zone, it enters ``DROP_PUCK`` mode.

It stops, opens the gripper and lowers the arm:

.. code-block:: java

   robot.motors().stop();
   robot.gripper().open();
   robot.arm().lower();

Then the puck is dropped with:

.. code-block:: java

   robot.pucks().dropPuck(currentPuckIndex, targetDropX, targetDropY, DROP_Z);

After the drop, the robot lifts the arm.

After the last puck
~~~~~~~~~~~~~~~~~~~

After each drop, the robot checks if all pucks are delivered:

.. code-block:: java

   if (robot.pucks().allPucksDelivered()) {
     mode = RobotMode.FINISHED;
     System.out.println("Arm lifted after last drop. Robot finished.");
   } else {
     mode = RobotMode.BACK_AND_TURN_AFTER_DROP;
   }

If all pucks are delivered, the robot does not go back to search mode.

It enters ``FINISHED`` mode and stops.

Debug output
~~~~~~~~~~~~

The behavior prints debug information at each step.

The debug output includes:

* current mode;
* left distance sensor value;
* right distance sensor value;
* front distance sensor value;
* RGB color;
* touch sensor state;
* current puck;
* target drop position;
* carry avoidance counter;
* wall stuck counter.

This is useful to understand what the robot is doing during the simulation.

How to use a behavior
---------------------

A behavior is assigned in the main controller.

Example with the full collection behavior:

.. code-block:: java

   import api.core.TERBot;
   import api.behavior.CollectPucksBehavior;

   public class FourWheelsCollisionAvoidanceAPI {
     public static void main(String[] args) {
       TERBot robot = new TERBot();

       robot.setBehavior(new CollectPucksBehavior(robot));

       robot.run();
     }
   }

Example with the simple avoidance behavior:

.. code-block:: java

   import api.core.TERBot;
   import api.behavior.SimpleAvoidObstacleBehavior;

   public class FourWheelsCollisionAvoidanceAPI {
     public static void main(String[] args) {
       TERBot robot = new TERBot();

       robot.setBehavior(new SimpleAvoidObstacleBehavior(robot));

       robot.run();
     }
   }

Difference between both behaviors
---------------------------------

.. list-table::
   :header-rows: 1

   * - Behavior
     - Purpose
   * - ``SimpleAvoidObstacleBehavior``
     - Simple test behavior for movement and sensors.
   * - ``CollectPucksBehavior``
     - Complete autonomous mission behavior.

``SimpleAvoidObstacleBehavior`` is useful at the beginning of the project.

``CollectPucksBehavior`` is the final behavior used for the complete puck collection mission.

Debugging
---------

If the robot does not move, check:

* the behavior is assigned with ``setBehavior``;
* ``robot.run()`` is called;
* the motors are correctly named in Webots;
* the controller has been recompiled.

If the robot never finds a puck, check:

* the pucks have ``DEF`` names starting with ``PALET_``;
* ``PuckManager`` detects the pucks;
* the puck is not already marked as delivered;
* the angle and distance thresholds are not too strict.

If the robot does not stop after the mission, check:

* ``RobotMode.FINISHED`` exists;
* ``PuckManager.allPucksDelivered()`` exists;
* each puck is marked as delivered in ``dropPuck``.

If the robot pushes pucks while carrying one, check:

* ``findBlockingPuckWhileCarrying`` detects pucks on the path;
* the lateral and forward thresholds are adapted;
* the carried puck is ignored with ``currentPuckIndex``.

Summary
-------

The ``api.behavior`` package contains the decision logic of the robot.

``RobotBehavior`` defines the common behavior structure.

``SimpleAvoidObstacleBehavior`` is a simple behavior used to test movement and obstacle detection.

``CollectPucksBehavior`` is the complete mission behavior.

It allows the robot to:

* search for pucks;
* approach and grab a puck;
* carry it to a drop zone;
* avoid pushing other pucks while carrying one;
* drop the puck behind a white line;
* repeat the mission;
* stop when all pucks are delivered.

This package is the highest-level part of the API because it connects all other modules together.