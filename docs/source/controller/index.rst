##########
Controller
##########

Présentation générale
=====================

Le controller ``FourWheelsCollisionAvoidance`` permet de contrôler le robot ramasseur de palets dans Webots.

Son objectif est de : 

* Rechercher les palets présents dans l'arène ;
* Détecter les palets à l'aide des capteurs et de leur position ;
* S'orienter vers un palet lorsqu'il est proche ;
* Ramasser le palet avec le bras et la pince ;
* Transporter le palet jusqu'à la zone de dépôt ;
* Déposer le palet dans la zone prévue ;
* Éviter les murs et les obstacles ;
* Éviter de rester bloqué contre un mur, notamment lorsque le robot arrive en diagonale.

Le controller est écrit en Java et utilise l'API Webots, notamment la classe ``Supervisor`` pour accéder aux objets de simulation.

Fichier concerné
----------------

Le controller se trouve dans le fichier suivant :

.. code-block:: text

    controllers/FourWheelsCollisionAvoidance/FourWheelsCollisionAvoidance.java

Classe principale
-----------------

.. code-block:: java

    public class FourWheelsCollisionAvoidance

Cette classe contient toute la logique du robot. Elle initialise les capteurs, les moteurs, les palets, puis exécute une boucle principale qui décide du comportement du robot à chaque pas de simulation.

Imports utilisés
----------------

Le controller utilise plusieurs classes de l'API Webots :

.. code-block:: java

    import com.cyberbotics.webots.controller.Supervisor;
    import com.cyberbotics.webots.controller.Node;
    import com.cyberbotics.webots.controller.Field;

    import com.cyberbotics.webots.controller.DistanceSensor;
    import com.cyberbotics.webots.controller.Motor;
    import com.cyberbotics.webots.controller.Camera;
    import com.cyberbotics.webots.controller.TouchSensor;
    import com.cyberbotics.webots.controller.PositionSensor;

Rôle des principales classes utilisées :

.. list-table::
    :header-rows: 1

    * - Classe
      - Rôle
    * - ``Supervisor``
      - Permet au controller d'accéder aux objets de la scène Webots, comme les palets.
    * - ``Node``
      - Représente un objet de la scène, par exemple un palet.
    * - ``Field``
      - Permet de modifier un champ d'un objet Webots, comme sa position.
    * - ``DistanceSensor``
      - Permet de détecter des obstacles devant ou sur les côtés du robot.
    * - ``Motor``
      - Permet de contrôler les roues, le bras et la pince.
    * - ``Camera``
      - Permet de lire la couleur moyenne devant le robot.
    * - ``TouchSensor``
      - Permet de détecter un contact physique avec un palet, un mur ou la zone de dépôt.
    * - ``PositionSensor``
      - Permet de suivre la position du bras.

Fonctions utilitaires
---------------------

``attachPuckToRobot``
~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public static void attachPuckToRobot(Supervisor robot, Node puckNode, Field puckTranslationField) {
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

Cette fonction permet de faire suivre un palet au robot après son ramassage.

Comme le robot ne saisit pas réellement le palet de manière physique, le controller déplace directement le palet devant le robot à chaque pas de simulation.

La fonction utilise : 

* La position du robot ;
* Son orientation ;
* Un décalage local devant le robot ;
* Le champ ``translation`` du palet.

Le palet est ensuite repositionné avec :

.. code-block:: java

    puckTranslationField.setSFVec3f(new double[] { worldX, worldY, worldZ });

Puis sa physique est réinitialisée avec :

.. code-block:: java

    puckNode.resetPhysics();

Cela permet d'éviter que le palet tombe ou parte dans une mauvaise direction pendant le transport.

``normalizeAngle``
~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public static double normalizeAngle(double angle) {
        while (angle > Math.PI) {
        angle -= 2.0 * Math.PI;
        }

        while (angle < -Math.PI) {
        angle += 2.0 * Math.PI;
        }

        return angle;
    }

Cette fonction permet de normaliser un angle entre ``-PI`` et ``PI``.

Elle est utilisée lorsque le robot doit calculer l'écart entre sa direction actuelle et la direction d'une cible, comme un palet ou la zone de dépôt.

Exemple :

.. code-block:: java

    double angleError = normalizeAngle(targetAngle - robotAngle);

``getRobotYaw``
~~~~~~~~~~~~~~~

.. code-block:: java

    public static double getRobotYaw(Supervisor robot) {
        double[] orientation = robot.getSelf().getOrientation();
        return Math.atan2(orientation[3], orientation[0]);
    }

Cette fonction récupère l'orientation du robot sur l'axe vertical.

Elle permet de savoir dans quelle direction le robot regarde.

Elle est utilisée pour :

* S'orienter vers un palet ;
* Se diriger vers la zone de dépôt ;
* Corriger la trajectoire du robot.

``findNearstAvailablePuck``
~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    public static int findNearestAvailablePuck(
        Supervisor robot,
        Node[] puckNodes,
        boolean[] puckDelivered
    ) {
        double[] robotPosition = robot.getSelf().getPosition();

        int nearestIndex = -1;
        double nearestDistance = Double.MAX_VALUE;

        for (int i = 0; i < puckNodes.length; i++) {
        if (puckNodes[i] == null || puckDelivered[i]) {
            continue;
        }

        double[] puckPosition = puckNodes[i].getPosition();

        double dx = puckPosition[0] - robotPosition[0];
        double dy = puckPosition[1] - robotPosition[1];

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < nearestDistance) {
            nearestDistance = distance;
            nearestIndex = i;
        }
        }

        return nearestIndex;
    }

