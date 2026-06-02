import api.behavior.*;
import api.core.TERBot;

public class FourWheelsCollisionAvoidanceAPI {
  public static void main(String[] args) {
    TERBot robot = new TERBot();
    robot.setBehavior(new CollectPucksBehavior(robot));
    robot.run();
  }
}
