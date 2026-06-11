package api.tasks;

public class TimedTask implements RobotTask {
  private final int duration;
  private final Runnable action;
  private final Runnable endAction;

  private int counter;

  public TimedTask(int duration, Runnable action) {
    this(duration, action, null);
  }

  public TimedTask(int duration, Runnable action, Runnable endAction) {
    this.duration = duration;
    this.action = action;
    this.endAction = endAction;
    this.counter = 0;
  }

  @Override
  public void start() {
    // Réinitialisez le compteur au début de la tâche.
    counter = 0;
  }

  @Override
  public void update() {
    // Exécutez l'action répétée à chaque étape de la simulation.
    if (action != null) {
      action.run();
    }

    counter++;
  }

  @Override
  public boolean isFinished() {
    return counter >= duration;
  }

  @Override
  public void end() {
    // Exécutez l'action finale une seule fois une fois la tâche terminée.
    if (endAction != null) {
      endAction.run();
    }
  }
}