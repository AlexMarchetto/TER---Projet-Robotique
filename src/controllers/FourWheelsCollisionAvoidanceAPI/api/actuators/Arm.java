package api.actuators;

import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.Supervisor;

public class Arm {
  private final Motor armMotor;
  private final PositionSensor armSensor;

  private double upPosition = -0.65;
  private double downPosition = 0.35;

  public Arm(Supervisor robot, int timeStep) {
    this.armMotor = robot.getMotor("arm_motor");
    this.armSensor = robot.getPositionSensor("arm_sensor");

    // Enable the position sensor to read the current arm position.
    if (armSensor != null) {
      armSensor.enable(timeStep);
    }
  }

  public void lift() {
    moveTo(upPosition);
  }

  public void lower() {
    moveTo(downPosition);
  }

  public void moveTo(double position) {
    // Send a target position to the arm motor.
    if (armMotor != null) {
      armMotor.setPosition(position);
    }
  }

  public double getPosition() {
    // Return 0.0 if the sensor is missing to avoid null pointer errors.
    if (armSensor == null) {
      return 0.0;
    }

    return armSensor.getValue();
  }

  public void setUpPosition(double upPosition) {
    this.upPosition = upPosition;
  }

  public void setDownPosition(double downPosition) {
    this.downPosition = downPosition;
  }

  public boolean exists() {
    return armMotor != null;
  }
}