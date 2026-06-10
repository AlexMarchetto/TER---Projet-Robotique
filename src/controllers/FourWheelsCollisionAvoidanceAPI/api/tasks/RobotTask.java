package api.tasks;

public interface RobotTask {
  // Called once when the task is added to the scheduler.
  default void start() {}

  // Called at each simulation step while the task is active.
  void update();

  // Returns true when the task has completed.
  boolean isFinished();

  // Called once when the task is finished.
  default void end() {}
}