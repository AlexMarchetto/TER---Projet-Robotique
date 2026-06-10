package api.utils;
import com.cyberbotics.webots.controller.Supervisor;

public class MathUtils {
  private MathUtils() {}
  public static double normalizeAngle(double angle) {
    while (angle > Math.PI) { angle -= 2.0 * Math.PI; }
    while (angle < -Math.PI) { angle += 2.0 * Math.PI; }
    return angle;
  }
  public static double getRobotYaw(Supervisor robot) {
    double[] orientation = robot.getSelf().getOrientation();
    return Math.atan2(orientation[3], orientation[0]);
  }
  public static double distance2D(double[] a, double[] b) {
    double dx = b[0] - a[0]; double dy = b[1] - a[1];
    return Math.sqrt(dx * dx + dy * dy);
  }
  public static double clamp(double value, double min, double max) {
    if (value < min) { return min; }
    if (value > max) { return max; }
    return value;
  }
}
