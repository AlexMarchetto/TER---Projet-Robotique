package api.actuators;

import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Supervisor;

public class Gripper {
  private final Motor leftMotor;
  private final Motor rightMotor;

  private double openLeftPosition = 0.2;
  private double openRightPosition = -0.2;

  private double closedLeftPosition = -0.55;
  private double closedRightPosition = 0.55;

  private boolean open;

  public Gripper(Supervisor robot) {
    this.leftMotor = robot.getMotor("gripper_left_motor");
    this.rightMotor = robot.getMotor("gripper_right_motor");
    this.open = false;
  }

  public void open() {
    // On vérifie que le moteur gauche existe avant de déplacer la partie gauche de la pince.
    if (leftMotor != null) {
      leftMotor.setPosition(openLeftPosition);
    }

    // On vérifie que le moteur droit existe avant de déplacer la partie droite de la pince.
    if (rightMotor != null) {
      rightMotor.setPosition(openRightPosition);
    }

    // La variable open indique que la pince est maintenant ouverte.
    open = true;
  }

  public void close() {
    // On vérifie que le moteur gauche existe avant de fermer la partie gauche de la pince.
    if (leftMotor != null) {
      leftMotor.setPosition(closedLeftPosition);
    }

    // On vérifie que le moteur droit existe avant de fermer la partie droite de la pince.
    if (rightMotor != null) {
      rightMotor.setPosition(closedRightPosition);
    }

    // La variable open indique que la pince est maintenant fermée.
    open = false;
  }

  public boolean isOpen() {
    return open;
  }

  public void setOpenPositions(double left, double right) {
    this.openLeftPosition = left;
    this.openRightPosition = right;
  }

  public void setClosedPositions(double left, double right) {
    this.closedLeftPosition = left;
    this.closedRightPosition = right;
  }

  public boolean exists() {
    return leftMotor != null || rightMotor != null;
  }
}