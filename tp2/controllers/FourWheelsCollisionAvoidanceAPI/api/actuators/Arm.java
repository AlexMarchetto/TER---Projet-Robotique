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

    // TODO 6.1
    // Activez le capteur de position du bras si celui-ci existe.
    if (armSensor != null) {
      // A COMPLETER
    }
  }

  public void lift() {
    // TODO 6.2
    // Lever le bras en utilisant upPosition.
  }

  public void lower() {
    // TODO 6.3
    // Baisser le bras en utilisant downPosition.
  }

  public void moveTo(double position) {
    // TODO 6.4
    // Envoyer une position au moteur du bras si celui-ci existe.
  }

  public double getPosition() {
    // TODO 6.5
    // Retournez la position du bras. Si le capteur n'existe pas, retournez 0.0.
    return 0.0; // A MODIFIER
  }

  public void setUpPosition(double upPosition) { this.upPosition = upPosition; }
  public void setDownPosition(double downPosition) { this.downPosition = downPosition; }
  public boolean exists() { return armMotor != null; }
}
