import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Supervisor;

public class TERBot {
  private final Supervisor supervisor;
  private final int timeStep;

  private final DriveBase driveBase;
  private final RobotSensors sensors;
  private final Arm arm;
  private final PuckManager puckManager;

  private RobotMode mode = RobotMode.SEARCH;

  private int currentPuckIndex = -1;

  private int avoidObstacleCounter = 0;
  private int actionCounter = 0;
  private int stepCounter = 0;
  private int wallStuckCounter = 0;

  private int avoidDirection = 1;

  private boolean puckDetected = false;
  private boolean puckTouched = false;
  private boolean previousTouched = false;
  private boolean puckAttached = false;

  private static final double SEARCH_SPEED = 4.0;
  private static final double APPROACH_SPEED = 0.8;
  private static final double GO_DROP_SPEED = 3.2;
  private static final double TURN_SPEED = 4.0;
  private static final double BACK_SPEED = -2.0;

  private static final double DROP_X = -0.9;
  private static final double DROP_Y = 0.0;
  private static final double DROP_Z = 0.095;

  private static final double FRONT_OBJECT_THRESHOLD = 350.0;
  private static final double PUCK_CONTACT_DISTANCE = 0.40;

  private static final double PUCK_DETECTION_DISTANCE = 1.20;
  private static final double PUCK_MAX_ANGLE = 1.40;

  private static final double SIDE_OBJECT_THRESHOLD = 900.0;
  private static final double SIDE_DANGER_THRESHOLD = 980.0;

  private static final double WALL_STUCK_THRESHOLD = 970.0;
  private static final int WALL_STUCK_TURN_TIME = 55;

  private static final double APPROACH_TURN_GAIN = 4.5;

  private static final int APPROACH_TIMEOUT = 180;

  private static final int TOUCH_BACK_TIME = 35;
  private static final int TOUCH_TURN_TIME = 90;
  private static final int SIDE_TURN_TIME = 40;

  public TERBot(Supervisor supervisor) {
    this.supervisor = supervisor;
    this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

    driveBase = new DriveBase(supervisor);
    sensors = new RobotSensors(supervisor, timeStep);
    arm = new Arm(supervisor, timeStep);

    String[] puckNames = {
        "PALET_1",
        "PALET_2",
        "PALET_3"
    };

    puckManager = new PuckManager(supervisor, puckNames);
  }

  public void run() {
    while (supervisor.step(timeStep) != -1) {
      stepCounter++;

      if (puckAttached && currentPuckIndex != -1) {
        puckManager.attachPuckToRobot(currentPuckIndex);
      }

      update();
    }
  }

