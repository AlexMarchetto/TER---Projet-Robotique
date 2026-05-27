Core API
========

Overview
--------

The ``api.core`` package contains the central class of the robot API:

.. code-block:: text

   TERBot.java

The ``TERBot`` class represents the robot in the Java controller.

It is the main entry point of the API.

Its role is to create and connect all the important parts of the robot:

* the Webots supervisor;
* the motors;
* the sensors;
* the arm;
* the gripper;
* the puck manager;
* the task scheduler;
* the robot behavior.

Thanks to this class, the main controller can stay very simple.

For example, instead of manually creating every sensor and every motor, the main controller can simply write:

.. code-block:: java

   TERBot robot = new TERBot();
   robot.setBehavior(new CollectPucksBehavior(robot));
   robot.run();

Why this class exists
---------------------

Without ``TERBot``, the main controller would have to directly manage all Webots devices.

It would need to:

* create the Webots ``Supervisor``;
* retrieve each motor;
* retrieve each sensor;
* enable sensors;
* create the arm;
* create the gripper;
* find all pucks;
* update the simulation loop;
* update the behavior at each step.

This would make the main controller long and difficult to understand.

The goal of ``TERBot`` is to centralize this initialization.

It acts as the link between Webots and the higher-level robot API.

Package location
----------------

The file is located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── core/
               └── TERBot.java

Package declaration
-------------------

The file starts with:

.. code-block:: java

   package api.core;

This means that the file must be located in the folder:

.. code-block:: text

   api/core/

If the folder and the package name do not match, Java will not compile the project correctly.

TERBot source code
------------------

.. code-block:: java

   package api.core;

   import com.cyberbotics.webots.controller.Supervisor;

   import api.actuators.Arm;
   import api.actuators.Gripper;
   import api.behavior.RobotBehavior;
   import api.motors.DriveBase;
   import api.sensors.SensorManager;
   import api.tasks.TaskScheduler;
   import api.world.PuckManager;

   public class TERBot {
     private final Supervisor supervisor;
     private final int timeStep;

     private final DriveBase driveBase;
     private final SensorManager sensorManager;
     private final Arm arm;
     private final Gripper gripper;
     private final PuckManager puckManager;
     private final TaskScheduler scheduler;

     private RobotBehavior behavior;

     public TERBot() {
       /*
        * Main Webots object.
        * It allows the controller to communicate with the simulated world.
        */
       this.supervisor = new Supervisor();

       /*
        * Webots simulation time step.
        */
       this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

       /*
        * Initialization of the robot APIs.
        */
       this.driveBase = new DriveBase(supervisor);
       this.sensorManager = new SensorManager(supervisor, timeStep);
       this.arm = new Arm(supervisor, timeStep);
       this.gripper = new Gripper(supervisor);

       /*
        * Puck API.
        * Automatically finds all objects whose DEF name starts with PALET_.
        */
       this.puckManager = PuckManager.findAllWithPrefix(supervisor, "PALET_");

       /*
        * Simple asynchronous task API.
        */
       this.scheduler = new TaskScheduler();

       /*
        * Initial arm and gripper position.
        */
       arm.lift();
       gripper.open();
     }

     public Supervisor supervisor() {
       return supervisor;
     }

     public int timeStep() {
       return timeStep;
     }

     public DriveBase motors() {
       return driveBase;
     }

     public SensorManager sensors() {
       return sensorManager;
     }

     public Arm arm() {
       return arm;
     }

     public Gripper gripper() {
       return gripper;
     }

     public PuckManager pucks() {
       return puckManager;
     }

     public TaskScheduler scheduler() {
       return scheduler;
     }

     public void setBehavior(RobotBehavior behavior) {
       this.behavior = behavior;
     }

     public void run() {
       if (behavior != null) {
         behavior.init();
       }

       while (supervisor.step(timeStep) != -1) {
         scheduler.update();

         if (behavior != null) {
           behavior.update();
         }
       }
     }

     public void stop() {
       driveBase.stop();
     }
   }

General organization
--------------------

The ``TERBot`` class creates the main modules of the API.

The structure can be represented like this:

.. code-block:: text

   TERBot
      |
      ├── Supervisor
      ├── DriveBase
      ├── SensorManager
      ├── Arm
      ├── Gripper
      ├── PuckManager
      ├── TaskScheduler
      └── RobotBehavior

Each part has a specific role.

