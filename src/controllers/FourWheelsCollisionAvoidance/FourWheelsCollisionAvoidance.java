import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Field;

import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.TouchSensor;
import com.cyberbotics.webots.controller.PositionSensor;

public class FourWheelsCollisionAvoidance {

  public static void attachPuckToRobot(Supervisor robot, Node puckNode, Field puckTranslationField) {
    if (puckNode == null || puckTranslationField == null) {
      return;
    }

    Node self = robot.getSelf();

    double[] robotPosition = self.getPosition();
    double[] orientation = self.getOrientation();

    double localX = 0.15;
    double localY = 0.0;
    double localZ = 0.09;

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

    puckTranslationField.setSFVec3f(new double[] { worldX, worldY, worldZ });
    puckNode.resetPhysics();
  }

  public static double normalizeAngle(double angle) {
    while (angle > Math.PI) {
      angle -= 2.0 * Math.PI;
    }

    while (angle < -Math.PI) {
      angle += 2.0 * Math.PI;
    }

    return angle;
  }

  public static double getRobotYaw(Supervisor robot) {
    double[] orientation = robot.getSelf().getOrientation();
    return Math.atan2(orientation[3], orientation[0]);
  }

  public static int findNearestAvailablePuck(
      Supervisor robot,
      Node[] puckNodes,
      boolean[] puckDelivered
  ) {
    double[] robotPosition = robot.getSelf().getPosition();

    int nearestIndex = -1;
    double nearestDistance = Double.MAX_VALUE;

    for (int i = 0; i < puckNodes.length; i++) {
      if (puckNodes[i] == null || puckDelivered[i]) {
        continue;
      }

      double[] puckPosition = puckNodes[i].getPosition();

      double dx = puckPosition[0] - robotPosition[0];
      double dy = puckPosition[1] - robotPosition[1];

      double distance = Math.sqrt(dx * dx + dy * dy);

      if (distance < nearestDistance) {
        nearestDistance = distance;
        nearestIndex = i;
      }
    }

    return nearestIndex;
  }

