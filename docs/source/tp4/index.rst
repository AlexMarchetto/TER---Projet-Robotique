Projet robotique sur Webots — TP4
=================================

Mise en place de l’algorithme de recherche et de dépôt des palets
-----------------------------------------------------------------

Sommaire
--------

-  `0. Informations et objectifs <#0-informations-et-objectifs>`__

   -  `0.1 Contexte du TP <#01-contexte-du-tp>`__
   -  `0.2 Matériel et outils nécessaires <#02-matériel-et-outils-nécessaires>`__
   -  `0.3 Objectifs de la séance <#03-objectifs-de-la-séance>`__

-  `1. Organisation du code <#1-organisation-du-code>`__

   -  `1.1 Organisation des fichiers <#11-organisation-des-fichiers>`__
   -  `1.2 Fichiers à lire et à compléter <#12-fichiers-à-lire-et-à-compléter>`__

-  `2. Principe général de l’algorithme <#2-principe-général-de-lalgorithme>`__

   -  `2.1 Objectif du comportement <#21-objectif-du-comportement>`__
   -  `2.2 Logique globale <#22-logique-globale>`__

-  `3. Recherche des palets avec les capteurs <#3-recherche-des-palets-avec-les-capteurs>`__

   -  `3.1 Déplacement de recherche <#31-déplacement-de-recherche>`__
   -  `3.2 Détection d’un objet <#32-détection-dun-objet>`__
   -  `3.3 Différence entre palet et mur <#33-différence-entre-palet-et-mur>`__

-  `4. Récupération d’un palet <#4-récupération-dun-palet>`__

   -  `4.1 Approche du palet <#41-approche-du-palet>`__
   -  `4.2 Confirmation du contact <#42-confirmation-du-contact>`__
   -  `4.3 Séquence de prise <#43-séquence-de-prise>`__

-  `5. Déplacement vers la base <#5-déplacement-vers-la-base>`__

   -  `5.1 Définition de la base <#51-définition-de-la-base>`__
   -  `5.2 Choix de la zone de dépôt <#52-choix-de-la-zone-de-dépôt>`__
   -  `5.3 Navigation vers la base <#53-navigation-vers-la-base>`__

-  `6. Dépôt du palet <#6-dépôt-du-palet>`__

   -  `6.1 Séquence de dépôt <#61-séquence-de-dépôt>`__
   -  `6.2 Retour en recherche <#62-retour-en-recherche>`__

-  `7. Gestion des modes du robot <#7-gestion-des-modes-du-robot>`__
-  `8. Test et validation <#8-test-et-validation>`__
-  `9. Bilan du TP <#9-bilan-du-tp>`__


0. Informations et objectifs
----------------------------

0.1 Contexte du TP
~~~~~~~~~~~~~~~~~~

Dans le **TP1**, vous avez construit la structure du robot dans Webots.

Dans le **TP2**, vous avez complété une première API Java permettant de commander :

-  les moteurs
-  les roues
-  les capteurs
-  le bras
-  la pince

Dans le **TP3**, vous avez découvert la gestion des palets avec ``PuckManager`` et mis en place une première logique de récupération.

Dans ce **TP4**, vous allez mettre en place un comportement plus complet.

Le robot devra :

#. parcourir la carte
#. chercher des palets avec ses capteurs
#. récupérer un palet
#. se déplacer jusqu’à une base
#. déposer le palet dans cette base
#. repartir chercher un autre palet
#. recommencer jusqu’à ce que tous les palets soient traités

Ce TP correspond donc à la première version complète de la mission du robot.

.. figure:: ../images/tp4/tp4_vue_globale_mission.png
   :alt: Vue globale de la mission du TP4

   Figure 1 — Vue globale de la mission du TP4 : chercher, récupérer et déposer les palets.


0.2 Matériel et outils nécessaires
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Pour réaliser ce TP, vous devez disposer des éléments suivants :

.. list-table::
   :header-rows: 1

   * - Élément
     - Utilisation
   * - Un ordinateur
     - Exécuter Webots et modifier le code
   * - Webots
     - Simuler le robot, les palets et la base
   * - Visual Studio Code
     - Modifier plus facilement les fichiers Java
   * - Documentation Webots
     - Comprendre les objets ``Supervisor``, ``Node``, ``Field``, ``Motor``, ``Sensor``
   * - Documentation Java
     - Comprendre les classes, tableaux, conditions et énumérations

