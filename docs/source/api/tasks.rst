Tasks API
=========

Overview
--------

The ``api.tasks`` package contains the classes used to manage actions that last several simulation steps.

In a robot controller, some actions cannot be completed instantly. For example:

* lowering the arm takes time;
* closing the gripper takes time;
* lifting the arm takes time;
* moving backward for a short duration takes time;
* turning for a fixed duration takes time.

The goal of this package is to avoid writing many counters directly inside the behavior classes.

Instead, the API uses tasks.

A task is an action that can:

* start;
* update itself at each simulation step;
* say when it is finished;
* optionally execute something when it ends.

Package location
----------------

The tasks API is located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── tasks/
               ├── RobotTask.java
               ├── TaskScheduler.java
               └── TimedTask.java

Package declaration
-------------------

Each file in this package starts with:

.. code-block:: java

   package api.tasks;

This means that the files must be located in:

.. code-block:: text

   api/tasks/

Main classes
------------

The package contains three main classes:

.. list-table::
   :header-rows: 1

   * - Class
     - Role
   * - ``RobotTask``
     - Defines the common structure of a task.
   * - ``TaskScheduler``
     - Stores and updates active tasks.
   * - ``TimedTask``
     - Represents a task that lasts for a fixed number of simulation steps.

General organization
--------------------

The task system is organized like this:

.. code-block:: text

   TERBot
      |
      └── TaskScheduler
              |
              ├── RobotTask
              ├── RobotTask
              └── TimedTask

At each simulation step, ``TERBot`` updates the scheduler:

.. code-block:: java

   scheduler.update();

Then the scheduler updates all active tasks.

RobotTask
---------

Overview
~~~~~~~~

``RobotTask`` is an interface.

It defines the structure that every task must follow.

A task can be used to represent an action that happens over time.

For example:

* lower the arm during 30 simulation steps;
* close the gripper during 20 simulation steps;
* move backward during 40 simulation steps;
* turn during 60 simulation steps.

Class code
~~~~~~~~~~

.. code-block:: java

   package api.tasks;

   public interface RobotTask {
     default void start() {}
     void update();
     boolean isFinished();
     default void end() {}
   }

Methods
~~~~~~~

The interface defines four methods:

.. list-table::
   :header-rows: 1

   * - Method
     - Role
   * - ``start()``
     - Called once when the task begins.
   * - ``update()``
     - Called at each simulation step while the task is active.
   * - ``isFinished()``
     - Returns ``true`` when the task is finished.
   * - ``end()``
     - Called once when the task finishes.

start
^^^^^

.. code-block:: java

   default void start() {}

The ``start`` method is called when the task is added to the scheduler.

It is declared as a default method, so a task is not forced to redefine it.

This method is useful when a task needs to initialize something before running.

Example:

.. code-block:: java

   @Override
   public void start() {
     counter = 0;
   }

update
^^^^^^

.. code-block:: java

   void update();

The ``update`` method is called at every simulation step.

This is where the task performs its action.

For example, a task can keep the robot moving backward:

.. code-block:: java

   @Override
   public void update() {
     bot.motors().backward(2.0);
   }

This method is required because it has no default implementation.

isFinished
^^^^^^^^^^

.. code-block:: java

   boolean isFinished();

This method tells the scheduler if the task is finished.

If it returns ``true``, the scheduler calls ``end`` and removes the task from the active task list.

Example:

.. code-block:: java

   @Override
   public boolean isFinished() {
     return counter >= duration;
   }

end
^^^

.. code-block:: java

   default void end() {}

The ``end`` method is called once when the task is finished.

It is also a default method, so it is optional.

This method is useful to stop the robot or launch a final action.

Example:

.. code-block:: java

   @Override
   public void end() {
     bot.motors().stop();
   }

Why use an interface?
~~~~~~~~~~~~~~~~~~~~~

Using an interface allows the project to create different types of tasks.

For example:

* a timed task;
* a task waiting for a sensor event;
* a task waiting for the arm to reach a position;
* a task that combines several actions.

All these tasks can be managed by the same ``TaskScheduler`` because they follow the same interface.

TaskScheduler
-------------

Overview
~~~~~~~~

``TaskScheduler`` manages all active tasks.

It stores tasks in a list and updates them at each simulation step.

When a task is finished, the scheduler automatically removes it.

Class code
~~~~~~~~~~

.. code-block:: java

   package api.tasks;

   import java.util.ArrayList;
   import java.util.Iterator;
   import java.util.List;

   public class TaskScheduler {
     private final List<RobotTask> tasks = new ArrayList<RobotTask>();

     public void add(RobotTask task) {
       if (task != null) {
         task.start();
         tasks.add(task);
       }
     }

     public void update() {
       Iterator<RobotTask> iterator = tasks.iterator();

       while (iterator.hasNext()) {
         RobotTask task = iterator.next();
         task.update();

         if (task.isFinished()) {
           task.end();
           iterator.remove();
         }
       }
     }

     public void clear() { tasks.clear(); }
     public boolean hasTasks() { return !tasks.isEmpty(); }
     public int size() { return tasks.size(); }
   }

