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
    this.frontLeft = new Wheel(robot.getMotor("wheel1"));
    this.frontRight = new Wheel(robot.getMotor("wheel2"));
    this.rearLeft = new Wheel(robot.getMotor("wheel3"));
    this.rearRight = new Wheel(robot.getMotor("wheel4"));
    this.leftWheels = new MotorGroup(frontLeft, rearLeft);
    this.rightWheels = new MotorGroup(frontRight, rearRight);
  }

  public void setSpeed(double leftSpeed, double rightSpeed) {
    leftWheels.setSpeed(leftSpeed);
    rightWheels.setSpeed(rightSpeed);
  }
  public void forward(double speed) { setSpeed(Math.abs(speed), Math.abs(speed)); }
  public void backward(double speed) { setSpeed(-Math.abs(speed), -Math.abs(speed)); }
  public void turnLeft(double speed) { double v = Math.abs(speed); setSpeed(-v, v); }
  public void turnRight(double speed) { double v = Math.abs(speed); setSpeed(v, -v); }
  public void curveLeft(double speed, double factor) { double v = Math.abs(speed); setSpeed(v * MathUtils.clamp(factor, 0.0, 1.0), v); }
  public void curveRight(double speed, double factor) { double v = Math.abs(speed); setSpeed(v, v * MathUtils.clamp(factor, 0.0, 1.0)); }
  public void stop() { setSpeed(0.0, 0.0); }
  public Wheel frontLeft() { return frontLeft; }
  public Wheel frontRight() { return frontRight; }
  public Wheel rearLeft() { return rearLeft; }
  public Wheel rearRight() { return rearRight; }
}