Liens utiles :

-  `Site officiel de Webots <https://www.cyberbotics.com/>`__
-  `Documentation Webots <https://cyberbotics.com/doc/reference/index>`__
-  `Visual Studio Code <https://code.visualstudio.com/>`__
-  `Documentation Java <https://docs.oracle.com/en/java/>`__


0.3 Objectifs de la séance
~~~~~~~~~~~~~~~~~~~~~~~~~~

À la fin de ce TP, vous devez être capables de :

-  comprendre la logique générale d’un comportement autonome
-  utiliser les capteurs pour rechercher un palet
-  différencier un obstacle proche d’un objet à approcher
-  déclencher une séquence de prise après contact
-  transporter un palet
-  se diriger vers une zone de dépôt
-  déposer le palet dans la base
-  revenir en mode recherche
-  organiser un comportement complexe avec plusieurs modes


1. Organisation du code
-----------------------

1.1 Organisation des fichiers
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le TP4 reprend la structure des TP précédents.

Le comportement principal se trouve dans :

.. code-block:: text

   api/behavior/CollectPucksBehavior.java

La gestion des modes se trouve dans :

.. code-block:: text

   api/state/RobotMode.java

La gestion des palets se trouve dans :

.. code-block:: text

   api/world/PuckManager.java

Organisation générale :

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       ├── FourWheelsCollisionAvoidanceAPI.java
       └── api/
           ├── actuators/
           │   ├── Arm.java
           │   └── Gripper.java
           ├── behavior/
           │   ├── RobotBehavior.java
           │   ├── SimpleAvoidObstacleBehavior.java
           │   └── CollectPucksBehavior.java
           ├── core/
           │   └── TERBot.java
           ├── motors/
           │   ├── Wheel.java
           │   ├── MotorGroup.java
           │   └── DriveBase.java
           ├── sensors/
           │   ├── DistanceSensorWrapper.java
           │   ├── SensorManager.java
           │   └── TouchSensorWrapper.java
           ├── state/
           │   └── RobotMode.java
           ├── tasks/
           │   ├── RobotTask.java
           │   ├── TaskScheduler.java
           │   └── TimedTask.java
           ├── utils/
           │   └── MathUtils.java
           └── world/
               └── PuckManager.java


1.2 Fichiers à lire et à compléter
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :header-rows: 1

   * - Fichier
     - Rôle
     - Action
   * - ``FourWheelsCollisionAvoidanceAPI.java``
     - Point d’entrée du contrôleur
     - À vérifier
   * - ``TERBot.java``
     - Regroupe les API du robot
     - À lire
   * - ``RobotMode.java``
     - Contient les modes du robot
     - À compléter ou vérifier
   * - ``CollectPucksBehavior.java``
     - Comportement principal de collecte
     - À compléter
   * - ``PuckManager.java``
     - Gestion des palets dans Webots
     - À réutiliser
   * - ``DriveBase.java``
     - Déplacements du robot
     - À réutiliser
   * - ``SensorManager.java``
     - Accès aux capteurs
     - À réutiliser
   * - ``Arm.java``
     - Commande du bras
     - À réutiliser
   * - ``Gripper.java``
     - Commande de la pince
     - À réutiliser


2. Principe général de l’algorithme
-----------------------------------

2.1 Objectif du comportement
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le comportement à mettre en place doit permettre au robot d’effectuer une mission complète.

Le robot doit être capable de fonctionner en boucle :

.. code-block:: text

   chercher un palet
   → s’approcher
   → le ramasser
   → aller à la base
   → le déposer
   → repartir chercher un autre palet

Cette logique sera gérée dans la classe :

.. code-block:: text

   CollectPucksBehavior.java


2.2 Logique globale
~~~~~~~~~~~~~~~~~~~

L’algorithme général peut être résumé ainsi :