  public static void main(String[] args) {
    Supervisor robot = new Supervisor();

    int timeStep = (int) Math.round(robot.getBasicTimeStep());

    String[] puckNames = {
        "PALET_1",
        "PALET_2",
        "PALET_3"
    };

    Node[] puckNodes = new Node[puckNames.length];
    Field[] puckTranslationFields = new Field[puckNames.length];
    boolean[] puckDelivered = new boolean[puckNames.length];

    for (int i = 0; i < puckNames.length; i++) {
      puckNodes[i] = robot.getFromDef(puckNames[i]);

      if (puckNodes[i] != null) {
        puckTranslationFields[i] = puckNodes[i].getField("translation");
        puckDelivered[i] = false;
      } else {
        System.out.println("Attention : impossible de trouver " + puckNames[i]);
      }
    }

    int currentPuckIndex = -1;

    DistanceSensor dsRight = robot.getDistanceSensor("ds_right");
    DistanceSensor dsLeft = robot.getDistanceSensor("ds_left");
    DistanceSensor dsFront = robot.getDistanceSensor("ds_front");

    if (dsRight != null) {
      dsRight.enable(timeStep);
    }

    if (dsLeft != null) {
      dsLeft.enable(timeStep);
    }

    if (dsFront != null) {
      dsFront.enable(timeStep);
    }

    Camera colorSensor = robot.getCamera("color_sensor");

    if (colorSensor != null) {
      colorSensor.enable(timeStep);
    }

    TouchSensor touchFront = robot.getTouchSensor("touch_front");

    if (touchFront != null) {
      touchFront.enable(timeStep);
    }

    Motor wheel1 = robot.getMotor("wheel1");
    Motor wheel2 = robot.getMotor("wheel2");
    Motor wheel3 = robot.getMotor("wheel3");
    Motor wheel4 = robot.getMotor("wheel4");

    Motor[] wheels = { wheel1, wheel2, wheel3, wheel4 };

    for (int i = 0; i < wheels.length; i++) {
      if (wheels[i] != null) {
        wheels[i].setPosition(Double.POSITIVE_INFINITY);
        wheels[i].setVelocity(0.0);
      }
    }

    Motor armMotor = robot.getMotor("arm_motor");
    Motor gripperLeftMotor = robot.getMotor("gripper_left_motor");
    Motor gripperRightMotor = robot.getMotor("gripper_right_motor");

    PositionSensor armSensor = robot.getPositionSensor("arm_sensor");

    if (armSensor != null) {
      armSensor.enable(timeStep);
    }

    double ARM_UP = -0.65;
    double ARM_DOWN = 0.35;

    double GRIPPER_OPEN_LEFT = 0.2;
    double GRIPPER_OPEN_RIGHT = -0.2;

    double GRIPPER_CLOSED_LEFT = -0.55;
    double GRIPPER_CLOSED_RIGHT = 0.55;

    if (armMotor != null) {
      armMotor.setPosition(ARM_UP);
    }

    if (gripperLeftMotor != null) {
      gripperLeftMotor.setPosition(GRIPPER_OPEN_LEFT);
    }

    if (gripperRightMotor != null) {
      gripperRightMotor.setPosition(GRIPPER_OPEN_RIGHT);
    }

    /*
     * Vitesses.
     */
    double SEARCH_SPEED = 4.0;
    double APPROACH_SPEED = 0.8;
    double GO_DROP_SPEED = 3.2;
    double TURN_SPEED = 4.0;
    double BACK_SPEED = -2.0;

    /*
     * Position de dépôt.
     */
    double DROP_X = -0.9;
    double DROP_Y = 0.0;
    double DROP_Z = 0.095;

    /*
     * Seuil de détection devant.
     */
    double FRONT_OBJECT_THRESHOLD = 350.0;

    /*
     * 0.40 = 40 cm.
     * Permet de mieux gérer les cas où le robot frôle le palet.
     */
    double PUCK_CONTACT_DISTANCE = 0.40;

    /*
     * Détection de palet proche.
     * Le robot peut cibler un palet même s'il n'est pas parfaitement devant ds_front.
     */
    double PUCK_DETECTION_DISTANCE = 1.20;
    double PUCK_MAX_ANGLE = 1.40;

    /*
     * Seuils des capteurs latéraux.
     */
    double SIDE_OBJECT_THRESHOLD = 900.0;
    double SIDE_DANGER_THRESHOLD = 980.0;

    /*
     * Anti-collage contre les murs en diagonale.
     */
    double WALL_STUCK_THRESHOLD = 970.0;
    int WALL_STUCK_TURN_TIME = 55;
    int wallStuckCounter = 0;

    /*
     * Correction de trajectoire vers le palet.
     */
    double APPROACH_TURN_GAIN = 4.5;

    /*
     * Timers.
     */
    int APPROACH_TIMEOUT = 180;

    int TOUCH_BACK_TIME = 35;
    int TOUCH_TURN_TIME = 90;
    int SIDE_TURN_TIME = 40;

    String robotMode = "SEARCH";

    int avoidObstacleCounter = 0;
    int actionCounter = 0;
    int stepCounter = 0;

    /*
     * avoidDirection = 1  -> tourner à gauche
     * avoidDirection = -1 -> tourner à droite
     */
    int avoidDirection = 1;

    boolean puckDetected = false;
    boolean puckTouched = false;
    boolean previousTouched = false;
    boolean puckAttached = false;

    while (robot.step(timeStep) != -1) {
      stepCounter++;

      double leftSpeed = 0.0;
      double rightSpeed = 0.0;

      double rightValue = 0.0;
      double leftValue = 0.0;
      double frontValue = 0.0;

      if (dsRight != null) {
        rightValue = dsRight.getValue();
      }

      if (dsLeft != null) {
        leftValue = dsLeft.getValue();
      }

      if (dsFront != null) {
        frontValue = dsFront.getValue();
      }

      boolean touched = false;

      if (touchFront != null) {
        touched = touchFront.getValue() > 0.0;
      }

      boolean newContact = touched && !previousTouched;

      int red = 0;
      int green = 0;
      int blue = 0;

      if (colorSensor != null) {
        int[] image = colorSensor.getImage();
        int width = colorSensor.getWidth();
        int height = colorSensor.getHeight();

        if (image != null && width > 0 && height > 0) {
          int pixelCount = width * height;

          int sumRed = 0;
          int sumGreen = 0;
          int sumBlue = 0;

          for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
              sumRed += Camera.imageGetRed(image, width, x, y);
              sumGreen += Camera.imageGetGreen(image, width, x, y);
              sumBlue += Camera.imageGetBlue(image, width, x, y);
            }
          }

          red = sumRed / pixelCount;
          green = sumGreen / pixelCount;
          blue = sumBlue / pixelCount;
        }
      }

