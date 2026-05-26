World API
=========

Overview
--------

The ``api.world`` package contains the classes used to interact with objects in the Webots world.

In this project, the main world objects managed by the API are the pucks.

The robot must be able to:

* find the pucks in the simulation;
* know which pucks are still available;
* select the nearest puck;
* select the best puck according to distance and angle;
* get the position of a puck;
* compute the distance to a puck;
* compute the angle error to a puck;
* attach a puck to the robot while carrying it;
* drop a puck in the drop zone;
* mark a puck as delivered.

The class responsible for this is:

.. code-block:: text

   PuckManager.java

Package location
----------------

The world API is located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── world/
               └── PuckManager.java

Package declaration
-------------------

The file starts with:

.. code-block:: java

   package api.world;

This means that the file must be located in:

.. code-block:: text

   api/world/

Imports
-------

The ``PuckManager`` class uses Webots classes:

.. code-block:: java

   import com.cyberbotics.webots.controller.Field;
   import com.cyberbotics.webots.controller.Node;
   import com.cyberbotics.webots.controller.Supervisor;

It also uses utility functions from the project:

.. code-block:: java

   import api.utils.MathUtils;

The imported classes have the following roles:

.. list-table::
   :header-rows: 1

   * - Class
     - Role
   * - ``Supervisor``
     - Allows access to the robot and to world objects.
   * - ``Node``
     - Represents an object in the Webots world, such as a puck.
   * - ``Field``
     - Allows access to a field of a Webots node, such as ``translation``.
   * - ``MathUtils``
     - Provides mathematical helper methods, such as distance and angle computation.

PuckManager
-----------

Overview
~~~~~~~~

``PuckManager`` is responsible for managing all pucks used by the robot.

It stores:

* the puck names;
* the Webots nodes of the pucks;
* the translation fields of the pucks;
* the delivery state of each puck.

It allows the robot behavior to work with pucks without directly manipulating Webots nodes everywhere in the code.

Class code
~~~~~~~~~~