.. code-block:: text

   Tant que tous les palets ne sont pas déposés :

       chercher un palet avec les capteurs

       si un objet est détecté :
           s’approcher de l’objet

       si le robot touche un palet :
           baisser le bras
           fermer la pince
           lever le bras

       aller vers la base

       déposer le palet

       repartir en recherche

Le robot ne doit pas faire toute cette logique dans une seule méthode.

Il doit utiliser plusieurs modes, afin que chaque étape soit claire et séparée.


3. Recherche des palets avec les capteurs
-----------------------------------------

3.1 Déplacement de recherche
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

En mode ``SEARCH``, le robot doit parcourir la carte pour trouver un palet.

Il peut avancer en ligne droite ou suivre une trajectoire de recherche.

Une stratégie simple consiste à faire avancer le robot en courbe afin de balayer une plus grande zone.

Exemple :

.. code-block:: java

   robot.motors().curveLeft(SEARCH_SPEED, 0.6);

ou :

.. code-block:: java

   robot.motors().curveRight(SEARCH_SPEED, 0.6);

Le robot peut alterner entre une courbe à gauche et une courbe à droite.

Exemple de logique :

.. code-block:: java

   if ((stepCounter / 80) % 2 == 0) {
     robot.motors().curveLeft(SEARCH_SPEED, 0.65);
   } else {
     robot.motors().curveRight(SEARCH_SPEED, 0.65);
   }

Cette logique permet au robot de traverser la carte au lieu d’avancer toujours dans la même direction.


3.2 Détection d’un objet
~~~~~~~~~~~~~~~~~~~~~~~~

Le robot utilise ses capteurs de distance pour détecter un objet.

Les capteurs principaux sont :

.. list-table::
   :header-rows: 1

   * - Capteur
     - Rôle
   * - ``ds_front``
     - Détecter un objet devant le robot
   * - ``ds_left``
     - Détecter un objet sur la gauche
   * - ``ds_right``
     - Détecter un objet sur la droite
   * - ``touch_front``
     - Confirmer le contact avec un objet

Exemple :

.. code-block:: java

   double frontValue = robot.sensors().frontDistance();
   double leftValue = robot.sensors().leftDistance();
   double rightValue = robot.sensors().rightDistance();

On peut ensuite vérifier si un objet est détecté :

.. code-block:: java

   boolean objectInFront =
       frontValue > FRONT_OBJECT_THRESHOLD
           && frontValue < FRONT_WALL_THRESHOLD;

Le seuil minimal permet de savoir si quelque chose est détecté.

Le seuil maximal permet d’éviter de confondre certains murs très proches avec des palets.


3.3 Différence entre palet et mur
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Un problème important est que les capteurs de distance peuvent détecter aussi bien :

-  un palet
-  un mur
-  un obstacle
-  une bordure de la carte

Le robot doit donc éviter de considérer tous les objets comme des palets.

Une stratégie simple consiste à utiliser des seuils différents.

Exemple :

.. code-block:: java

   private static final double FRONT_OBJECT_THRESHOLD = 120.0;
   private static final double FRONT_WALL_THRESHOLD = 900.0;

   private static final double SIDE_OBJECT_THRESHOLD = 120.0;
   private static final double SIDE_WALL_THRESHOLD = 920.0;

Logique possible :

.. code-block:: text

   Si la valeur est faible ou moyenne :
       l’objet peut être un palet

   Si la valeur est très forte :
       l’objet est probablement un mur

Si un mur est détecté, le robot doit se réorienter pour continuer la recherche.


4. Récupération d’un palet
--------------------------

4.1 Approche du palet
~~~~~~~~~~~~~~~~~~~~~

Lorsque le robot détecte un objet, il passe en mode :

.. code-block:: java

   APPROACH_PUCK

Dans ce mode, le robot doit continuer à utiliser ses capteurs pour s’approcher de l’objet.

Exemple de logique :

.. code-block:: text

   Si l’objet est devant :
       avancer doucement

   Si l’objet est davantage détecté à gauche :
       courber vers la gauche

   Si l’objet est davantage détecté à droite :
       courber vers la droite

   Si l’objet n’est plus détecté :
       chercher à nouveau

Exemple de code :

