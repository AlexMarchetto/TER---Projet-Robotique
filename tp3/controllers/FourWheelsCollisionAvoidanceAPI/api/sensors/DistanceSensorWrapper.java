package api.sensors;
import com.cyberbotics.webots.controller.DistanceSensor;

public class DistanceSensorWrapper {
  private final DistanceSensor sensor;

  public DistanceSensorWrapper(DistanceSensor sensor, int timeStep) {
    this.sensor = sensor;

    // TODO 4.1
    // Activez le capteur avec enable(timeStep) si celui-ci existe.
    if (this.sensor != null) {
      // A COMPLETER
    }
  }

  public double getValue() {
    // TODO 4.2
    // Retournez la valeur du capteur. Si le capteur n'existe pas, retournez 0.0.
    return 0.0; // A MODIFIER
  }

  public boolean detectsObject(double threshold) {
    // TODO 4.3
    // Retournez true si la valeur du capteur depasse le seuil donne.
    // Dans cette configuration Webots, une valeur plus grande indique un objet plus proche.
    return false; // A MODIFIER
  }

  public boolean exists() { return sensor != null; }
}
