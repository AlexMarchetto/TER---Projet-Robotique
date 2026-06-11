package api.behavior;

import api.core.Robot;
import api.state.RobotMode;
import api.utils.MathUtils;

/*
 * Comportement TP3 : première récupération d'un palet.
 * Objectif : chercher un palet, s'en approcher, baisser le bras,
 * fermer la pince, puis lever le bras.
 * Le dépôt des palets sera traité dans un TP suivant.
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
    // Au démarrage, le bras est levé, la pince est ouverte et le robot est arrêté.
    robot.arm().lift();
    robot.gripper().open();
    robot.motors().stop();
  }

  @Override
  public void update() {
    // Les capteurs sont mis à jour à chaque pas de simulation.
    robot.sensors().update();

    /*
     * Le comportement est découpé en plusieurs modes.
     * Chaque mode correspond à une étape précise de la récupération du palet.
     */
    switch (mode) {
      case SEARCH:
        updateSearch();
        break;

      case APPROACH_PUCK:
        updateApproachPuck();
        break;

      case LOWER_ARM:
        updateLowerArm();
        break;

      case CLOSE_GRIPPER:
        updateCloseGripper();
        break;

      case LIFT_ARM:
        updateLiftArm();
        break;

      case FINISHED:
        updateFinished();
        break;
    }

    printDebug();
  }

  private void updateSearch() {
    // On demande à PuckManager l'indice du palet disponible le plus proche.
    int nearestPuckIndex = robot.pucks().findNearestAvailablePuck();

    if (nearestPuckIndex == -1) {
      // Si aucun palet n'est disponible, le robot continue à tourner pour chercher.
      robot.motors().turnLeft(TURN_SPEED);
      return;
    }

    double distanceToPuck = robot.pucks().getDistanceToPuck(nearestPuckIndex);
    double angleError = robot.pucks().getAngleErrorToPuck(nearestPuckIndex);

    /*
     * Si le palet est assez proche et suffisamment dans l'axe du robot,
     * on le sélectionne comme palet courant et on commence l'approche.
     */
    if (distanceToPuck < PUCK_DETECTION_DISTANCE && Math.abs(angleError) < PUCK_MAX_ANGLE) {
      currentPuckIndex = nearestPuckIndex;
      mode = RobotMode.APPROACH_PUCK;
      counter = 0;

      System.out.println("Puck detected: " + robot.pucks().getPuckName(currentPuckIndex));
      return;
    }

    // Si le palet n'est pas encore bien placé, le robot continue à chercher.
    robot.motors().turnLeft(SEARCH_SPEED);
  }

  private void updateApproachPuck() {
    /*
     * Si aucun palet n'est sélectionné ou si le noeud Webots du palet est introuvable,
     * le robot revient en mode recherche.
     */
    if (currentPuckIndex == -1 || robot.pucks().getPuckNode(currentPuckIndex) == null) {
      mode = RobotMode.SEARCH;
      counter = 0;
      robot.motors().stop();
      return;
    }

    double angleError = robot.pucks().getAngleErrorToPuck(currentPuckIndex);

    /*
     * La correction dépend de l'erreur d'angle.
     * Plus le palet est décalé, plus la correction appliquée aux roues est forte.
     */
    double correction = APPROACH_TURN_GAIN * angleError;
    correction = MathUtils.clamp(correction, -TURN_SPEED, TURN_SPEED);

    /*
     * Pour corriger la direction :
     * - si le palet est à gauche, une roue ira plus vite que l'autre ;
     * - si le palet est à droite, l'inverse se produit.
     */
    double leftSpeed = APPROACH_SPEED - correction;
    double rightSpeed = APPROACH_SPEED + correction;

    // On envoie les vitesses calculées à la base roulante.
    robot.motors().setSpeed(leftSpeed, rightSpeed);

    counter++;

    /*
     * Dans ce TP, la prise est déclenchée après un court temps d'approche
     * ou dès que le capteur tactile frontal détecte un contact.
     */
    if (counter > 80 || robot.sensors().isFrontTouched()) {
      mode = RobotMode.LOWER_ARM;
      counter = 0;
      robot.motors().stop();

      System.out.println("Puck reached. Starting pickup sequence.");
    }

    /*
     * Si l'approche dure trop longtemps, le robot abandonne ce palet
     * et retourne en mode recherche.
     */
    if (counter > APPROACH_TIMEOUT) {
      mode = RobotMode.SEARCH;
      counter = 0;
      currentPuckIndex = -1;
      robot.motors().stop();
    }
  }

  private void updateLowerArm() {
    robot.motors().stop();

    /*
     * Le robot baisse le bras pour préparer la prise.
     * La pince reste ouverte pour pouvoir entourer le palet.
     */
    robot.arm().lower();
    robot.gripper().open();

    counter++;

    // Après quelques pas de simulation, on passe à la fermeture de la pince.
    if (counter > 25) {
      mode = RobotMode.CLOSE_GRIPPER;
      counter = 0;
    }
  }

  private void updateCloseGripper() {
    robot.motors().stop();

    // La pince se ferme pour attraper le palet.
    robot.gripper().close();

    counter++;

    if (counter > 25) {
      /*
       * Le palet est considéré comme attaché au robot.
       * Dans ce TP, cela permet ensuite de le garder placé devant le robot.
       */
      if (currentPuckIndex != -1) {
        robot.pucks().attachPuckToRobot(currentPuckIndex);
      }

      puckAttached = true;
      mode = RobotMode.LIFT_ARM;
      counter = 0;
    }
  }

  private void updateLiftArm() {
    robot.motors().stop();

    // Le bras est levé après la fermeture de la pince.
    robot.arm().lift();

    /*
     * Tant que le palet est attaché, on met à jour sa position
     * pour qu'il reste devant le robot pendant la montée du bras.
     */
    if (puckAttached && currentPuckIndex != -1) {
      robot.pucks().attachPuckToRobot(currentPuckIndex);
    }

    counter++;

    // Le TP3 se termine après la récupération d'un premier palet.
    if (counter > 30) {
      mode = RobotMode.FINISHED;
      counter = 0;
    }
  }

  private void updateFinished() {
    // Le robot reste immobile une fois la séquence terminée.
    robot.motors().stop();
    robot.arm().lift();

    if (!finishedMessagePrinted) {
      System.out.println("TP3 finished: one puck pickup sequence completed.");
      finishedMessagePrinted = true;
    }
  }

  private void printDebug() {
    /*
     * Affichage de quelques informations utiles pour suivre le comportement
     * du robot dans la console Webots.
     */
    System.out.println(
        "mode=" + mode
            + " | currentPuck=" + currentPuckIndex
            + " | attached=" + puckAttached
            + " | ds_front=" + robot.sensors().frontDistance()
    );
  }
}