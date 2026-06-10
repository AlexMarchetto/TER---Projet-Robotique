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
      // TODO 1.1 : recuperer le noeud Webots du palet avec robot.getFromDef(...)
      puckNodes[i] = null;

      if (puckNodes[i] != null) {
        // TODO 1.2 : recuperer le champ translation du palet
        puckTranslationFields[i] = null;

        // TODO 1.3 : indiquer que le palet n'est pas encore livre
        puckDelivered[i] = false;
      } else {
        System.out.println("Warning: unable to find " + puckNames[i]);
      }
    }

    System.out.println("Number of loaded pucks: " + puckNames.length);
  }

  public static PuckManager findAllWithPrefix(Supervisor robot, String prefix) {
    List<String> names = new ArrayList<String>();

    // TODO 2.1 : recuperer le noeud racine du monde avec robot.getRoot()
    Node root = null;

    // TODO 2.2 : recuperer le champ children de la racine
    Field childrenField = null;

    if (childrenField != null) {
      int count = childrenField.getCount();

      for (int i = 0; i < count; i++) {
        // TODO 2.3 : recuperer chaque enfant et lancer la recherche recursive
        Node child = null;
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

    // TODO 3.1 : recuperer le nom DEF du noeud
    String defName = null;

    // TODO 3.2 : si le DEF commence par le prefixe et n'est pas deja dans la liste, l'ajouter

    // Recherche dans les enfants du noeud
    Field childrenField = node.getField("children");
    if (childrenField != null) {
      int count = childrenField.getCount();
      for (int i = 0; i < count; i++) {
        collectDefNamesWithPrefix(childrenField.getMFNode(i), prefix, names);
      }
    }

    // Recherche dans le endPoint des joints
    Field endPointField = node.getField("endPoint");
    if (endPointField != null) {
      collectDefNamesWithPrefix(endPointField.getSFNode(), prefix, names);
    }
  }

  public int count() {
    // TODO 4.1 : retourner le nombre de palets connus
    return 0;
  }

  public boolean allPucksDelivered() {
    // TODO 4.2 : retourner true uniquement si tous les palets valides sont livres
    return false;
  }

  public int findNearestAvailablePuck() {
    // TODO 5.1 : recuperer la position du robot
    double[] robotPosition = robot.getSelf().getPosition();

    int nearestIndex = -1;
    double nearestDistance = Double.MAX_VALUE;

    for (int i = 0; i < puckNodes.length; i++) {
      // TODO 5.2 : ignorer les palets inexistants ou deja livres

      // TODO 5.3 : calculer la distance entre le robot et le palet
      double distance = 0.0;

      // TODO 5.4 : conserver l'indice du palet le plus proche
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
    // TODO 6.1 : verifier que l'indice est valide, puis retourner le noeud du palet
    return null;
  }

  public double[] getPuckPosition(int index) {
    Node puckNode = getPuckNode(index);

    if (puckNode == null) {
      return new double[] {0.0, 0.0, 0.0};
    }

    // TODO 6.2 : retourner la position du palet
    return new double[] {0.0, 0.0, 0.0};
  }

  public double getDistanceToPuck(int index) {
    Node puckNode = getPuckNode(index);

    if (puckNode == null) {
      return Double.MAX_VALUE;
    }

    // TODO 7.1 : calculer et retourner la distance 2D entre le robot et le palet
    return Double.MAX_VALUE;
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
    // TODO 8.1 : verifier l'indice et retourner l'etat du palet
    return true;
  }

  public String getPuckName(int index) {
    // TODO 8.2 : verifier l'indice et retourner le nom du palet
    return "unknown";
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
