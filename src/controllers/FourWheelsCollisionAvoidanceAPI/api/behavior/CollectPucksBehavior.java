package api.behavior;

import api.core.Robot;
import api.sensors.RGBColor;
import api.state.RobotMode;
import api.tasks.TimedTask;
import api.utils.MathUtils;

public class CollectPucksBehavior implements RobotBehavior {
  private final Robot robot;

  private RobotMode mode = RobotMode.SEARCH;

  private int currentPuckIndex = -1;

  private int avoidObstacleCounter = 0;
  private int counter = 0;
  private int stepCounter = 0;
  private int wallStuckCounter = 0;

  private int avoidDirection = 1;

  private boolean puckDetected = false;
  private boolean puckTouched = false;
  private boolean puckAttached = false;
  private boolean finishedMessagePrinted = false;

  private static final double SEARCH_SPEED = 4.0;
  private static final double APPROACH_SPEED = 0.8;
  private static final double GO_DROP_SPEED = 3.2;
  private static final double TURN_SPEED = 4.0;
  private static final double BACK_SPEED = -2.0;

  private static final double LEFT_WHITE_LINE_X = -1.0;
  private static final double RIGHT_WHITE_LINE_X = 1.0;

  private static final double DROP_OUTSIDE_OFFSET = 0.25;
  private static final double DROP_Z = 0.095;

  private static final double DROP_MIN_Y = -0.75;
  private static final double DROP_MAX_Y = 0.75;

  private static final double DROP_DISTANCE_THRESHOLD = 0.15;

  private double targetDropX = RIGHT_WHITE_LINE_X + DROP_OUTSIDE_OFFSET;
  private double targetDropY = 0.0;

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

  private static final double DROP_ALIGNMENT_THRESHOLD = 0.20;

  /*
   * Delay before hiding a dropped puck.
   * 3000 ms = 3 seconds.
   */
  private static final double HIDE_PUCK_DELAY_MS = 3000.0;

  public CollectPucksBehavior(Robot robot) {
    this.robot = robot;
  }

  @Override
  public void init() {
    robot.arm().lift();
    robot.gripper().open();
    robot.motors().stop();
  }

  @Override
  public void update() {
    stepCounter++;

    robot.sensors().update();

    if (robot.pucks().allPucksDelivered()) {
      mode = RobotMode.FINISHED;
    }

    if (mode == RobotMode.FINISHED) {
      updateFinished();
      printDebug();
      return;
    }

    if (puckAttached && currentPuckIndex != -1) {
      robot.pucks().attachPuckToRobot(currentPuckIndex);
    }

    handleContact();

    switch (mode) {
      case SEARCH:
        updateSearch();
        break;

      case TOUCH_AVOID:
        updateTouchAvoid();
        break;

      case APPROACH_PUCK:
        updateApproachPuck();
        break;

      case LOWER_ARM:
        updateLowerArm();
        break;

      case CLOSE_GRIPPER:
        updateCloseGripper();
        break;

      case LIFT_ARM:
        updateLiftArm();
        break;

      case GO_TO_DROP_ZONE:
        updateGoToDropZone();
        break;

      case DROP_PUCK:
        updateDropPuck();
        break;

      case LIFT_ARM_AFTER_DROP:
        updateLiftArmAfterDrop();
        break;

      case BACK_AND_TURN_AFTER_DROP:
        updateBackAndTurnAfterDrop();
        break;

      case FINISHED:
        updateFinished();
        break;
    }

    printDebug();
  }

