package api.utils;

import com.cyberbotics.webots.controller.Supervisor;

public class MathUtils {
  private MathUtils() {
    // Classe utilitaire : empêcher l'instanciation.
  }

  public static double normalizeAngle(double angle) {
    // Normaliser un angle pour le ramener à l'intervalle [-π, π].
    while (angle > Math.PI) {
      angle -= 2.0 * Math.PI;
    }

    while (angle < -Math.PI) {
      angle += 2.0 * Math.PI;
    }

    return angle;
  }

  public static double getRobotYaw(Supervisor robot) {
    // Calculez l'angle de lacet du robot à partir de sa matrice d'orientation.
    double[] orientation = robot.getSelf().getOrientation();

    return Math.atan2(orientation[3], orientation[0]);
  }

  public static double distance2D(double[] a, double[] b) {
    // Calculez la distance entre deux points en utilisant uniquement les coordonnées X et Y.
    double dx = b[0] - a[0];
    double dy = b[1] - a[1];

    return Math.sqrt(dx * dx + dy * dy);
  }

  public static double clamp(double value, double min, double max) {
    // Veillez à ce que la valeur reste dans l'intervalle indiqué.
    if (value < min) {
      return min;
    }

    if (value > max) {
      return max;
    }

    return value;
  }
}