  private void update() {
    double leftSpeed = 0.0;
    double rightSpeed = 0.0;

    boolean touched = sensors.isTouched();

    handleContact(touched);

    switch (mode) {
      case SEARCH:
        double[] searchSpeeds = updateSearch();
        leftSpeed = searchSpeeds[0];
        rightSpeed = searchSpeeds[1];
        break;

      case TOUCH_AVOID:
        double[] touchAvoidSpeeds = updateTouchAvoid();
        leftSpeed = touchAvoidSpeeds[0];
        rightSpeed = touchAvoidSpeeds[1];
        break;

      case APPROACH_PUCK:
        double[] approachSpeeds = updateApproachPuck();
        leftSpeed = approachSpeeds[0];
        rightSpeed = approachSpeeds[1];
        break;

      case LOWER_ARM:
        driveBase.stop();

        arm.lower();
        arm.openGripper();

        actionCounter++;

        if (actionCounter > 25) {
          mode = RobotMode.CLOSE_GRIPPER;
          actionCounter = 0;

          System.out.println("Bras baisse. Fermeture de la pince.");
        }
        break;

      case CLOSE_GRIPPER:
        driveBase.stop();

        arm.closeGripper();

        actionCounter++;

        if (actionCounter > 25) {
          puckAttached = true;

          mode = RobotMode.LIFT_ARM;
          actionCounter = 0;
          wallStuckCounter = 0;

          System.out.println("Palet ramasse : " + puckManager.getPuckName(currentPuckIndex));
        }
        break;

      case LIFT_ARM:
        driveBase.stop();

        arm.lift();

        actionCounter++;

        if (actionCounter > 30) {
          mode = RobotMode.GO_TO_DROP_ZONE;
          actionCounter = 0;

          System.out.println("Direction la zone de depot.");
        }
        break;

      case GO_TO_DROP_ZONE:
        double[] dropZoneSpeeds = updateGoToDropZone(touched);
        leftSpeed = dropZoneSpeeds[0];
        rightSpeed = dropZoneSpeeds[1];
        break;

      case DROP_PUCK:
        driveBase.stop();

        arm.openGripper();
        arm.lower();

        actionCounter++;

        if (actionCounter > 25) {
          puckAttached = false;

          if (currentPuckIndex != -1) {
            puckManager.dropPuck(currentPuckIndex, DROP_X, DROP_Y, DROP_Z);
          }

          currentPuckIndex = -1;

          mode = RobotMode.LIFT_ARM_AFTER_DROP;
          actionCounter = 0;
        }
        break;

      case LIFT_ARM_AFTER_DROP:
        driveBase.stop();

        arm.lift();

        actionCounter++;

        if (actionCounter > 25) {
          mode = RobotMode.BACK_AND_TURN_AFTER_DROP;
          actionCounter = 0;

          System.out.println("Bras releve. Recul puis rotation.");
        }
        break;

      case BACK_AND_TURN_AFTER_DROP:
        double[] backAndTurnSpeeds = updateBackAndTurnAfterDrop();
        leftSpeed = backAndTurnSpeeds[0];
        rightSpeed = backAndTurnSpeeds[1];
        break;
    }

    driveBase.setSpeed(leftSpeed, rightSpeed);

    previousTouched = touched;

    printDebug(touched);
  }

  private void handleContact(boolean touched) {
    if (
        stepCounter > 50
            && touched
            && !puckAttached
            && mode != RobotMode.LOWER_ARM
            && mode != RobotMode.CLOSE_GRIPPER
            && mode != RobotMode.LIFT_ARM
            && mode != RobotMode.GO_TO_DROP_ZONE
            && mode != RobotMode.DROP_PUCK
            && mode != RobotMode.LIFT_ARM_AFTER_DROP
            && mode != RobotMode.BACK_AND_TURN_AFTER_DROP
            && mode != RobotMode.TOUCH_AVOID
    ) {
      int nearestPuckIndex = puckManager.findNearestAvailablePuck();

      boolean puckClose = false;

      if (nearestPuckIndex != -1) {
        double distanceToPuck = puckManager.getDistanceToPuck(nearestPuckIndex);

        System.out.println(
            "Contact detecte. Palet le plus proche = "
                + puckManager.getPuckName(nearestPuckIndex)
                + " | distance="
                + distanceToPuck
        );

        if (distanceToPuck < PUCK_CONTACT_DISTANCE) {
          puckClose = true;
        }
      }

      if (puckClose && !puckTouched) {
        currentPuckIndex = nearestPuckIndex;

        puckDetected = true;
        puckTouched = true;

        mode = RobotMode.LOWER_ARM;
        actionCounter = 0;
        wallStuckCounter = 0;

        System.out.println(
            "Contact avec "
                + puckManager.getPuckName(currentPuckIndex)
                + ". Debut du ramassage."
        );
      } else if (!puckClose) {
        mode = RobotMode.TOUCH_AVOID;
        actionCounter = 0;

        currentPuckIndex = -1;
        puckDetected = false;
        puckTouched = false;
        wallStuckCounter = 0;

        System.out.println("Contact avec obstacle/dropzone sans palet. Evitement.");
      }
    }
  }