.. code-block:: java

   package api.world;

   import com.cyberbotics.webots.controller.Field;
   import com.cyberbotics.webots.controller.Node;
   import com.cyberbotics.webots.controller.Supervisor;

   import api.utils.MathUtils;

   public class PuckManager {
     private final Supervisor robot;
     private final String[] puckNames;
     private final Node[] puckNodes;
     private final Field[] puckTranslationFields;
     private final boolean[] puckDelivered;

     public PuckManager(Supervisor robot, String[] puckNames) {
       this.robot = robot;
       this.puckNames = puckNames;
       this.puckNodes = new Node[puckNames.length];
       this.puckTranslationFields = new Field[puckNames.length];
       this.puckDelivered = new boolean[puckNames.length];

       for (int i = 0; i < puckNames.length; i++) {
         puckNodes[i] = robot.getFromDef(puckNames[i]);

         if (puckNodes[i] != null) {
           puckTranslationFields[i] = puckNodes[i].getField("translation");
           puckDelivered[i] = false;
         } else {
           System.out.println("Attention : impossible de trouver " + puckNames[i]);
         }
       }
     }

     public int count() { return puckNames.length; }

     public int findNearestAvailablePuck() {
       double[] robotPosition = robot.getSelf().getPosition();
       int nearestIndex = -1;
       double nearestDistance = Double.MAX_VALUE;

       for (int i = 0; i < puckNodes.length; i++) {
         if (puckNodes[i] == null || puckDelivered[i]) { continue; }

         double distance = MathUtils.distance2D(robotPosition, puckNodes[i].getPosition());

         if (distance < nearestDistance) {
           nearestDistance = distance;
           nearestIndex = i;
         }
       }

       return nearestIndex;
     }

     public int findBestAvailablePuck(double angleWeight) {
       double[] robotPosition = robot.getSelf().getPosition();
       double robotYaw = MathUtils.getRobotYaw(robot);

       int bestIndex = -1;
       double bestScore = Double.MAX_VALUE;

       for (int i = 0; i < puckNodes.length; i++) {
         if (puckNodes[i] == null || puckDelivered[i]) { continue; }

         double[] puckPosition = puckNodes[i].getPosition();

         double dx = puckPosition[0] - robotPosition[0];
         double dy = puckPosition[1] - robotPosition[1];

         double distance = Math.sqrt(dx * dx + dy * dy);
         double targetAngle = Math.atan2(dy, dx);
         double angleError = Math.abs(MathUtils.normalizeAngle(targetAngle - robotYaw));

         double score = distance + angleError * angleWeight;

         if (score < bestScore) {
           bestScore = score;
           bestIndex = i;
         }
       }

       return bestIndex;
     }

     public Node getPuckNode(int index) {
       return (index < 0 || index >= puckNodes.length) ? null : puckNodes[index];
     }

     public double[] getPuckPosition(int index) {
       Node puckNode = getPuckNode(index);
       return puckNode == null ? new double[] {0.0, 0.0, 0.0} : puckNode.getPosition();
     }

     public double getDistanceToPuck(int index) {
       Node puckNode = getPuckNode(index);

       if (puckNode == null) {
         return Double.MAX_VALUE;
       }

       return MathUtils.distance2D(robot.getSelf().getPosition(), puckNode.getPosition());
     }

     public double getAngleErrorToPuck(int index) {
       Node puckNode = getPuckNode(index);

       if (puckNode == null) {
         return 0.0;
       }

       double[] robotPosition = robot.getSelf().getPosition();
       double[] puckPosition = puckNode.getPosition();

       double dx = puckPosition[0] - robotPosition[0];
       double dy = puckPosition[1] - robotPosition[1];

       double targetAngle = Math.atan2(dy, dx);
       double robotAngle = MathUtils.getRobotYaw(robot);

       return MathUtils.normalizeAngle(targetAngle - robotAngle);
     }

     public boolean isDelivered(int index) {
       return (index < 0 || index >= puckDelivered.length) || puckDelivered[index];
     }

     public String getPuckName(int index) {
       return (index < 0 || index >= puckNames.length) ? "unknown" : puckNames[index];
     }

     public void attachPuckToRobot(int index) {
       if (index < 0 || index >= puckNodes.length) { return; }

       Node puckNode = puckNodes[index];
       Field puckTranslationField = puckTranslationFields[index];

       if (puckNode == null || puckTranslationField == null) { return; }

       Node self = robot.getSelf();

       double[] robotPosition = self.getPosition();
       double[] orientation = self.getOrientation();

       double localX = 0.15;
       double localY = 0.0;
       double localZ = 0.09;

       double worldX = robotPosition[0]
           + orientation[0] * localX
           + orientation[1] * localY
           + orientation[2] * localZ;

       double worldY = robotPosition[1]
           + orientation[3] * localX
           + orientation[4] * localY
           + orientation[5] * localZ;

       double worldZ = robotPosition[2]
           + orientation[6] * localX
           + orientation[7] * localY
           + orientation[8] * localZ;

       puckTranslationField.setSFVec3f(new double[] {worldX, worldY, worldZ});
       puckNode.resetPhysics();
     }

     public void dropPuck(int index, double dropX, double dropY, double dropZ) {
       if (index < 0 || index >= puckNodes.length) { return; }

       puckDelivered[index] = true;

       if (puckTranslationFields[index] != null) {
         puckTranslationFields[index].setSFVec3f(new double[] {dropX, dropY, dropZ});
       }

       if (puckNodes[index] != null) {
         puckNodes[index].resetPhysics();
       }

       System.out.println("Palet depose : " + getPuckName(index));
     }
   }

Attributes
----------

The class contains five main attributes:

.. code-block:: java

   private final Supervisor robot;
   private final String[] puckNames;
   private final Node[] puckNodes;
   private final Field[] puckTranslationFields;
   private final boolean[] puckDelivered;

``robot``
~~~~~~~~~

.. code-block:: java

   private final Supervisor robot;

The ``robot`` attribute stores the Webots ``Supervisor``.

The supervisor is necessary because ``PuckManager`` needs to access objects in the Webots world.

