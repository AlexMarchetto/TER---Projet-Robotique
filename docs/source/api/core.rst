Core API — TERBot
=================

Overview
--------

The ``api.core`` package contains the central class of the robot API:

.. code-block:: text

   TERBot.java

The ``TERBot`` class represents the robot at the API level.

It is the main access point used by the controller and by the robot behaviors.  
Instead of creating and managing every Webots component manually in the main controller, ``TERBot`` creates and stores all important robot modules.

The class gives access to:

* the Webots ``Supervisor``;
* the simulation time step;
* the drive base;
* the sensors;
* the arm;
* the gripper;
* the puck manager;
* the task scheduler;
* the current robot behavior.

In other words, ``TERBot`` acts as the central object that connects all parts of the robot API together.

Class location
--------------

The class is located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── core/
               └── TERBot.java

Package declaration
-------------------

At the beginning of the file, the class belongs to the ``api.core`` package:

.. code-block:: java

   package api.core;

This means that the file must be located in the following folder:

.. code-block:: text

   api/core/

If the package name and the folder path do not match, Java will not compile the project correctly.

Imports
-------

The class imports the Webots ``Supervisor`` class:

.. code-block:: java

   import com.cyberbotics.webots.controller.Supervisor;

It also imports the different modules of the robot API:

.. code-block:: java

   import api.actuators.Arm;
   import api.actuators.Gripper;
   import api.behavior.RobotBehavior;
   import api.motors.DriveBase;
   import api.sensors.SensorManager;
   import api.tasks.TaskScheduler;
   import api.world.PuckManager;

These imports show that ``TERBot`` depends on several parts of the API.

Each imported class has a specific role:

.. list-table::
   :header-rows: 1

   * - Class
     - Role
   * - ``Supervisor``
     - Webots object used to control the robot and access the simulated world.
   * - ``DriveBase``
     - Controls the robot wheels and movements.
   * - ``SensorManager``
     - Gives access to all robot sensors.
   * - ``Arm``
     - Controls the robot arm.
   * - ``Gripper``
     - Controls the robot gripper.
   * - ``PuckManager``
     - Manages the pucks in the Webots world.
   * - ``TaskScheduler``
     - Manages timed tasks.
   * - ``RobotBehavior``
     - Represents the current high-level behavior of the robot.

Class declaration
-----------------

The class is declared as follows:

.. code-block:: java

   public class TERBot {

The class is public because it must be accessible from the main controller and from other packages.

Attributes
----------

The class contains several private attributes.

.. code-block:: java

   private final Supervisor supervisor;
   private final int timeStep;
   private final DriveBase driveBase;
   private final SensorManager sensorManager;
   private final Arm arm;
   private final Gripper gripper;
   private final PuckManager puckManager;
   private final TaskScheduler scheduler;
   private RobotBehavior behavior;

These attributes store the main parts of the robot.

``supervisor``
~~~~~~~~~~~~~~

.. code-block:: java

   private final Supervisor supervisor;

The ``supervisor`` is the Webots object used to communicate with the simulated world.

It allows the controller to:

* step the simulation;
* access the robot devices;
* access objects in the world;
* retrieve pucks using their ``DEF`` names;
* modify the position of objects when needed.

This project uses ``Supervisor`` instead of a simple Webots ``Robot`` because the robot needs to interact with world objects such as pucks.

``timeStep``
~~~~~~~~~~~~

.. code-block:: java

   private final int timeStep;

The ``timeStep`` is the duration of one simulation step in Webots.

It is used to:

* update the simulation;
* enable sensors;
* synchronize the robot controller with Webots.

It is retrieved from Webots using:

.. code-block:: java

   this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

``driveBase``
~~~~~~~~~~~~~

.. code-block:: java

   private final DriveBase driveBase;

The ``driveBase`` is the API module responsible for moving the robot.

It controls the wheels and provides methods such as:

* move forward;
* move backward;
* turn left;
* turn right;
* stop.

Example usage:

.. code-block:: java

   bot.motors().forward(2.0);
   bot.motors().stop();

``sensorManager``
~~~~~~~~~~~~~~~~~

.. code-block:: java

   private final SensorManager sensorManager;

The ``sensorManager`` gives access to all sensors of the robot.

It can manage:

* the front distance sensor;
* the left distance sensor;
* the right distance sensor;
* the front touch sensor;
* the color sensor.

Example usage:

.. code-block:: java

   bot.sensors().front().getValue();
   bot.sensors().touchFront().isTouched();

``arm``
~~~~~~~

.. code-block:: java

   private final Arm arm;

The ``arm`` controls the robot arm.

It is used to raise or lower the gripper.

Example usage:

.. code-block:: java

   bot.arm().lower();
   bot.arm().lift();

``gripper``
~~~~~~~~~~~

.. code-block:: java

   private final Gripper gripper;

The ``gripper`` controls the robot gripper.

It is used to open or close the gripper during puck collection.

Example usage:

.. code-block:: java

   bot.gripper().open();
   bot.gripper().close();

``puckManager``
~~~~~~~~~~~~~~~

.. code-block:: java

   private final PuckManager puckManager;

The ``puckManager`` manages the pucks in the world.

It can be used to:

* find pucks;
* know which puck is closest;
* attach a puck to the robot;
* mark a puck as delivered;
* drop a puck in the base.

In the current code, three pucks are used:

.. code-block:: java

   new String[] {"PALET_1", "PALET_2", "PALET_3"}

This means that the Webots world must contain pucks with the following ``DEF`` names:

.. code-block:: text

   PALET_1
   PALET_2
   PALET_3

``scheduler``
~~~~~~~~~~~~~

.. code-block:: java

   private final TaskScheduler scheduler;

The ``scheduler`` manages tasks that last several simulation steps.

This is useful for actions that cannot be completed instantly, such as:

* lowering the arm;
* closing the gripper;
* lifting the arm;
* moving backward for a short time;
* turning for a fixed duration.

``behavior``
~~~~~~~~~~~~

.. code-block:: java

   private RobotBehavior behavior;

The ``behavior`` represents the current high-level behavior of the robot.

For example, the behavior can be:

* a simple obstacle avoidance behavior;
* a puck collection behavior;
* a custom behavior created for a practical session.

Unlike the other attributes, ``behavior`` is not ``final`` because it can be assigned after the robot is created.

Constructor
-----------

The constructor initializes all parts of the robot API.

.. code-block:: java

   public TERBot() {
     this.supervisor = new Supervisor();
     this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());
     this.driveBase = new DriveBase(supervisor);
     this.sensorManager = new SensorManager(supervisor, timeStep);
     this.arm = new Arm(supervisor, timeStep);
     this.gripper = new Gripper(supervisor);
     this.puckManager = new PuckManager(supervisor, new String[] {"PALET_1", "PALET_2", "PALET_3"});
     this.scheduler = new TaskScheduler();
     arm.lift();
     gripper.open();
   }

