DriveBase
=========

Rôle de la classe
-----------------

La classe ``DriveBase`` représente la base roulante du robot. Elle regroupe les quatre roues du robot et permet de contrôler le déplacement avec une vitesse gauche et une vitesse droite.

Responsabilités
---------------

- Créer les quatre roues du robot.
- Associer les moteurs Webots aux objets ``Wheel``.
- Appliquer une vitesse aux roues gauches.
- Appliquer une vitesse aux roues droites.
- Arrêter le robot.

Encapsulation
-------------

Les roues sont stockées dans des attributs privés : ``frontLeft``, ``frontRight``, ``rearLeft`` et ``rearRight``. ``TERBot`` ne manipule donc pas directement les moteurs Webots.

Possession et composition
-------------------------

``DriveBase`` possède quatre objets ``Wheel``. Cette relation correspond à une composition : la base roulante est composée de roues.

Relations avec les autres classes
---------------------------------

``DriveBase`` est possédée par ``TERBot``. Elle utilise ``Wheel`` et dépend du ``Supervisor`` Webots pour récupérer les moteurs par leur nom.

Fonctions
---------

``DriveBase(Supervisor robot)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Constructeur de la classe. Il récupère les moteurs ``wheel1``, ``wheel2``, ``wheel3`` et ``wheel4`` depuis Webots, puis crée les quatre objets ``Wheel`` correspondants.

``setSpeed(double leftSpeed, double rightSpeed)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Applique les vitesses aux roues. ``leftSpeed`` est appliquée aux roues gauches, et ``rightSpeed`` aux roues droites. Cette méthode permet d'avancer, reculer ou tourner selon la différence entre les deux vitesses.

``stop()``
~~~~~~~~~~

Arrête la base roulante en appelant ``setSpeed(0.0, 0.0)``.