      boolean redDetected = red > 150 && green < 100 && blue < 100;
      boolean objectInFront = frontValue > FRONT_OBJECT_THRESHOLD;

      /*
       * Le palet ramassé suit le robot.
       */
      if (puckAttached && currentPuckIndex != -1) {
        attachPuckToRobot(
            robot,
            puckNodes[currentPuckIndex],
            puckTranslationFields[currentPuckIndex]
        );
      }

      System.out.println(
          "mode=" + robotMode
              + " | ds_left=" + leftValue
              + " | ds_right=" + rightValue
              + " | ds_front=" + frontValue
              + " | RGB=(" + red + "," + green + "," + blue + ")"
              + " | touched=" + touched
              + " | newContact=" + newContact
              + " | attached=" + puckAttached
              + " | currentPuck=" + currentPuckIndex
              + " | wallStuckCounter=" + wallStuckCounter
      );

      /*
       * Gestion robuste du contact.
       */
      if (
          stepCounter > 50
              && touched
              && !puckAttached
              && !robotMode.equals("LOWER_ARM")
              && !robotMode.equals("CLOSE_GRIPPER")
              && !robotMode.equals("LIFT_ARM")
              && !robotMode.equals("GO_TO_DROP_ZONE")
              && !robotMode.equals("DROP_PUCK")
              && !robotMode.equals("LIFT_ARM_AFTER_DROP")
              && !robotMode.equals("BACK_AND_TURN_AFTER_DROP")
              && !robotMode.equals("TOUCH_AVOID")
      ) {
        int nearestPuckIndex = findNearestAvailablePuck(robot, puckNodes, puckDelivered);

        boolean puckClose = false;

        if (nearestPuckIndex != -1) {
          double[] robotPosition = robot.getSelf().getPosition();
          double[] puckPosition = puckNodes[nearestPuckIndex].getPosition();

          double dx = puckPosition[0] - robotPosition[0];
          double dy = puckPosition[1] - robotPosition[1];

          double distanceToPuck = Math.sqrt(dx * dx + dy * dy);

          System.out.println("Contact detecte. Palet le plus proche = "
              + puckNames[nearestPuckIndex]
              + " | distance=" + distanceToPuck);

          if (distanceToPuck < PUCK_CONTACT_DISTANCE) {
            puckClose = true;
          }
        }

        if (puckClose && !puckTouched) {
          currentPuckIndex = nearestPuckIndex;

          puckDetected = true;
          puckTouched = true;
          robotMode = "LOWER_ARM";
          actionCounter = 0;
          wallStuckCounter = 0;

          System.out.println("Contact avec " + puckNames[currentPuckIndex] + ". Debut du ramassage.");
        }

        else if (!puckClose) {
          robotMode = "TOUCH_AVOID";
          actionCounter = 0;
          currentPuckIndex = -1;
          puckDetected = false;
          puckTouched = false;
          wallStuckCounter = 0;

          System.out.println("Contact avec obstacle/dropzone sans palet. Evitement.");
        }
      }

      /*
       * ============================
       * MACHINE A ETATS
       * ============================
       */