  private double[] updateSearch() {
    double leftSpeed = SEARCH_SPEED;
    double rightSpeed = SEARCH_SPEED;

    double leftValue = sensors.getLeftDistance();
    double rightValue = sensors.getRightDistance();
    double frontValue = sensors.getFrontDistance();

    boolean objectInFront = frontValue > FRONT_OBJECT_THRESHOLD;

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

    if (avoidObstacleCounter == 0 && !puckAttached) {
      int nearestPuckIndex = puckManager.findNearestAvailablePuck();

      if (nearestPuckIndex != -1) {
        Node puckNode = puckManager.getPuckNode(nearestPuckIndex);

        if (puckNode != null) {
          double[] robotPosition = supervisor.getSelf().getPosition();
          double[] puckPosition = puckNode.getPosition();

          double dx = puckPosition[0] - robotPosition[0];
          double dy = puckPosition[1] - robotPosition[1];

          double distanceToPuck = Math.sqrt(dx * dx + dy * dy);

          double targetAngle = Math.atan2(dy, dx);
          double robotAngle = MathUtils.getRobotYaw(supervisor);
          double angleError = MathUtils.normalizeAngle(targetAngle - robotAngle);

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

            mode = RobotMode.APPROACH_PUCK;
            actionCounter = 0;

            System.out.println(
                "Palet cible detecte : "
                    + puckManager.getPuckName(currentPuckIndex)
                    + " | distance="
                    + distanceToPuck
                    + " | angleError="
                    + angleError
            );
          }
        }
      }
    } else if (avoidObstacleCounter > 0) {
      avoidObstacleCounter--;

      leftSpeed = -avoidDirection * TURN_SPEED;
      rightSpeed = avoidDirection * TURN_SPEED;
    } else {
      if (leftValue > SIDE_DANGER_THRESHOLD && rightValue > SIDE_DANGER_THRESHOLD) {
        avoidDirection = 1;
        avoidObstacleCounter = SIDE_TURN_TIME;

        System.out.println("Obstacle detecte par les deux capteurs. Rotation simple.");
      } else if (leftValue > SIDE_OBJECT_THRESHOLD && leftValue > rightValue + 150.0) {
        avoidDirection = -1;
        avoidObstacleCounter = SIDE_TURN_TIME;

        System.out.println("Obstacle proche a gauche. Rotation vers la droite.");
      } else if (rightValue > SIDE_OBJECT_THRESHOLD && rightValue > leftValue + 150.0) {
        avoidDirection = 1;
        avoidObstacleCounter = SIDE_TURN_TIME;

        System.out.println("Obstacle proche a droite. Rotation vers la gauche.");
      }
    }

