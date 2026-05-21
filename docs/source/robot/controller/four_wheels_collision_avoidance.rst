################################
FourWheelsCollisionAvoidance
################################

*****************
Role of the class
*****************

The ``FourWheelsCollisionAvoidance`` class is the main class of the Webots
controller. It is the entry point of the program. Webots launches this class
when the robot uses the ``FourWheelsCollisionAvoidance`` controller.

Its role is intentionally limited. It does not contain the complete robot
logic. It simply creates a ``Supervisor`` object, then creates a ``TERBot``
object and calls its ``run()`` method.

****************
Responsibilities
****************

- Start the Webots controller.
- Create the ``Supervisor`` object.
- Create the main ``TERBot`` object.
- Start the main robot loop with ``run()``.

*************
Encapsulation
*************

This class contains almost no internal state. It only serves as an entry
point.

The robot logic is delegated to the ``TERBot`` class. This avoids having a
main file that is too long and difficult to maintain.

*****************************
Relations with other classes
*****************************

``FourWheelsCollisionAvoidance`` depends on ``Supervisor``, provided by the
Webots API, and on ``TERBot``, which contains the main robot logic.

This is a creation relationship: the main class creates an instance of
``TERBot``.

*********
Functions
*********

``main(String[] args)``
~~~~~~~~~~~~~~~~~~~~~~~

Entry point of the Java program. This method is automatically called when
the controller is launched by Webots.

It performs the following actions:

#. Create a ``Supervisor`` object.
#. Create a ``TERBot`` object.
#. Call the robot ``run()`` method.

Example:

.. code-block:: java

   Supervisor supervisor = new Supervisor();
   TERBot robot = new TERBot(supervisor);
   robot.run();

This method does not directly contain the movement, detection or puck
collection logic.