      if (robotMode.equals("SEARCH")) {
        leftSpeed = SEARCH_SPEED;
        rightSpeed = SEARCH_SPEED;

        /*
         * Sécurité anti-collage contre les murs.
         */
        if (leftValue > WALL_STUCK_THRESHOLD || rightValue > WALL_STUCK_THRESHOLD) {
          wallStuckCounter++;
        } else {
          wallStuckCounter = 0;
        }

        if (wallStuckCounter > 5) {
          if (leftValue > rightValue) {
            avoidDirection = -1;
            System.out.println("Robot colle le mur a gauche. Rotation forte vers la droite.");
          } else {
            avoidDirection = 1;
            System.out.println("Robot colle le mur a droite. Rotation forte vers la gauche.");
          }

          avoidObstacleCounter = WALL_STUCK_TURN_TIME;
          wallStuckCounter = 0;
        }

        /*
         * Priorité 1 :
         * Le robot cherche un palet proche, même s'il n'est pas parfaitement
         * en face du capteur ds_front.
         */
        if (avoidObstacleCounter == 0 && !puckAttached) {
          int nearestPuckIndex = findNearestAvailablePuck(robot, puckNodes, puckDelivered);

          if (nearestPuckIndex != -1) {
            double[] robotPosition = robot.getSelf().getPosition();
            double[] puckPosition = puckNodes[nearestPuckIndex].getPosition();

            double dx = puckPosition[0] - robotPosition[0];
            double dy = puckPosition[1] - robotPosition[1];

            double distanceToPuck = Math.sqrt(dx * dx + dy * dy);

            double targetAngle = Math.atan2(dy, dx);
            double robotAngle = getRobotYaw(robot);
            double angleError = normalizeAngle(targetAngle - robotAngle);

            /*
             * Le robot va vers le palet si :
             * - ds_front voit quelque chose
             * OU
             * - le palet est proche et dans une zone devant le robot.
             */
            if (
                objectInFront
                    || (
                        distanceToPuck < PUCK_DETECTION_DISTANCE
                            && Math.abs(angleError) < PUCK_MAX_ANGLE
                    )
            ) {
              currentPuckIndex = nearestPuckIndex;

              puckDetected = true;
              puckTouched = false;
              robotMode = "APPROACH_PUCK";
              actionCounter = 0;

              System.out.println(
                  "Palet cible detecte : " + puckNames[currentPuckIndex]
                      + " | distance=" + distanceToPuck
                      + " | angleError=" + angleError
              );
            }
          }
        }

        /*
         * Priorité 2 : rotation déjà commencée.
         */
        else if (avoidObstacleCounter > 0) {
          avoidObstacleCounter--;

          leftSpeed = -avoidDirection * TURN_SPEED;
          rightSpeed = avoidDirection * TURN_SPEED;
        }

        /*
         * Priorité 3 : capteurs latéraux.
         */
        else {
          if (leftValue > SIDE_DANGER_THRESHOLD && rightValue > SIDE_DANGER_THRESHOLD) {
            avoidDirection = 1;
            avoidObstacleCounter = SIDE_TURN_TIME;

            System.out.println("Obstacle detecte par les deux capteurs. Rotation simple.");
          }

          else if (leftValue > SIDE_OBJECT_THRESHOLD && leftValue > rightValue + 150.0) {
            avoidDirection = -1;
            avoidObstacleCounter = SIDE_TURN_TIME;

            System.out.println("Obstacle proche a gauche. Rotation vers la droite.");
          }

          else if (rightValue > SIDE_OBJECT_THRESHOLD && rightValue > leftValue + 150.0) {
            avoidDirection = 1;
            avoidObstacleCounter = SIDE_TURN_TIME;

            System.out.println("Obstacle proche a droite. Rotation vers la gauche.");
          }
        }
      }

      else if (robotMode.equals("TOUCH_AVOID")) {
        /*
         * Recul puis rotation.
         */
        if (actionCounter < TOUCH_BACK_TIME) {
          leftSpeed = BACK_SPEED;
          rightSpeed = BACK_SPEED;
        }

        else if (actionCounter < TOUCH_BACK_TIME + TOUCH_TURN_TIME) {
          leftSpeed = TURN_SPEED;
          rightSpeed = -TURN_SPEED;
        }

        else {
          robotMode = "SEARCH";
          actionCounter = 0;
          previousTouched = false;
          currentPuckIndex = -1;
          puckDetected = false;
          puckTouched = false;
          wallStuckCounter = 0;

          System.out.println("Obstacle touche evite. Retour en recherche.");
        }

        actionCounter++;
      }