It is used to:

* retrieve pucks by their ``DEF`` names;
* get the robot position;
* get the robot orientation;
* move pucks by modifying their ``translation`` field.

``puckNames``
~~~~~~~~~~~~~

.. code-block:: java

   private final String[] puckNames;

The ``puckNames`` array stores the names of the pucks.

These names must match the ``DEF`` names in the Webots world.

Example:

.. code-block:: java

   new String[] {"PALET_1", "PALET_2", "PALET_3"}

This means that the Webots world must contain:

.. code-block:: text

   DEF PALET_1 Solid { ... }
   DEF PALET_2 Solid { ... }
   DEF PALET_3 Solid { ... }

``puckNodes``
~~~~~~~~~~~~~

.. code-block:: java

   private final Node[] puckNodes;

The ``puckNodes`` array stores the Webots nodes corresponding to the pucks.

Each node represents one puck in the world.

The nodes are retrieved with:

.. code-block:: java

   robot.getFromDef(puckNames[i]);

``puckTranslationFields``
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   private final Field[] puckTranslationFields;

The ``puckTranslationFields`` array stores the ``translation`` field of each puck.

This field is used to move a puck in the world.

For example, when the robot carries a puck, the puck position is updated at each simulation step.

``puckDelivered``
~~~~~~~~~~~~~~~~~

.. code-block:: java

   private final boolean[] puckDelivered;

The ``puckDelivered`` array stores whether each puck has already been delivered.

If ``puckDelivered[i]`` is ``true``, the robot should ignore this puck and search for another one.

Constructor
-----------

The constructor initializes the puck manager.

.. code-block:: java

   public PuckManager(Supervisor robot, String[] puckNames) {
     this.robot = robot;
     this.puckNames = puckNames;
     this.puckNodes = new Node[puckNames.length];
     this.puckTranslationFields = new Field[puckNames.length];
     this.puckDelivered = new boolean[puckNames.length];

     for (int i = 0; i < puckNames.length; i++) {
       puckNodes[i] = robot.getFromDef(puckNames[i]);

       if (puckNodes[i] != null) {
         puckTranslationFields[i] = puckNodes[i].getField("translation");
         puckDelivered[i] = false;
       } else {
         System.out.println("Attention : impossible de trouver " + puckNames[i]);
       }
     }
   }

Parameters
~~~~~~~~~~

The constructor receives:

.. list-table::
   :header-rows: 1

   * - Parameter
     - Role
   * - ``robot``
     - The Webots supervisor used to access world objects.
   * - ``puckNames``
     - The list of puck ``DEF`` names to manage.

Puck initialization
~~~~~~~~~~~~~~~~~~~

For each puck name, the constructor tries to retrieve the corresponding Webots node:

.. code-block:: java

   puckNodes[i] = robot.getFromDef(puckNames[i]);

If the node exists, the constructor retrieves its ``translation`` field:

.. code-block:: java

   puckTranslationFields[i] = puckNodes[i].getField("translation");

Then the puck is marked as not delivered:

.. code-block:: java

   puckDelivered[i] = false;

If a puck cannot be found, the program displays:

.. code-block:: java

   System.out.println("Attention : impossible de trouver " + puckNames[i]);

This message usually means that the puck name in Java does not match the ``DEF`` name in the Webots world.

count
-----

.. code-block:: java

   public int count() {
     return puckNames.length;
   }

This method returns the number of pucks managed by the ``PuckManager``.

Example:

.. code-block:: java

   int numberOfPucks = bot.pucks().count();

findNearestAvailablePuck
------------------------

.. code-block:: java

   public int findNearestAvailablePuck()

This method finds the nearest puck that is still available.

A puck is available if:

* its node exists;
* it has not already been delivered.

The method returns:

* the index of the nearest available puck;
* ``-1`` if no puck is available.

How it works
~~~~~~~~~~~~

The method first retrieves the robot position:

.. code-block:: java

   double[] robotPosition = robot.getSelf().getPosition();

Then it loops through all pucks.

Unavailable pucks are ignored:

