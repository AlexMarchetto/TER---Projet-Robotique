package api.tasks;
public class TimedTask implements RobotTask {
  private final int duration;
  private final Runnable action;
  private final Runnable endAction;
  private int counter;
  public TimedTask(int duration, Runnable action) { this(duration, action, null); }
  public TimedTask(int duration, Runnable action, Runnable endAction) {
    this.duration = duration; this.action = action; this.endAction = endAction; this.counter = 0;
  }
  @Override public void start() { counter = 0; }
  @Override public void update() { if (action != null) { action.run(); } counter++; }
  @Override public boolean isFinished() { return counter >= duration; }
  @Override public void end() { if (endAction != null) { endAction.run(); } }
}
