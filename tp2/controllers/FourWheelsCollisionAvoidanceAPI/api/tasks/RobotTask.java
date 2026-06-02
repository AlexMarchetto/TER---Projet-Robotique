package api.tasks;
public interface RobotTask {
  default void start() {}
  void update();
  boolean isFinished();
  default void end() {}
}
