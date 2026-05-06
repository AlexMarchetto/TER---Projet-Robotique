import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;

public class MyController {
  public static void main(String[] args) {
    final int TIME_STEP = 64;
    final double MAX_SPEED = 6.28;

    Robot robot = new Robot();

    DistanceSensor dsRight = robot.getDistanceSensor("ds_right");
    DistanceSensor dsLeft  = robot.getDistanceSensor("ds_left");
    dsRight.enable(TIME_STEP);
    dsLeft.enable(TIME_STEP);

    Motor wheel1 = robot.getMotor("wheel1"); // gauche avant
    Motor wheel2 = robot.getMotor("wheel2"); // droite avant
    Motor wheel3 = robot.getMotor("wheel3"); // gauche arrière
    Motor wheel4 = robot.getMotor("wheel4"); // droite arrière

    wheel1.setPosition(Double.POSITIVE_INFINITY);
    wheel2.setPosition(Double.POSITIVE_INFINITY);
    wheel3.setPosition(Double.POSITIVE_INFINITY);
    wheel4.setPosition(Double.POSITIVE_INFINITY);

    wheel1.setVelocity(0);
    wheel2.setVelocity(0);
    wheel3.setVelocity(0);
    wheel4.setVelocity(0);

    int avoidCounter = 0;
    int stepCount = 0;

    while (robot.step(TIME_STEP) != -1) {
      stepCount++;

      double right = dsRight.getValue();
      double left  = dsLeft.getValue();

      if (stepCount % 20 == 0) {
        System.out.printf("[Step %4d] ds_right=%.1f | ds_left=%.1f | avoidCounter=%d%n",
            stepCount, right, left, avoidCounter);
      }

      // Avancer : toutes les roues au même signe (phase 1 = avance)
      double leftSpeed  = MAX_SPEED;
      double rightSpeed = MAX_SPEED;

      if (avoidCounter > 0) {
        avoidCounter--;
        // Rotation sur place : toutes les roues dans le même sens
        // (phase 3 du test = tourne)
        leftSpeed  =  MAX_SPEED;
        rightSpeed = -MAX_SPEED;
        if (avoidCounter == 0) {
          System.out.println(">>> Fin de rotation, reprise en ligne droite.");
        }
      } else {
        if (right > 500.0 || left > 500.0) {
          avoidCounter = 50; // ~3 secondes de rotation
          System.out.printf(">>> OBSTACLE ! ds_right=%.1f | ds_left=%.1f => rotation%n", right, left);
        }
      }

      // Toutes les roues : même signe pour avancer, différence pour tourner
      wheel1.setVelocity(leftSpeed);
      wheel2.setVelocity(rightSpeed);
      wheel3.setVelocity(leftSpeed);
      wheel4.setVelocity(rightSpeed);
    }
  }
}