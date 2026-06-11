package api.behavior;

import api.core.Robot;
import api.state.RobotMode;
import api.tasks.TimedTask;
import api.utils.MathUtils;

/*
 * Comportement TP4 : recherche, récupération et dépôt des palets.
 *
 * Les méthodes correspondant à la première récupération du palet ont déjà été
 * travaillées au TP3. Elles sont donc conservées dans ce fichier.
 *
 * Dans ce TP4, vous devez compléter uniquement les parties liées :
 * - au choix de la zone de dépôt
 * - au déplacement vers la zone de dépôt
 * - au dépôt du palet
 * - au retour en mode recherche après le dépôt
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
  private static final double GO_DROP_SPEED = 3.2;
  private static final double BACK_SPEED = -2.0;

  private static final double PUCK_DETECTION_DISTANCE = 1.20;
  private static final double PUCK_MAX_ANGLE = 1.40;
  private static final double APPROACH_TURN_GAIN = 4.0;
  private static final int APPROACH_TIMEOUT = 220;

  /*
   * Constantes utilisées pour définir les zones de dépôt.
   * Les lignes blanches sont placées à gauche et à droite de la carte.
   */
  private static final double LEFT_WHITE_LINE_X = -1.0;
  private static final double RIGHT_WHITE_LINE_X = 1.0;
  private static final double DROP_OUTSIDE_OFFSET = 0.25;

  private static final double DROP_MIN_Y = -0.75;
  private static final double DROP_MAX_Y = 0.75;
  private static final double DROP_Z = 0.095;

  private static final double DROP_DISTANCE_THRESHOLD = 0.15;
  private static final double DROP_ALIGNMENT_THRESHOLD = 0.20;

  /*
   * Délai avant de cacher un palet après son dépôt.
   * 3000 ms = 3 secondes.
   */
  private static final double HIDE_PUCK_DELAY_MS = 3000.0;

  /*
   * Coordonnées de la zone de dépôt choisie.
   * Elles seront calculées après la prise du palet.
   */
  private double targetDropX = RIGHT_WHITE_LINE_X + DROP_OUTSIDE_OFFSET;
  private double targetDropY = 0.0;

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
     * Si tous les palets ont été livrés, le robot passe en mode FINISHED.
     */
    if (robot.pucks().allPucksDelivered()) {
      mode = RobotMode.FINISHED;
    }

    /*
     * Tant qu'un palet est attaché, sa position est mise à jour pour le garder
     * devant le robot pendant le transport.
     */
    if (puckAttached && currentPuckIndex != -1) {
      robot.pucks().attachPuckToRobot(currentPuckIndex);
    }

    /*
     * Le comportement est découpé en plusieurs modes.
     * Les premiers modes viennent du TP3. Les nouveaux modes de dépôt sont à
     * ajouter dans RobotMode.java et à gérer dans ce switch.
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

      // TODO TP4 : ajoutez ici les nouveaux cas du switch pour le dépôt.
      // Exemple attendu :
      // case GO_TO_DROP_ZONE:
      //   updateGoToDropZone();
      //   break;

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

      System.out.println("Palet détecté : " + robot.pucks().getPuckName(currentPuckIndex));
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
     * - si le palet est à gauche, une roue ira plus vite que l'autre
     * - si le palet est à droite, l'inverse se produit
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

      System.out.println("Palet atteint. Début de la séquence de prise.");
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

      /*
       * TODO TP4
       * Avant de partir vers la zone de dépôt, choisissez la zone de dépôt
       * la plus adaptée en appelant la méthode chooseNearestDropZone().
       */

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

    /*
     * TODO TP4
     * Au TP3, le robot passait directement en mode FINISHED après avoir levé le bras.
     * Dans ce TP4, il doit maintenant passer au mode qui permet d'aller vers la zone
     * de dépôt.
     */
    if (counter > 30) {
      mode = RobotMode.FINISHED; // À modifier pour le TP4.
      counter = 0;
    }
  }

  private void updateGoToDropZone() {
    /*
     * TODO TP4
     * Complétez cette méthode.
     *
     * Objectif :
     * - vérifier si le robot est proche de la zone de dépôt
     * - si oui, passer au mode DROP_PUCK
     * - sinon, calculer l'angle vers la zone de dépôt
     * - tourner si le robot n'est pas aligné
     * - avancer si le robot est aligné
     *
     * Méthodes utiles :
     * - robot.supervisor().getSelf().getPosition();
     * - Math.atan2(...);
     * - MathUtils.getRobotYaw(robot.supervisor());
     * - MathUtils.normalizeAngle(...);
     * - robot.motors().turnLeft(...);
     * - robot.motors().turnRight(...);
     * - robot.motors().forward(...).
     */
  }

  private void updateDropPuck() {
    /*
     * TODO TP4
     * Complétez cette méthode.
     *
     * Objectif :
     * - arrêter le robot
     * - ouvrir la pince
     * - baisser le bras
     * - déposer le palet avec robot.pucks().dropPuck(...)
     * - cacher le palet après un court délai avec TimedTask
     * - détacher le palet du robot
     * - passer au mode LIFT_ARM_AFTER_DROP
     */
  }

  private void updateLiftArmAfterDrop() {
    /*
     * TODO TP4
     * Complétez cette méthode.
     *
     * Objectif :
     * - arrêter le robot
     * - lever le bras
     * - si tous les palets sont livrés, passer en mode FINISHED
     * - sinon, passer en mode BACK_AND_TURN_AFTER_DROP
     */
  }

  private void updateBackAndTurnAfterDrop() {
    /*
     * TODO TP4
     * Complétez cette méthode.
     *
     * Objectif :
     * - reculer pendant quelques pas de simulation
     * - tourner pendant quelques pas de simulation
     * - réinitialiser les informations du palet courant
     * - revenir en mode SEARCH
     */
  }

  private void updateFinished() {
    // Le robot reste immobile une fois la séquence terminée.
    robot.motors().stop();
    robot.arm().lift();
    robot.gripper().open();

    puckAttached = false;
    currentPuckIndex = -1;

    if (!finishedMessagePrinted) {
      System.out.println("Tous les palets ont été livrés. Le robot est arrêté.");
      finishedMessagePrinted = true;
    }
  }

  private void chooseNearestDropZone() {
    /*
     * TODO TP4
     * Complétez cette méthode.
     *
     * Objectif :
     * - récupérer la position actuelle du robot
     * - comparer sa distance avec la ligne blanche de gauche et celle de droite
     * - choisir la zone de dépôt la plus proche
     * - limiter la valeur de targetDropY avec MathUtils.clamp(...)
     *
     * Indices :
     * - la position du robot est accessible avec robot.supervisor().getSelf().getPosition()
     * - la coordonnée X est position[0]
     * - la coordonnée Y est position[1]
     */
  }

  private boolean isNearDropZone() {
    /*
     * TODO TP4
     * Complétez cette méthode.
     *
     * Objectif :
     * - récupérer la position actuelle du robot
     * - calculer la distance entre le robot et la zone de dépôt
     * - retourner true si la distance est inférieure à DROP_DISTANCE_THRESHOLD
     */
    return false;
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
            + " | targetDrop=(" + targetDropX + ", " + targetDropY + ")"
    );
  }
}