Creating the Supervisor
~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.supervisor = new Supervisor();

This line creates the Webots supervisor object.

It is the connection between the Java code and the Webots simulation.

Retrieving the time step
~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

This line retrieves the basic time step of the simulation.

This value is required to update the robot and enable sensors correctly.

Creating the drive base
~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.driveBase = new DriveBase(supervisor);

This line creates the motor API.

The ``DriveBase`` receives the ``Supervisor`` because it needs to access the wheel motors from Webots.

Creating the sensor manager
~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.sensorManager = new SensorManager(supervisor, timeStep);

This line creates the sensor API.

The ``SensorManager`` receives:

* the ``Supervisor``, to access the sensors;
* the ``timeStep``, to enable the sensors.

Creating the arm
~~~~~~~~~~~~~~~~

.. code-block:: java

   this.arm = new Arm(supervisor, timeStep);

This line creates the arm API.

The arm needs the ``Supervisor`` to access its motor, and the ``timeStep`` to enable its position sensor if needed.

Creating the gripper
~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.gripper = new Gripper(supervisor);

This line creates the gripper API.

The gripper uses the Webots motors that control the left and right parts of the gripper.

Creating the puck manager
~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.puckManager = new PuckManager(supervisor, new String[] {"PALET_1", "PALET_2", "PALET_3"});

This line creates the puck manager.

The puck manager receives:

* the ``Supervisor``;
* the list of puck names.

These names must match the ``DEF`` names in the Webots world.

Creating the scheduler
~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   this.scheduler = new TaskScheduler();

This line creates the task scheduler.

The scheduler will be updated at each simulation step.

Initial arm and gripper position
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

At the end of the constructor, the robot starts in a safe initial position:

.. code-block:: java

   arm.lift();
   gripper.open();

This means:

* the arm starts lifted;
* the gripper starts open.

This is useful because the robot begins the simulation ready to search for a puck.

Access methods
--------------

The class provides several public methods to access the robot modules.

These methods are simple getters.

Supervisor access
~~~~~~~~~~~~~~~~~

.. code-block:: java

   public Supervisor supervisor() { return supervisor; }

This method returns the Webots ``Supervisor``.

It should only be used when direct access to Webots is necessary.

Time step access
~~~~~~~~~~~~~~~~

.. code-block:: java

   public int timeStep() { return timeStep; }

This method returns the simulation time step.

Drive base access
~~~~~~~~~~~~~~~~~

.. code-block:: java

   public DriveBase motors() { return driveBase; }

This method returns the drive base API.

Example:

.. code-block:: java

   bot.motors().forward(2.0);

Sensor access
~~~~~~~~~~~~~

.. code-block:: java

   public SensorManager sensors() { return sensorManager; }

This method returns the sensor manager.

Example:

.. code-block:: java

   bot.sensors().front().getValue();

Arm access
~~~~~~~~~~

.. code-block:: java

   public Arm arm() { return arm; }

This method returns the arm API.

Example:

.. code-block:: java

   bot.arm().lift();

