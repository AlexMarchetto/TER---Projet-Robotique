package api.world;

import java.util.ArrayList;
import java.util.List;

import com.cyberbotics.webots.controller.Field;
import com.cyberbotics.webots.controller.Node;
import com.cyberbotics.webots.controller.Supervisor;

import api.utils.MathUtils;

public class PuckManager {
  private final Supervisor robot;

  private final String[] puckNames;
  private final Node[] puckNodes;
  private final Field[] puckTranslationFields;
  private final boolean[] puckDelivered;

  public PuckManager(Supervisor robot, String[] puckNames) {
    this.robot = robot;
    this.puckNames = puckNames;

    this.puckNodes = new Node[puckNames.length];
    this.puckTranslationFields = new Field[puckNames.length];
    this.puckDelivered = new boolean[puckNames.length];

    for (int i = 0; i < puckNames.length; i++) {
      puckNodes[i] = robot.getFromDef(puckNames[i]);

      if (puckNodes[i] != null) {
        puckTranslationFields[i] = puckNodes[i].getField("translation");
        puckDelivered[i] = false;
      } else {
        System.out.println("Warning: unable to find " + puckNames[i]);
      }
    }

    System.out.println("Number of loaded pucks: " + puckNames.length);
  }

  public static PuckManager findAllWithPrefix(Supervisor robot, String prefix) {
    List<String> names = new ArrayList<String>();

    Node root = robot.getRoot();
    Field childrenField = root.getField("children");

    if (childrenField != null) {
      int count = childrenField.getCount();

      for (int i = 0; i < count; i++) {
        Node child = childrenField.getMFNode(i);
        collectDefNamesWithPrefix(child, prefix, names);
      }
    }

    String[] puckNames = names.toArray(new String[0]);

    System.out.println("Automatically detected pucks:");
    for (String name : puckNames) {
      System.out.println("- " + name);
    }

    return new PuckManager(robot, puckNames);
  }

  private static void collectDefNamesWithPrefix(Node node, String prefix, List<String> names) {
    if (node == null) {
      return;
    }

    String defName = node.getDef();

    if (defName != null && defName.startsWith(prefix) && !names.contains(defName)) {
      names.add(defName);
    }

    /*
     * Some Webots nodes can contain child nodes in a "children" field.
     * This is common for Group, Transform, Solid, Robot, etc.
     */
    Field childrenField = node.getField("children");

    if (childrenField != null) {
      int count = childrenField.getCount();

      for (int i = 0; i < count; i++) {
        collectDefNamesWithPrefix(childrenField.getMFNode(i), prefix, names);
      }
    }

    /*
     * Some joint nodes contain another node in an "endPoint" field.
     * This allows the search to continue inside HingeJoint structures.
     */
    Field endPointField = node.getField("endPoint");

    if (endPointField != null) {
      collectDefNamesWithPrefix(endPointField.getSFNode(), prefix, names);
    }
  }

  public int count() {
    return puckNames.length;
  }

  public int findNearestAvailablePuck() {
    double[] robotPosition = robot.getSelf().getPosition();

    int nearestIndex = -1;
    double nearestDistance = Double.MAX_VALUE;

    for (int i = 0; i < puckNodes.length; i++) {
      if (puckNodes[i] == null || puckDelivered[i]) {
        continue;
      }

      double distance = MathUtils.distance2D(robotPosition, puckNodes[i].getPosition());

      if (distance < nearestDistance) {
        nearestDistance = distance;
        nearestIndex = i;
      }
    }

    return nearestIndex;
  }

  public int findBestAvailablePuck(double angleWeight) {
    double[] robotPosition = robot.getSelf().getPosition();
    double robotYaw = MathUtils.getRobotYaw(robot);

    int bestIndex = -1;
    double bestScore = Double.MAX_VALUE;

    for (int i = 0; i < puckNodes.length; i++) {
      if (puckNodes[i] == null || puckDelivered[i]) {
        continue;
      }

      double[] puckPosition = puckNodes[i].getPosition();
      double dx = puckPosition[0] - robotPosition[0];
      double dy = puckPosition[1] - robotPosition[1];

      double distance = Math.sqrt(dx * dx + dy * dy);
      double targetAngle = Math.atan2(dy, dx);
      double angleError = Math.abs(MathUtils.normalizeAngle(targetAngle - robotYaw));

      double score = distance + angleError * angleWeight;

      if (score < bestScore) {
        bestScore = score;
        bestIndex = i;
      }
    }

    return bestIndex;
  }

  public Node getPuckNode(int index) {
    if (index < 0 || index >= puckNodes.length) {
      return null;
    }

    return puckNodes[index];
  }

  public double[] getPuckPosition(int index) {
    Node puckNode = getPuckNode(index);

    if (puckNode == null) {
      return new double[] {0.0, 0.0, 0.0};
    }

    return puckNode.getPosition();
  }

  public double getDistanceToPuck(int index) {
    Node puckNode = getPuckNode(index);

    if (puckNode == null) {
      return Double.MAX_VALUE;
    }

    return MathUtils.distance2D(robot.getSelf().getPosition(), puckNode.getPosition());
  }

  public double getAngleErrorToPuck(int index) {
    Node puckNode = getPuckNode(index);

    if (puckNode == null) {
      return 0.0;
    }

    double[] robotPosition = robot.getSelf().getPosition();
    double[] puckPosition = puckNode.getPosition();

    double dx = puckPosition[0] - robotPosition[0];
    double dy = puckPosition[1] - robotPosition[1];

    double targetAngle = Math.atan2(dy, dx);
    double robotAngle = MathUtils.getRobotYaw(robot);

    return MathUtils.normalizeAngle(targetAngle - robotAngle);
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

    puckTranslationField.setSFVec3f(new double[] {worldX, worldY, worldZ});
    puckNode.resetPhysics();
  }

  public void dropPuck(int index, double dropX, double dropY, double dropZ) {
    if (index < 0 || index >= puckNodes.length) {
      return;
    }

    puckDelivered[index] = true;

    if (puckTranslationFields[index] != null) {
      puckTranslationFields[index].setSFVec3f(new double[] {dropX, dropY, dropZ});
    }

    if (puckNodes[index] != null) {
      puckNodes[index].resetPhysics();
    }

    System.out.println("Puck dropped: " + getPuckName(index));
  }
}