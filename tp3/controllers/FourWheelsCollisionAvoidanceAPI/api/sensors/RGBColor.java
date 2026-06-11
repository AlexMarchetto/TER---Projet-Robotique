package api.sensors;

public class RGBColor {
  private final int red;
  private final int green;
  private final int blue;

  public RGBColor(int red, int green, int blue) {
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public int red() {
    return red;
  }

  public int green() {
    return green;
  }

  public int blue() {
    return blue;
  }

  public boolean isRed() {
    // Détecter une couleur rouge intense avec de faibles composantes de vert et de bleu.
    return red > 150 && green < 100 && blue < 100;
  }

  public boolean isDominantRed() {
    // Vérifiez si le rouge est la composante de couleur dominante.
    return red > green && red > blue;
  }

  @Override
  public String toString() {
    return "(" + red + "," + green + "," + blue + ")";
  }
}