Gripper access
~~~~~~~~~~~~~~

.. code-block:: java

   public Gripper gripper() { return gripper; }

This method returns the gripper API.

Example:

.. code-block:: java

   bot.gripper().open();

Puck manager access
~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public PuckManager pucks() { return puckManager; }

This method returns the puck manager.

Example:

.. code-block:: java

   bot.pucks().findNearestAvailablePuck();

Scheduler access
~~~~~~~~~~~~~~~~

.. code-block:: java

   public TaskScheduler scheduler() { return scheduler; }

This method returns the task scheduler.

Example:

.. code-block:: java

   bot.scheduler().update();

Behavior management
-------------------

The robot behavior can be assigned using:

.. code-block:: java

   public void setBehavior(RobotBehavior behavior) {
     this.behavior = behavior;
   }

This method allows the main controller to choose what the robot should do.

Example:

.. code-block:: java

   TERBot bot = new TERBot();
   bot.setBehavior(new CollectPucksBehavior(bot));
   bot.run();

In this example, the robot uses the ``CollectPucksBehavior``.

The run method
--------------

The ``run`` method starts the main control loop of the robot.

.. code-block:: java

   public void run() {
     if (behavior != null) { behavior.init(); }
     while (supervisor.step(timeStep) != -1) {
       scheduler.update();
       if (behavior != null) { behavior.update(); }
     }
   }

This method is one of the most important parts of the class.

Behavior initialization
~~~~~~~~~~~~~~~~~~~~~~~

Before the loop starts, the behavior is initialized:

.. code-block:: java

   if (behavior != null) { behavior.init(); }

The ``init`` method is called only once.

It can be used to prepare the behavior before the simulation starts.

Main simulation loop
~~~~~~~~~~~~~~~~~~~~

The loop is:

.. code-block:: java

   while (supervisor.step(timeStep) != -1) {
     ...
   }

This is the standard Webots control loop.

The loop continues as long as the simulation is running.

Scheduler update
~~~~~~~~~~~~~~~~

At each simulation step, the scheduler is updated first:

.. code-block:: java

   scheduler.update();

This allows timed tasks to progress.

For example, a task that lasts 40 steps will be updated once per simulation step.

Behavior update
~~~~~~~~~~~~~~~

After the scheduler, the behavior is updated:

.. code-block:: java

   if (behavior != null) { behavior.update(); }

The behavior decides what the robot should do at the current simulation step.

For example, it can decide to:

* move forward;
* turn;
* stop;
* lower the arm;
* close the gripper;
* go to the drop zone.

Stop method
-----------

The class provides a simple stop method:

.. code-block:: java

   public void stop() {
     driveBase.stop();
   }

This method stops the robot wheels.

It is a shortcut for:

.. code-block:: java

   bot.motors().stop();

Example of use in the main controller
-------------------------------------

A simple main controller can be written as follows:

.. code-block:: java

   import api.core.TERBot;
   import api.behavior.CollectPucksBehavior;

   public class FourWheelsCollisionAvoidanceAPI {
     public static void main(String[] args) {
       TERBot bot = new TERBot();

       bot.setBehavior(new CollectPucksBehavior(bot));

       bot.run();
     }
   }

This main controller is very short because most of the work is done by the API.

Execution order
---------------

When the program starts, the following steps happen:

.. code-block:: text

   1. The main controller creates a TERBot object.
   2. TERBot creates the Supervisor.
   3. TERBot retrieves the simulation time step.
   4. TERBot creates the drive base.
   5. TERBot creates the sensor manager.
   6. TERBot creates the arm.
   7. TERBot creates the gripper.
   8. TERBot creates the puck manager.
   9. TERBot creates the task scheduler.
   10. The arm is lifted.
   11. The gripper is opened.
   12. A behavior is assigned with setBehavior.
   13. The run method starts the simulation loop.
   14. At each step, the scheduler is updated.
   15. At each step, the behavior is updated.

Why TERBot is useful
--------------------

Without ``TERBot``, the main controller would need to manually create and manage all components.

It would contain code for:

* creating the Webots robot;
* retrieving motors;
* retrieving sensors;
* enabling sensors;
* controlling the arm;
* controlling the gripper;
* managing pucks;
* updating tasks;
* running behaviors.

This would make the main controller long and difficult to understand.

With ``TERBot``, the main controller stays simple.

Example:

.. code-block:: java

   TERBot bot = new TERBot();
   bot.setBehavior(new CollectPucksBehavior(bot));
   bot.run();

This makes the project easier to read, easier to debug, and easier to extend.

Summary
-------

The ``TERBot`` class is the central class of the robot API.

It creates and stores all main robot modules:

* ``Supervisor``;
* ``DriveBase``;
* ``SensorManager``;
* ``Arm``;
* ``Gripper``;
* ``PuckManager``;
* ``TaskScheduler``;
* ``RobotBehavior``.

It also provides the main execution loop through the ``run`` method.

This class makes the rest of the controller much simpler, because all robot components can be accessed through a single object.