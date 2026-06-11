package api.state;

/*
 * TODO TP4
 * Complétez l'énumération avec les nouveaux modes nécessaires au dépôt.
 *
 * Les modes déjà utilisés au TP3 sont conservés :
 * - SEARCH;
 * - APPROACH_PUCK;
 * - LOWER_ARM;
 * - CLOSE_GRIPPER;
 * - LIFT_ARM;
 * - FINISHED.
 *
 * Pour ce TP4, vous devez ajouter les modes permettant :
 * - d'aller vers la zone de dépôt
 * - de déposer le palet
 * - de relever le bras après le dépôt
 * - de s'éloigner de la zone de dépôt avant de repartir en recherche
 */
public enum RobotMode {
  SEARCH,

  // Modes déjà complétés au TP3.
  APPROACH_PUCK,
  LOWER_ARM,
  CLOSE_GRIPPER,
  LIFT_ARM,

  // TODO TP4 : ajoutez ici les nouveaux modes de dépôt.

  FINISHED
}