.. code-block:: java

   if (objectInFront) {
     robot.motors().forward(APPROACH_SPEED);
   } else if (objectOnLeft) {
     robot.motors().curveLeft(APPROACH_SPEED, APPROACH_CURVE_FACTOR);
   } else if (objectOnRight) {
     robot.motors().curveRight(APPROACH_SPEED, APPROACH_CURVE_FACTOR);
   } else {
     mode = RobotMode.SEARCH;
   }


4.2 Confirmation du contact
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le robot ne doit pas ramasser un palet simplement parce qu’un capteur de distance a détecté un objet.

Il doit attendre un contact avec le capteur frontal :

.. code-block:: java

   robot.sensors().isFrontTouched()

Lorsque le ``TouchSensor`` est activé, le robot vérifie si l’objet touché correspond bien à un palet connu.

Pour cela, ``PuckManager`` peut être utilisé pour identifier le palet proche de la zone de contact.

La logique attendue est :

.. code-block:: text

   TouchSensor activé
   → vérifier si un palet est proche de l’avant du robot
   → si oui : lancer la prise
   → sinon : considérer que c’est un mur ou un obstacle


4.3 Séquence de prise
~~~~~~~~~~~~~~~~~~~~~

La séquence de prise se fait en plusieurs modes.


Mode ``LOWER_ARM``
^^^^^^^^^^^^^^^^^^

Le robot baisse le bras et prépare la pince.

.. code-block:: java

   robot.arm().lower();
   robot.gripper().open();


Mode ``CLOSE_GRIPPER``
^^^^^^^^^^^^^^^^^^^^^^

Le robot ferme la pince.

.. code-block:: java

   robot.gripper().close();

À ce moment, le palet peut être considéré comme attaché au robot.


Mode ``LIFT_ARM``
^^^^^^^^^^^^^^^^^

Le robot lève le bras.

.. code-block:: java

   robot.arm().lift();

Une fois le bras levé, le robot peut passer au mode de déplacement vers la base.


5. Déplacement vers la base
---------------------------

5.1 Définition de la base
~~~~~~~~~~~~~~~~~~~~~~~~~

Dans ce TP, la base correspond à une zone de dépôt.

Elle peut être située derrière une ligne blanche, à gauche ou à droite de la carte.

On peut définir deux lignes de dépôt :

.. code-block:: java

   private static final double LEFT_WHITE_LINE_X = -1.0;
   private static final double RIGHT_WHITE_LINE_X = 1.0;

Le robot déposera le palet légèrement derrière l’une de ces lignes :

.. code-block:: java

   private static final double DROP_OUTSIDE_OFFSET = 0.25;

Exemple :

.. code-block:: java

   targetDropX = RIGHT_WHITE_LINE_X + DROP_OUTSIDE_OFFSET;


5.2 Choix de la zone de dépôt
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Une stratégie simple consiste à choisir la base la plus proche du robot.

Exemple :

.. code-block:: java

   double distanceToLeftLine = Math.abs(robotX - LEFT_WHITE_LINE_X);
   double distanceToRightLine = Math.abs(robotX - RIGHT_WHITE_LINE_X);

   if (distanceToLeftLine < distanceToRightLine) {
     targetDropX = LEFT_WHITE_LINE_X - DROP_OUTSIDE_OFFSET;
   } else {
     targetDropX = RIGHT_WHITE_LINE_X + DROP_OUTSIDE_OFFSET;
   }

Le robot peut garder la même coordonnée ``Y``, mais limitée à une zone raisonnable :

.. code-block:: java

   targetDropY = MathUtils.clamp(robotY, DROP_MIN_Y, DROP_MAX_Y);


5.3 Navigation vers la base
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Après avoir ramassé un palet, le robot passe en mode :

.. code-block:: java

   GO_TO_DROP_ZONE

Dans ce mode, il doit avancer jusqu’à la zone de dépôt.

Il peut utiliser une logique simple basée sur l’angle entre sa position et la position de dépôt.

Exemple :

.. code-block:: java

   double dx = targetDropX - position[0];
   double dy = targetDropY - position[1];

   double targetAngle = Math.atan2(dy, dx);
   double robotAngle = MathUtils.getRobotYaw(robot.supervisor());
   double angleError = MathUtils.normalizeAngle(targetAngle - robotAngle);