.. code-block:: java

   if (puckNodes[i] == null || puckDelivered[i]) {
     continue;
   }

For each available puck, the 2D distance is computed:

.. code-block:: java

   double distance = MathUtils.distance2D(robotPosition, puckNodes[i].getPosition());

The method keeps the puck with the smallest distance.

Example
~~~~~~~

.. code-block:: java

   int puckIndex = bot.pucks().findNearestAvailablePuck();

   if (puckIndex != -1) {
     System.out.println("Nearest puck: " + bot.pucks().getPuckName(puckIndex));
   }

findBestAvailablePuck
---------------------

.. code-block:: java

   public int findBestAvailablePuck(double angleWeight)

This method selects a puck using both:

* the distance to the puck;
* the angle between the robot direction and the puck.

It is more advanced than ``findNearestAvailablePuck``.

The goal is to avoid selecting a puck that is close but badly placed behind or to the side of the robot.

Scoring formula
~~~~~~~~~~~~~~~

For each available puck, the method computes a score:

.. code-block:: java

   double score = distance + angleError * angleWeight;

The best puck is the puck with the smallest score.

The score uses:

.. list-table::
   :header-rows: 1

   * - Value
     - Meaning
   * - ``distance``
     - Distance between the robot and the puck.
   * - ``angleError``
     - Difference between the robot orientation and the direction to the puck.
   * - ``angleWeight``
     - Importance given to the angle error.

Effect of ``angleWeight``
~~~~~~~~~~~~~~~~~~~~~~~~~

If ``angleWeight`` is small, the robot mainly chooses the nearest puck.

If ``angleWeight`` is large, the robot prefers pucks that are better aligned with its current direction.

Example:

.. code-block:: java

   int puckIndex = bot.pucks().findBestAvailablePuck(0.5);

This chooses a puck using both distance and orientation.

getPuckNode
-----------

.. code-block:: java

   public Node getPuckNode(int index)

This method returns the Webots node of a puck.

If the index is invalid, it returns ``null``.

Invalid indexes include:

* negative indexes;
* indexes greater than or equal to the number of pucks.

Example:

.. code-block:: java

   Node puck = bot.pucks().getPuckNode(0);

This returns the Webots node of the first puck.

getPuckPosition
---------------

.. code-block:: java

   public double[] getPuckPosition(int index)

This method returns the position of a puck.

If the puck does not exist, it returns:

.. code-block:: java

   new double[] {0.0, 0.0, 0.0}

Example:

.. code-block:: java

   double[] position = bot.pucks().getPuckPosition(puckIndex);

   System.out.println("x = " + position[0]);
   System.out.println("y = " + position[1]);
   System.out.println("z = " + position[2]);

getDistanceToPuck
-----------------

.. code-block:: java

   public double getDistanceToPuck(int index)

This method returns the 2D distance between the robot and a puck.

If the puck does not exist, the method returns:

.. code-block:: java

   Double.MAX_VALUE

This is useful because an invalid puck will never be considered close.

Example:

.. code-block:: java

   double distance = bot.pucks().getDistanceToPuck(puckIndex);

   if (distance < 0.4) {
     System.out.println("The puck is close");
   }

getAngleErrorToPuck
-------------------

.. code-block:: java

   public double getAngleErrorToPuck(int index)

This method returns the angle error between the robot direction and the direction to a puck.

It is used when the robot needs to turn toward a puck.

How it works
~~~~~~~~~~~~

The method retrieves:

* the robot position;
* the puck position;
* the target angle;
* the robot angle.

The target angle is computed with:

.. code-block:: java

   double targetAngle = Math.atan2(dy, dx);

The robot angle is retrieved with:

.. code-block:: java

   double robotAngle = MathUtils.getRobotYaw(robot);

The angle error is normalized with:

.. code-block:: java

   return MathUtils.normalizeAngle(targetAngle - robotAngle);

A positive or negative value indicates the direction in which the robot should turn.

Example:

