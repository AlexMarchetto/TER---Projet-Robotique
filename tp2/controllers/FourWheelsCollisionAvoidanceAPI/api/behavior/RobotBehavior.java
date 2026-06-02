package api.behavior;
public interface RobotBehavior {
  default void init() {}
  void update();
}