Cette fonction cherche le palet disponible le plus proche du robot.

Un palet est considéré comme disponible si :

* Il existe dans la scène ;
* Il n'a pas encore été livré dans la zone de dépôt.

La fonction retourne :

* L'indice du palet le plus proche ;
* ``-1`` si aucun palet disponible n'est trouvé.

Initialisation du robot
-----------------------

Création du Supervisor
~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    Supervisor robot = new Supervisor();

Le robot est crée comme ``Supervisor`` afin de pouvoir accéder aux objets de la scène Webots.

Cela est nécessaire pour récupérer les palets avec : 

.. code-block:: java

    robot.getFromDef("PALET_1");

Récupération du pas de simulation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    int timeStep = (int) Math.round(robot.getBasicTimeStep());

Le ``timeStep`` correspond au pas de simulation Webots. Il est utilisé pour activer les capteurs et faire avancer la boucle principale du controller.

Déclaration des palets
~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

    String[] puckNames = {
        "PALET_1",
        "PALET_2",
        "PALET_3"
    };

Chaque palet doit avoir un ``DEF`` correspondant dans le fichier monde Webots.

Exemple :

.. code-block:: text

    DEF PALET_1 Solid {
        translation 0.6 4.93026e-14 0.065
        rotation -0.9999934699443532 -9.589734506671008e-09 0.0036138716983296877 -5.307179586466759e-06
        children [
            Shape {
            appearance PBRAppearance {
                baseColor 1 0 0
                roughness 1
                metalness 0
            }
            geometry Cylinder {
                height 0.03
                radius 0.04
            }
            }
        ]
        name "palet_rouge_1"
        model "palet"
        boundingObject Cylinder {
            height 0.03
            radius 0.04
        }
        physics Physics {
            density -1
            mass 0.05
        }
    }

Le controller stocke ensuite :

* Les noeuds des palets ;
* Leur champ ``translation`` ;
* Leur état de livraison.

.. code-block:: java

    Node[] puckNodes = new Node[puckNames.length];
    Field[] puckTranslationFields = new Field[puckNames.length];
    boolean[] puckDelivered = new boolean[puckNames.length];

Capteurs utilisés
-----------------

Capteurs de distance
~~~~~~~~~~~~~~~~~~~~

Le robot utilise trois capteurs de distance :

.. code-block:: java

    DistanceSensor dsRight = robot.getDistanceSensor("ds_right");
    DistanceSensor dsLeft = robot.getDistanceSensor("ds_left");
    DistanceSensor dsFront = robot.getDistanceSensor("ds_front");

Rôle des capteurs :

.. list-table::
    :header-rows: 1

    * - Capteur
      - Rôle
    * - ``ds_front``
      - Détecte un objet devant le robot.
    * - ``ds_left``
      - Détecte un obstacle ou un mur sur la gauche.
    * - ``ds_right``
      - Détecte un obstacle ou un mur sur la droite.

