import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Supervisor;

public class PuckManager {
  private final Supervisor robot;

  private final String[] puckNames;
  private final Node[] puckNodes;
  private final Field[] puckTranslationFields;
  private final boolean[] puckDelivered;

  public PuckManager(Supervisor robot, String[] puckNames) {
    this.robot = robot;
    this.puckNames = puckNames;

    puckNodes = new Node[puckNames.length];
    puckTranslationFields = new Field[puckNames.length];
    puckDelivered = new boolean[puckNames.length];

    for (int i = 0; i < puckNames.length; i++) {
      puckNodes[i] = robot.getFromDef(puckNames[i]);

      if (puckNodes[i] != null) {
        puckTranslationFields[i] = puckNodes[i].getField("translation");
        puckDelivered[i] = false;
      } else {
        System.out.println("Attention : impossible de trouver " + puckNames[i]);
      }
    }
  }

  public int findNearestAvailablePuck() {
    double[] robotPosition = robot.getSelf().getPosition();

    int nearestIndex = -1;
    double nearestDistance = Double.MAX_VALUE;

    for (int i = 0; i < puckNodes.length; i++) {
      if (puckNodes[i] == null || puckDelivered[i]) {
        continue;
      }

      double[] puckPosition = puckNodes[i].getPosition();
      double distance = MathUtils.distance2D(robotPosition, puckPosition);

      if (distance < nearestDistance) {
        nearestDistance = distance;
        nearestIndex = i;
      }
    }

    return nearestIndex;
  }

  public Node getPuckNode(int index) {
    if (index < 0 || index >= puckNodes.length) {
      return null;
    }

    return puckNodes[index];
  }

  public double getDistanceToPuck(int index) {
    Node puckNode = getPuckNode(index);

    if (puckNode == null) {
      return Double.MAX_VALUE;
    }

    double[] robotPosition = robot.getSelf().getPosition();
    double[] puckPosition = puckNode.getPosition();

    return MathUtils.distance2D(robotPosition, puckPosition);
  }

  public boolean isDelivered(int index) {
    if (index < 0 || index >= puckDelivered.length) {
      return true;
    }

    return puckDelivered[index];
  }

  public String getPuckName(int index) {
    if (index < 0 || index >= puckNames.length) {
      return "unknown";
    }

    return puckNames[index];
  }

  public void attachPuckToRobot(int index) {
    if (index < 0 || index >= puckNodes.length) {
      return;
    }

    Node puckNode = puckNodes[index];
    Field puckTranslationField = puckTranslationFields[index];

    if (puckNode == null || puckTranslationField == null) {
      return;
    }

    Node self = robot.getSelf();

    double[] robotPosition = self.getPosition();
    double[] orientation = self.getOrientation();

    double localX = 0.15;
    double localY = 0.0;
    double localZ = 0.09;

    double worldX = robotPosition[0]
        + orientation[0] * localX
        + orientation[1] * localY
        + orientation[2] * localZ;

    double worldY = robotPosition[1]
        + orientation[3] * localX
        + orientation[4] * localY
        + orientation[5] * localZ;

    double worldZ = robotPosition[2]
        + orientation[6] * localX
        + orientation[7] * localY
        + orientation[8] * localZ;

    puckTranslationField.setSFVec3f(new double[] { worldX, worldY, worldZ });
    puckNode.resetPhysics();
  }

  public void dropPuck(int index, double dropX, double dropY, double dropZ) {
    if (index < 0 || index >= puckNodes.length) {
      return;
    }

    puckDelivered[index] = true;

    if (puckTranslationFields[index] != null) {
      puckTranslationFields[index].setSFVec3f(new double[] {
          dropX,
          dropY,
          dropZ
      });
    }

    if (puckNodes[index] != null) {
      puckNodes[index].resetPhysics();
    }

    System.out.println("Palet depose : " + getPuckName(index));
  }
}
