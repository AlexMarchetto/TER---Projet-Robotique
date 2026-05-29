package api.sensors;
import com.cyberbotics.webots.controller.TouchSensor;

public class TouchSensorWrapper {
  private final TouchSensor sensor;
  private boolean previousPressed;
  private boolean currentPressed;

  public TouchSensorWrapper(TouchSensor sensor, int timeStep) {
    this.sensor = sensor;
    this.previousPressed = false;
    this.currentPressed = false;
    if (this.sensor != null) { this.sensor.enable(timeStep); }
  }
  public void update() { previousPressed = currentPressed; currentPressed = sensor != null && sensor.getValue() > 0.0; }
  public boolean isPressed() { return currentPressed; }
  public boolean wasJustPressed() { return currentPressed && !previousPressed; }
  public boolean wasJustReleased() { return !currentPressed && previousPressed; }
  public boolean exists() { return sensor != null; }
}