Les capteurs latéraux ne déclenchent pas directement un recul. Ils servent surtout à corriger la trajectoire ou à éviter les murs.

Caméra couleur
~~~~~~~~~~~~~~

.. code-block:: java

    Camera colorSensor = robot.getCamera("color_sensor");

La caméra permet de lire la couleur moyenne de l'image capté par le robot.

Le controller calcule une moyenne RGB :

.. code-block:: java

    red = sumRed / pixelCount;
    green = sumGreen / pixelCount;
    blue = sumBlue / pixelCount;

Puis il vérifie si la couleur rouge est détectée :

.. code-block:: java

    boolean redDetected = red > 150 && green < 100 && blue < 100;

Cette détection peut aider à identifier un palet rouge.

Capteur de contact
~~~~~~~~~~~~~~~~~~

.. code-block:: java

   TouchSensor touchFront = robot.getTouchSensor("touch_front");

Le capteur de contact permet de savoir si le robot touche un objet.

Il est utilisé pour distinguer :

* un contact avec un palet ;
* un contact avec un mur ;
* un contact avec la zone de dépôt.

La détection est faite avec :

.. code-block:: java

   boolean touched = touchFront.getValue() > 0.0;

Moteurs
-------

Moteurs des roues
~~~~~~~~~~~~~~~~~

Le robot possède quatre roues :

.. code-block:: java

   Motor wheel1 = robot.getMotor("wheel1");
   Motor wheel2 = robot.getMotor("wheel2");
   Motor wheel3 = robot.getMotor("wheel3");
   Motor wheel4 = robot.getMotor("wheel4");

Les roues sont configurées en contrôle de vitesse :

.. code-block:: java

   wheels[i].setPosition(Double.POSITIVE_INFINITY);
   wheels[i].setVelocity(0.0);

Les roues de gauche utilisent ``leftSpeed`` et les roues de droite utilisent ``rightSpeed``.

.. code-block:: java

   wheel1.setVelocity(leftSpeed);
   wheel2.setVelocity(rightSpeed);
   wheel3.setVelocity(leftSpeed);
   wheel4.setVelocity(rightSpeed);

Moteurs du bras et de la pince
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le robot possède :

.. code-block:: java

   Motor armMotor = robot.getMotor("arm_motor");
   Motor gripperLeftMotor = robot.getMotor("gripper_left_motor");
   Motor gripperRightMotor = robot.getMotor("gripper_right_motor");

Le bras peut être levé ou baissé :

.. code-block:: java

   double ARM_UP = -0.65;
   double ARM_DOWN = 0.35;

La pince peut être ouverte ou fermée :

.. code-block:: java

   double GRIPPER_OPEN_LEFT = 0.2;
   double GRIPPER_OPEN_RIGHT = -0.2;

   double GRIPPER_CLOSED_LEFT = -0.55;
   double GRIPPER_CLOSED_RIGHT = 0.55;

Paramètres principaux
---------------------

Vitesses
~~~~~~~~

.. code-block:: java

   double SEARCH_SPEED = 4.0;
   double APPROACH_SPEED = 0.8;
   double GO_DROP_SPEED = 3.2;
   double TURN_SPEED = 4.0;
   double BACK_SPEED = -2.0;

Rôle des vitesses :

.. list-table::
   :header-rows: 1

   * - Constante
     - Rôle
   * - ``SEARCH_SPEED``
     - Vitesse du robot lorsqu'il cherche un palet.
   * - ``APPROACH_SPEED``
     - Vitesse utilisée lorsqu'il approche un palet.
   * - ``GO_DROP_SPEED``
     - Vitesse utilisée pour aller vers la zone de dépôt.
   * - ``TURN_SPEED``
     - Vitesse utilisée pour tourner.
   * - ``BACK_SPEED``
     - Vitesse utilisée pour reculer.

Position de dépôt
~~~~~~~~~~~~~~~~~

.. code-block:: java

   double DROP_X = -0.9;
   double DROP_Y = 0.0;
   double DROP_Z = 0.095;

Ces valeurs correspondent à la position où le palet est placé après le dépôt.

Seuils de détection
~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   double FRONT_OBJECT_THRESHOLD = 350.0;
   double PUCK_CONTACT_DISTANCE = 0.40;
   double PUCK_DETECTION_DISTANCE = 1.20;
   double PUCK_MAX_ANGLE = 1.40;

