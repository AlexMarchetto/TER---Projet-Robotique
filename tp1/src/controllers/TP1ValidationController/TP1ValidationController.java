import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.DistanceSensor;

/**
 * Controller de validation du TP1.
 *
 * Objectif : vérifier que les éléments à compléter dans le PROTO sont présents.
 * Si un élément manque, un message est affiché dans la console Webots.
 * Si tout est présent, le robot avance quelques secondes et la pince est testée.
 */
public class TP1ValidationController {

  private static Motor getMotor(Robot robot, String name) {
    try {
      Motor motor = robot.getMotor(name);
      if (motor == null) {
        System.out.println("[ERREUR] Moteur introuvable : " + name);
      }
      return motor;
    } catch (Exception e) {
      System.out.println("[ERREUR] Moteur introuvable : " + name);
      return null;
    }
  }

  private static DistanceSensor getDistanceSensor(Robot robot, String name) {
    try {
      DistanceSensor sensor = robot.getDistanceSensor(name);
      if (sensor == null) {
        System.out.println("[ERREUR] Capteur introuvable : " + name);
      }
      return sensor;
    } catch (Exception e) {
      System.out.println("[ERREUR] Capteur introuvable : " + name);
      return null;
    }
  }

  private static void setWheelVelocity(Motor[] wheels, double velocity) {
    for (Motor wheel : wheels) {
      if (wheel != null) {
        wheel.setVelocity(velocity);
      }
    }
  }

  public static void main(String[] args) {
    Robot robot = new Robot();
    int timeStep = (int) Math.round(robot.getBasicTimeStep());

    System.out.println("==============================");
    System.out.println(" Validation du TP1 - RobotTER");
    System.out.println("==============================");

    Motor wheel1 = getMotor(robot, "wheel1");
    Motor wheel2 = getMotor(robot, "wheel2");
    Motor wheel3 = getMotor(robot, "wheel3");
    Motor wheel4 = getMotor(robot, "wheel4");

    Motor armMotor = getMotor(robot, "arm_motor");
    Motor gripperLeftMotor = getMotor(robot, "gripper_left_motor");
    Motor gripperRightMotor = getMotor(robot, "gripper_right_motor");

    DistanceSensor dsFront = getDistanceSensor(robot, "ds_front");

    boolean isComplete = true;
    isComplete = isComplete && wheel1 != null && wheel2 != null && wheel3 != null && wheel4 != null;
    isComplete = isComplete && armMotor != null && gripperLeftMotor != null && gripperRightMotor != null;
    isComplete = isComplete && dsFront != null;

    if (!isComplete) {
      System.out.println("");
      System.out.println("[RESULTAT] Le robot n'est pas encore complet.");
      System.out.println("Vérifiez les éléments demandés dans le TP1 :");
      System.out.println("");
      while (robot.step(timeStep) != -1) {
        // Le robot reste immobile pour laisser le temps de lire la console.
      }
      return;
    }

    Motor[] wheels = {wheel1, wheel2, wheel3, wheel4};
    for (Motor wheel : wheels) {
      wheel.setPosition(Double.POSITIVE_INFINITY);
      wheel.setVelocity(0.0);
    }

    dsFront.enable(timeStep);

    System.out.println("");
    System.out.println("[OK] Tous les éléments du TP1 ont été détectés.");
    System.out.println("Le robot va maintenant avancer, puis tester la pince.");
    System.out.println("");

    double elapsedTime = 0.0;
    boolean finishedMessageDisplayed = false;

    while (robot.step(timeStep) != -1) {
      elapsedTime += timeStep / 1000.0;

      if (elapsedTime < 2.0) {
        setWheelVelocity(wheels, 3.0);
      } else if (elapsedTime < 3.0) {
        setWheelVelocity(wheels, 0.0);
        armMotor.setPosition(0.2);
        gripperLeftMotor.setPosition(-0.6);
        gripperRightMotor.setPosition(0.6);
      } else if (elapsedTime < 4.0) {
        gripperLeftMotor.setPosition(0.2);
        gripperRightMotor.setPosition(-0.2);
      } else {
        setWheelVelocity(wheels, 0.0);
        if (!finishedMessageDisplayed) {
          System.out.println("[RESULTAT] Test terminé avec succès.");
          System.out.println("Valeur du capteur frontal ds_front : " + dsFront.getValue());
          finishedMessageDisplayed = true;
        }
      }
    }
  }
}