Si l’erreur d’angle est trop grande, le robot tourne :

.. code-block:: java

   if (Math.abs(angleError) > DROP_ALIGNMENT_THRESHOLD) {
     if (angleError > 0.0) {
       robot.motors().turnLeft(TURN_SPEED);
     } else {
       robot.motors().turnRight(TURN_SPEED);
     }
   }

Sinon, il avance :

.. code-block:: java

   robot.motors().forward(GO_DROP_SPEED);


6. Dépôt du palet
-----------------

6.1 Séquence de dépôt
~~~~~~~~~~~~~~~~~~~~~

Lorsque le robot atteint la base, il passe en mode :

.. code-block:: java

   DROP_PUCK

La séquence de dépôt peut être la suivante :

#. arrêter le robot
#. ouvrir la pince
#. baisser légèrement le bras
#. placer le palet dans la zone de dépôt
#. marquer le palet comme livré

Exemple :

.. code-block:: java

   robot.motors().stop();
   robot.gripper().open();
   robot.arm().lower();

Puis le palet peut être déposé avec ``PuckManager``.

Exemple :

.. code-block:: java

   robot.pucks().dropPuck(currentPuckIndex, targetDropX, targetDropY, DROP_Z);


6.2 Retour en recherche
~~~~~~~~~~~~~~~~~~~~~~~

Après le dépôt, le robot ne doit pas reprendre immédiatement le palet qu’il vient de poser.

Il peut donc :

#. lever le bras
#. reculer
#. tourner légèrement
#. revenir en mode ``SEARCH``

Exemple de modes :

.. code-block:: text

   DROP_PUCK
   → LIFT_ARM_AFTER_DROP
   → BACK_AND_TURN_AFTER_DROP
   → SEARCH

Cela permet au robot de s’éloigner de la base et de reprendre sa recherche.


7. Gestion des modes du robot
-----------------------------

Pour ce TP, l’énumération ``RobotMode`` doit contenir les modes nécessaires à la mission complète.

Exemple :

.. code-block:: java

   public enum RobotMode {
     SEARCH,
     TOUCH_AVOID,
     APPROACH_PUCK,
     LOWER_ARM,
     CLOSE_GRIPPER,
     LIFT_ARM,
     GO_TO_DROP_ZONE,
     DROP_PUCK,
     LIFT_ARM_AFTER_DROP,
     BACK_AND_TURN_AFTER_DROP,
     FINISHED
   }

Chaque mode correspond à une étape précise.

.. list-table::
   :header-rows: 1

   * - Mode
     - Rôle
   * - ``SEARCH``
     - Chercher un palet avec les capteurs
   * - ``TOUCH_AVOID``
     - Gérer un contact non valide
   * - ``APPROACH_PUCK``
     - S’approcher de l’objet détecté
   * - ``LOWER_ARM``
     - Baisser le bras pour préparer la prise
   * - ``CLOSE_GRIPPER``
     - Fermer la pince
   * - ``LIFT_ARM``
     - Lever le bras avec le palet
   * - ``GO_TO_DROP_ZONE``
     - Aller jusqu’à la base
   * - ``DROP_PUCK``
     - Déposer le palet
   * - ``LIFT_ARM_AFTER_DROP``
     - Relever le bras après dépôt
   * - ``BACK_AND_TURN_AFTER_DROP``
     - S’éloigner de la base
   * - ``FINISHED``
     - Arrêter le robot


Exemple de structure dans ``update()``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   switch (mode) {
     case SEARCH:
       updateSearch();
       break;

     case APPROACH_PUCK:
       updateApproachPuck();
       break;

     case LOWER_ARM:
       updateLowerArm();
       break;

     case CLOSE_GRIPPER:
       updateCloseGripper();
       break;

     case LIFT_ARM:
       updateLiftArm();
       break;

     case GO_TO_DROP_ZONE:
       updateGoToDropZone();
       break;

     case DROP_PUCK:
       updateDropPuck();
       break;

     case LIFT_ARM_AFTER_DROP:
       updateLiftArmAfterDrop();
       break;

     case BACK_AND_TURN_AFTER_DROP:
       updateBackAndTurnAfterDrop();
       break;

     case FINISHED:
       updateFinished();
       break;
   }