.. list-table::
   :header-rows: 1

   * - Module
     - Role
   * - ``Supervisor``
     - Communicates with the Webots simulation.
   * - ``DriveBase``
     - Controls the wheels and movement.
   * - ``SensorManager``
     - Reads the robot sensors.
   * - ``Arm``
     - Moves the arm up and down.
   * - ``Gripper``
     - Opens and closes the gripper.
   * - ``PuckManager``
     - Finds and manages the pucks.
   * - ``TaskScheduler``
     - Updates simple timed tasks.
   * - ``RobotBehavior``
     - Defines what the robot does during the simulation.

Attributes
----------

The ``TERBot`` class stores all main components as attributes.

Supervisor
~~~~~~~~~~

.. code-block:: java

   private final Supervisor supervisor;

The ``Supervisor`` is the main Webots object used by the controller.

It allows the Java program to communicate with the simulation.

In this project, ``Supervisor`` is useful because the robot needs to:

* access its own position;
* access its own orientation;
* retrieve devices such as motors and sensors;
* find pucks in the world;
* move pucks when they are carried or dropped.

A normal Webots ``Robot`` object would be enough for simple movement, but this project uses a ``Supervisor`` because it needs access to objects in the world.

Time step
~~~~~~~~~

.. code-block:: java

   private final int timeStep;

The ``timeStep`` is the simulation step duration used by Webots.

It is used to:

* advance the simulation;
* enable sensors;
* update the controller regularly.

It is initialized with:

.. code-block:: java

   this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

This means that the controller uses the same basic time step as the Webots world.

Drive base
~~~~~~~~~~

.. code-block:: java

   private final DriveBase driveBase;

The ``DriveBase`` controls the robot wheels.

It provides movement methods such as:

.. code-block:: java

   robot.motors().forward(2.0);
   robot.motors().turnLeft(2.0);
   robot.motors().stop();

This avoids controlling each wheel motor manually in the behavior code.

Sensor manager
~~~~~~~~~~~~~~

.. code-block:: java

   private final SensorManager sensorManager;

The ``SensorManager`` gives access to all robot sensors.

It manages:

* the front distance sensor;
* the left distance sensor;
* the right distance sensor;
* the color sensor;
* the front touch sensor.

Example:

.. code-block:: java

   robot.sensors().frontDistance();
   robot.sensors().isFrontTouched();
   robot.sensors().seesRed();

Arm
~~~

.. code-block:: java

   private final Arm arm;

The ``Arm`` controls the vertical movement of the arm.

Example:

.. code-block:: java

   robot.arm().lower();
   robot.arm().lift();

The arm is used when grabbing or dropping a puck.

Gripper
~~~~~~~

.. code-block:: java

   private final Gripper gripper;

The ``Gripper`` controls the opening and closing of the gripper.

Example:

.. code-block:: java

   robot.gripper().open();
   robot.gripper().close();

The gripper is used to grab and release pucks.

Puck manager
~~~~~~~~~~~~

.. code-block:: java

   private final PuckManager puckManager;

The ``PuckManager`` manages all pucks in the Webots world.

It can:

* find all pucks;
* select a puck to collect;
* get the position of a puck;
* attach a puck to the robot;
* drop a puck in a drop zone;
* check if all pucks have been delivered.

In the current version, pucks are detected automatically:

.. code-block:: java

   this.puckManager = PuckManager.findAllWithPrefix(supervisor, "PALET_");

This means that the robot automatically finds every object whose ``DEF`` name starts with:

.. code-block:: text

   PALET_

For example:

.. code-block:: text

   PALET_1
   PALET_2
   PALET_3
   PALET_4

This is useful because the number of pucks can change without modifying ``TERBot``.

Task scheduler
~~~~~~~~~~~~~~

.. code-block:: java

   private final TaskScheduler scheduler;

The ``TaskScheduler`` is used to manage simple tasks that last several simulation steps.

For example:

* moving for a short time;
* waiting while the arm moves;
* executing timed actions.

In the current full behavior, many actions are still managed with counters, but the scheduler remains available for future improvements or simpler behaviors.

Behavior
~~~~~~~~

.. code-block:: java

   private RobotBehavior behavior;

The ``behavior`` attribute stores the current behavior of the robot.

A behavior defines what the robot does during the simulation.

For example:

* avoiding obstacles;
* collecting pucks;
* testing motors;
* testing sensors.

This attribute is not ``final`` because it can be changed with:

