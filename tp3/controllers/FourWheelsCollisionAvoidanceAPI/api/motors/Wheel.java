package api.motors;
import com.cyberbotics.webots.controller.Motor;

public class Wheel {
  private final Motor motor;
  private double currentSpeed;

  public Wheel(Motor motor) {
    this.motor = motor;
    this.currentSpeed = 0.0;
    if (this.motor != null) {
      this.motor.setPosition(Double.POSITIVE_INFINITY);
      this.motor.setVelocity(0.0);
    }
  }

  public void setSpeed(double speed) {
    currentSpeed = speed;
    if (motor != null) { motor.setVelocity(speed); }
  }
  public void forward(double speed) { setSpeed(Math.abs(speed)); }
  public void backward(double speed) { setSpeed(-Math.abs(speed)); }
  public void stop() { setSpeed(0.0); }
  public double getCurrentSpeed() { return currentSpeed; }
  public boolean exists() { return motor != null; }
}
