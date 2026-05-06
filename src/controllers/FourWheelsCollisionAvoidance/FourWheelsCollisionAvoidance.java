import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;

import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.TouchSensor;
import com.cyberbotics.webots.controller.PositionSensor;

public class FourWheelsCollisionAvoidance {

  public static int getRedValue(Camera colorSensor) {
    int[] image = colorSensor.getImage();
    int w = colorSensor.getWidth();
    int h = colorSensor.getHeight();

    if (w == 1 && h == 1) {
      return Camera.imageGetRed(image, w, 0, 0);
    }

    int total = 0;
    int count = 0;

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        total += Camera.imageGetRed(image, w, x, y);
        count++;
      }
    }

    return count == 0 ? 0 : total / count;
  }

  public static int getGreenValue(Camera colorSensor) {
    int[] image = colorSensor.getImage();
    int w = colorSensor.getWidth();
    int h = colorSensor.getHeight();

    if (w == 1 && h == 1) {
      return Camera.imageGetGreen(image, w, 0, 0);
    }

    int total = 0;
    int count = 0;

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        total += Camera.imageGetGreen(image, w, x, y);
        count++;
      }
    }

    return count == 0 ? 0 : total / count;
  }

  public static int getBlueValue(Camera colorSensor) {
    int[] image = colorSensor.getImage();
    int w = colorSensor.getWidth();
    int h = colorSensor.getHeight();

    if (w == 1 && h == 1) {
      return Camera.imageGetBlue(image, w, 0, 0);
    }

    int total = 0;
    int count = 0;

    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        total += Camera.imageGetBlue(image, w, x, y);
        count++;
      }
    }

    return count == 0 ? 0 : total / count;
  }

  /*
   * Déplace le palet devant le robot pour simuler le transport.
   * resetPhysics() évite que le palet tremble ou bug à cause de la physique.
   */
  public static void attachPuckToRobot(Supervisor robot, Node puckNode, Field puckTranslationField) {
    if (puckNode == null || puckTranslationField == null) {
      return;
    }

    Node self = robot.getSelf();

    double[] robotPosition = self.getPosition();
    double[] orientation = self.getOrientation();

    double localX = 0.15;
    double localY = 0.0;
    double localZ = 0.06;

    double worldX = robotPosition[0]
        + orientation[0] * localX
        + orientation[1] * localY
        + orientation[2] * localZ;

    double worldY = robotPosition[1]
        + orientation[3] * localX
        + orientation[4] * localY
        + orientation[5] * localZ;

    double worldZ = robotPosition[2]
        + orientation[6] * localX
        + orientation[7] * localY
        + orientation[8] * localZ;

    puckTranslationField.setSFVec3f(new double[] {worldX, worldY, worldZ});

    // Stabilise le palet à chaque step
    puckNode.resetPhysics();
  }

  public static void main(String[] args) {

    Supervisor robot = new Supervisor();
    int timeStep = 64;

    Node puckNode = robot.getFromDef("PALET");
    Field puckTranslationField = null;

    if (puckNode != null) {
      puckTranslationField = puckNode.getField("translation");
      System.out.println("Palet PALET trouve par le Supervisor.");
    } else {
      System.out.println("ATTENTION : aucun DEF PALET trouve dans le monde.");
      System.out.println("Ajoute DEF PALET devant le Solid du palet dans Robot.wbt.");
    }

    DistanceSensor[] ds = new DistanceSensor[2];
    String[] dsNames = {"ds_right", "ds_left"};

    for (int i = 0; i < 2; i++) {
      ds[i] = robot.getDistanceSensor(dsNames[i]);
      ds[i].enable(timeStep);
    }

    DistanceSensor dsFront = robot.getDistanceSensor("ds_front");
    dsFront.enable(timeStep);

    Camera colorSensor = robot.getCamera("color_sensor");
    colorSensor.enable(timeStep);

    TouchSensor touchFront = robot.getTouchSensor("touch_front");
    touchFront.enable(timeStep);

    Motor[] wheels = new Motor[4];
    String[] wheelNames = {"wheel1", "wheel2", "wheel3", "wheel4"};

    for (int i = 0; i < 4; i++) {
      wheels[i] = robot.getMotor(wheelNames[i]);
      wheels[i].setPosition(Double.POSITIVE_INFINITY);
      wheels[i].setVelocity(0.0);
    }

    Motor armMotor = robot.getMotor("arm_motor");
    Motor gripperLeftMotor = robot.getMotor("gripper_left_motor");
    Motor gripperRightMotor = robot.getMotor("gripper_right_motor");

    PositionSensor armSensor = robot.getPositionSensor("arm_sensor");
    armSensor.enable(timeStep);

    double ARM_UP = -0.65;
    double ARM_DOWN = 0.35;

    double GRIPPER_OPEN_LEFT = 0.2;
    double GRIPPER_OPEN_RIGHT = -0.2;

    double GRIPPER_CLOSED_LEFT = -0.55;
    double GRIPPER_CLOSED_RIGHT = 0.55;

    armMotor.setPosition(ARM_UP);
    gripperLeftMotor.setPosition(GRIPPER_OPEN_LEFT);
    gripperRightMotor.setPosition(GRIPPER_OPEN_RIGHT);

    int avoidObstacleCounter = 0;
    int stepCounter = 0;
    int actionCounter = 0;

    boolean puckDetected = false;
    boolean puckTouched = false;
    boolean previousTouched = false;
    boolean puckAttached = false;

    String robotMode = "SEARCH";
    String lastMode = "";

    while (robot.step(timeStep) != -1) {

      stepCounter++;

      double leftSpeed = 1.0;
      double rightSpeed = 1.0;

      double rightSensorValue = ds[0].getValue();
      double leftSensorValue = ds[1].getValue();
      double frontValue = dsFront.getValue();

      int red = getRedValue(colorSensor);
      int green = getGreenValue(colorSensor);
      int blue = getBlueValue(colorSensor);

      boolean redDetected = red > 150 && green < 100 && blue < 100;

      double touchValue = touchFront.getValue();
      boolean touched = touchValue > 0.0;
      boolean newContact = touched && !previousTouched;

      boolean objectAt30cm = frontValue >= 500.0;

      if (robotMode.equals("SEARCH") && !puckDetected && objectAt30cm && redDetected) {
        puckDetected = true;
        robotMode = "APPROACH_PUCK";

        System.out.println("================================");
        System.out.println("PALET DETECTE A DISTANCE !");
        System.out.println("Distance avant : " + frontValue);
        System.out.println("Couleur RGB : " + red + ", " + green + ", " + blue);
        System.out.println("================================");
      }

      if (stepCounter > 50 && newContact && !puckTouched) {
        puckDetected = true;
        puckTouched = true;
        robotMode = "LOWER_ARM";
        actionCounter = 0;

        System.out.println("================================");
        System.out.println("CONTACT PALET !");
        System.out.println("touchValue = " + touchValue);
        System.out.println("Debut du ramassage.");
        System.out.println("================================");
      }

      if (robotMode.equals("SEARCH")) {
        leftSpeed = 1.0;
        rightSpeed = 1.0;

        if (avoidObstacleCounter > 0) {
          avoidObstacleCounter--;
          leftSpeed = 1.0;
          rightSpeed = -1.0;
        } else {
          if (rightSensorValue > 500.0 || leftSensorValue > 500.0) {
            avoidObstacleCounter = 100;

            System.out.println("================================");
            System.out.println("OBSTACLE DETECTE !");
            System.out.println("Capteur droit : " + rightSensorValue);
            System.out.println("Capteur gauche : " + leftSensorValue);
            System.out.println("================================");
          }
        }
      }

      else if (robotMode.equals("APPROACH_PUCK")) {
        leftSpeed = 0.5;
        rightSpeed = 0.5;
      }

      else if (robotMode.equals("LOWER_ARM")) {
        leftSpeed = 0.0;
        rightSpeed = 0.0;

        armMotor.setPosition(ARM_DOWN);
        gripperLeftMotor.setPosition(GRIPPER_OPEN_LEFT);
        gripperRightMotor.setPosition(GRIPPER_OPEN_RIGHT);

        actionCounter++;

        if (actionCounter > 60) {
          robotMode = "CLOSE_GRIPPER";
          actionCounter = 0;

          System.out.println("Bras baisse. Fermeture de la pince.");
        }
      }

      else if (robotMode.equals("CLOSE_GRIPPER")) {
        leftSpeed = 0.0;
        rightSpeed = 0.0;

        gripperLeftMotor.setPosition(GRIPPER_CLOSED_LEFT);
        gripperRightMotor.setPosition(GRIPPER_CLOSED_RIGHT);

        actionCounter++;

        if (actionCounter > 60) {
          puckAttached = true;

          robotMode = "LIFT_ARM";
          actionCounter = 0;

          System.out.println("Pince fermee. Palet attache virtuellement. Le bras remonte.");
        }
      }

      else if (robotMode.equals("LIFT_ARM")) {
        leftSpeed = 0.0;
        rightSpeed = 0.0;

        armMotor.setPosition(ARM_UP);

        actionCounter++;

        if (actionCounter > 80) {
          robotMode = "TRANSPORT";
          actionCounter = 0;

          System.out.println("Ramassage termine. Le robot transporte le palet.");
        }
      }

      else if (robotMode.equals("TRANSPORT")) {
        leftSpeed = 0.5;
        rightSpeed = 0.5;
      }

      if (puckAttached) {
        attachPuckToRobot(robot, puckNode, puckTranslationField);
      }

      if (stepCounter % 20 == 0) {
        System.out.println(
          "[STEP " + stepCounter + "] " +
          "Mode=" + robotMode +
          " | ds_front=" + frontValue +
          " | ds_right=" + rightSensorValue +
          " | ds_left=" + leftSensorValue +
          " | RGB=(" + red + "," + green + "," + blue + ")" +
          " | rouge=" + redDetected +
          " | objet30cm=" + objectAt30cm +
          " | touchValue=" + touchValue +
          " | toucher=" + touched +
          " | nouveauContact=" + newContact +
          " | puckDetected=" + puckDetected +
          " | puckTouched=" + puckTouched +
          " | puckAttached=" + puckAttached +
          " | armPosition=" + armSensor.getValue() +
          " | leftSpeed=" + leftSpeed +
          " | rightSpeed=" + rightSpeed
        );
      }

      if (!robotMode.equals(lastMode)) {
        System.out.println("Changement de mode : " + lastMode + " -> " + robotMode);
        lastMode = robotMode;
      }

      wheels[0].setVelocity(leftSpeed);
      wheels[1].setVelocity(rightSpeed);
      wheels[2].setVelocity(leftSpeed);
      wheels[3].setVelocity(rightSpeed);

      previousTouched = touched;
    }

    robot.delete();
  }
}