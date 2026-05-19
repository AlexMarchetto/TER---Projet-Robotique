Controller du robot
===================

Cette partie documente l'architecture logicielle du contrôleur Webots du robot.

L'objectif de cette factorisation est de remplacer un unique fichier contenant toute la logique par plusieurs classes spécialisées. Chaque classe possède une responsabilité précise, ce qui rend le code plus lisible, plus maintenable et plus simple à faire évoluer.

Principe général
----------------

Le contrôleur repose principalement sur la composition.

La classe ``FourWheelsCollisionAvoidance`` est le point d'entrée du programme. Elle crée un objet ``TERBot``, qui représente le robot logiciel.

``TERBot`` possède ensuite plusieurs sous-systèmes :

- ``DriveBase`` pour le déplacement.
- ``RobotSensors`` pour les capteurs.
- ``Arm`` pour le bras et la pince.
- ``PuckManager`` pour la gestion des palets.
- ``RobotMode`` pour les états du robot.
- ``MathUtils`` pour les fonctions mathématiques utiles.

Il n'y a pas d'héritage métier entre les classes du projet. Le modèle utilise surtout des relations de possession, aussi appelées relations de composition.

.. toctree::
   :maxdepth: 1
   :caption: Classes

   four_wheels_collision_avoidance
   terbot
   robot_mode
   wheel
   drive_base
   robot_sensors
   arm
   puck_manager
   math_utils
   uml
