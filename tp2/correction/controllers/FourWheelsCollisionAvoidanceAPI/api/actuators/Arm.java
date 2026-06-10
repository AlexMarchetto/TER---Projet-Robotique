package api.actuators;

import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.Supervisor;

public class Arm {
  private final Motor armMotor;
  private final PositionSensor armSensor;

  private double upPosition = -0.65;
  private double downPosition = 0.35;

  public Arm(Supervisor robot, int timeStep) {
    this.armMotor = robot.getMotor("arm_motor");
    this.armSensor = robot.getPositionSensor("arm_sensor");

    if (armSensor != null) {
      // Activation du capteur de position pour pouvoir lire la position du bras.
      armSensor.enable(timeStep);
    }
  }

  public void lift() {
    // Le bras est déplacé vers sa position haute.
    moveTo(upPosition);
  }

  public void lower() {
    // Le bras est déplacé vers sa position basse.
    moveTo(downPosition);
  }

  public void moveTo(double position) {
    // On vérifie que le moteur existe avant de lui envoyer une position.
    if (armMotor != null) {
      armMotor.setPosition(position);
    }
  }

  public double getPosition() {
    // Si le capteur n'existe pas, on retourne une valeur par défaut.
    if (armSensor == null) {
      return 0.0;
    }

    // Retourne la position actuelle du bras mesurée par le capteur.
    return armSensor.getValue();
  }

  public void setUpPosition(double upPosition) {
    this.upPosition = upPosition;
  }

  public void setDownPosition(double downPosition) {
    this.downPosition = downPosition;
  }

  public boolean exists() {
    return armMotor != null;
  }
}