Rôle des seuils :

.. list-table::
   :header-rows: 1

   * - Constante
     - Rôle
   * - ``FRONT_OBJECT_THRESHOLD``
     - Seuil à partir duquel ``ds_front`` considère qu'un objet est devant le robot.
   * - ``PUCK_CONTACT_DISTANCE``
     - Distance maximale pour considérer qu'un contact correspond à un palet.
   * - ``PUCK_DETECTION_DISTANCE``
     - Distance à partir de laquelle le robot peut cibler un palet proche.
   * - ``PUCK_MAX_ANGLE``
     - Angle maximal autorisé pour considérer qu'un palet est devant le robot.

Évitement des murs
~~~~~~~~~~~~~~~~~~

.. code-block:: java

   double SIDE_OBJECT_THRESHOLD = 900.0;
   double SIDE_DANGER_THRESHOLD = 980.0;

Ces seuils permettent au robot de détecter les obstacles latéraux.

Le robot ne recule pas directement à cause de ces capteurs. Il tourne simplement pour éviter l'obstacle.

Anti-collage contre les murs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   double WALL_STUCK_THRESHOLD = 970.0;
   int WALL_STUCK_TURN_TIME = 55;
   int wallStuckCounter = 0;

Cette sécurité permet d'éviter que le robot continue d'avancer contre un mur lorsqu'il arrive en diagonale.

Si un capteur latéral reste très élevé pendant plusieurs pas de simulation, le robot force une rotation.

Correction vers le palet
~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   double APPROACH_TURN_GAIN = 4.5;

Ce coefficient contrôle la force avec laquelle le robot corrige sa trajectoire vers le palet.

Plus la valeur est grande, plus le robot tourne fortement vers le palet.

Timers
~~~~~~

.. code-block:: java

   int APPROACH_TIMEOUT = 180;

   int TOUCH_BACK_TIME = 35;
   int TOUCH_TURN_TIME = 90;
   int SIDE_TURN_TIME = 40;

Rôle des timers :

.. list-table::
   :header-rows: 1

   * - Constante
     - Rôle
   * - ``APPROACH_TIMEOUT``
     - Temps maximal passé à approcher un palet.
   * - ``TOUCH_BACK_TIME``
     - Durée du recul après un contact avec un obstacle.
   * - ``TOUCH_TURN_TIME``
     - Durée de rotation après un contact avec un obstacle.
   * - ``SIDE_TURN_TIME``
     - Durée de rotation après détection latérale.

États du robot
--------------

Le controller fonctionne avec une machine à états.

.. code-block:: java

   String robotMode = "SEARCH";

Les principaux états sont :

.. list-table::
   :header-rows: 1

   * - État
     - Description
   * - ``SEARCH``
     - Le robot cherche un palet dans l'arène.
   * - ``APPROACH_PUCK``
     - Le robot se dirige vers un palet détecté.
   * - ``LOWER_ARM``
     - Le robot baisse son bras.
   * - ``CLOSE_GRIPPER``
     - Le robot ferme sa pince.
   * - ``LIFT_ARM``
     - Le robot lève le bras avec le palet.
   * - ``GO_TO_DROP_ZONE``
     - Le robot se dirige vers la zone de dépôt.
   * - ``DROP_PUCK``
     - Le robot dépose le palet.
   * - ``LIFT_ARM_AFTER_DROP``
     - Le robot relève le bras après le dépôt.
   * - ``BACK_AND_TURN_AFTER_DROP``
     - Le robot recule et tourne pour quitter la zone de dépôt.
   * - ``TOUCH_AVOID``
     - Le robot recule puis tourne après un contact avec un obstacle.

Logique de recherche
--------------------

En mode ``SEARCH``, le robot avance et cherche un palet.

Le robot peut détecter un palet de deux manières :

* avec le capteur frontal ``ds_front`` ;
* avec la position du palet si celui-ci est proche et dans une zone raisonnable devant le robot.

Condition utilisée :

.. code-block:: java

   if (
       objectInFront
           || (
               distanceToPuck < PUCK_DETECTION_DISTANCE
                   && Math.abs(angleError) < PUCK_MAX_ANGLE
           )
   )

