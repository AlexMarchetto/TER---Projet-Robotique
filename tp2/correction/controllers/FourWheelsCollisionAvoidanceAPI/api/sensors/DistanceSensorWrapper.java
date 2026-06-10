package api.sensors;

import com.cyberbotics.webots.controller.DistanceSensor;

public class DistanceSensorWrapper {
  private final DistanceSensor sensor;

  public DistanceSensorWrapper(DistanceSensor sensor, int timeStep) {
    this.sensor = sensor;

    if (this.sensor != null) {
      // Activation du capteur pour permettre la lecture de ses valeurs à chaque pas de simulation.
      this.sensor.enable(timeStep);
    }
  }

  public double getValue() {
    // Si le capteur n'existe pas, on retourne 0.0 pour éviter une erreur d'exécution.
    if (sensor == null) {
      return 0.0;
    }

    // Retourne la valeur actuelle mesurée par le capteur de distance.
    return sensor.getValue();
  }

  public boolean detectsObject(double threshold) {
    /*
     * Dans cette configuration Webots, plus la valeur est grande,
     * plus l'objet détecté est proche du capteur.
     */
    return getValue() > threshold;
  }

  public boolean exists() {
    return sensor != null;
  }
}