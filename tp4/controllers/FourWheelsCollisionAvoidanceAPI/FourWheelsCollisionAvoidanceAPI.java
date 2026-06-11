import api.behavior.*;
import api.core.Robot;

public class FourWheelsCollisionAvoidanceAPI {
  public static void main(String[] args) {
    Robot robot = new Robot();
    robot.setBehavior(new CollectPucksBehavior(robot));
    robot.run();
  }
}
