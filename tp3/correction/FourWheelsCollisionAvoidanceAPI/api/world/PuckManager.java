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
      // On récupère le noeud Webots du palet grâce à son nom DEF.
      puckNodes[i] = robot.getFromDef(puckNames[i]);

      if (puckNodes[i] != null) {
        // Le champ "translation" permet de déplacer le palet dans le monde.
        puckTranslationFields[i] = puckNodes[i].getField("translation");

        // Au départ, le palet existe mais il n'est pas encore livré.
        puckDelivered[i] = false;
      } else {
        System.out.println("Warning: unable to find " + puckNames[i]);
      }
    }

    System.out.println("Number of loaded pucks: " + puckNames.length);
  }

  public static PuckManager findAllWithPrefix(Supervisor robot, String prefix) {
    List<String> names = new ArrayList<String>();

    // La racine du monde permet d'accéder à tous les objets présents dans la scène.
    Node root = robot.getRoot();

    // Le champ "children" contient les objets directement placés dans le monde.
    Field childrenField = root.getField("children");

    if (childrenField != null) {
      int count = childrenField.getCount();

      for (int i = 0; i < count; i++) {
        // On récupère chaque enfant de la scène pour rechercher les palets.
        Node child = childrenField.getMFNode(i);

        // La recherche est récursive car un palet peut être contenu dans un autre noeud.
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

    // On récupère le nom DEF du noeud courant.
    String defName = node.getDef();

    /*
     * Si le nom DEF commence par le préfixe demandé,
     * alors ce noeud correspond à un palet à gérer.
     */
    if (defName != null && defName.startsWith(prefix) && !names.contains(defName)) {
      names.add(defName);
    }

    /*
     * Certains noeuds Webots contiennent d'autres noeuds dans un champ "children".
     * On continue donc la recherche à l'intérieur de ces enfants.
     */
    Field childrenField = node.getField("children");

    if (childrenField != null) {
      int count = childrenField.getCount();

      for (int i = 0; i < count; i++) {
        collectDefNamesWithPrefix(childrenField.getMFNode(i), prefix, names);
      }
    }

    /*
     * Les articulations comme les HingeJoint peuvent contenir leur objet final
     * dans un champ "endPoint". On le parcourt aussi pour ne pas manquer de noeud.
     */
    Field endPointField = node.getField("endPoint");

    if (endPointField != null) {
      collectDefNamesWithPrefix(endPointField.getSFNode(), prefix, names);
    }
  }

  public int count() {
    // Le nombre de palets connus correspond au nombre de noms enregistrés.
    return puckNames.length;
  }

  public boolean allPucksDelivered() {
    /*
     * On vérifie tous les palets.
     * Si un palet existe encore et n'est pas livré, la mission n'est pas terminée.
     */
    for (int i = 0; i < puckNodes.length; i++) {
      if (puckNodes[i] != null && !puckDelivered[i]) {
        return false;
      }
    }

    return true;
  }

  public int findNearestAvailablePuck() {
    // On récupère la position actuelle du robot.
    double[] robotPosition = robot.getSelf().getPosition();

    int nearestIndex = -1;
    double nearestDistance = Double.MAX_VALUE;

    for (int i = 0; i < puckNodes.length; i++) {
      // On ignore les palets inexistants ou déjà livrés.
      if (puckNodes[i] == null || puckDelivered[i]) {
        continue;
      }

      // On calcule la distance 2D entre le robot et le palet.
      double distance = MathUtils.distance2D(robotPosition, puckNodes[i].getPosition());

      // Si ce palet est plus proche que les précédents, on le mémorise.
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

      /*
       * Le score combine la distance et l'erreur d'angle.
       * Un palet proche et bien aligné avec le robot obtient un meilleur score.
       */
      double score = distance + angleError * angleWeight;

      if (score < bestScore) {
        bestScore = score;
        bestIndex = i;
      }
    }

    return bestIndex;
  }

  public Node getPuckNode(int index) {
    // On vérifie que l'indice est dans les limites du tableau.
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

    // On retourne la position actuelle du palet dans le monde Webots.
    return puckNode.getPosition();
  }

  public double getDistanceToPuck(int index) {
    Node puckNode = getPuckNode(index);

    if (puckNode == null) {
      return Double.MAX_VALUE;
    }

    // On calcule la distance 2D entre la position du robot et la position du palet.
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

    /*
     * On retourne l'écart entre l'angle actuel du robot
     * et l'angle nécessaire pour regarder vers le palet.
     */
    return MathUtils.normalizeAngle(targetAngle - robotAngle);
  }

  public boolean isDelivered(int index) {
    /*
     * Si l'indice est invalide, on considère le palet comme livré
     * pour éviter de l'utiliser par erreur.
     */
    if (index < 0 || index >= puckDelivered.length) {
      return true;
    }

    return puckDelivered[index];
  }

  public String getPuckName(int index) {
    // Si l'indice est invalide, on retourne un nom par défaut.
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

    /*
     * Position locale du palet par rapport au robot.
     * Le palet est placé devant le robot et légèrement au-dessus du sol.
     */
    double localX = 0.15;
    double localY = 0.0;
    double localZ = 0.09;

    /*
     * Conversion d'une position locale vers une position globale.
     * Cela permet au palet de suivre l'orientation du robot.
     */
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

    // Le palet est marqué comme livré.
    puckDelivered[index] = true;

    if (puckTranslationFields[index] != null) {
      // On place le palet à la position de dépôt donnée.
      puckTranslationFields[index].setSFVec3f(new double[] {dropX, dropY, dropZ});
    }

    if (puckNodes[index] != null) {
      // On réinitialise la physique pour stabiliser le palet à sa nouvelle position.
      puckNodes[index].resetPhysics();
    }

    System.out.println("Puck dropped: " + getPuckName(index));
  }
}