.. code-block:: java

   robot.setBehavior(...);

Constructor
-----------

The constructor is the most important part of ``TERBot``.

It creates and prepares the robot API.

.. code-block:: java

   public TERBot() {
     this.supervisor = new Supervisor();
     this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

     this.driveBase = new DriveBase(supervisor);
     this.sensorManager = new SensorManager(supervisor, timeStep);
     this.arm = new Arm(supervisor, timeStep);
     this.gripper = new Gripper(supervisor);

     this.puckManager = PuckManager.findAllWithPrefix(supervisor, "PALET_");

     this.scheduler = new TaskScheduler();

     arm.lift();
     gripper.open();
   }

Step 1: creating the Supervisor
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.supervisor = new Supervisor();

This creates the Webots supervisor object.

It is the link between the Java controller and the Webots simulation.

Step 2: reading the time step
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

This reads the basic time step from the Webots world.

The same value is later used in the main simulation loop.

Step 3: creating the movement API
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.driveBase = new DriveBase(supervisor);

This creates the API that controls the wheels.

The ``DriveBase`` uses the ``Supervisor`` to retrieve the wheel motors from Webots.

Step 4: creating the sensor API
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.sensorManager = new SensorManager(supervisor, timeStep);

This creates the API that manages all sensors.

The ``timeStep`` is given to the ``SensorManager`` because sensors must be enabled with a time step.

Step 5: creating the arm and gripper
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.arm = new Arm(supervisor, timeStep);
   this.gripper = new Gripper(supervisor);

This creates the mechanical actuator APIs.

The arm receives the ``timeStep`` because it uses a position sensor.

The gripper does not need a ``timeStep`` because it only uses motors.

Step 6: detecting pucks automatically
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.puckManager = PuckManager.findAllWithPrefix(supervisor, "PALET_");

This line automatically searches the Webots world for all pucks.

Any object whose ``DEF`` name starts with ``PALET_`` is considered a puck.

This avoids writing a fixed list such as:

.. code-block:: java

   new String[] {"PALET_1", "PALET_2", "PALET_3"}

With the current version, if the world contains nine pucks, for example:

.. code-block:: text

   PALET_1
   PALET_2
   PALET_3
   PALET_4
   PALET_5
   PALET_6
   PALET_7
   PALET_8
   PALET_9

they can be loaded automatically.

Step 7: creating the task scheduler
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.scheduler = new TaskScheduler();

This creates the task scheduler.

It can be used by behaviors to execute actions over time.

Step 8: setting the initial robot position
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   arm.lift();
   gripper.open();

At the start of the simulation:

* the arm is lifted;
* the gripper is opened.

This makes the robot ready to search for a puck.

Access methods
--------------

The ``TERBot`` class provides public access methods.

These methods allow behaviors to use the robot modules without directly accessing the private attributes.

supervisor
~~~~~~~~~~

.. code-block:: java

   public Supervisor supervisor() {
     return supervisor;
   }

Returns the Webots ``Supervisor``.

This is useful when a behavior needs direct access to the robot position or orientation.

Example:

.. code-block:: java

   double[] position = robot.supervisor().getSelf().getPosition();

timeStep
~~~~~~~~

.. code-block:: java

   public int timeStep() {
     return timeStep;
   }

Returns the Webots simulation time step.

motors
~~~~~~

.. code-block:: java

   public DriveBase motors() {
     return driveBase;
   }

Returns the motor API.

Example:

.. code-block:: java

   robot.motors().forward(3.0);
   robot.motors().stop();

sensors
~~~~~~~

.. code-block:: java

   public SensorManager sensors() {
     return sensorManager;
   }

Returns the sensor API.

Example:

.. code-block:: java

   if (robot.sensors().frontDetectsObject(350.0)) {
     robot.motors().turnLeft(2.0);
   }

arm
~~~

.. code-block:: java

   public Arm arm() {
     return arm;
   }

Returns the arm API.

Example:

.. code-block:: java

   robot.arm().lower();

gripper
~~~~~~~

.. code-block:: java

   public Gripper gripper() {
     return gripper;
   }

Returns the gripper API.

Example:

.. code-block:: java

   robot.gripper().close();

pucks
~~~~~

.. code-block:: java

   public PuckManager pucks() {
     return puckManager;
   }

Returns the puck manager.

Example:

.. code-block:: java

   int puckIndex = robot.pucks().findNearestAvailablePuck();

