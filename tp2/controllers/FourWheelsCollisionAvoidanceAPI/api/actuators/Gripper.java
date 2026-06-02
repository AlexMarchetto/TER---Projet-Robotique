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
    // TODO 7.1
    // Ouvrir la pince avec les positions openLeftPosition et openRightPosition.
    // Pensez a verifier que les moteurs existent.
  }

  public void close() {
    // TODO 7.2
    // Fermer la pince avec les positions closedLeftPosition et closedRightPosition.
    // Pensez a mettre a jour la variable open.
  }

  public boolean isOpen() { return open; }
  public void setOpenPositions(double left, double right) { this.openLeftPosition = left; this.openRightPosition = right; }
  public void setClosedPositions(double left, double right) { this.closedLeftPosition = left; this.closedRightPosition = right; }
  public boolean exists() { return leftMotor != null || rightMotor != null; }
}
