import com.cyberbotics.webots.controller.Supervisor;

public class DriveBase {
  private final Wheel frontLeft;
  private final Wheel frontRight;
  private final Wheel rearLeft;
  private final Wheel rearRight;

  public DriveBase(Supervisor robot) {
    frontLeft = new Wheel(robot.getMotor("wheel1"));
    frontRight = new Wheel(robot.getMotor("wheel2"));
    rearLeft = new Wheel(robot.getMotor("wheel3"));
    rearRight = new Wheel(robot.getMotor("wheel4"));
  }

  public void setSpeed(double leftSpeed, double rightSpeed) {
    frontLeft.setVelocity(leftSpeed);
    rearLeft.setVelocity(leftSpeed);

    frontRight.setVelocity(rightSpeed);
    rearRight.setVelocity(rightSpeed);
  }

  public void stop() {
    setSpeed(0.0, 0.0);
  }
}
