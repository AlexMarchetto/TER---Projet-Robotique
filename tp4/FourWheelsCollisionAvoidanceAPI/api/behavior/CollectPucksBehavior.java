package api.behavior;

import api.core.Robot;
import api.state.RobotMode;
import api.utils.MathUtils;

/*
 * Comportement TP3 : premiere recuperation d'un palet.
 * Objectif : chercher un palet, s'en approcher, baisser le bras,
 * fermer la pince, puis lever le bras.
 * Le depot des palets sera traite dans un TP suivant.
 */
public class CollectPucksBehavior implements RobotBehavior {
  private final Robot robot;

  private RobotMode mode = RobotMode.SEARCH;
  private int currentPuckIndex = -1;
  private int counter = 0;
  private boolean puckAttached = false;
  private boolean finishedMessagePrinted = false;

  private static final double SEARCH_SPEED = 3.0;
  private static final double APPROACH_SPEED = 0.8;
  private static final double TURN_SPEED = 3.0;
  private static final double PUCK_DETECTION_DISTANCE = 1.20;
  private static final double PUCK_MAX_ANGLE = 1.40;
  private static final double APPROACH_TURN_GAIN = 4.0;
  private static final int APPROACH_TIMEOUT = 220;

  public CollectPucksBehavior(Robot robot) {
    this.robot = robot;
  }

  @Override
  public void init() {
    robot.arm().lift();
    robot.gripper().open();
    robot.motors().stop();
  }

  @Override
  public void update() {
    robot.sensors().update();

    switch (mode) {
      case SEARCH: updateSearch(); break;
      // ...
    }

    printDebug();
  }

  private void updateSearch() {
    // TODO 9.1 : demander a PuckManager l'indice du palet disponible le plus proche
    int nearestPuckIndex = -1;

    if (nearestPuckIndex == -1) {
      robot.motors().turnLeft(TURN_SPEED);
      return;
    }

    double distanceToPuck = robot.pucks().getDistanceToPuck(nearestPuckIndex);
    double angleError = robot.pucks().getAngleErrorToPuck(nearestPuckIndex);

    // TODO 9.2 : si le palet est assez proche et dans l'axe, passer en mode APPROACH_PUCK
    // Sinon, continuer a chercher en tournant lentement.

    robot.motors().turnLeft(SEARCH_SPEED);
  }

  private void updateApproachPuck() {
    if (currentPuckIndex == -1 || robot.pucks().getPuckNode(currentPuckIndex) == null) {
      mode = RobotMode.SEARCH;
      counter = 0;
      robot.motors().stop();
      return;
    }

    double angleError = robot.pucks().getAngleErrorToPuck(currentPuckIndex);

    double correction = APPROACH_TURN_GAIN * angleError;
    correction = MathUtils.clamp(correction, -TURN_SPEED, TURN_SPEED);

    // TODO 10.1 : calculer les vitesses gauche et droite pour corriger la direction
    double leftSpeed = 0.0;
    double rightSpeed = 0.0;

    // TODO 10.2 : envoyer les vitesses a la base roulante

    counter++;

    // Dans ce TP, on declenche la prise apres un temps d'approche.
    if (counter > 80 || robot.sensors().isFrontTouched()) {
      mode = RobotMode.LOWER_ARM;
      counter = 0;
      robot.motors().stop();
      System.out.println("Puck reached. Starting pickup sequence.");
    }

    if (counter > APPROACH_TIMEOUT) {
      mode = RobotMode.SEARCH;
      counter = 0;
      currentPuckIndex = -1;
      robot.motors().stop();
    }
  }

  private void updateLowerArm() {
    robot.motors().stop();

    // TODO 11.1 : baisser le bras et ouvrir la pince

    counter++;
    if (counter > 25) {
      mode = RobotMode.CLOSE_GRIPPER;
      counter = 0;
    }
  }

  private void updateCloseGripper() {
    robot.motors().stop();

    // TODO 11.2 : fermer la pince

    counter++;
    if (counter > 25) {
      puckAttached = true;
      mode = RobotMode.LIFT_ARM;
      counter = 0;
    }
  }

  private void updateLiftArm() {
    robot.motors().stop();

    // TODO 11.3 : lever le bras

    counter++;
    if (counter > 30) {
      mode = RobotMode.FINISHED;
      counter = 0;
    }
  }

  private void updateFinished() {
    robot.motors().stop();
    robot.arm().lift();

    if (!finishedMessagePrinted) {
      System.out.println("TP3 finished: one puck pickup sequence completed.");
      finishedMessagePrinted = true;
    }
  }

  private void printDebug() {
    System.out.println(
        "mode=" + mode
            + " | currentPuck=" + currentPuckIndex
            + " | attached=" + puckAttached
            + " | ds_front=" + robot.sensors().frontDistance()
    );
  }
}