.. code-block:: java

   double angleError = bot.pucks().getAngleErrorToPuck(puckIndex);

   if (angleError > 0) {
     bot.motors().turnLeft(2.0);
   } else {
     bot.motors().turnRight(2.0);
   }

isDelivered
-----------

.. code-block:: java

   public boolean isDelivered(int index)

This method returns whether a puck has already been delivered.

If the index is invalid, the method returns ``true``.

This is a safety choice: an invalid puck is treated as already delivered, so the robot will ignore it.

Example:

.. code-block:: java

   if (!bot.pucks().isDelivered(puckIndex)) {
     System.out.println("This puck is still available");
   }

getPuckName
-----------

.. code-block:: java

   public String getPuckName(int index)

This method returns the name of a puck.

If the index is invalid, it returns:

.. code-block:: text

   unknown

Example:

.. code-block:: java

   System.out.println("Current puck: " + bot.pucks().getPuckName(puckIndex));

attachPuckToRobot
-----------------

.. code-block:: java

   public void attachPuckToRobot(int index)

This method virtually attaches a puck to the robot.

It updates the puck position so that it stays in front of the robot.

This is used after the puck has been collected.

Why virtual attachment is used
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In a physics simulation, physically grabbing a small object with a gripper can be unstable.

The puck can:

* slide away;
* fall;
* vibrate;
* escape the gripper.

To avoid these problems during the practical work, the controller moves the puck directly with the Webots ``Supervisor``.

This makes the behavior more stable and easier to understand.

Index checks
~~~~~~~~~~~~

The method first checks that the index is valid:

.. code-block:: java

   if (index < 0 || index >= puckNodes.length) {
     return;
   }

Then it checks that the puck node and translation field exist:

.. code-block:: java

   if (puckNode == null || puckTranslationField == null) {
     return;
   }

These checks prevent errors if a puck is missing.

Local position
~~~~~~~~~~~~~~

The puck is placed using a local offset relative to the robot:

.. code-block:: java

   double localX = 0.15;
   double localY = 0.0;
   double localZ = 0.09;

This means that the puck is placed:

* slightly in front of the robot;
* centered on the robot;
* slightly above the ground.

World position computation
~~~~~~~~~~~~~~~~~~~~~~~~~~

The local position is converted into world coordinates using the robot orientation matrix:

.. code-block:: java

   double worldX = robotPosition[0]
       + orientation[0] * localX
       + orientation[1] * localY
       + orientation[2] * localZ;

   double worldY = robotPosition[1]
       + orientation[3] * localX
       + orientation[4] * localY
       + orientation[5] * localZ;

   double worldZ = robotPosition[2]
       + orientation[6] * localX
       + orientation[7] * localY
       + orientation[8] * localZ;

Then the puck is moved:

.. code-block:: java

   puckTranslationField.setSFVec3f(new double[] {worldX, worldY, worldZ});

Finally, its physics is reset:

.. code-block:: java

   puckNode.resetPhysics();

This prevents unwanted physical movement after repositioning.

Example
~~~~~~~

In a behavior, while the robot is carrying a puck:

.. code-block:: java

   bot.pucks().attachPuckToRobot(currentPuckIndex);

This should be called repeatedly while the puck is attached.

dropPuck
--------

.. code-block:: java

   public void dropPuck(int index, double dropX, double dropY, double dropZ)

This method drops a puck at a fixed position.

It is used when the robot reaches the drop zone.

Parameters
~~~~~~~~~~

.. list-table::
   :header-rows: 1

   * - Parameter
     - Role
   * - ``index``
     - Index of the puck to drop.
   * - ``dropX``
     - X coordinate of the drop position.
   * - ``dropY``
     - Y coordinate of the drop position.
   * - ``dropZ``
     - Z coordinate of the drop position.

How it works
~~~~~~~~~~~~

The method first checks if the index is valid:

.. code-block:: java

   if (index < 0 || index >= puckNodes.length) {
     return;
   }

Then it marks the puck as delivered:

.. code-block:: java

   puckDelivered[index] = true;

If the translation field exists, the puck is moved to the drop position:

.. code-block:: java

   puckTranslationFields[index].setSFVec3f(new double[] {dropX, dropY, dropZ});

