RobotSensors
============

Rôle de la classe
-----------------

La classe ``RobotSensors`` regroupe tous les capteurs utilisés par le robot. Elle centralise l'accès aux capteurs Webots afin que ``TERBot`` ne manipule pas directement les objets ``DistanceSensor``, ``Camera`` ou ``TouchSensor``.

Responsabilités
---------------

- Récupérer les capteurs Webots.
- Activer les capteurs avec le pas de temps de la simulation.
- Lire les distances à gauche, à droite et devant.
- Lire l'état du capteur de contact.
- Lire la couleur moyenne de la caméra.
- Détecter si la couleur observée est rouge.

Encapsulation
-------------

Les capteurs sont stockés dans des attributs privés : ``dsRight``, ``dsLeft``, ``dsFront``, ``colorSensor`` et ``touchFront``.

Relations avec les autres classes
---------------------------------

``RobotSensors`` est possédée par ``TERBot``. Elle dépend de ``DistanceSensor``, ``Camera``, ``TouchSensor`` et ``Supervisor``.

Fonctions
---------

``RobotSensors(Supervisor robot, int timeStep)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Constructeur. Il récupère les capteurs ``ds_right``, ``ds_left``, ``ds_front``, ``color_sensor`` et ``touch_front``. Chaque capteur trouvé est activé avec ``enable(timeStep)``.

``getRightDistance()``
~~~~~~~~~~~~~~~~~~~~~~

Retourne la valeur du capteur de distance droit. Si le capteur n'existe pas, retourne ``0.0``.

``getLeftDistance()``
~~~~~~~~~~~~~~~~~~~~~

Retourne la valeur du capteur de distance gauche. Si le capteur n'existe pas, retourne ``0.0``.

``getFrontDistance()``
~~~~~~~~~~~~~~~~~~~~~~

Retourne la valeur du capteur de distance avant. Ce capteur est utilisé pour détecter un objet ou un palet devant le robot.

``isTouched()``
~~~~~~~~~~~~~~~

Retourne ``true`` si le capteur de contact avant détecte une collision.

``getAverageColor()``
~~~~~~~~~~~~~~~~~~~~~

Calcule la couleur moyenne vue par la caméra. La méthode retourne un tableau ``{red, green, blue}``. Si la caméra n'existe pas ou si l'image n'est pas valide, elle retourne ``{0, 0, 0}``.

``isRedDetected()``
~~~~~~~~~~~~~~~~~~~

Indique si la couleur moyenne détectée correspond à du rouge. La méthode vérifie que le rouge est supérieur à 150, le vert inférieur à 100 et le bleu inférieur à 100.
