package api.sensors;
import com.cyberbotics.webots.controller.Camera;

public class ColorSensorWrapper {
  private final Camera camera;
  public ColorSensorWrapper(Camera camera, int timeStep) {
    this.camera = camera;
    if (this.camera != null) { this.camera.enable(timeStep); }
  }
  public RGBColor getRGB() {
    if (camera == null) { return new RGBColor(0, 0, 0); }
    int[] image = camera.getImage();
    int width = camera.getWidth();
    int height = camera.getHeight();
    if (image == null || width <= 0 || height <= 0) { return new RGBColor(0, 0, 0); }
    int pixelCount = width * height;
    int sumRed = 0, sumGreen = 0, sumBlue = 0;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        sumRed += Camera.imageGetRed(image, width, x, y);
        sumGreen += Camera.imageGetGreen(image, width, x, y);
        sumBlue += Camera.imageGetBlue(image, width, x, y);
      }
    }
    return new RGBColor(sumRed / pixelCount, sumGreen / pixelCount, sumBlue / pixelCount);
  }
  public boolean seesRed() { return getRGB().isRed(); }
  public boolean exists() { return camera != null; }
}
