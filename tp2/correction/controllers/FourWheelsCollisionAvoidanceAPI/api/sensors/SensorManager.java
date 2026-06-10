package api.sensors;

import com.cyberbotics.webots.controller.Supervisor;

public class SensorManager {
  private final DistanceSensorWrapper rightDistanceSensor;
  private final DistanceSensorWrapper leftDistanceSensor;
  private final DistanceSensorWrapper frontDistanceSensor;

  private final ColorSensorWrapper colorSensor;
  private final TouchSensorWrapper frontTouchSensor;

  public SensorManager(Supervisor robot, int timeStep) {
    /*
     * Initialisation des trois capteurs de distance.
     * Les noms doivent correspondre exactement aux noms déclarés dans Webots.
     */
    this.rightDistanceSensor = new DistanceSensorWrapper(
        robot.getDistanceSensor("ds_right"),
        timeStep
    );

    this.leftDistanceSensor = new DistanceSensorWrapper(
        robot.getDistanceSensor("ds_left"),
        timeStep
    );

    this.frontDistanceSensor = new DistanceSensorWrapper(
        robot.getDistanceSensor("ds_front"),
        timeStep
    );

    /*
     * Initialisation du capteur de couleur et du capteur tactile.
     * Ces deux wrappers sont déjà fournis pour ce TP.
     */
    this.colorSensor = new ColorSensorWrapper(
        robot.getCamera("color_sensor"),
        timeStep
    );

    this.frontTouchSensor = new TouchSensorWrapper(
        robot.getTouchSensor("touch_front"),
        timeStep
    );
  }

  public void update() {
    /*
     * Le capteur tactile doit être mis à jour à chaque pas de simulation.
     * Cela permet de savoir si le robot vient juste de toucher un objet.
     */
    frontTouchSensor.update();
  }

  public double rightDistance() {
    // Retourne la valeur mesurée par le capteur de distance droit.
    return rightDistanceSensor.getValue();
  }

  public double leftDistance() {
    // Retourne la valeur mesurée par le capteur de distance gauche.
    return leftDistanceSensor.getValue();
  }

  public double frontDistance() {
    // Retourne la valeur mesurée par le capteur de distance avant.
    return frontDistanceSensor.getValue();
  }

  public boolean frontDetectsObject(double threshold) {
    // Indique si le capteur avant détecte un objet selon le seuil donné.
    return frontDistanceSensor.detectsObject(threshold);
  }

  public boolean leftDetectsObject(double threshold) {
    // Indique si le capteur gauche détecte un objet selon le seuil donné.
    return leftDistanceSensor.detectsObject(threshold);
  }

  public boolean rightDetectsObject(double threshold) {
    // Indique si le capteur droit détecte un objet selon le seuil donné.
    return rightDistanceSensor.detectsObject(threshold);
  }

  public boolean isFrontTouched() {
    return frontTouchSensor.isPressed();
  }

  public boolean wasFrontJustTouched() {
    return frontTouchSensor.wasJustPressed();
  }

  public RGBColor color() {
    return colorSensor.getRGB();
  }

  public boolean seesRed() {
    return colorSensor.seesRed();
  }

  public DistanceSensorWrapper frontDistanceSensor() {
    return frontDistanceSensor;
  }

  public DistanceSensorWrapper leftDistanceSensor() {
    return leftDistanceSensor;
  }

  public DistanceSensorWrapper rightDistanceSensor() {
    return rightDistanceSensor;
  }

  public TouchSensorWrapper frontTouchSensor() {
    return frontTouchSensor;
  }

  public ColorSensorWrapper colorSensor() {
    return colorSensor;
  }
}