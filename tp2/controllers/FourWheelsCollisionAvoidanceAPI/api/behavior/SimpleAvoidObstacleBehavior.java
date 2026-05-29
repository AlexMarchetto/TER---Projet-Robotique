package api.behavior;
import api.core.TERBot;

public class SimpleAvoidObstacleBehavior implements RobotBehavior {
  private final TERBot robot;

  private static final double SPEED = 4.0;
  private static final double TURN_SPEED = 3.0;
  private static final double FRONT_THRESHOLD = 350.0;
  private static final double SIDE_THRESHOLD = 900.0;

  public SimpleAvoidObstacleBehavior(TERBot robot) {
    this.robot = robot;
  }

  @Override
  public void init() {
    // TODO 8.1
    // Au lancement, le bras doit etre leve et la pince ouverte.
  }

  @Override
  public void update() {
    // TODO 8.2
    // Mettez d'abord a jour les capteurs.

    // TODO 8.3
    // Completez le comportement simple :
    // - si un obstacle est detecte devant, tourner a gauche ;
    // - sinon si un obstacle est detecte a gauche, tourner a droite ;
    // - sinon si un obstacle est detecte a droite, tourner a gauche ;
    // - sinon avancer.
  }
}