Attribute
~~~~~~~~~

The scheduler stores tasks in a list:

.. code-block:: java

   private final List<RobotTask> tasks = new ArrayList<RobotTask>();

This list contains all currently active tasks.

A task stays in the list until its ``isFinished`` method returns ``true``.

add
~~~

.. code-block:: java

   public void add(RobotTask task) {
     if (task != null) {
       task.start();
       tasks.add(task);
     }
   }

This method adds a new task to the scheduler.

It first checks that the task is not ``null``.

Then it calls:

.. code-block:: java

   task.start();

Finally, it adds the task to the list.

This means that ``start`` is called only once, when the task begins.

Example:

.. code-block:: java

   bot.scheduler().add(new TimedTask(40, () -> bot.motors().backward(2.0)));

update
~~~~~~

.. code-block:: java

   public void update() {
     Iterator<RobotTask> iterator = tasks.iterator();

     while (iterator.hasNext()) {
       RobotTask task = iterator.next();
       task.update();

       if (task.isFinished()) {
         task.end();
         iterator.remove();
       }
     }
   }

This method updates all active tasks.

For each task, the scheduler:

1. calls ``update``;
2. checks ``isFinished``;
3. if the task is finished, calls ``end``;
4. removes the task from the list.

The scheduler uses an ``Iterator`` because tasks are removed while looping through the list.

This avoids errors that can happen when removing elements from a list during a loop.

clear
~~~~~

.. code-block:: java

   public void clear() {
     tasks.clear();
   }

This method removes all active tasks.

It can be useful when the robot changes state and all previous tasks should be cancelled.

Example:

.. code-block:: java

   bot.scheduler().clear();

hasTasks
~~~~~~~~

.. code-block:: java

   public boolean hasTasks() {
     return !tasks.isEmpty();
   }

This method returns ``true`` if at least one task is currently active.

Example:

.. code-block:: java

   if (bot.scheduler().hasTasks()) {
     return;
   }

This is useful in behaviors to avoid starting a new action while another task is still running.

size
~~~~

.. code-block:: java

   public int size() {
     return tasks.size();
   }

This method returns the number of active tasks.

It can be useful for debugging.

Example:

.. code-block:: java

   System.out.println("Active tasks: " + bot.scheduler().size());

TimedTask
---------

Overview
~~~~~~~~

``TimedTask`` is a concrete implementation of ``RobotTask``.

It represents a task that lasts for a fixed number of simulation steps.

It executes:

* an action while the task is running;
* an optional final action when the task ends.

This class is useful for simple timed actions.

Class code
~~~~~~~~~~

.. code-block:: java

   package api.tasks;

   public class TimedTask implements RobotTask {
     private final int duration;
     private final Runnable action;
     private final Runnable endAction;
     private int counter;

     public TimedTask(int duration, Runnable action) {
       this(duration, action, null);
     }

     public TimedTask(int duration, Runnable action, Runnable endAction) {
       this.duration = duration;
       this.action = action;
       this.endAction = endAction;
       this.counter = 0;
     }

     @Override
     public void start() {
       counter = 0;
     }

     @Override
     public void update() {
       if (action != null) {
         action.run();
       }
       counter++;
     }

     @Override
     public boolean isFinished() {
       return counter >= duration;
     }

     @Override
     public void end() {
       if (endAction != null) {
         endAction.run();
       }
     }
   }

Attributes
~~~~~~~~~~

The class contains four attributes:

.. code-block:: java

   private final int duration;
   private final Runnable action;
   private final Runnable endAction;
   private int counter;

``duration``
^^^^^^^^^^^^

The ``duration`` is the number of simulation steps during which the task runs.

Example:

.. code-block:: java

   new TimedTask(40, action);

This task lasts 40 simulation steps.

``action``
^^^^^^^^^^

The ``action`` is executed at each simulation step while the task is active.

It is a ``Runnable``, which means it can be written as a lambda expression.

Example:

.. code-block:: java

   () -> bot.motors().backward(2.0)

``endAction``
^^^^^^^^^^^^^

The ``endAction`` is optional.

It is executed once when the task ends.

Example:

.. code-block:: java

   () -> bot.motors().stop()

``counter``
^^^^^^^^^^^

The ``counter`` stores how many simulation steps have passed since the task started.

Constructors
~~~~~~~~~~~~

Constructor without end action
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   public TimedTask(int duration, Runnable action) {
     this(duration, action, null);
   }

This constructor creates a timed task with no final action.

Example:

.. code-block:: java

   new TimedTask(30, () -> bot.arm().lower());

Constructor with end action
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: java

   public TimedTask(int duration, Runnable action, Runnable endAction) {
     this.duration = duration;
     this.action = action;
     this.endAction = endAction;
     this.counter = 0;
   }

