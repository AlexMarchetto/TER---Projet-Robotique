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

    if (this.sensor != null) {
      // Enable the touch sensor to read contact values during the simulation.
      this.sensor.enable(timeStep);
    }
  }

  public void update() {
    // Store the previous state before reading the current one.
    previousPressed = currentPressed;
    currentPressed = sensor != null && sensor.getValue() > 0.0;
  }

  public boolean isPressed() {
    return currentPressed;
  }

  public boolean wasJustPressed() {
    // Detect the exact step when the sensor becomes pressed.
    return currentPressed && !previousPressed;
  }

  public boolean wasJustReleased() {
    // Detect the exact step when the sensor stops being pressed.
    return !currentPressed && previousPressed;
  }

  public boolean exists() {
    return sensor != null;
  }
}