      else if (robotMode.equals("APPROACH_PUCK")) {
        /*
         * Approche intelligente :
         * le robot se réoriente vers le palet ciblé.
         */
        if (
            currentPuckIndex != -1
                && puckNodes[currentPuckIndex] != null
                && !puckDelivered[currentPuckIndex]
        ) {
          double[] robotPosition = robot.getSelf().getPosition();
          double[] puckPosition = puckNodes[currentPuckIndex].getPosition();

          double dx = puckPosition[0] - robotPosition[0];
          double dy = puckPosition[1] - robotPosition[1];

          double distanceToPuck = Math.sqrt(dx * dx + dy * dy);

          double targetAngle = Math.atan2(dy, dx);
          double robotAngle = getRobotYaw(robot);
          double angleError = normalizeAngle(targetAngle - robotAngle);

          double correction = APPROACH_TURN_GAIN * angleError;

          if (correction > TURN_SPEED) {
            correction = TURN_SPEED;
          }

          if (correction < -TURN_SPEED) {
            correction = -TURN_SPEED;
          }

          leftSpeed = APPROACH_SPEED - correction;
          rightSpeed = APPROACH_SPEED + correction;

          System.out.println(
              "Approche palet | distance=" + distanceToPuck
                  + " | angleError=" + angleError
                  + " | correction=" + correction
          );
        }

        else {
          robotMode = "SEARCH";
          actionCounter = 0;
          currentPuckIndex = -1;
          puckDetected = false;
          puckTouched = false;
          previousTouched = false;
          wallStuckCounter = 0;

          System.out.println("Palet cible perdu. Retour en recherche.");
        }

        actionCounter++;

        if (actionCounter > APPROACH_TIMEOUT) {
          robotMode = "SEARCH";
          actionCounter = 0;
          currentPuckIndex = -1;
          puckDetected = false;
          puckTouched = false;
          previousTouched = false;
          wallStuckCounter = 0;

          System.out.println("Approche trop longue. Retour en recherche sans recul.");
        }
      }

      else if (robotMode.equals("LOWER_ARM")) {
        leftSpeed = 0.0;
        rightSpeed = 0.0;

        if (armMotor != null) {
          armMotor.setPosition(ARM_DOWN);
        }

        if (gripperLeftMotor != null) {
          gripperLeftMotor.setPosition(GRIPPER_OPEN_LEFT);
        }

        if (gripperRightMotor != null) {
          gripperRightMotor.setPosition(GRIPPER_OPEN_RIGHT);
        }

        actionCounter++;

        if (actionCounter > 25) {
          robotMode = "CLOSE_GRIPPER";
          actionCounter = 0;

          System.out.println("Bras baisse. Fermeture de la pince.");
        }
      }

      else if (robotMode.equals("CLOSE_GRIPPER")) {
        leftSpeed = 0.0;
        rightSpeed = 0.0;

        if (gripperLeftMotor != null) {
          gripperLeftMotor.setPosition(GRIPPER_CLOSED_LEFT);
        }

        if (gripperRightMotor != null) {
          gripperRightMotor.setPosition(GRIPPER_CLOSED_RIGHT);
        }

        actionCounter++;

        if (actionCounter > 25) {
          puckAttached = true;

          robotMode = "LIFT_ARM";
          actionCounter = 0;
          wallStuckCounter = 0;

          System.out.println("Palet ramasse : " + puckNames[currentPuckIndex]);
        }
      }

      else if (robotMode.equals("LIFT_ARM")) {
        leftSpeed = 0.0;
        rightSpeed = 0.0;

        if (armMotor != null) {
          armMotor.setPosition(ARM_UP);
        }

        actionCounter++;

        if (actionCounter > 30) {
          robotMode = "GO_TO_DROP_ZONE";
          actionCounter = 0;

          System.out.println("Direction la zone de depot.");
        }
      }