    return new double[] { leftSpeed, rightSpeed };
  }

  private double[] updateTouchAvoid() {
    double leftSpeed;
    double rightSpeed;

    if (actionCounter < TOUCH_BACK_TIME) {
      leftSpeed = BACK_SPEED;
      rightSpeed = BACK_SPEED;
    } else if (actionCounter < TOUCH_BACK_TIME + TOUCH_TURN_TIME) {
      leftSpeed = TURN_SPEED;
      rightSpeed = -TURN_SPEED;
    } else {
      mode = RobotMode.SEARCH;
      actionCounter = 0;

      previousTouched = false;
      currentPuckIndex = -1;
      puckDetected = false;
      puckTouched = false;
      wallStuckCounter = 0;

      System.out.println("Obstacle touche evite. Retour en recherche.");

      return new double[] { 0.0, 0.0 };
    }

    actionCounter++;

    return new double[] { leftSpeed, rightSpeed };
  }

  private double[] updateApproachPuck() {
    if (
        currentPuckIndex == -1
            || puckManager.getPuckNode(currentPuckIndex) == null
            || puckManager.isDelivered(currentPuckIndex)
    ) {
      resetSearch();

      System.out.println("Palet cible perdu. Retour en recherche.");

      return new double[] { 0.0, 0.0 };
    }

    double[] robotPosition = supervisor.getSelf().getPosition();
    double[] puckPosition = puckManager.getPuckNode(currentPuckIndex).getPosition();

    double dx = puckPosition[0] - robotPosition[0];
    double dy = puckPosition[1] - robotPosition[1];

    double distanceToPuck = Math.sqrt(dx * dx + dy * dy);

    double targetAngle = Math.atan2(dy, dx);
    double robotAngle = MathUtils.getRobotYaw(supervisor);
    double angleError = MathUtils.normalizeAngle(targetAngle - robotAngle);

    double correction = APPROACH_TURN_GAIN * angleError;

    if (correction > TURN_SPEED) {
      correction = TURN_SPEED;
    }

    if (correction < -TURN_SPEED) {
      correction = -TURN_SPEED;
    }

    double leftSpeed = APPROACH_SPEED - correction;
    double rightSpeed = APPROACH_SPEED + correction;

    System.out.println(
        "Approche palet | distance="
            + distanceToPuck
            + " | angleError="
            + angleError
            + " | correction="
            + correction
    );

    actionCounter++;

    if (actionCounter > APPROACH_TIMEOUT) {
      resetSearch();

      System.out.println("Approche trop longue. Retour en recherche sans recul.");
    }

    return new double[] { leftSpeed, rightSpeed };
  }

  private double[] updateGoToDropZone(boolean touched) {
    if (touched && puckAttached) {
      mode = RobotMode.DROP_PUCK;
      actionCounter = 0;

      System.out.println("Dropzone touchee avec palet. Depot du palet.");

      return new double[] { 0.0, 0.0 };
    }

    double[] robotPosition = supervisor.getSelf().getPosition();

    double dx = DROP_X - robotPosition[0];
    double dy = DROP_Y - robotPosition[1];

    double targetAngle = Math.atan2(dy, dx);
    double robotAngle = MathUtils.getRobotYaw(supervisor);
    double angleError = MathUtils.normalizeAngle(targetAngle - robotAngle);

    if (Math.abs(angleError) > 0.15) {
      if (angleError > 0.0) {
        return new double[] { -TURN_SPEED, TURN_SPEED };
      } else {
        return new double[] { TURN_SPEED, -TURN_SPEED };
      }
    }

    return new double[] { GO_DROP_SPEED, GO_DROP_SPEED };
  }

  private double[] updateBackAndTurnAfterDrop() {
    double leftSpeed;
    double rightSpeed;

    if (actionCounter < 35) {
      leftSpeed = BACK_SPEED;
      rightSpeed = BACK_SPEED;
    } else if (actionCounter < 85) {
      leftSpeed = TURN_SPEED;
      rightSpeed = -TURN_SPEED;
    } else {
      mode = RobotMode.SEARCH;
      actionCounter = 0;

      puckDetected = false;
      puckTouched = false;
      previousTouched = false;
      puckAttached = false;
      currentPuckIndex = -1;
      wallStuckCounter = 0;

      System.out.println("Retour en mode recherche apres depot.");

      return new double[] { 0.0, 0.0 };
    }

    actionCounter++;

    return new double[] { leftSpeed, rightSpeed };
  }

  private void resetSearch() {
    mode = RobotMode.SEARCH;
    actionCounter = 0;

    currentPuckIndex = -1;
    puckDetected = false;
    puckTouched = false;
    previousTouched = false;
    wallStuckCounter = 0;
  }

  private void printDebug(boolean touched) {
    int[] color = sensors.getAverageColor();

    System.out.println(
        "mode="
            + mode
            + " | ds_left="
            + sensors.getLeftDistance()
            + " | ds_right="
            + sensors.getRightDistance()
            + " | ds_front="
            + sensors.getFrontDistance()
            + " | RGB=("
            + color[0]
            + ","
            + color[1]
            + ","
            + color[2]
            + ")"
            + " | touched="
            + touched
            + " | previousTouched="
            + previousTouched
            + " | attached="
            + puckAttached
            + " | currentPuck="
            + currentPuckIndex
            + " | wallStuckCounter="
            + wallStuckCounter
    );
  }
}