8. Test et validation
---------------------

8.1 Lancer la simulation
~~~~~~~~~~~~~~~~~~~~~~~~

Lancez la simulation dans Webots avec le contrôleur :

.. code-block:: text

   FourWheelsCollisionAvoidanceAPI

Assurez-vous que le comportement utilisé est bien :

.. code-block:: java

   CollectPucksBehavior

Exemple :

.. code-block:: java

   TERBot robot = new TERBot();
   robot.setBehavior(new CollectPucksBehavior(robot));
   robot.run();


8.2 Critères de validation
~~~~~~~~~~~~~~~~~~~~~~~~~~

Votre travail est validé si :

-  le projet compile sans erreur
-  le robot démarre correctement
-  le bras se lève au lancement
-  la pince s’ouvre au lancement
-  le robot traverse la carte pour chercher un palet
-  le robot réagit lorsqu’un capteur détecte un objet
-  le robot s’approche d’un objet détecté
-  le robot déclenche la prise lorsqu’un contact valide est détecté
-  le bras descend
-  la pince se ferme
-  le bras se lève
-  le robot se dirige vers la base
-  le robot dépose le palet dans la base
-  le robot repart chercher un autre palet
-  le comportement peut se répéter


8.3 Messages utiles dans la console
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il est conseillé d’afficher des messages dans la console pour suivre le comportement.

Exemples :

.. code-block:: text

   Searching for puck.
   Object detected in front. Approaching.
   Puck confirmed. Starting pickup sequence.
   Puck collected. Going to drop zone.
   Drop zone reached. Dropping puck.
   Back to search mode after drop.

Ces messages permettent de vérifier dans quel mode se trouve le robot.


8.4 Commande de compilation
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Depuis le dossier du contrôleur, vous pouvez recompiler avec :

.. code-block:: bash

   ./controller.bat rebuild

Sous Windows PowerShell :

.. code-block:: powershell

   .\controller.bat rebuild

Après compilation, pensez à faire un **Reset Simulation** dans Webots.


9. Bilan du TP
--------------

Dans ce TP, vous avez mis en place une première version complète de la mission du robot.

Le robot est maintenant capable de :

-  parcourir la carte
-  rechercher un palet avec ses capteurs
-  s’approcher d’un objet détecté
-  confirmer un contact
-  récupérer un palet
-  transporter le palet
-  aller jusqu’à une base
-  déposer le palet
-  repartir chercher un autre palet

Vous avez également utilisé une organisation par modes avec ``RobotMode``.

Cette organisation permet de découper un comportement complexe en plusieurs étapes simples :

.. code-block:: text

   SEARCH
   → APPROACH_PUCK
   → LOWER_ARM
   → CLOSE_GRIPPER
   → LIFT_ARM
   → GO_TO_DROP_ZONE
   → DROP_PUCK
   → LIFT_ARM_AFTER_DROP
   → BACK_AND_TURN_AFTER_DROP
   → SEARCH

Cette structure rend le code plus clair, plus lisible et plus facile à améliorer.


Compétences travaillées
~~~~~~~~~~~~~~~~~~~~~~~

À la fin de ce TP, vous devez être capables de :

-  expliquer le fonctionnement général de l’algorithme de collecte
-  utiliser les capteurs pour chercher un objet
-  gérer une séquence de prise avec un bras et une pince
-  transporter un palet
-  définir une zone de dépôt
-  déplacer le robot vers une base
-  déposer un palet
-  gérer un comportement avec plusieurs modes
-  tester et corriger un comportement robotique complet


Pour la suite
~~~~~~~~~~~~~

Ce TP constitue une première version fonctionnelle de la mission.

Dans les prochains travaux, il sera possible d’améliorer :

-  la stratégie de recherche
-  l’évitement des murs
-  la fiabilité de l’approche
-  la détection des palets
-  la trajectoire vers la base
-  le dépôt de plusieurs palets
-  le comportement du robot lorsqu’il est bloqué
-  la coopération ou la compétition entre plusieurs robots