If the puck node exists, its physics is reset:

.. code-block:: java

   puckNodes[index].resetPhysics();

Finally, a message is printed:

.. code-block:: java

   System.out.println("Palet depose : " + getPuckName(index));

Example
~~~~~~~

.. code-block:: java

   bot.pucks().dropPuck(currentPuckIndex, -0.9, 0.0, 0.095);

This places the current puck at the drop zone position.

Typical use in a behavior
-------------------------

A simplified puck collection logic can look like this:

.. code-block:: java

   int puckIndex = bot.pucks().findBestAvailablePuck(0.5);

   if (puckIndex != -1) {
     double angleError = bot.pucks().getAngleErrorToPuck(puckIndex);

     if (Math.abs(angleError) > 0.2) {
       if (angleError > 0) {
         bot.motors().turnLeft(2.0);
       } else {
         bot.motors().turnRight(2.0);
       }
     } else {
       bot.motors().forward(1.0);
     }
   }

When carrying a puck:

.. code-block:: java

   bot.pucks().attachPuckToRobot(currentPuckIndex);

When dropping a puck:

.. code-block:: java

   bot.pucks().dropPuck(currentPuckIndex, DROP_X, DROP_Y, DROP_Z);

Naming convention
-----------------

The world API depends on the ``DEF`` names of the pucks in the Webots world.

The default puck names are:

.. code-block:: text

   PALET_1
   PALET_2
   PALET_3

The world file must contain matching definitions.

Example:

.. code-block:: text

   DEF PALET_1 Solid {
     translation 0.6 0 0.065
     ...
   }

   DEF PALET_2 Solid {
     translation 0.2 0.5 0.065
     ...
   }

   DEF PALET_3 Solid {
     translation 0.2 -0.5 0.065
     ...
   }

If the names are different, the constructor will display a warning:

.. code-block:: text

   Attention : impossible de trouver PALET_1

Adding more pucks
-----------------

To manage more pucks, the list given to ``PuckManager`` must be changed.

For example, in ``TERBot``:

.. code-block:: java

   this.puckManager = new PuckManager(
     supervisor,
     new String[] {"PALET_1", "PALET_2", "PALET_3", "PALET_4"}
   );

The Webots world must also contain:

.. code-block:: text

   DEF PALET_4 Solid {
     ...
   }

If the puck exists in Java but not in the Webots world, it will not be found.

Debugging
---------

If the robot does not find pucks, check:

* the puck names in Java;
* the ``DEF`` names in the Webots world;
* the pucks are inside the world file;
* the controller is running as a ``Supervisor``;
* the controller has been recompiled.

If the robot tries to collect the same puck again, check:

* ``dropPuck`` is called correctly;
* the puck is marked as delivered;
* ``puckDelivered[index]`` becomes ``true``.

If the puck does not follow the robot, check:

* ``attachPuckToRobot`` is called every simulation step while carrying;
* the puck node exists;
* the puck has a ``translation`` field;
* the puck physics is reset after movement.

If the puck is dropped at the wrong position, check:

* the ``dropX``, ``dropY`` and ``dropZ`` values;
* the drop zone position in the Webots world;
* the puck height is not too low or too high.

Limitations
-----------

The current ``PuckManager`` uses the Webots ``Supervisor`` to move pucks directly.

This makes the simulation more stable, but it is not a fully physical gripping system.

The robot does not hold the puck only through physical contact forces.  
Instead, the controller updates the puck position while it is being carried.

This is acceptable for this project because the objective is to focus on:

* robot control;
* sensors;
* state machines;
* movement logic;
* API architecture.

Summary
-------

The ``api.world`` package manages the pucks in the Webots world.

The ``PuckManager`` class can:

* count pucks;
* find the nearest available puck;
* find the best available puck using distance and angle;
* return puck positions;
* compute distance and angle errors;
* check if a puck is delivered;
* attach a puck to the robot;
* drop a puck in the drop zone.

This class separates world-object management from the rest of the robot behavior and makes the controller easier to understand.