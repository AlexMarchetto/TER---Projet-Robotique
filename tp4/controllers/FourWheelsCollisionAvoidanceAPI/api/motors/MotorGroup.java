package api.motors;

public class MotorGroup {
  private final Wheel[] wheels;

  public MotorGroup(Wheel... wheels) {
    this.wheels = wheels;
  }

  public void setSpeed(double speed) {
    // Apply the same speed to every available wheel in the group.
    for (Wheel wheel : wheels) {
      if (wheel != null) {
        wheel.setSpeed(speed);
      }
    }
  }

  public void forward(double speed) {
    setSpeed(Math.abs(speed));
  }

  public void backward(double speed) {
    setSpeed(-Math.abs(speed));
  }

  public void stop() {
    setSpeed(0.0);
  }
}