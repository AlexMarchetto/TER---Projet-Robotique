PuckManager
===========

Rôle de la classe
-----------------

La classe ``PuckManager`` gère les palets présents dans la scène Webots. Elle centralise toutes les actions liées aux palets : recherche, calcul de distance, attachement au robot, dépôt et suivi des palets livrés.

Responsabilités
---------------

- Récupérer les palets à partir de leurs noms DEF.
- Stocker les objets ``Node`` des palets.
- Stocker les champs ``translation`` des palets.
- Savoir si un palet a déjà été livré.
- Trouver le palet disponible le plus proche du robot.
- Calculer la distance entre le robot et un palet.
- Faire suivre un palet par le robot lorsqu'il est transporté.
- Déposer un palet à une position donnée.

Encapsulation
-------------

Les informations sur les palets sont stockées dans des attributs privés : ``puckNames``, ``puckNodes``, ``puckTranslationFields`` et ``puckDelivered``. ``TERBot`` ne manipule pas directement ces tableaux.

Relations avec les autres classes
---------------------------------

``PuckManager`` est possédée par ``TERBot``. Elle utilise ``Supervisor``, ``Node``, ``Field`` et ``MathUtils``.

Fonctions
---------

``PuckManager(Supervisor robot, String[] puckNames)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Constructeur. Il reçoit le ``Supervisor`` Webots et la liste des noms des palets. Pour chaque palet, il cherche le noeud correspondant avec ``getFromDef``. Si le palet est trouvé, il récupère son champ ``translation`` et l'initialise comme non livré.

``findNearestAvailablePuck()``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Recherche le palet disponible le plus proche du robot. La méthode ignore les palets inexistants et les palets déjà livrés. Elle retourne l'indice du palet trouvé, ou ``-1`` si aucun palet disponible n'est trouvé.

``getPuckNode(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~

Retourne le ``Node`` Webots d'un palet à partir de son indice. Si l'indice est invalide, retourne ``null``.

``getDistanceToPuck(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Calcule la distance entre le robot et un palet. Si le palet n'existe pas, retourne ``Double.MAX_VALUE``.

``isDelivered(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~

Indique si un palet a déjà été livré. Si l'indice est invalide, retourne ``true`` afin de considérer le palet comme non utilisable.

``getPuckName(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~

Retourne le nom d'un palet à partir de son indice. Si l'indice est invalide, retourne ``unknown``.

``attachPuckToRobot(int index)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Place le palet devant le robot pour simuler son transport. La méthode calcule une position locale devant le robot, la convertit en position dans le monde, modifie le champ ``translation`` du palet puis appelle ``resetPhysics``.

``dropPuck(int index, double dropX, double dropY, double dropZ)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dépose un palet à une position donnée. La méthode marque le palet comme livré, modifie sa position, réinitialise sa physique et affiche un message de debug.
