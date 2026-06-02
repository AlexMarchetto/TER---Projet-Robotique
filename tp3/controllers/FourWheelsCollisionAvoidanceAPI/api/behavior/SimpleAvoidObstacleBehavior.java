package api.behavior;
import api.core.Robot;

public class SimpleAvoidObstacleBehavior implements RobotBehavior {
  private final Robot robot;
  private static final double SPEED = 4.0;
  private static final double TURN_SPEED = 3.0;
  private static final double FRONT_THRESHOLD = 350.0;
  private static final double SIDE_THRESHOLD = 900.0;
  public SimpleAvoidObstacleBehavior(Robot robot) { this.robot = robot; }
  @Override public void init() { robot.arm().lift(); robot.gripper().open(); }
  @Override public void update() {
    robot.sensors().update();
    if (robot.sensors().frontDetectsObject(FRONT_THRESHOLD)) { robot.motors().turnLeft(TURN_SPEED); }
    else if (robot.sensors().leftDetectsObject(SIDE_THRESHOLD)) { robot.motors().turnRight(TURN_SPEED); }
    else if (robot.sensors().rightDetectsObject(SIDE_THRESHOLD)) { robot.motors().turnLeft(TURN_SPEED); }
    else { robot.motors().forward(SPEED); }
  }
}
