package api.motors;

import com.cyberbotics.webots.controller.Supervisor;

import api.utils.MathUtils;

public class DriveBase {
  private final Wheel frontLeft;
  private final Wheel frontRight;
  private final Wheel rearLeft;
  private final Wheel rearRight;

  private final MotorGroup leftWheels;
  private final MotorGroup rightWheels;

  public DriveBase(Supervisor robot) {
    // Les noms des moteurs doivent correspondre aux noms déclarés dans Webots.
    this.frontLeft = new Wheel(robot.getMotor("wheel1"));
    this.frontRight = new Wheel(robot.getMotor("wheel2"));
    this.rearLeft = new Wheel(robot.getMotor("wheel3"));
    this.rearRight = new Wheel(robot.getMotor("wheel4"));

    // Les roues sont regroupées par côté pour simplifier les déplacements.
    this.leftWheels = new MotorGroup(frontLeft, rearLeft);
    this.rightWheels = new MotorGroup(frontRight, rearRight);
  }

  public void setSpeed(double leftSpeed, double rightSpeed) {
    // Applique une vitesse aux roues gauches et une autre aux roues droites.
    leftWheels.setSpeed(leftSpeed);
    rightWheels.setSpeed(rightSpeed);
  }

  public void forward(double speed) {
    // Math.abs permet de garantir une vitesse positive pour avancer.
    double velocity = Math.abs(speed);

    // Les deux côtés avancent à la même vitesse.
    setSpeed(velocity, velocity);
  }

  public void backward(double speed) {
    // Math.abs permet d'éviter les erreurs si une vitesse négative est donnée.
    double velocity = Math.abs(speed);

    // Les deux côtés reçoivent une vitesse négative pour reculer.
    setSpeed(-velocity, -velocity);
  }

  public void turnLeft(double speed) {
    double velocity = Math.abs(speed);

    /*
     * Pour tourner à gauche sur place :
     * - les roues gauches reculent ;
     * - les roues droites avancent.
     */
    setSpeed(-velocity, velocity);
  }

  public void turnRight(double speed) {
    double velocity = Math.abs(speed);

    /*
     * Pour tourner à droite sur place :
     * - les roues gauches avancent ;
     * - les roues droites reculent.
     */
    setSpeed(velocity, -velocity);
  }

  public void curveLeft(double speed, double factor) {
    double velocity = Math.abs(speed);

    /*
     * Le facteur est limité entre 0.0 et 1.0.
     * 1.0 signifie que les deux côtés vont presque tout droit.
     * 0.0 signifie que le côté gauche est fortement ralenti.
     */
    double clampedFactor = MathUtils.clamp(factor, 0.0, 1.0);

    // Le côté gauche avance moins vite que le côté droit pour créer une courbe à gauche.
    setSpeed(velocity * clampedFactor, velocity);
  }

  public void curveRight(double speed, double factor) {
    double velocity = Math.abs(speed);

    /*
     * Le facteur est limité entre 0.0 et 1.0.
     * Cela évite d'envoyer une valeur trop faible ou trop élevée aux moteurs.
     */
    double clampedFactor = MathUtils.clamp(factor, 0.0, 1.0);

    // Le côté droit avance moins vite que le côté gauche pour créer une courbe à droite.
    setSpeed(velocity, velocity * clampedFactor);
  }

  public void stop() {
    // Une vitesse de 0.0 sur les deux côtés arrête le robot.
    setSpeed(0.0, 0.0);
  }

  public Wheel frontLeft() {
    return frontLeft;
  }

  public Wheel frontRight() {
    return frontRight;
  }

  public Wheel rearLeft() {
    return rearLeft;
  }

  public Wheel rearRight() {
    return rearRight;
  }
}