scheduler
~~~~~~~~~

.. code-block:: java

   public TaskScheduler scheduler() {
     return scheduler;
   }

Returns the task scheduler.

Example:

.. code-block:: java

   robot.scheduler().update();

In normal use, the scheduler is updated automatically inside ``run``.

Behavior management
-------------------

The robot behavior is assigned using:

.. code-block:: java

   public void setBehavior(RobotBehavior behavior) {
     this.behavior = behavior;
   }

A behavior is a class that defines what the robot should do.

Example:

.. code-block:: java

   TERBot robot = new TERBot();
   robot.setBehavior(new CollectPucksBehavior(robot));
   robot.run();

In this example:

* the robot is created;
* the collection behavior is assigned;
* the robot starts running.

The run method
--------------

The ``run`` method starts the simulation loop.

.. code-block:: java

   public void run() {
     if (behavior != null) {
       behavior.init();
     }

     while (supervisor.step(timeStep) != -1) {
       scheduler.update();

       if (behavior != null) {
         behavior.update();
       }
     }
   }

This method is the heart of the robot execution.

It does three important things:

1. It initializes the behavior.
2. It advances the Webots simulation.
3. It updates the scheduler and the behavior at each step.

Behavior initialization
~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   if (behavior != null) {
     behavior.init();
   }

The ``init`` method of the behavior is called once before the simulation loop starts.

It can be used to prepare the robot.

For example, a behavior can lift the arm and open the gripper.

Simulation loop
~~~~~~~~~~~~~~~

.. code-block:: java

   while (supervisor.step(timeStep) != -1) {
     ...
   }

This is the standard Webots loop.

At each iteration, Webots advances the simulation by one step.

The loop continues until the simulation stops.

Scheduler update
~~~~~~~~~~~~~~~~

.. code-block:: java

   scheduler.update();

At each step, the scheduler is updated.

This allows timed tasks to continue running.

Behavior update
~~~~~~~~~~~~~~~

.. code-block:: java

   if (behavior != null) {
     behavior.update();
   }

At each step, the behavior is updated.

This is where the robot decides what to do next.

For example, the behavior can decide to:

* move forward;
* turn;
* stop;
* approach a puck;
* lower the arm;
* close the gripper;
* go to the drop zone.

stop
----

.. code-block:: java

   public void stop() {
     driveBase.stop();
   }

The ``stop`` method stops the robot wheels.

It is a shortcut for:

.. code-block:: java

   robot.motors().stop();

Example main controller
-----------------------

A simple controller using the API can be written like this:

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

This file is short because the robot initialization is handled by ``TERBot``.

Execution order
---------------

When the controller starts, the following steps happen:

.. code-block:: text

   1. The main controller creates a TERBot object.
   2. TERBot creates the Webots Supervisor.
   3. TERBot reads the simulation time step.
   4. TERBot creates the motor API.
   5. TERBot creates the sensor API.
   6. TERBot creates the arm API.
   7. TERBot creates the gripper API.
   8. TERBot automatically detects all PALET_ objects.
   9. TERBot creates the task scheduler.
   10. TERBot lifts the arm.
   11. TERBot opens the gripper.
   12. The main controller assigns a behavior.
   13. TERBot calls behavior.init().
   14. The Webots simulation loop starts.
   15. At each step, the scheduler is updated.
   16. At each step, the behavior is updated.

How TERBot helps understanding the project
------------------------------------------

``TERBot`` makes the project easier to understand because it gives one central object to access everything.

Instead of writing:

.. code-block:: java

   Motor wheel1 = robot.getMotor("wheel1");
   DistanceSensor sensor = robot.getDistanceSensor("ds_front");
   Motor armMotor = robot.getMotor("arm_motor");

The behavior can write:

.. code-block:: java

   robot.motors().forward(3.0);
   robot.sensors().frontDistance();
   robot.arm().lower();

This makes the code closer to natural robot actions.

Summary
-------

The ``TERBot`` class is the central class of the API.

It creates and connects:

* the Webots ``Supervisor``;
* the movement API;
* the sensor API;
* the arm API;
* the gripper API;
* the puck manager;
* the task scheduler;
* the robot behavior.

It also contains the main Webots loop through the ``run`` method.

The main advantage of ``TERBot`` is that it hides the technical initialization and provides a simple way to control the robot.

With ``TERBot``, the rest of the project can focus on the robot mission instead of low-level Webots setup.