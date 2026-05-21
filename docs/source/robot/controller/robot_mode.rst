################
RobotMode
################

*****************
Role of the class
*****************

``RobotMode`` is a Java enumeration.

It defines the different possible states of the robot and provides a clean
representation of the state machine used by ``TERBot``.

****************
Responsibilities
****************

- List the possible robot states.
- Make the state machine easier to understand.
- Replace string values with controlled values.
- Improve code readability.

*************
Encapsulation
*************

The enumeration does not contain any internal attributes.

Its sole purpose is to group together the possible values of the robot
mode.

*****************************
Relations with other classes
*****************************

``RobotMode`` is used by ``TERBot`` through the ``mode`` attribute.

******
Values
******

``SEARCH`` : searching for a puck.

``TOUCH_AVOID`` : avoidance behavior after contact with an obstacle.

``APPROACH_PUCK`` : approaching a targeted puck.

``LOWER_ARM`` : lowering the arm.

``CLOSE_GRIPPER`` : closing the gripper.

``LIFT_ARM`` : raising the arm after grasping.

``GO_TO_DROP_ZONE`` : moving toward the drop zone.

``DROP_PUCK`` : dropping the puck.

``LIFT_ARM_AFTER_DROP`` : raising the arm after dropping the puck.

``BACK_AND_TURN_AFTER_DROP`` : reversing and turning after dropping the puck.