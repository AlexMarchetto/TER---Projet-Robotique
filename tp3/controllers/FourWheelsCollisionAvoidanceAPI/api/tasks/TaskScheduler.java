package api.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskScheduler {
  private final List<RobotTask> tasks = new ArrayList<RobotTask>();

  public void add(RobotTask task) {
    if (task != null) {
      // Lancez la tâche avant de l'ajouter à la liste des tâches en cours.
      task.start();
      tasks.add(task);
    }
  }

  public void update() {
    Iterator<RobotTask> iterator = tasks.iterator();

    while (iterator.hasNext()) {
      RobotTask task = iterator.next();

      // Mettre à jour chaque tâche active à chaque étape de la simulation.
      task.update();

      if (task.isFinished()) {
        // Une fois la tâche terminée, mettez-y un terme et supprimez-la.
        task.end();
        iterator.remove();
      }
    }
  }

  public void clear() {
    tasks.clear();
  }

  public boolean hasTasks() {
    return !tasks.isEmpty();
  }

  public int size() {
    return tasks.size();
  }
}