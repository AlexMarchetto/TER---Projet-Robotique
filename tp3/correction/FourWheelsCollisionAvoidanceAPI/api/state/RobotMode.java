package api.state;

/*
 * Un mode correspond à une étape du comportement du robot.
 * Le robot change de mode au fur et à mesure de la récupération du palet.
 */
public enum RobotMode {
  /*
   * Le robot cherche un palet disponible dans le monde.
   */
  SEARCH,

  /*
   * Le robot se dirige vers le palet sélectionné.
   */
  APPROACH_PUCK,

  /*
   * Le robot baisse son bras pour préparer la prise du palet.
   */
  LOWER_ARM,

  /*
   * Le robot ferme la pince pour attraper le palet.
   */
  CLOSE_GRIPPER,

  /*
   * Le robot lève le bras après avoir attrapé le palet.
   */
  LIFT_ARM,

  /*
   * Le robot a terminé la séquence de récupération du TP3.
   */
  FINISHED
}