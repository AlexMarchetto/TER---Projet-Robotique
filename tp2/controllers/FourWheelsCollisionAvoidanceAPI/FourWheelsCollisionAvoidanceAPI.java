import api.behavior.SimpleAvoidObstacleBehavior;
import api.core.TERBot;

public class FourWheelsCollisionAvoidanceAPI {
  public static void main(String[] args) {
    TERBot robot = new TERBot();
    robot.setBehavior(new SimpleAvoidObstacleBehavior(robot));
    robot.run();
  }
}