Cela permet au robot de ne pas passer à côté d'un palet placé légèrement sur le côté.

Approche du palet
-----------------

En mode ``APPROACH_PUCK``, le robot ne se contente pas d'avancer tout droit. Il corrige sa trajectoire vers le palet.

Le controller calcule :

* la position du robot ;
* la position du palet ;
* l'angle vers le palet ;
* l'angle actuel du robot ;
* l'erreur d'angle.

.. code-block:: java

   double targetAngle = Math.atan2(dy, dx);
   double robotAngle = getRobotYaw(robot);
   double angleError = normalizeAngle(targetAngle - robotAngle);

La correction est calculée avec :

.. code-block:: java

   double correction = APPROACH_TURN_GAIN * angleError;

Puis les vitesses des roues sont ajustées :

.. code-block:: java

   leftSpeed = APPROACH_SPEED - correction;
   rightSpeed = APPROACH_SPEED + correction;

Ainsi :

* si le palet est à gauche, le robot tourne vers la gauche ;
* si le palet est à droite, le robot tourne vers la droite ;
* si le robot est aligné, il avance droit.

Gestion du contact
------------------

Le controller utilise ``touched`` au lieu de seulement ``newContact``.

.. code-block:: java

   boolean touched = touchFront.getValue() > 0.0;

Cela rend la détection plus robuste, car à grande vitesse le robot peut rater l'instant exact du premier contact.

Lorsqu'un contact est détecté, le controller cherche le palet le plus proche.

Si un palet est proche :

.. code-block:: java

   if (distanceToPuck < PUCK_CONTACT_DISTANCE) {
       puckClose = true;
   }

Alors le robot commence le ramassage.

Sinon, il considère qu'il touche un obstacle ou la zone de dépôt sans palet, et passe en mode ``TOUCH_AVOID``.

Ramassage du palet
------------------

Le ramassage se fait en plusieurs étapes :

1. ``LOWER_ARM`` : le bras descend.
2. ``CLOSE_GRIPPER`` : la pince se ferme.
3. ``LIFT_ARM`` : le bras remonte.
4. Le palet est attaché virtuellement au robot.

Le palet est considéré comme attaché avec :

.. code-block:: java

   puckAttached = true;

Ensuite, à chaque pas de simulation, la fonction ``attachPuckToRobot`` déplace le palet devant le robot.

Dépôt du palet
--------------

Lorsque le robot transporte un palet, il passe en mode ``GO_TO_DROP_ZONE``.

Il calcule l'angle vers la zone de dépôt :

.. code-block:: java

   double targetAngle = Math.atan2(dy, dx);
   double robotAngle = getRobotYaw(robot);
   double angleError = normalizeAngle(targetAngle - robotAngle);

S'il n'est pas aligné, il tourne.

S'il est aligné, il avance.

Quand il touche la zone de dépôt avec un palet, il passe en mode ``DROP_PUCK`` :

.. code-block:: java

   if (touched && puckAttached) {
       robotMode = "DROP_PUCK";
   }

Le palet est ensuite placé directement dans la zone de dépôt :

.. code-block:: java

   puckTranslationFields[currentPuckIndex].setSFVec3f(new double[] {
       DROP_X,
       DROP_Y,
       DROP_Z
   });

Évitement des obstacles
-----------------------

Le robot possède deux types d'évitement.

Évitement par contact
~~~~~~~~~~~~~~~~~~~~~

Si le robot touche un obstacle sans palet, il passe en mode ``TOUCH_AVOID``.

Ce mode contient deux étapes :

1. reculer ;
2. tourner.

.. code-block:: java

   if (actionCounter < TOUCH_BACK_TIME) {
       leftSpeed = BACK_SPEED;
       rightSpeed = BACK_SPEED;
   }

   else if (actionCounter < TOUCH_BACK_TIME + TOUCH_TURN_TIME) {
       leftSpeed = TURN_SPEED;
       rightSpeed = -TURN_SPEED;
   }

Évitement latéral
~~~~~~~~~~~~~~~~~

Les capteurs ``ds_left`` et ``ds_right`` permettent de détecter un mur ou un obstacle sur les côtés.

Si l'obstacle est à gauche, le robot tourne à droite.

Si l'obstacle est à droite, le robot tourne à gauche.

