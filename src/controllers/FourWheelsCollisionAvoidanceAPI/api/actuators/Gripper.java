package api.actuators;

import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Supervisor;

public class Gripper {
  private final Motor leftMotor;
  private final Motor rightMotor;

  private double openLeftPosition = 0.2;
  private double openRightPosition = -0.2;

  private double closedLeftPosition = -0.55;
  private double closedRightPosition = 0.55;

  private boolean open;

  public Gripper(Supervisor robot) {
    this.leftMotor = robot.getMotor("gripper_left_motor");
    this.rightMotor = robot.getMotor("gripper_right_motor");
    this.open = false;
  }

  public void open() {
    // Move both gripper motors to their open positions.
    if (leftMotor != null) {
      leftMotor.setPosition(openLeftPosition);
    }

    if (rightMotor != null) {
      rightMotor.setPosition(openRightPosition);
    }

    open = true;
  }

  public void close() {
    // Move both gripper motors to their closed positions.
    if (leftMotor != null) {
      leftMotor.setPosition(closedLeftPosition);
    }

    if (rightMotor != null) {
      rightMotor.setPosition(closedRightPosition);
    }

    open = false;
  }

  public boolean isOpen() {
    return open;
  }

  public void setOpenPositions(double left, double right) {
    this.openLeftPosition = left;
    this.openRightPosition = right;
  }

  public void setClosedPositions(double left, double right) {
    this.closedLeftPosition = left;
    this.closedRightPosition = right;
  }

  public boolean exists() {
    // The gripper exists if at least one of its motors is available.
    return leftMotor != null || rightMotor != null;
  }
}