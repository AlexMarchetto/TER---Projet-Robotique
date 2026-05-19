RobotMode
=========

Rôle de la classe
-----------------

``RobotMode`` est une énumération Java. Elle définit les différents états possibles du robot et permet de représenter proprement la machine à états utilisée par ``TERBot``.

Responsabilités
---------------

- Lister les états possibles du robot.
- Rendre la machine à états plus claire.
- Remplacer les chaînes de caractères par des valeurs contrôlées.
- Faciliter la lecture du code.

Encapsulation
-------------

L'énumération ne possède pas d'attribut interne. Elle sert uniquement à regrouper les valeurs possibles du mode du robot.

Relations avec les autres classes
---------------------------------

``RobotMode`` est utilisée par ``TERBot`` via l'attribut ``mode``.

Valeurs
-------

``SEARCH`` : recherche d'un palet.

``TOUCH_AVOID`` : évitement après contact avec un obstacle.

``APPROACH_PUCK`` : approche d'un palet ciblé.

``LOWER_ARM`` : abaissement du bras.

``CLOSE_GRIPPER`` : fermeture de la pince.

``LIFT_ARM`` : levée du bras après saisie.

``GO_TO_DROP_ZONE`` : déplacement vers la zone de dépôt.

``DROP_PUCK`` : dépôt du palet.

``LIFT_ARM_AFTER_DROP`` : remontée du bras après dépôt.

``BACK_AND_TURN_AFTER_DROP`` : recul et rotation après dépôt.