      else if (robotMode.equals("GO_TO_DROP_ZONE")) {
        double[] robotPosition = robot.getSelf().getPosition();

        double dx = DROP_X - robotPosition[0];
        double dy = DROP_Y - robotPosition[1];

        double targetAngle = Math.atan2(dy, dx);
        double robotAngle = getRobotYaw(robot);
        double angleError = normalizeAngle(targetAngle - robotAngle);

        if (touched && puckAttached) {
          leftSpeed = 0.0;
          rightSpeed = 0.0;

          robotMode = "DROP_PUCK";
          actionCounter = 0;

          System.out.println("Dropzone touchee avec palet. Depot du palet.");
        }

        else if (Math.abs(angleError) > 0.15) {
          if (angleError > 0.0) {
            leftSpeed = -TURN_SPEED;
            rightSpeed = TURN_SPEED;
          } else {
            leftSpeed = TURN_SPEED;
            rightSpeed = -TURN_SPEED;
          }
        }

        else {
          leftSpeed = GO_DROP_SPEED;
          rightSpeed = GO_DROP_SPEED;
        }
      }

      else if (robotMode.equals("DROP_PUCK")) {
        leftSpeed = 0.0;
        rightSpeed = 0.0;

        if (gripperLeftMotor != null) {
          gripperLeftMotor.setPosition(GRIPPER_OPEN_LEFT);
        }

        if (gripperRightMotor != null) {
          gripperRightMotor.setPosition(GRIPPER_OPEN_RIGHT);
        }

        if (armMotor != null) {
          armMotor.setPosition(ARM_DOWN);
        }

        actionCounter++;

        if (actionCounter > 25) {
          puckAttached = false;

          if (currentPuckIndex != -1) {
            puckDelivered[currentPuckIndex] = true;

            if (puckTranslationFields[currentPuckIndex] != null) {
              puckTranslationFields[currentPuckIndex].setSFVec3f(new double[] {
                  DROP_X,
                  DROP_Y,
                  DROP_Z
              });
            }

            if (puckNodes[currentPuckIndex] != null) {
              puckNodes[currentPuckIndex].resetPhysics();
            }

            System.out.println("Palet depose : " + puckNames[currentPuckIndex]);
          }

          currentPuckIndex = -1;

          robotMode = "LIFT_ARM_AFTER_DROP";
          actionCounter = 0;
        }
      }

      else if (robotMode.equals("LIFT_ARM_AFTER_DROP")) {
        leftSpeed = 0.0;
        rightSpeed = 0.0;

        if (armMotor != null) {
          armMotor.setPosition(ARM_UP);
        }

        actionCounter++;

        if (actionCounter > 25) {
          robotMode = "BACK_AND_TURN_AFTER_DROP";
          actionCounter = 0;

          System.out.println("Bras releve. Recul puis rotation.");
        }
      }

      else if (robotMode.equals("BACK_AND_TURN_AFTER_DROP")) {
        if (actionCounter < 35) {
          leftSpeed = BACK_SPEED;
          rightSpeed = BACK_SPEED;
        }

        else if (actionCounter < 85) {
          leftSpeed = TURN_SPEED;
          rightSpeed = -TURN_SPEED;
        }

        else {
          robotMode = "SEARCH";
          actionCounter = 0;

          puckDetected = false;
          puckTouched = false;
          previousTouched = false;
          puckAttached = false;
          currentPuckIndex = -1;
          wallStuckCounter = 0;

          System.out.println("Retour en mode recherche apres depot.");
        }

        actionCounter++;
      }

      /*
       * Application des vitesses.
       */
      if (wheel1 != null) {
        wheel1.setVelocity(leftSpeed);
      }

      if (wheel2 != null) {
        wheel2.setVelocity(rightSpeed);
      }

      if (wheel3 != null) {
        wheel3.setVelocity(leftSpeed);
      }

      if (wheel4 != null) {
        wheel4.setVelocity(rightSpeed);
      }

      previousTouched = touched;
    }
  }
}