This constructor creates a timed task with:

* a duration;
* an action repeated while the task is active;
* a final action executed when the task ends.

Example:

.. code-block:: java

   new TimedTask(
     40,
     () -> bot.motors().backward(2.0),
     () -> bot.motors().stop()
   );

start
~~~~~

.. code-block:: java

   @Override
   public void start() {
     counter = 0;
   }

When the task starts, the counter is reset to zero.

This ensures that the task always runs for its full duration.

update
~~~~~~

.. code-block:: java

   @Override
   public void update() {
     if (action != null) {
       action.run();
     }
     counter++;
   }

At each simulation step, the task:

1. executes the action if it exists;
2. increments the counter.

Example:

.. code-block:: java

   new TimedTask(20, () -> bot.gripper().close());

The gripper close command will be sent during 20 simulation steps.

isFinished
~~~~~~~~~~

.. code-block:: java

   @Override
   public boolean isFinished() {
     return counter >= duration;
   }

The task is finished when the counter reaches the duration.

Example:

.. code-block:: text

   duration = 40

   counter = 0   -> task active
   counter = 20  -> task active
   counter = 40  -> task finished

end
~~~

.. code-block:: java

   @Override
   public void end() {
     if (endAction != null) {
       endAction.run();
     }
   }

When the task finishes, the optional ``endAction`` is executed.

This is useful to stop a movement after a timed action.

Example:

.. code-block:: java

   new TimedTask(
     40,
     () -> bot.motors().backward(2.0),
     () -> bot.motors().stop()
   );

In this example:

* the robot moves backward for 40 steps;
* then it stops.

How tasks are updated
---------------------

The task scheduler is updated in the ``TERBot`` main loop.

In ``TERBot.run()``:

.. code-block:: java

   while (supervisor.step(timeStep) != -1) {
     scheduler.update();
     if (behavior != null) {
       behavior.update();
     }
   }

This means that tasks are updated before the behavior at each simulation step.

This order is important.

It allows scheduled actions to continue running regularly while the robot behavior is also updated.

Example: move backward for a short time
---------------------------------------

The following task makes the robot move backward for 35 simulation steps, then stop:

.. code-block:: java

   bot.scheduler().add(
     new TimedTask(
       35,
       () -> bot.motors().backward(2.0),
       () -> bot.motors().stop()
     )
   );

Explanation:

* ``35`` is the duration;
* the second argument is the action repeated at each step;
* the third argument is the action executed at the end.

Example: close the gripper for a short time
-------------------------------------------

.. code-block:: java

   bot.scheduler().add(
     new TimedTask(
       25,
       () -> bot.gripper().close()
     )
   );

This task sends the close command to the gripper for 25 simulation steps.

Example: task sequence idea
---------------------------

The current scheduler can run several tasks at the same time.

For a sequence, a behavior can use states to add one task at a time.

Example logic:

.. code-block:: text

   State LOWER_ARM:
       add TimedTask to lower arm
       when scheduler has no tasks:
           go to CLOSE_GRIPPER

   State CLOSE_GRIPPER:
       add TimedTask to close gripper
       when scheduler has no tasks:
           go to LIFT_ARM

   State LIFT_ARM:
       add TimedTask to lift arm
       when scheduler has no tasks:
           go to GO_TO_DROP_ZONE

This approach keeps timed actions clean and readable.

Important note about parallel tasks
-----------------------------------

The ``TaskScheduler`` can contain several tasks at the same time.

This means that if several tasks are added before previous tasks finish, they will all be updated together.

This can be useful, but it can also cause conflicts.

For example, if one task tells the robot to move forward and another task tells it to stop, the final behavior may be confusing.

To avoid conflicts, behaviors should usually check:

.. code-block:: java

   if (!bot.scheduler().hasTasks()) {
     bot.scheduler().add(...);
   }

This prevents adding too many tasks at once.

Debugging
---------

If a timed action does not work, check:

* the task is added with ``bot.scheduler().add(...)``;
* ``bot.scheduler().update()`` is called at each simulation step;
* the task duration is not too short;
* the action is not ``null``;
* another part of the behavior is not immediately overriding the same motors;
* ``bot.scheduler().hasTasks()`` is used correctly;
* ``bot.scheduler().size()`` returns the expected number of tasks.

Example debug print:

.. code-block:: java

   System.out.println("Active tasks: " + bot.scheduler().size());

Summary
-------

The ``api.tasks`` package is used to manage actions over time.

``RobotTask`` defines the structure of a task.

``TaskScheduler`` stores, updates, and removes active tasks.

``TimedTask`` is a simple task that runs for a fixed number of simulation steps.

This package is especially useful for actions such as:

* lowering the arm;
* closing the gripper;
* lifting the arm;
* backing up;
* turning for a short time.

It helps keep behavior code cleaner and avoids writing too many manual counters.