.. code-block:: java

   else if (leftValue > SIDE_OBJECT_THRESHOLD && leftValue > rightValue + 150.0) {
       avoidDirection = -1;
       avoidObstacleCounter = SIDE_TURN_TIME;
   }

   else if (rightValue > SIDE_OBJECT_THRESHOLD && rightValue > leftValue + 150.0) {
       avoidDirection = 1;
       avoidObstacleCounter = SIDE_TURN_TIME;
   }

Anti-collage contre les murs
----------------------------

Lorsque le robot arrive en diagonale contre un mur, il peut parfois avancer en frottant le mur.

Pour éviter cela, le controller utilise un compteur :

.. code-block:: java

   int wallStuckCounter = 0;

Si un capteur latéral reste très élevé pendant plusieurs pas de simulation, le robot force une rotation.

.. code-block:: java

   if (wallStuckCounter > 5) {
       avoidObstacleCounter = WALL_STUCK_TURN_TIME;
       wallStuckCounter = 0;
   }

Cela permet au robot de se décoller du mur.

Variables d'état
----------------

.. code-block:: java

   int currentPuckIndex = -1;
   boolean[] puckDelivered = new boolean[puckNames.length];

   boolean puckDetected = false;
   boolean puckTouched = false;
   boolean previousTouched = false;
   boolean puckAttached = false;

Rôle des variables :

.. list-table::
   :header-rows: 1

   * - Variable
     - Rôle
   * - ``currentPuckIndex``
     - Indice du palet actuellement ciblé ou transporté.
   * - ``puckDelivered``
     - Indique si un palet a déjà été déposé.
   * - ``puckDetected``
     - Indique qu'un palet a été détecté.
   * - ``puckTouched``
     - Indique qu'un palet a été touché.
   * - ``previousTouched``
     - Mémorise l'état précédent du capteur de contact.
   * - ``puckAttached``
     - Indique que le palet est actuellement transporté par le robot.

Résumé du comportement
----------------------

Le comportement global du robot est le suivant :

1. Le robot commence en mode ``SEARCH``.
2. Il avance dans l'arène.
3. Il cherche le palet disponible le plus proche.
4. Si un palet est détecté, il passe en mode ``APPROACH_PUCK``.
5. Il se réoriente progressivement vers le palet.
6. Lorsqu'il touche le palet, il baisse son bras.
7. Il ferme la pince.
8. Il relève le bras.
9. Il transporte le palet vers la zone de dépôt.
10. Lorsqu'il touche la zone de dépôt, il dépose le palet.
11. Il recule et tourne pour sortir de la zone.
12. Il repart chercher un autre palet.

Réglages possibles
------------------

Vitesse du robot
~~~~~~~~~~~~~~~~

Pour accélérer ou ralentir le robot, modifier :

.. code-block:: java

   double SEARCH_SPEED = 4.0;
   double APPROACH_SPEED = 0.8;
   double GO_DROP_SPEED = 3.2;
   double TURN_SPEED = 4.0;
   double BACK_SPEED = -2.0;

Détection des palets
~~~~~~~~~~~~~~~~~~~~

Pour détecter les palets plus loin :

.. code-block:: java

   double PUCK_DETECTION_DISTANCE = 1.20;

Pour détecter uniquement les palets plus proches :

.. code-block:: java

   double PUCK_DETECTION_DISTANCE = 0.80;

Pour accepter un palet plus sur le côté :

.. code-block:: java

   double PUCK_MAX_ANGLE = 1.60;

Pour réduire cette zone :

.. code-block:: java

   double PUCK_MAX_ANGLE = 1.00;

Correction vers le palet
~~~~~~~~~~~~~~~~~~~~~~~~

Si le robot ne tourne pas assez vers le palet :

.. code-block:: java

   double APPROACH_TURN_GAIN = 5.0;

Si le robot zigzague trop :

.. code-block:: java

   double APPROACH_TURN_GAIN = 3.0;

Évitement des murs
~~~~~~~~~~~~~~~~~~

Si le robot colle encore aux murs :

.. code-block:: java

   int WALL_STUCK_TURN_TIME = 80;

Si la sécurité anti-collage se déclenche trop souvent :

.. code-block:: java

   double WALL_STUCK_THRESHOLD = 990.0;

