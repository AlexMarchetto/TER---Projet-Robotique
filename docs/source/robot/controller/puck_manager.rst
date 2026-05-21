################
PuckManager
################

*****************
Role of the class
*****************

The ``PuckManager`` class manages the pucks present in the Webots scene.

It centralizes all actions related to pucks: searching, distance
calculation, attachment to the robot, dropping, and tracking of delivered
pucks.

****************
Responsibilities
****************

- Retrieve pucks from their DEF names.
- Store the ``Node`` objects of the pucks.
- Store the ``translation`` fields of the pucks.
- Determine whether a puck has already been delivered.
- Find the nearest available puck to the robot.
- Compute the distance between the robot and a puck.
- Make a puck follow the robot while it is being transported.
- Drop a puck at a given position.

*************
Encapsulation
*************

Information about the pucks is stored in private attributes:
``puckNames``, ``puckNodes``, ``puckTranslationFields`` and
``puckDelivered``.

``TERBot`` does not directly manipulate these arrays.

*****************************
Relations with other classes
*****************************

``PuckManager`` is owned by ``TERBot``.

It uses ``Supervisor``, ``Node``, ``Field`` and ``MathUtils``.

*********
Functions
*********

``PuckManager(Supervisor robot, String[] puckNames)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Constructor.

It receives the Webots ``Supervisor`` and the list of puck names.

For each puck, it searches for the corresponding node using
``getFromDef``. If the puck is found, it retrieves its ``translation``
field and initializes it as not delivered.

``findNearestAvailablePuck()``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Searches for the nearest available puck to the robot.

The method ignores non-existing pucks and pucks that have already been
delivered.

It returns the index of the found puck, or ``-1`` if no available puck
is found.

``getPuckNode(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~

Returns the Webots ``Node`` of a puck from its index.

If the index is invalid, it returns ``null``.

``getDistanceToPuck(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Computes the distance between the robot and a puck.

If the puck does not exist, it returns ``Double.MAX_VALUE``.

``isDelivered(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~

Indicates whether a puck has already been delivered.

If the index is invalid, it returns ``true`` in order to consider the
puck unusable.

``getPuckName(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~

Returns the name of a puck from its index.

If the index is invalid, it returns ``unknown``.

``attachPuckToRobot(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Places the puck in front of the robot to simulate transportation.

The method computes a local position in front of the robot, converts it
to a world position, modifies the puck ``translation`` field, then calls
``resetPhysics``.

``dropPuck(int index, double dropX, double dropY, double dropZ)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Drops a puck at a given position.

The method marks the puck as delivered, updates its position, resets its
physics and displays a debug message.