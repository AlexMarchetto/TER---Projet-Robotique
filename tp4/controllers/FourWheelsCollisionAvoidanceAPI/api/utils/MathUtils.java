package api.utils;

import com.cyberbotics.webots.controller.Supervisor;

public class MathUtils {
  private MathUtils() {
    // Utility class: prevent instantiation.
  }

  public static double normalizeAngle(double angle) {
    // Normalize an angle to the range [-PI, PI].
    while (angle > Math.PI) {
      angle -= 2.0 * Math.PI;
    }

    while (angle < -Math.PI) {
      angle += 2.0 * Math.PI;
    }

    return angle;
  }

  public static double getRobotYaw(Supervisor robot) {
    // Extract the robot yaw angle from its orientation matrix.
    double[] orientation = robot.getSelf().getOrientation();

    return Math.atan2(orientation[3], orientation[0]);
  }

  public static double distance2D(double[] a, double[] b) {
    // Compute the distance between two points using only X and Y coordinates.
    double dx = b[0] - a[0];
    double dy = b[1] - a[1];

    return Math.sqrt(dx * dx + dy * dy);
  }

  public static double clamp(double value, double min, double max) {
    // Keep a value inside the given range.
    if (value < min) {
      return min;
    }

    if (value > max) {
      return max;
    }

    return value;
  }
}