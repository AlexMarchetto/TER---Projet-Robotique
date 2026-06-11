package api.behavior;

import api.core.Robot;

public class SimpleAvoidObstacleBehavior implements RobotBehavior {
  private final Robot robot;

  private static final double SPEED = 4.0;
  private static final double TURN_SPEED = 3.0;
  private static final double FRONT_THRESHOLD = 350.0;
  private static final double SIDE_THRESHOLD = 900.0;

  public SimpleAvoidObstacleBehavior(Robot robot) {
    this.robot = robot;
  }

  @Override
  public void init() {
    // Set the arm and gripper to their initial safe positions.
    robot.arm().lift();
    robot.gripper().open();
  }

  @Override
  public void update() {
    // Refresh sensor values before making a movement decision.
    robot.sensors().update();

    if (robot.sensors().frontDetectsObject(FRONT_THRESHOLD)) {
      // Turn left when an obstacle is detected in front.
      robot.motors().turnLeft(TURN_SPEED);
    } else if (robot.sensors().leftDetectsObject(SIDE_THRESHOLD)) {
      // Turn right to move away from an obstacle on the left.
      robot.motors().turnRight(TURN_SPEED);
    } else if (robot.sensors().rightDetectsObject(SIDE_THRESHOLD)) {
      // Turn left to move away from an obstacle on the right.
      robot.motors().turnLeft(TURN_SPEED);
    } else {
      // Move forward when no obstacle is detected.
      robot.motors().forward(SPEED);
    }
  }
}