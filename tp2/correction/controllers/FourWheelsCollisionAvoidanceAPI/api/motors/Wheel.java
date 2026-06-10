package api.motors;

import com.cyberbotics.webots.controller.Motor;

public class Wheel {
  private final Motor motor;
  private double currentSpeed;

  public Wheel(Motor motor) {
    this.motor = motor;
    this.currentSpeed = 0.0;

    if (this.motor != null) {
      /*
       * Une roue doit être configurée en rotation continue.
       * Double.POSITIVE_INFINITY indique que le moteur ne cherche pas à atteindre
       * une position précise, mais qu'il peut tourner sans limite.
       */
      this.motor.setPosition(Double.POSITIVE_INFINITY);

      // Au départ, la roue est arrêtée.
      this.motor.setVelocity(0.0);
    }
  }

  public void setSpeed(double speed) {
    // On mémorise la vitesse courante pour pouvoir la consulter plus tard.
    currentSpeed = speed;

    // On vérifie que le moteur existe avant de lui appliquer la vitesse.
    if (motor != null) {
      motor.setVelocity(speed);
    }
  }

  public void forward(double speed) {
    // Math.abs garantit que la vitesse envoyée est positive pour avancer.
    setSpeed(Math.abs(speed));
  }

  public void backward(double speed) {
    // Math.abs récupère une valeur positive, puis on la rend négative pour reculer.
    setSpeed(-Math.abs(speed));
  }

  public void stop() {
    // Une vitesse de 0.0 arrête la roue.
    setSpeed(0.0);
  }

  public double getCurrentSpeed() {
    return currentSpeed;
  }

  public boolean exists() {
    return motor != null;
  }
}