package api.sensors;

import com.cyberbotics.webots.controller.DistanceSensor;

public class DistanceSensorWrapper {
  private final DistanceSensor sensor;

  public DistanceSensorWrapper(DistanceSensor sensor, int timeStep) {
    this.sensor = sensor;

    if (this.sensor != null) {
      // Enable the distance sensor to read values during the simulation.
      this.sensor.enable(timeStep);
    }
  }

  public double getValue() {
    // Return 0.0 if the sensor is missing to avoid null pointer errors.
    if (sensor == null) {
      return 0.0;
    }

    return sensor.getValue();
  }

  public boolean detectsObject(double threshold) {
    // An object is detected when the sensor value is above the given threshold.
    return getValue() > threshold;
  }

  public boolean exists() {
    return sensor != null;
  }
}