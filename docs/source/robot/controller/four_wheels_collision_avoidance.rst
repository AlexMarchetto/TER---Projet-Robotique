FourWheelsCollisionAvoidance
============================

Rôle de la classe
-----------------

La classe ``FourWheelsCollisionAvoidance`` est la classe principale du contrôleur Webots. C'est le point d'entrée du programme. Webots lance cette classe lorsque le robot utilise le contrôleur ``FourWheelsCollisionAvoidance``.

Son rôle est volontairement limité. Elle ne contient pas la logique complète du robot. Elle crée simplement un objet ``Supervisor``, puis crée un objet ``TERBot`` et lance sa méthode ``run()``.

Responsabilités
---------------

- Démarrer le contrôleur Webots.
- Créer l'objet ``Supervisor``.
- Créer l'objet principal ``TERBot``.
- Lancer la boucle principale du robot avec ``run()``.

Encapsulation
-------------

Cette classe ne contient presque pas d'état interne. Elle sert uniquement de point d'entrée. La logique du robot est déléguée à la classe ``TERBot``. Cela évite d'avoir un fichier principal trop long et difficile à maintenir.

Relations avec les autres classes
---------------------------------

``FourWheelsCollisionAvoidance`` dépend de ``Supervisor``, fourni par l'API Webots, et de ``TERBot``, qui contient la logique principale du robot. Il s'agit d'une relation de création : la classe principale crée une instance de ``TERBot``.

Fonctions
---------

``main(String[] args)``
~~~~~~~~~~~~~~~~~~~~~~

Point d'entrée du programme Java. Cette méthode est appelée automatiquement au lancement du contrôleur par Webots.

Elle effectue les actions suivantes :

1. Création d'un objet ``Supervisor``.
2. Création d'un objet ``TERBot``.
3. Appel de la méthode ``run()`` du robot.

Exemple :

.. code-block:: java

   Supervisor supervisor = new Supervisor();
   TERBot robot = new TERBot(supervisor);
   robot.run();

Cette méthode ne contient pas directement la logique de déplacement, de détection ou de ramassage.
