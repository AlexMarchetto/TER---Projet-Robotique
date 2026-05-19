import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Supervisor;
import com.cyberbotics.webots.controller.TouchSensor;

public class RobotSensors {
  private final DistanceSensor dsRight;
  private final DistanceSensor dsLeft;
  private final DistanceSensor dsFront;

  private final Camera colorSensor;
  private final TouchSensor touchFront;

  public RobotSensors(Supervisor robot, int timeStep) {
    dsRight = robot.getDistanceSensor("ds_right");
    dsLeft = robot.getDistanceSensor("ds_left");
    dsFront = robot.getDistanceSensor("ds_front");

    colorSensor = robot.getCamera("color_sensor");
    touchFront = robot.getTouchSensor("touch_front");

    if (dsRight != null) {
      dsRight.enable(timeStep);
    }

    if (dsLeft != null) {
      dsLeft.enable(timeStep);
    }

    if (dsFront != null) {
      dsFront.enable(timeStep);
    }

    if (colorSensor != null) {
      colorSensor.enable(timeStep);
    }

    if (touchFront != null) {
      touchFront.enable(timeStep);
    }
  }

  public double getRightDistance() {
    if (dsRight == null) {
      return 0.0;
    }

    return dsRight.getValue();
  }

  public double getLeftDistance() {
    if (dsLeft == null) {
      return 0.0;
    }

    return dsLeft.getValue();
  }

  public double getFrontDistance() {
    if (dsFront == null) {
      return 0.0;
    }

    return dsFront.getValue();
  }

  public boolean isTouched() {
    return touchFront != null && touchFront.getValue() > 0.0;
  }

  public int[] getAverageColor() {
    int red = 0;
    int green = 0;
    int blue = 0;

    if (colorSensor == null) {
      return new int[] { red, green, blue };
    }

    int[] image = colorSensor.getImage();
    int width = colorSensor.getWidth();
    int height = colorSensor.getHeight();

    if (image == null || width <= 0 || height <= 0) {
      return new int[] { red, green, blue };
    }

    int pixelCount = width * height;

    int sumRed = 0;
    int sumGreen = 0;
    int sumBlue = 0;

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        sumRed += Camera.imageGetRed(image, width, x, y);
        sumGreen += Camera.imageGetGreen(image, width, x, y);
        sumBlue += Camera.imageGetBlue(image, width, x, y);
      }
    }

    red = sumRed / pixelCount;
    green = sumGreen / pixelCount;
    blue = sumBlue / pixelCount;

    return new int[] { red, green, blue };
  }

  public boolean isRedDetected() {
    int[] color = getAverageColor();

    int red = color[0];
    int green = color[1];
    int blue = color[2];

    return red > 150 && green < 100 && blue < 100;
  }
}
