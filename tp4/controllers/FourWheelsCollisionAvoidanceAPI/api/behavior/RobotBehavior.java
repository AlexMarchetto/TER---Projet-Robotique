package api.behavior;

public interface RobotBehavior {
  // Called once before the robot behavior starts.
  default void init() {}

  // Called at each simulation step to update the robot behavior.
  void update();
}