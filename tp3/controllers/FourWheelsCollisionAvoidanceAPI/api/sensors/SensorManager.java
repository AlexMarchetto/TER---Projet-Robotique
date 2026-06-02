package api.sensors;
import com.cyberbotics.webots.controller.Supervisor;

public class SensorManager {
  private final DistanceSensorWrapper rightDistanceSensor;
  private final DistanceSensorWrapper leftDistanceSensor;
  private final DistanceSensorWrapper frontDistanceSensor;
  private final ColorSensorWrapper colorSensor;
  private final TouchSensorWrapper frontTouchSensor;

  public SensorManager(Supervisor robot, int timeStep) {
    this.rightDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_right"), timeStep);
    this.leftDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_left"), timeStep);
    this.frontDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_front"), timeStep);
    this.colorSensor = new ColorSensorWrapper(robot.getCamera("color_sensor"), timeStep);
    this.frontTouchSensor = new TouchSensorWrapper(robot.getTouchSensor("touch_front"), timeStep);
  }

  public void update() { frontTouchSensor.update(); }
  public double rightDistance() { return rightDistanceSensor.getValue(); }
  public double leftDistance() { return leftDistanceSensor.getValue(); }
  public double frontDistance() { return frontDistanceSensor.getValue(); }
  public boolean frontDetectsObject(double threshold) { return frontDistanceSensor.detectsObject(threshold); }
  public boolean leftDetectsObject(double threshold) { return leftDistanceSensor.detectsObject(threshold); }
  public boolean rightDetectsObject(double threshold) { return rightDistanceSensor.detectsObject(threshold); }
  public boolean isFrontTouched() { return frontTouchSensor.isPressed(); }
  public boolean wasFrontJustTouched() { return frontTouchSensor.wasJustPressed(); }
  public RGBColor color() { return colorSensor.getRGB(); }
  public boolean seesRed() { return colorSensor.seesRed(); }
  public DistanceSensorWrapper frontDistanceSensor() { return frontDistanceSensor; }
  public DistanceSensorWrapper leftDistanceSensor() { return leftDistanceSensor; }
  public DistanceSensorWrapper rightDistanceSensor() { return rightDistanceSensor; }
  public TouchSensorWrapper frontTouchSensor() { return frontTouchSensor; }
  public ColorSensorWrapper colorSensor() { return colorSensor; }
}
