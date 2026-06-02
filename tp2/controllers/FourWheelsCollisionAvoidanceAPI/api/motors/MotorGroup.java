package api.motors;

public class MotorGroup {
  private final Wheel[] wheels;

  public MotorGroup(Wheel... wheels) {
    this.wheels = wheels;
  }

  public void setSpeed(double speed) {
    // TODO 2.1
    // Parcourez toutes les roues du groupe et appliquez la meme vitesse.
    // Pensez a verifier que la roue n'est pas null.
  }

  public void forward(double speed) {
    // TODO 2.2
    // Faire avancer tout le groupe avec une vitesse positive.
  }

  public void backward(double speed) {
    // TODO 2.3
    // Faire reculer tout le groupe avec une vitesse negative.
  }

  public void stop() {
    // TODO 2.4
    // Arreter toutes les roues du groupe.
  }
}
