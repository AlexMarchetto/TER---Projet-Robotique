TERBot
======

Rôle de la classe
-----------------

La classe ``TERBot`` est la classe centrale du contrôleur. Elle représente le robot du point de vue logiciel et coordonne tous les sous-systèmes du robot. C'est dans cette classe que se trouve la boucle principale du programme ainsi que la machine à états du robot.

Responsabilités
---------------

- Gérer la boucle principale du robot.
- Lire les informations provenant des capteurs.
- Décider de l'action à effectuer selon l'état courant.
- Commander les roues via ``DriveBase``.
- Commander le bras et la pince via ``Arm``.
- Gérer les palets via ``PuckManager``.
- Gérer les transitions entre les différents états du robot.
- Appliquer les comportements de recherche, d'approche, d'évitement, de ramassage et de dépôt.

Encapsulation
-------------

La classe ``TERBot`` encapsule la logique globale du robot. Ses attributs principaux sont privés : ``supervisor``, ``driveBase``, ``sensors``, ``arm``, ``puckManager``, ``mode``, ``currentPuckIndex`` et ``puckAttached``.

La seule méthode publique importante est ``run()``. Les autres méthodes sont privées, car elles correspondent à des comportements internes du robot.

Possession et composition
-------------------------

``TERBot`` possède un ``DriveBase``, un ``RobotSensors``, un ``Arm`` et un ``PuckManager``. Cette relation correspond à de la composition. Le robot logiciel est composé de plusieurs sous-systèmes spécialisés.

Machine à états
---------------

Les principaux états sont : ``SEARCH``, ``APPROACH_PUCK``, ``LOWER_ARM``, ``CLOSE_GRIPPER``, ``LIFT_ARM``, ``GO_TO_DROP_ZONE``, ``DROP_PUCK``, ``TOUCH_AVOID``, ``LIFT_ARM_AFTER_DROP`` et ``BACK_AND_TURN_AFTER_DROP``.

Fonctions
---------

``TERBot(Supervisor supervisor)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Constructeur de la classe. Il initialise le robot logiciel à partir du ``Supervisor`` Webots. Il stocke le ``Supervisor``, récupère le pas de temps de Webots, crée ``DriveBase``, ``RobotSensors``, ``Arm`` et ``PuckManager``, puis définit la liste des palets à gérer.

``run()``
~~~~~~~~~

Lance la boucle principale du robot. Cette méthode appelle ``supervisor.step(timeStep)`` à chaque itération. Tant que la simulation est active, le robot continue à exécuter sa logique. Si un palet est attaché, sa position est mise à jour pour suivre le robot, puis ``update()`` est appelée.

``update()``
~~~~~~~~~~~~

Met à jour le comportement du robot à chaque pas de simulation. Cette méthode lit l'état du capteur de contact, gère les contacts, exécute le comportement correspondant au mode courant, applique les vitesses aux roues, met à jour l'état précédent du contact et affiche les informations de debug.

``handleContact(boolean touched)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Gère les contacts détectés par le capteur avant. La méthode distingue un contact avec un palet proche et un contact avec un obstacle. Si le contact correspond à un palet, le robot passe en mode ``LOWER_ARM``. Sinon, il passe en mode ``TOUCH_AVOID``.

``updateSearch()``
~~~~~~~~~~~~~~~~~~

Gère le comportement de recherche d'un palet. Le robot avance, surveille les capteurs latéraux et avant, évite les murs et cherche le palet disponible le plus proche. Si un palet est détecté, le robot passe en mode ``APPROACH_PUCK``. La méthode retourne les vitesses gauche et droite.

``updateTouchAvoid()``
~~~~~~~~~~~~~~~~~~~~~~

Gère l'évitement après un contact avec un obstacle. Le robot recule, puis tourne. Une fois la séquence terminée, il revient en mode ``SEARCH``.

``updateApproachPuck()``
~~~~~~~~~~~~~~~~~~~~~~~~

Gère l'approche vers le palet ciblé. La méthode calcule la direction vers le palet, l'orientation actuelle du robot et l'erreur d'angle. Elle applique ensuite une correction de trajectoire. Si le palet est perdu ou si l'approche dure trop longtemps, le robot revient en mode ``SEARCH``.

``updateGoToDropZone(boolean touched)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Gère le déplacement vers la zone de dépôt. La méthode oriente le robot vers la position de dépôt, puis le fait avancer. Si le robot touche la zone de dépôt avec un palet attaché, il passe en mode ``DROP_PUCK``.

``updateBackAndTurnAfterDrop()``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Gère le comportement après dépôt. Le robot recule, puis tourne, avant de revenir en mode ``SEARCH``.

``resetSearch()``
~~~~~~~~~~~~~~~~~

Réinitialise l'état interne du robot pour revenir en mode recherche. Elle remet à zéro le mode, les compteurs, le palet courant et les indicateurs de détection.

``printDebug(boolean touched)``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Affiche dans la console les informations utiles au debug : mode courant, valeurs des capteurs, couleur moyenne, état du contact, palet attaché, palet courant et compteur de blocage.
