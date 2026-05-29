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
    // Les noms des moteurs doivent correspondre aux noms declares dans Webots.
    this.frontLeft = new Wheel(robot.getMotor("wheel1"));
    this.frontRight = new Wheel(robot.getMotor("wheel2"));
    this.rearLeft = new Wheel(robot.getMotor("wheel3"));
    this.rearRight = new Wheel(robot.getMotor("wheel4"));

    this.leftWheels = new MotorGroup(frontLeft, rearLeft);
    this.rightWheels = new MotorGroup(frontRight, rearRight);
  }

  public void setSpeed(double leftSpeed, double rightSpeed) {
    // TODO 3.1
    // Appliquez leftSpeed aux roues gauches et rightSpeed aux roues droites.
  }

  public void forward(double speed) {
    // TODO 3.2
    // Faire avancer le robot en appliquant une vitesse positive des deux cotes.
  }

  public void backward(double speed) {
    // TODO 3.3
    // Faire reculer le robot en appliquant une vitesse negative des deux cotes.
  }

  public void turnLeft(double speed) {
    // TODO 3.4
    // Tourner a gauche : roues gauches en arriere, roues droites en avant.
  }

  public void turnRight(double speed) {
    // TODO 3.5
    // Tourner a droite : roues gauches en avant, roues droites en arriere.
  }

  public void curveLeft(double speed, double factor) {
    // TODO 3.6
    // Avancer en courbe vers la gauche : les roues gauches tournent moins vite.
    // Utilisez MathUtils.clamp(factor, 0.0, 1.0).
  }

  public void curveRight(double speed, double factor) {
    // TODO 3.7
    // Avancer en courbe vers la droite : les roues droites tournent moins vite.
    // Utilisez MathUtils.clamp(factor, 0.0, 1.0).
  }

  public void stop() {
    // TODO 3.8
    // Arreter le robot.
  }

  public Wheel frontLeft() { return frontLeft; }
  public Wheel frontRight() { return frontRight; }
  public Wheel rearLeft() { return rearLeft; }
  public Wheel rearRight() { return rearRight; }
}
