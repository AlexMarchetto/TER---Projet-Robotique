package api.sensors;
import com.cyberbotics.webots.controller.Supervisor;

public class SensorManager {
  private final DistanceSensorWrapper rightDistanceSensor;
  private final DistanceSensorWrapper leftDistanceSensor;
  private final DistanceSensorWrapper frontDistanceSensor;
  private final ColorSensorWrapper colorSensor;
  private final TouchSensorWrapper frontTouchSensor;

  public SensorManager(Supervisor robot, int timeStep) {
    // TODO 5.1
    // Initialisez les trois capteurs de distance avec les noms Webots :
    // "ds_right", "ds_left" et "ds_front".
    this.rightDistanceSensor = null; // A MODIFIER
    this.leftDistanceSensor = null;  // A MODIFIER
    this.frontDistanceSensor = null; // A MODIFIER

    // Ces capteurs sont fournis complets pour ce TP.
    this.colorSensor = new ColorSensorWrapper(robot.getCamera("color_sensor"), timeStep);
    this.frontTouchSensor = new TouchSensorWrapper(robot.getTouchSensor("touch_front"), timeStep);
  }

  public void update() {
    // TODO 5.2
    // Mettez a jour le capteur tactile pour detecter les nouveaux contacts.
  }

  public double rightDistance() {
    // TODO 5.3
    return 0.0; // A MODIFIER
  }

  public double leftDistance() {
    // TODO 5.4
    return 0.0; // A MODIFIER
  }

  public double frontDistance() {
    // TODO 5.5
    return 0.0; // A MODIFIER
  }

  public boolean frontDetectsObject(double threshold) {
    // TODO 5.6
    return false; // A MODIFIER
  }

  public boolean leftDetectsObject(double threshold) {
    // TODO 5.7
    return false; // A MODIFIER
  }

  public boolean rightDetectsObject(double threshold) {
    // TODO 5.8
    return false; // A MODIFIER
  }

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
