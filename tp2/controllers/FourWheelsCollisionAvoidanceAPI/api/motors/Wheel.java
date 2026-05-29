package api.motors;
import com.cyberbotics.webots.controller.Motor;

public class Wheel {
  private final Motor motor;
  private double currentSpeed;

  public Wheel(Motor motor) {
    this.motor = motor;
    this.currentSpeed = 0.0;

    // TODO 1.1
    // Une roue doit tourner en continu pour faire avancer le robot.
    // Configurez le moteur en rotation continue avec setPosition(Double.POSITIVE_INFINITY),
    // puis initialisez sa vitesse a 0.0.
    if (this.motor != null) {
      // A COMPLETER
    }
  }

  public void setSpeed(double speed) {
    // TODO 1.2
    // Memorisez la vitesse courante dans currentSpeed, puis appliquez-la au moteur.
    // Pensez a verifier que motor n'est pas null.
    currentSpeed = 0.0; // A MODIFIER
  }

  public void forward(double speed) {
    // TODO 1.3
    // Faire avancer la roue avec une vitesse positive.
  }

  public void backward(double speed) {
    // TODO 1.4
    // Faire reculer la roue avec une vitesse negative.
  }

  public void stop() {
    // TODO 1.5
    // Arreter la roue.
  }

  public double getCurrentSpeed() { return currentSpeed; }
  public boolean exists() { return motor != null; }
}
