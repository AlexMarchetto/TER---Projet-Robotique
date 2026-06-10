package api.motors;

import com.cyberbotics.webots.controller.Supervisor;

import api.utils.MathUtils;

public class DriveBase {
  private final Wheel frontLeft;
  private final Wheel frontRight;
  private final Wheel rearLeft;
  private final Wheel rearRight;

  private final MotorGroup leftWheels;
  private final MotorGroup rightWheels;

  public DriveBase(Supervisor robot) {
    // Initialize each wheel with its Webots motor name.
    this.frontLeft = new Wheel(robot.getMotor("wheel1"));
    this.frontRight = new Wheel(robot.getMotor("wheel2"));
    this.rearLeft = new Wheel(robot.getMotor("wheel3"));
    this.rearRight = new Wheel(robot.getMotor("wheel4"));

    // Group wheels by side to simplify differential drive commands.
    this.leftWheels = new MotorGroup(frontLeft, rearLeft);
    this.rightWheels = new MotorGroup(frontRight, rearRight);
  }

  public void setSpeed(double leftSpeed, double rightSpeed) {
    leftWheels.setSpeed(leftSpeed);
    rightWheels.setSpeed(rightSpeed);
  }

  public void forward(double speed) {
    double velocity = Math.abs(speed);
    setSpeed(velocity, velocity);
  }

  public void backward(double speed) {
    double velocity = Math.abs(speed);
    setSpeed(-velocity, -velocity);
  }

  public void turnLeft(double speed) {
    double velocity = Math.abs(speed);

    // Left wheels move backward while right wheels move forward.
    setSpeed(-velocity, velocity);
  }

  public void turnRight(double speed) {
    double velocity = Math.abs(speed);

    // Left wheels move forward while right wheels move backward.
    setSpeed(velocity, -velocity);
  }

  public void curveLeft(double speed, double factor) {
    double velocity = Math.abs(speed);
    double clampedFactor = MathUtils.clamp(factor, 0.0, 1.0);

    // Slow down the left side to create a left curve.
    setSpeed(velocity * clampedFactor, velocity);
  }

  public void curveRight(double speed, double factor) {
    double velocity = Math.abs(speed);
    double clampedFactor = MathUtils.clamp(factor, 0.0, 1.0);

    // Slow down the right side to create a right curve.
    setSpeed(velocity, velocity * clampedFactor);
  }

  public void stop() {
    setSpeed(0.0, 0.0);
  }

  public Wheel frontLeft() {
    return frontLeft;
  }

  public Wheel frontRight() {
    return frontRight;
  }

  public Wheel rearLeft() {
    return rearLeft;
  }

  public Wheel rearRight() {
    return rearRight;
  }
}