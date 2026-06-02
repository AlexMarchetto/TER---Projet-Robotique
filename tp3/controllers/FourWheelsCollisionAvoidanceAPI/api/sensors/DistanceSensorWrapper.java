package api.sensors;
import com.cyberbotics.webots.controller.DistanceSensor;

public class DistanceSensorWrapper {
  private final DistanceSensor sensor;
  public DistanceSensorWrapper(DistanceSensor sensor, int timeStep) {
    this.sensor = sensor;
    if (this.sensor != null) { this.sensor.enable(timeStep); }
  }
  public double getValue() { return sensor == null ? 0.0 : sensor.getValue(); }
  public boolean detectsObject(double threshold) { return getValue() > threshold; }
  public boolean exists() { return sensor != null; }
}
