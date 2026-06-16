Projet robotique sur Webots — TP5
=================================

Course de robots et amélioration du comportement
------------------------------------------------

Sommaire
--------

-  `0. Informations et objectifs <#0-informations-et-objectifs>`__

   -  `0.1 Contexte du TP <#01-contexte-du-tp>`__
   -  `0.2 Objectif de la séance <#02-objectif-de-la-séance>`__

-  `1. Principe de la course <#1-principe-de-la-course>`__
-  `2. Préparation du monde Webots <#2-préparation-du-monde-webots>`__
-  `3. Amélioration du robot <#3-amélioration-du-robot>`__
-  `4. Déroulement de la course <#4-déroulement-de-la-course>`__
-  `5. Critères d’évaluation <#5-critères-dévaluation>`__
-  `6. Bilan du TP <#6-bilan-du-tp>`__


0. Informations et objectifs
----------------------------

0.1 Contexte du TP
~~~~~~~~~~~~~~~~~~

Dans les TP précédents, vous avez construit un robot dans Webots, complété son API Java et mis en place un comportement de recherche, de récupération et de dépôt des palets.

Dans ce TP5, l’objectif est différent. Il ne s’agit plus seulement de compléter une partie précise du code. Vous devez maintenant améliorer votre robot afin qu’il soit le plus efficace possible dans une situation de course.

La course oppose deux robots placés sur deux plateformes différentes. Chaque robot doit récupérer des palets et les déposer dans sa zone de dépôt le plus rapidement possible.


0.2 Objectif de la séance
~~~~~~~~~~~~~~~~~~~~~~~~~

À la fin de ce TP, vous devez être capables de :

-  améliorer le comportement de votre robot
-  tester plusieurs stratégies de déplacement
-  rendre la recherche des palets plus efficace
-  améliorer la récupération et le dépôt des palets
-  comparer les performances de deux robots
-  adapter le code pour obtenir le meilleur résultat possible


1. Principe de la course
------------------------

Le TP5 consiste à organiser une course entre deux robots.

Chaque robot possède sa propre plateforme. Les deux plateformes doivent être placées dans le même monde Webots, afin de pouvoir lancer les deux robots en même temps.

Le principe est le suivant :

.. code-block:: text

   Robot 1 → Plateforme 1 → Palets à récupérer → Zone de dépôt 1

   Robot 2 → Plateforme 2 → Palets à récupérer → Zone de dépôt 2

Chaque robot doit essayer de récupérer et déposer ses palets le plus rapidement possible.

Le gagnant peut être déterminé selon plusieurs critères :

-  le premier robot à déposer tous ses palets
-  le robot qui dépose le plus de palets dans un temps limité
-  le robot qui réalise la mission avec le moins d’erreurs
-  le robot qui a le comportement le plus stable


2. Préparation du monde Webots
------------------------------

Pour ce TP, le monde Webots doit contenir deux plateformes.

Chaque plateforme doit contenir :

-  un robot
-  plusieurs palets
-  une zone de dépôt
-  les lignes ou repères nécessaires à la navigation

Exemple d’organisation possible :

.. code-block:: text

   Monde Webots
   ├── Plateforme du robot 1
   │   ├── Robot 1
   │   ├── Palets du robot 1
   │   └── Zone de dépôt du robot 1
   │
   └── Plateforme du robot 2
       ├── Robot 2
       ├── Palets du robot 2
       └── Zone de dépôt du robot 2

Les deux robots doivent avoir des noms différents dans Webots afin d’éviter les conflits.

Il faut aussi faire attention aux noms des palets. Par exemple :

.. code-block:: text

   PALET_R1_1
   PALET_R1_2
   PALET_R1_3

   PALET_R2_1
   PALET_R2_2
   PALET_R2_3

Cela permet de distinguer les palets du robot 1 et ceux du robot 2.


3. Amélioration du robot
------------------------

Dans ce TP, vous êtes libres d’améliorer votre robot comme vous le souhaitez.

Vous pouvez modifier :

-  la stratégie de recherche
-  les vitesses du robot
-  les seuils des capteurs
-  la manière d’éviter les murs
-  la manière de s’approcher d’un palet
-  la séquence de prise
-  la trajectoire vers la zone de dépôt
-  le comportement après le dépôt

L’objectif est de rendre le robot plus rapide, plus fiable et plus efficace.


Exemples d’améliorations possibles
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Vous pouvez par exemple :

-  augmenter légèrement la vitesse de recherche
-  réduire les temps d’attente entre les modes
-  améliorer l’évitement des murs
-  mieux utiliser les capteurs latéraux
-  améliorer la détection des palets
-  choisir une meilleure zone de dépôt
-  éviter que le robot reste bloqué contre un mur
-  éviter que le robot reprenne un palet déjà déposé
-  optimiser le retour en mode recherche après un dépôt


4. Déroulement de la course
---------------------------

Avant de lancer la course, chaque groupe doit vérifier que son robot fonctionne correctement.

Le robot doit être capable de :

-  démarrer correctement
-  chercher un palet
-  détecter un objet
-  récupérer un palet
-  aller jusqu’à la zone de dépôt
-  déposer le palet
-  repartir chercher un autre palet

Une fois les deux robots prêts, la simulation peut être lancée.

La course commence lorsque les deux robots démarrent.

Elle se termine lorsque :

-  un robot a déposé tous ses palets
-  ou lorsque le temps limite est atteint


5. Critères d’évaluation
------------------------

L’évaluation peut prendre en compte plusieurs éléments.

.. list-table::
   :header-rows: 1

   * - Critère
     - Description
   * - Fonctionnement général
     - Le robot réalise bien la mission demandée
   * - Rapidité
     - Le robot récupère et dépose les palets rapidement
   * - Fiabilité
     - Le robot évite les blocages et les comportements imprévus
   * - Qualité du code
     - Le code reste lisible, organisé et compréhensible
   * - Stratégie
     - Le comportement choisi est cohérent et efficace
   * - Améliorations
     - Le robot a été réellement optimisé par rapport au TP4

Le but n’est pas seulement d’avoir le robot le plus rapide.

Il faut aussi que le comportement soit stable et compréhensible.


6. Bilan du TP
--------------

Ce dernier TP permet de réutiliser l’ensemble des notions vues dans les séances précédentes.

Vous devez mobiliser :

-  la structure du robot créée dans Webots
-  l’API Java des moteurs, capteurs, bras et pince
-  la gestion des palets
-  les modes du robot
-  la logique de recherche, de prise et de dépôt
-  les tests dans Webots

Ce TP vous permet aussi d’expérimenter davantage. Vous pouvez tester plusieurs stratégies et comparer leurs résultats.

L’objectif final est d’obtenir un robot plus performant, capable de participer à une course contre un autre robot dans un même monde Webots.