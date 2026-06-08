import api.behavior.SimpleAvoidObstacleBehavior;
import api.core.Robot;

public class FourWheelsCollisionAvoidanceAPI {
  public static void main(String[] args) {
    Robot robot = new Robot();
    robot.setBehavior(new SimpleAvoidObstacleBehavior(robot));
    robot.run();
  }
}
