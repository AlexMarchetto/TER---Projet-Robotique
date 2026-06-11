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
      // Activez le capteur tactile pour lire les valeurs de contact pendant la simulation.
      this.sensor.enable(timeStep);
    }
  }

  public void update() {
    // Enregistrez l'état précédent avant de lire l'état actuel.
    previousPressed = currentPressed;
    currentPressed = sensor != null && sensor.getValue() > 0.0;
  }

  public boolean isPressed() {
    return currentPressed;
  }

  public boolean wasJustPressed() {
    // Détecter le moment précis où le capteur est enfoncé.
    return currentPressed && !previousPressed;
  }

  public boolean wasJustReleased() {
    // Détecter le moment précis où le capteur cesse d'être enfoncé.
    return !currentPressed && previousPressed;
  }

  public boolean exists() {
    return sensor != null;
  }
}