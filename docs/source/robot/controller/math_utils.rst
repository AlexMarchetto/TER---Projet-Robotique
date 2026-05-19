MathUtils
=========

Rôle de la classe
-----------------

La classe ``MathUtils`` est une classe utilitaire. Elle ne représente pas un composant physique du robot. Elle regroupe des fonctions mathématiques utilisées par plusieurs classes du contrôleur.

Responsabilités
---------------

- Normaliser un angle.
- Récupérer l'orientation du robot.
- Calculer une distance en deux dimensions.

Encapsulation
-------------

``MathUtils`` ne possède pas d'état interne. Son constructeur est privé afin d'empêcher la création d'objets ``MathUtils``. Les méthodes sont statiques et peuvent être appelées directement depuis la classe.

Relations avec les autres classes
---------------------------------

``MathUtils`` est utilisée par ``TERBot`` et ``PuckManager``.

Fonctions
---------

``normalizeAngle(double angle)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Normalise un angle afin qu'il reste compris entre ``-PI`` et ``PI``. Cette fonction est utile pour comparer deux orientations et calculer une erreur d'angle propre.

``getRobotYaw(Supervisor robot)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Retourne l'angle d'orientation du robot sur le plan horizontal. La méthode récupère la matrice d'orientation du robot avec ``getOrientation()``, puis utilise ``Math.atan2``.

``distance2D(double[] a, double[] b)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Calcule la distance entre deux points sur le plan horizontal en utilisant uniquement les coordonnées ``x`` et ``y``.

Formule utilisée :

.. code-block:: text

   distance = sqrt((b.x - a.x)^2 + (b.y - a.y)^2)
