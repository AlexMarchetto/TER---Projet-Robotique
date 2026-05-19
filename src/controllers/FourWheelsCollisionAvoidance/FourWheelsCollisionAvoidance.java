import com.cyberbotics.webots.controller.Supervisor;

public class FourWheelsCollisionAvoidance {
  public static void main(String[] args) {
    System.out.println("Bonjour, ceci est un affichage dans la console.");
    Supervisor supervisor = new Supervisor();

    TERBot robot = new TERBot(supervisor);
    robot.run();
  }
}
