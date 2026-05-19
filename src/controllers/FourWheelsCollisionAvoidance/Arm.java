import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.Supervisor;

public class Arm {
  private final Motor armMotor;
  private final Motor gripperLeftMotor;
  private final Motor gripperRightMotor;
  private final PositionSensor armSensor;

  private static final double ARM_UP = -0.65;
  private static final double ARM_DOWN = 0.35;

  private static final double GRIPPER_OPEN_LEFT = 0.2;
  private static final double GRIPPER_OPEN_RIGHT = -0.2;

  private static final double GRIPPER_CLOSED_LEFT = -0.55;
  private static final double GRIPPER_CLOSED_RIGHT = 0.55;

  public Arm(Supervisor robot, int timeStep) {
    armMotor = robot.getMotor("arm_motor");
    gripperLeftMotor = robot.getMotor("gripper_left_motor");
    gripperRightMotor = robot.getMotor("gripper_right_motor");

    armSensor = robot.getPositionSensor("arm_sensor");

    if (armSensor != null) {
      armSensor.enable(timeStep);
    }

    lift();
    openGripper();
  }

  public void lift() {
    if (armMotor != null) {
      armMotor.setPosition(ARM_UP);
    }
  }

  public void lower() {
    if (armMotor != null) {
      armMotor.setPosition(ARM_DOWN);
    }
  }

  public void openGripper() {
    if (gripperLeftMotor != null) {
      gripperLeftMotor.setPosition(GRIPPER_OPEN_LEFT);
    }

    if (gripperRightMotor != null) {
      gripperRightMotor.setPosition(GRIPPER_OPEN_RIGHT);
    }
  }

  public void closeGripper() {
    if (gripperLeftMotor != null) {
      gripperLeftMotor.setPosition(GRIPPER_CLOSED_LEFT);
    }

    if (gripperRightMotor != null) {
      gripperRightMotor.setPosition(GRIPPER_CLOSED_RIGHT);
    }
  }
}