  private void handleContact() {
    boolean touched = robot.sensors().isFrontTouched();

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
            && mode != RobotMode.FINISHED
    ) {
      int nearestPuckIndex = robot.pucks().findNearestAvailablePuck();

      boolean puckClose = false;

      if (nearestPuckIndex != -1) {
        double distanceToPuck = robot.pucks().getDistanceToPuck(nearestPuckIndex);

        System.out.println(
            "Contact detected. Nearest puck = "
                + robot.pucks().getPuckName(nearestPuckIndex)
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
        counter = 0;
        wallStuckCounter = 0;

        robot.motors().stop();

        System.out.println(
            "Contact with "
                + robot.pucks().getPuckName(currentPuckIndex)
                + ". Starting pickup sequence."
        );
      } else if (!puckClose) {
        mode = RobotMode.TOUCH_AVOID;
        counter = 0;

        currentPuckIndex = -1;
        puckDetected = false;
        puckTouched = false;
        wallStuckCounter = 0;

        System.out.println("Contact with obstacle. Avoiding.");
      }
    }
  }

  private void updateSearch() {
    double leftValue = robot.sensors().leftDistance();
    double rightValue = robot.sensors().rightDistance();
    double frontValue = robot.sensors().frontDistance();

    boolean objectInFront = frontValue > FRONT_OBJECT_THRESHOLD;

    double leftSpeed = SEARCH_SPEED;
    double rightSpeed = SEARCH_SPEED;

    if (leftValue > WALL_STUCK_THRESHOLD || rightValue > WALL_STUCK_THRESHOLD) {
      wallStuckCounter++;
    } else {
      wallStuckCounter = 0;
    }

    if (wallStuckCounter > 5) {
      if (leftValue > rightValue) {
        avoidDirection = -1;
        System.out.println("Robot stuck near left wall. Strong turn right.");
      } else {
        avoidDirection = 1;
        System.out.println("Robot stuck near right wall. Strong turn left.");
      }

      avoidObstacleCounter = WALL_STUCK_TURN_TIME;
      wallStuckCounter = 0;
    }

    if (avoidObstacleCounter == 0 && !puckAttached) {
      int bestPuckIndex = robot.pucks().findBestAvailablePuck(0.35);

      if (bestPuckIndex != -1) {
        double distanceToPuck = robot.pucks().getDistanceToPuck(bestPuckIndex);
        double angleError = robot.pucks().getAngleErrorToPuck(bestPuckIndex);

        if (
            objectInFront
                || (
                    distanceToPuck < PUCK_DETECTION_DISTANCE
                        && Math.abs(angleError) < PUCK_MAX_ANGLE
                )
        ) {
          currentPuckIndex = bestPuckIndex;

          puckDetected = true;
          puckTouched = false;

          mode = RobotMode.APPROACH_PUCK;
          counter = 0;

          System.out.println(
              "Target puck detected: "
                  + robot.pucks().getPuckName(currentPuckIndex)
                  + " | distance="
                  + distanceToPuck
                  + " | angleError="
                  + angleError
          );

          return;
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

        System.out.println("Obstacle detected by both side sensors. Turning.");
      } else if (leftValue > SIDE_OBJECT_THRESHOLD && leftValue > rightValue + 150.0) {
        avoidDirection = -1;
        avoidObstacleCounter = SIDE_TURN_TIME;

        System.out.println("Obstacle close on the left. Turning right.");
      } else if (rightValue > SIDE_OBJECT_THRESHOLD && rightValue > leftValue + 150.0) {
        avoidDirection = 1;
        avoidObstacleCounter = SIDE_TURN_TIME;

        System.out.println("Obstacle close on the right. Turning left.");
      }
    }

    robot.motors().setSpeed(leftSpeed, rightSpeed);
  }

  private void updateTouchAvoid() {
    if (counter < TOUCH_BACK_TIME) {
      robot.motors().backward(Math.abs(BACK_SPEED));
    } else if (counter < TOUCH_BACK_TIME + TOUCH_TURN_TIME) {
      robot.motors().turnLeft(TURN_SPEED);
    } else {
      mode = RobotMode.SEARCH;
      counter = 0;

      currentPuckIndex = -1;
      puckDetected = false;
      puckTouched = false;
      wallStuckCounter = 0;

      robot.motors().stop();

      System.out.println("Obstacle avoided. Back to search mode.");

      return;
    }

    counter++;
  }

  private void updateApproachPuck() {
    if (
        currentPuckIndex == -1
            || robot.pucks().getPuckNode(currentPuckIndex) == null
            || robot.pucks().isDelivered(currentPuckIndex)
    ) {
      resetSearch();

      System.out.println("Target puck lost. Back to search mode.");

      return;
    }

    double distanceToPuck = robot.pucks().getDistanceToPuck(currentPuckIndex);
    double angleError = robot.pucks().getAngleErrorToPuck(currentPuckIndex);

    double correction = APPROACH_TURN_GAIN * angleError;
    correction = MathUtils.clamp(correction, -TURN_SPEED, TURN_SPEED);

    double leftSpeed = APPROACH_SPEED - correction;
    double rightSpeed = APPROACH_SPEED + correction;

    robot.motors().setSpeed(leftSpeed, rightSpeed);

    System.out.println(
        "Approaching puck | distance="
            + distanceToPuck
            + " | angleError="
            + angleError
            + " | correction="
            + correction
    );

    counter++;

    if (counter > APPROACH_TIMEOUT) {
      resetSearch();

      System.out.println("Approach timeout. Back to search mode.");
    }
  }

  private void updateLowerArm() {
    robot.motors().stop();

    robot.arm().lower();
    robot.gripper().open();

    counter++;

    if (counter > 25) {
      mode = RobotMode.CLOSE_GRIPPER;
      counter = 0;

      System.out.println("Arm lowered. Closing gripper.");
    }
  }

  private void updateCloseGripper() {
    robot.motors().stop();

    robot.gripper().close();

    counter++;

    if (counter > 25) {
      puckAttached = true;

      chooseNearestDropZone();

      mode = RobotMode.LIFT_ARM;
      counter = 0;
      wallStuckCounter = 0;

      System.out.println(
          "Puck collected: "
              + robot.pucks().getPuckName(currentPuckIndex)
              + ". Going to selected drop zone."
      );
    }
  }

  private void updateLiftArm() {
    robot.motors().stop();

    robot.arm().lift();

    counter++;

    if (counter > 30) {
      mode = RobotMode.GO_TO_DROP_ZONE;
      counter = 0;

      System.out.println("Heading to drop zone.");
    }
  }

  private void updateGoToDropZone() {
    if (puckAttached && isNearDropZone()) {
      mode = RobotMode.DROP_PUCK;
      counter = 0;

      robot.motors().stop();

      System.out.println("Drop zone reached. Dropping puck.");

      return;
    }

    double[] position = robot.supervisor().getSelf().getPosition();

    double dx = targetDropX - position[0];
    double dy = targetDropY - position[1];

    double targetAngle = Math.atan2(dy, dx);
    double robotAngle = MathUtils.getRobotYaw(robot.supervisor());
    double angleError = MathUtils.normalizeAngle(targetAngle - robotAngle);

    if (Math.abs(angleError) > DROP_ALIGNMENT_THRESHOLD) {
      if (angleError > 0.0) {
        robot.motors().turnLeft(TURN_SPEED);
      } else {
        robot.motors().turnRight(TURN_SPEED);
      }
    } else {
      robot.motors().forward(GO_DROP_SPEED);
    }
  }

  private void updateDropPuck() {
    robot.motors().stop();

    robot.gripper().open();
    robot.arm().lower();

    counter++;

    if (counter > 25) {
      puckAttached = false;

      if (currentPuckIndex != -1) {
        final int droppedPuckIndex = currentPuckIndex;

        robot.pucks().dropPuck(droppedPuckIndex, targetDropX, targetDropY, DROP_Z);

        int delaySteps = (int) Math.round(HIDE_PUCK_DELAY_MS / robot.timeStep());

        robot.scheduler().add(new TimedTask(
            delaySteps,
            null,
            new Runnable() {
              @Override
              public void run() {
                robot.pucks().hidePuck(droppedPuckIndex);
              }
            }
        ));
      }

      currentPuckIndex = -1;

      mode = RobotMode.LIFT_ARM_AFTER_DROP;
      counter = 0;
    }
  }

  private void updateLiftArmAfterDrop() {
    robot.motors().stop();

    robot.arm().lift();

    counter++;

    if (counter > 25) {
      if (robot.pucks().allPucksDelivered()) {
        mode = RobotMode.FINISHED;
        counter = 0;

        System.out.println("Arm lifted after last drop. Robot finished.");
      } else {
        mode = RobotMode.BACK_AND_TURN_AFTER_DROP;
        counter = 0;

        System.out.println("Arm lifted after drop. Moving away from drop zone.");
      }
    }
  }

  private void updateBackAndTurnAfterDrop() {
    if (counter < 35) {
      robot.motors().backward(Math.abs(BACK_SPEED));
    } else if (counter < 85) {
      robot.motors().turnRight(TURN_SPEED);
    } else {
      mode = RobotMode.SEARCH;
      counter = 0;

      puckDetected = false;
      puckTouched = false;
      puckAttached = false;
      currentPuckIndex = -1;
      wallStuckCounter = 0;

      robot.motors().stop();

      System.out.println("Back to search mode after drop.");

      return;
    }

    counter++;
  }

  private void updateFinished() {
    robot.motors().stop();
    robot.arm().lift();
    robot.gripper().open();

    puckAttached = false;
    currentPuckIndex = -1;

    if (!finishedMessagePrinted) {
      System.out.println("All pucks have been delivered. Robot stopped.");
      finishedMessagePrinted = true;
    }
  }

  private void chooseNearestDropZone() {
    double[] referencePosition;

    if (currentPuckIndex != -1 && robot.pucks().getPuckNode(currentPuckIndex) != null) {
      referencePosition = robot.pucks().getPuckPosition(currentPuckIndex);
    } else {
      referencePosition = robot.supervisor().getSelf().getPosition();
    }

    double referenceX = referencePosition[0];
    double referenceY = referencePosition[1];

    double distanceToLeftLine = Math.abs(referenceX - LEFT_WHITE_LINE_X);
    double distanceToRightLine = Math.abs(referenceX - RIGHT_WHITE_LINE_X);

    if (distanceToLeftLine < distanceToRightLine) {
      targetDropX = LEFT_WHITE_LINE_X - DROP_OUTSIDE_OFFSET;
    } else {
      targetDropX = RIGHT_WHITE_LINE_X + DROP_OUTSIDE_OFFSET;
    }

    targetDropY = MathUtils.clamp(referenceY, DROP_MIN_Y, DROP_MAX_Y);

    System.out.println(
        "Selected drop zone: x="
            + targetDropX
            + " | y="
            + targetDropY
    );
  }

  private boolean isNearDropZone() {
    double[] position = robot.supervisor().getSelf().getPosition();

    double dx = targetDropX - position[0];
    double dy = targetDropY - position[1];

    double distance = Math.sqrt(dx * dx + dy * dy);

    return distance < DROP_DISTANCE_THRESHOLD;
  }

  private void resetSearch() {
    mode = RobotMode.SEARCH;
    counter = 0;

    currentPuckIndex = -1;
    puckDetected = false;
    puckTouched = false;
    wallStuckCounter = 0;

    robot.motors().stop();
  }

  private void printDebug() {
    RGBColor color = robot.sensors().color();

    System.out.println(
        "mode="
            + mode
            + " | ds_left="
            + robot.sensors().leftDistance()
            + " | ds_right="
            + robot.sensors().rightDistance()
            + " | ds_front="
            + robot.sensors().frontDistance()
            + " | RGB="
            + color
            + " | touched="
            + robot.sensors().isFrontTouched()
            + " | attached="
            + puckAttached
            + " | currentPuck="
            + currentPuckIndex
            + " | targetDrop=("
            + targetDropX
            + ", "
            + targetDropY
            + ")"
            + " | wallStuckCounter="
            + wallStuckCounter
    );
  }
}