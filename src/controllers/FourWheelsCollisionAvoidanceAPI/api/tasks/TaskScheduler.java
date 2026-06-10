package api.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskScheduler {
  private final List<RobotTask> tasks = new ArrayList<RobotTask>();

  public void add(RobotTask task) {
    if (task != null) {
      // Start the task before adding it to the active task list.
      task.start();
      tasks.add(task);
    }
  }

  public void update() {
    Iterator<RobotTask> iterator = tasks.iterator();

    while (iterator.hasNext()) {
      RobotTask task = iterator.next();

      // Update each active task at every simulation step.
      task.update();

      if (task.isFinished()) {
        // End and remove the task once it is completed.
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