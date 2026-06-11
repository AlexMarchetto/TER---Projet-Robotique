package api.tasks;

public interface RobotTask {
  // Appelée une fois lorsque la tâche est ajoutée au planificateur.
  default void start() {}

  // Appelée à chaque étape de la simulation tant que la tâche est active.
  void update();

  // Renvoie « true » lorsque la tâche est terminée.
  boolean isFinished();

  // Appelée une fois la tâche terminée.
  default void end() {}
}