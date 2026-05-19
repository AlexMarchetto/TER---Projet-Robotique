import com.cyberbotics.webots.controller.Motor;

public class Wheel {
  private final Motor motor;

  public Wheel(Motor motor) {
    this.motor = motor;

    if (this.motor != null) {
      this.motor.setPosition(Double.POSITIVE_INFINITY);
      this.motor.setVelocity(0.0);
    }
  }

  public void setVelocity(double velocity) {
    if (motor != null) {
      motor.setVelocity(velocity);
    }
  }

  public void stop() {
    setVelocity(0.0);
  }
}
