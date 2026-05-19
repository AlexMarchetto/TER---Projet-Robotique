Wheel
=====

Rôle de la classe
-----------------

La classe ``Wheel`` représente une roue motorisée du robot. Elle encapsule un objet ``Motor`` fourni par Webots.

Responsabilités
---------------

- Stocker un moteur Webots.
- Configurer le moteur en rotation continue.
- Appliquer une vitesse à la roue.
- Arrêter la roue.

Encapsulation
-------------

La classe encapsule l'objet Webots ``Motor`` dans un attribut privé :

.. code-block:: java

   private final Motor motor;

Les autres classes ne manipulent pas directement le moteur Webots. Elles utilisent les méthodes publiques de ``Wheel``.

Relations avec les autres classes
---------------------------------

``Wheel`` est utilisée par ``DriveBase``. ``DriveBase`` possède quatre objets ``Wheel``. ``Wheel`` dépend aussi de la classe Webots ``Motor``.

Fonctions
---------

``Wheel(Motor motor)``
~~~~~~~~~~~~~~~~~~~~~~

Constructeur de la classe. Il reçoit un objet ``Motor`` Webots et le stocke. Si le moteur existe, il est configuré en rotation continue avec ``setPosition(Double.POSITIVE_INFINITY)`` et sa vitesse est initialisée à ``0.0``.

``setVelocity(double velocity)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Applique une vitesse à la roue. La méthode vérifie que le moteur n'est pas ``null`` avant d'appeler ``setVelocity``.

``stop()``
~~~~~~~~~~

Arrête la roue en appelant ``setVelocity(0.0)``.
