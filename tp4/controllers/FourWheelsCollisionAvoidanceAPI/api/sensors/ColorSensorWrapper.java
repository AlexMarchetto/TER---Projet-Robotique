package api.sensors;

import com.cyberbotics.webots.controller.Camera;

public class ColorSensorWrapper {
  private final Camera camera;

  public ColorSensorWrapper(Camera camera, int timeStep) {
    this.camera = camera;

    if (this.camera != null) {
      // Enable the camera to read pixel values during the simulation.
      this.camera.enable(timeStep);
    }
  }

  public RGBColor getRGB() {
    if (camera == null) {
      return new RGBColor(0, 0, 0);
    }

    int[] image = camera.getImage();
    int width = camera.getWidth();
    int height = camera.getHeight();

    if (image == null || width <= 0 || height <= 0) {
      return new RGBColor(0, 0, 0);
    }

    int pixelCount = width * height;

    int redSum = 0;
    int greenSum = 0;
    int blueSum = 0;

    // Compute the average RGB color of the camera image.
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        redSum += Camera.imageGetRed(image, width, x, y);
        greenSum += Camera.imageGetGreen(image, width, x, y);
        blueSum += Camera.imageGetBlue(image, width, x, y);
      }
    }

    return new RGBColor(
        redSum / pixelCount,
        greenSum / pixelCount,
        blueSum / pixelCount
    );
  }

  public boolean seesRed() {
    return getRGB().isRed();
  }

  public boolean exists() {
    return camera != null;
  }
}