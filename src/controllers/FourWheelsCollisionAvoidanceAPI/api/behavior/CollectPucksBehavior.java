package api.behavior;
import api.core.TERBot;
import api.state.RobotMode;
import api.utils.MathUtils;

public class CollectPucksBehavior implements RobotBehavior {
  private final TERBot robot;
  private RobotMode mode = RobotMode.SEARCH;
  private int currentPuckIndex = -1;
  private int counter = 0;
  private boolean puckAttached = false;
  private static final double SEARCH_SPEED = 4.0;
  private static final double APPROACH_SPEED = 0.8;
  private static final double TURN_SPEED = 4.0;
  private static final double BACK_SPEED = -2.0;
  private static final double DROP_X = -0.9;
  private static final double DROP_Y = 0.0;
  private static final double DROP_Z = 0.095;
  private static final double FRONT_THRESHOLD = 350.0;
  private static final double SIDE_THRESHOLD = 900.0;
  private static final double PUCK_CONTACT_DISTANCE = 0.40;
  private static final double APPROACH_GAIN = 4.5;

  public CollectPucksBehavior(TERBot robot) { this.robot = robot; }

  @Override public void init() {
    robot.arm().lift();
    robot.gripper().open();
    robot.motors().stop();
  }

  @Override public void update() {
    robot.sensors().update();
    if (puckAttached && currentPuckIndex != -1) { robot.pucks().attachPuckToRobot(currentPuckIndex); }

    switch (mode) {
      case SEARCH: updateSearch(); break;
      case APPROACH_PUCK: updateApproachPuck(); break;
      case LOWER_ARM: updateLowerArm(); break;
      case CLOSE_GRIPPER: updateCloseGripper(); break;
      case LIFT_ARM: updateLiftArm(); break;
      case GO_TO_DROP_ZONE: updateGoToDropZone(); break;
      case DROP_PUCK: updateDropPuck(); break;
      case LIFT_ARM_AFTER_DROP: updateLiftArmAfterDrop(); break;
      case BACK_AND_TURN_AFTER_DROP: updateBackAndTurnAfterDrop(); break;
      case TOUCH_AVOID: updateTouchAvoid(); break;
    }

    debug();
  }

  private void updateSearch() {
    currentPuckIndex = robot.pucks().findBestAvailablePuck(0.35);

    if (robot.sensors().leftDetectsObject(SIDE_THRESHOLD)) { robot.motors().turnRight(TURN_SPEED); return; }
    if (robot.sensors().rightDetectsObject(SIDE_THRESHOLD)) { robot.motors().turnLeft(TURN_SPEED); return; }

    if (currentPuckIndex != -1) {
      double distance = robot.pucks().getDistanceToPuck(currentPuckIndex);
      double angleError = robot.pucks().getAngleErrorToPuck(currentPuckIndex);
      if (robot.sensors().frontDetectsObject(FRONT_THRESHOLD) || distance < 1.20 || Math.abs(angleError) < 1.40) {
        mode = RobotMode.APPROACH_PUCK;
        counter = 0;
        return;
      }
    }

    robot.motors().forward(SEARCH_SPEED);
  }

  private void updateApproachPuck() {
    if (currentPuckIndex == -1 || robot.pucks().isDelivered(currentPuckIndex)) { resetSearch(); return; }

    double distance = robot.pucks().getDistanceToPuck(currentPuckIndex);
    double angleError = robot.pucks().getAngleErrorToPuck(currentPuckIndex);
    double correction = MathUtils.clamp(APPROACH_GAIN * angleError, -TURN_SPEED, TURN_SPEED);
    robot.motors().setSpeed(APPROACH_SPEED - correction, APPROACH_SPEED + correction);

    if (robot.sensors().isFrontTouched() && distance < PUCK_CONTACT_DISTANCE) {
      mode = RobotMode.LOWER_ARM;
      counter = 0;
      robot.motors().stop();
    }

    counter++;
    if (counter > 200) { resetSearch(); }
  }

  private void updateLowerArm() {
    robot.motors().stop();
    robot.arm().lower();
    robot.gripper().open();
    counter++;
    if (counter > 25) { mode = RobotMode.CLOSE_GRIPPER; counter = 0; }
  }

  private void updateCloseGripper() {
    robot.motors().stop();
    robot.gripper().close();
    counter++;
    if (counter > 25) { puckAttached = true; mode = RobotMode.LIFT_ARM; counter = 0; }
  }

  private void updateLiftArm() {
    robot.motors().stop();
    robot.arm().lift();
    counter++;
    if (counter > 30) { mode = RobotMode.GO_TO_DROP_ZONE; counter = 0; }
  }

  private void updateGoToDropZone() {
    if (robot.sensors().isFrontTouched() && puckAttached) { mode = RobotMode.DROP_PUCK; counter = 0; robot.motors().stop(); return; }
    double[] position = robot.supervisor().getSelf().getPosition();
    double dx = DROP_X - position[0];
    double dy = DROP_Y - position[1];
    double targetAngle = Math.atan2(dy, dx);
    double robotAngle = MathUtils.getRobotYaw(robot.supervisor());
    double angleError = MathUtils.normalizeAngle(targetAngle - robotAngle);
    if (Math.abs(angleError) > 0.15) {
      if (angleError > 0.0) { robot.motors().turnLeft(TURN_SPEED); }
      else { robot.motors().turnRight(TURN_SPEED); }
    } else {
      robot.motors().forward(3.2);
    }
  }

  private void updateDropPuck() {
    robot.motors().stop();
    robot.arm().lower();
    robot.gripper().open();
    counter++;
    if (counter > 25) {
      puckAttached = false;
      if (currentPuckIndex != -1) { robot.pucks().dropPuck(currentPuckIndex, DROP_X, DROP_Y, DROP_Z); }
      currentPuckIndex = -1;
      mode = RobotMode.LIFT_ARM_AFTER_DROP;
      counter = 0;
    }
  }

  private void updateLiftArmAfterDrop() {
    robot.arm().lift();
    counter++;
    if (counter > 25) { mode = RobotMode.BACK_AND_TURN_AFTER_DROP; counter = 0; }
  }

  private void updateBackAndTurnAfterDrop() {
    if (counter < 35) { robot.motors().backward(Math.abs(BACK_SPEED)); }
    else if (counter < 85) { robot.motors().turnRight(TURN_SPEED); }
    else { resetSearch(); return; }
    counter++;
  }

  private void updateTouchAvoid() {
    if (counter < 35) { robot.motors().backward(Math.abs(BACK_SPEED)); }
    else if (counter < 90) { robot.motors().turnLeft(TURN_SPEED); }
    else { resetSearch(); return; }
    counter++;
  }

  private void resetSearch() {
    mode = RobotMode.SEARCH;
    currentPuckIndex = -1;
    counter = 0;
    puckAttached = false;
  }

  private void debug() {
    System.out.println("mode=" + mode
        + " | ds_left=" + robot.sensors().leftDistance()
        + " | ds_right=" + robot.sensors().rightDistance()
        + " | ds_front=" + robot.sensors().frontDistance()
        + " | color=" + robot.sensors().color()
        + " | touched=" + robot.sensors().isFrontTouched()
        + " | puck=" + currentPuckIndex
        + " | attached=" + puckAttached);
  }
}
