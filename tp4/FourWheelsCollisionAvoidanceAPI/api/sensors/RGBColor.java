package api.sensors;
public class RGBColor {
  private final int red;
  private final int green;
  private final int blue;
  public RGBColor(int red, int green, int blue) { this.red = red; this.green = green; this.blue = blue; }
  public int red() { return red; }
  public int green() { return green; }
  public int blue() { return blue; }
  public boolean isRed() { return red > 150 && green < 100 && blue < 100; }
  public boolean isDominantRed() { return red > green && red > blue; }
  @Override public String toString() { return "(" + red + "," + green + "," + blue + ")"; }
}
