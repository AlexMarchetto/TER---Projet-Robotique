package api.motors;

public class MotorGroup {
  private final Wheel[] wheels;

  public MotorGroup(Wheel... wheels) {
    this.wheels = wheels;
  }

  public void setSpeed(double speed) {
    // On parcourt toutes les roues du groupe.
    for (Wheel wheel : wheels) {
      // On vérifie que la roue existe avant de lui appliquer une vitesse.
      if (wheel != null) {
        wheel.setSpeed(speed);
      }
    }
  }

  public void forward(double speed) {
    // Math.abs permet de garantir une vitesse positive pour avancer.
    setSpeed(Math.abs(speed));
  }

  public void backward(double speed) {
    // Math.abs permet d'obtenir une valeur positive, puis on la rend négative pour reculer.
    setSpeed(-Math.abs(speed));
  }

  public void stop() {
    // Une vitesse de 0.0 arrête toutes les roues du groupe.
    setSpeed(0.0);
  }
}