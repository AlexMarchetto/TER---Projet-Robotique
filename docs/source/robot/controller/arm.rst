Arm
===

Rôle de la classe
-----------------

La classe ``Arm`` représente le bras et la pince du robot. Elle regroupe les moteurs utilisés pour déplacer le bras et ouvrir ou fermer la pince.

Responsabilités
---------------

- Récupérer le moteur du bras.
- Récupérer les deux moteurs de la pince.
- Récupérer le capteur de position du bras.
- Lever le bras.
- Baisser le bras.
- Ouvrir la pince.
- Fermer la pince.

Encapsulation
-------------

Les moteurs et le capteur sont privés : ``armMotor``, ``gripperLeftMotor``, ``gripperRightMotor`` et ``armSensor``. Les positions du bras et de la pince sont stockées dans des constantes privées.

Relations avec les autres classes
---------------------------------

``Arm`` est possédée par ``TERBot``. Elle dépend de ``Motor``, ``PositionSensor`` et ``Supervisor``.

Fonctions
---------

``Arm(Supervisor robot, int timeStep)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Constructeur. Il récupère ``arm_motor``, ``gripper_left_motor``, ``gripper_right_motor`` et ``arm_sensor``. Si le capteur de position existe, il est activé. Le constructeur place ensuite le bras en position levée et ouvre la pince.

``lift()``
~~~~~~~~~~

Lève le bras en plaçant le moteur du bras à la position ``ARM_UP``.

``lower()``
~~~~~~~~~~~

Baisse le bras en plaçant le moteur du bras à la position ``ARM_DOWN``.

``openGripper()``
~~~~~~~~~~~~~~~~~

Ouvre la pince en appliquant une position d'ouverture au moteur gauche et au moteur droit.

``closeGripper()``
~~~~~~~~~~~~~~~~~~

Ferme la pince en appliquant une position de fermeture au moteur gauche et au moteur droit.
