Projet robotique sur Webots — TP1
================================= 

Finalisation du robot et prise en main
--------------------------------------

   **Objectif général :** découvrir Webots, comprendre la structure d’un robot dans l’arborescence, ajouter des composants depuis l’interface graphique, puis vérifier le robot avec un contrôleur de validation.

Sommaire
--------

-  `I. Informations et objectifs <#i-informations-et-objectifs>`__
-  `II. Fonctionnement de Webots <#ii-fonctionnement-de-webots>`__
-  `III. Mise en place du projet <#iii-mise-en-place-du-projet>`__
-  `IV. Manipulations depuis l’interface graphique de Webots <#iv-manipulations-depuis-linterface-graphique-de-webots>`__
-  `V. Ajout des roues du robot depuis l’interface graphique <#v-ajout-des-roues-du-robot-depuis-linterface-graphique>`__
-  `VI. Ajout des capteurs de distance latéraux <#vi-ajout-des-capteurs-de-distance-latéraux>`__
-  `VII. Complétion de la pince <#vii-complétion-de-la-pince>`__
-  `VIII. Comprendre le rôle du contrôleur <#viii-comprendre-le-rôle-du-contrôleur>`__
-  `IX. Questions de compréhension finales <#ix-questions-de-compréhension-finales>`__
-  `X. Validation attendue <#x-validation-attendue>`__
-  `XI. Annexes <#xi-annexes>`__

Chemins utiles du projet
------------------------

Ces chemins sont donnés depuis la racine du dépôt Git.

.. list-table::
   :header-rows: 1

   * - Élément
     - Chemin
   * - Monde principal
     - ``src/worlds/main.wbt``
   * - Contrôleur de validation
     - ``src/controllers/TP1ValidationController/TP1ValidationController.java``
   * - Dossier des contrôleurs
     - ``src/controllers/``
   * - Dossier des mondes Webots
     - ``src/worlds/``
   * - Dossier des PROTO
     - ``src/protos/``
   * - Documentation du projet
     - `Read the Docs <https://ter-projet-robotique.readthedocs.io/en/latest/index.html>`__

I. Informations et objectifs
----------------------------

1. Contexte général du projet
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Ce projet consiste en la réalisation et la programmation d’un robot à l’aide du logiciel **Webots**. Webots est un simulateur robotique qui permet de créer, tester et observer le comportement d’un robot dans un environnement virtuel.

Grâce à ce logiciel, il est possible de manipuler un robot sans avoir besoin de matériel physique réel, tout en conservant une logique proche de la robotique réelle.

Dans ce projet, vous devrez progressivement prendre en main le robot, comprendre ses différents composants, puis programmer ses actions. Le robot sera équipé de plusieurs éléments importants :

- des roues pour se déplacer
- des capteurs pour percevoir son environnement
- une pince pour attraper puis déposer des objets appelés **palets**

L’objectif global du projet est de rendre le robot capable d’accomplir une mission complète de manière autonome :

#. parcourir une zone de jeu
#. rechercher des palets
#. les attraper avec la pince
#. les transporter
#. les déposer dans une base prévue à cet effet
#. recommencer jusqu’à avoir traité plusieurs palets

Le projet est découpé en plusieurs travaux pratiques afin de construire progressivement les compétences nécessaires.

À la fin du projet, vous devrez être capables de proposer un programme fonctionnel permettant au robot de réaliser une mission complète dans l’environnement simulé. Le but n’est pas seulement d’obtenir un robot qui fonctionne, mais aussi de comprendre les choix effectués, d’expliquer l’algorithme utilisé et d’améliorer progressivement la solution proposée.

2. Contexte du TP
~~~~~~~~~~~~~~~~~

Dans ce TP, vous allez commencer à manipuler le robot et son environnement logiciel.

Le robot est déjà présent dans le monde Webots, mais il est volontairement incomplet. Certains éléments doivent encore être ajoutés ou vérifiés directement depuis l’interface graphique de Webots.

Dans ce premier TP, le robot ne sera pas encore totalement autonome. L’objectif est d’abord de comprendre :

- l’environnement de travail
- la structure d’un robot dans Webots
- l’organisation des nœuds
- la manière d’ajouter ou de modifier des composants depuis l’interface

Les modifications ne seront pas réalisées directement dans le code d’un fichier ``PROTO``. Elles seront effectuées depuis l’arborescence Webots, en utilisant les champs disponibles dans l’interface graphique.

.. important::

   Même si vous modifiez le robot depuis l’interface graphique, les modifications sont enregistrées dans les fichiers du projet.

3. Matériel et outils nécessaires
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Pour réaliser cette suite de travaux pratiques, vous devez avoir les éléments suivants :

- un ordinateur
- le logiciel Webots
- Visual Studio Code
- la documentation Webots
- la documentation Java

Liens utiles
~~~~~~~~~~~~

- `Webots <https://www.cyberbotics.com/>`__
- `Visual Studio Code <https://code.visualstudio.com/>`__
- `Documentation Webots <https://cyberbotics.com/doc/reference/index>`__
- `Documentation Java <https://docs.oracle.com/en/java/>`__
- `Documentation du projet <https://ter-projet-robotique.readthedocs.io/en/latest/index.html>`__

4. Objectifs de la séance
~~~~~~~~~~~~~~~~~~~~~~~~~

À la fin de ce TP, vous serez capables de :

- identifier les principaux éléments du robot
- comprendre l’organisation d’un robot dans Webots
- ajouter des composants depuis l’interface graphique
- modifier les champs d’un nœud Webots
- ajouter des roues, des capteurs et une pince
- comprendre pourquoi les noms des moteurs et capteurs sont importants
- utiliser un contrôleur de validation simple

À la fin du TP, vous devrez également être capables de rédiger un court compte rendu présentant vos réponses, vos observations et les difficultés rencontrées pendant la séance.

II. Fonctionnement de Webots
----------------------------

Avant de commencer à programmer le robot, il est important de comprendre l’organisation générale de Webots. Webots est un logiciel de simulation robotique. Il permet de créer un environnement virtuel, d’y placer un robot, puis de programmer son comportement à l’aide d’un contrôleur.

L’interface de Webots est composée de plusieurs zones principales. Chacune a un rôle précis dans la création, la visualisation et la programmation du projet.

5. Arborescence du monde
~~~~~~~~~~~~~~~~~~~~~~~~

Sur la partie gauche de l’interface se trouve l’arborescence du monde. Elle contient l’ensemble des éléments présents dans la simulation.

Chaque élément est représenté sous forme de **nœud**. Un nœud correspond à un objet ou à un composant de la scène.

Dans le projet, on peut retrouver :

.. list-table::
   :header-rows: 1

   * - Nœud
     - Rôle
   * - ``WorldInfo``
     - Contient les informations générales du monde.
   * - ``Viewpoint``
     - Définit la position de la caméra.
   * - ``TexturedBackground``
     - Gère l’arrière-plan.
   * - ``TexturedBackgroundLight``
     - Gère l’éclairage.
   * - ``Platform``
     - Correspond à la plateforme.
   * - ``Robot``
     - Correspond au robot utilisé dans ce TP.

L’arborescence permet de visualiser la structure complète du monde simulé. Elle sert aussi à sélectionner un élément pour consulter ou modifier ses propriétés.

.. figure:: ../images/tp1/arborescence_webots.png
   :alt: Arborescence générale du monde Webots

   Figure 1 — Arborescence générale du monde Webots.

6. Zone de visualisation et de simulation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Au centre de l’interface se trouve la zone de visualisation. C’est dans cette partie que vous pouvez observer la simulation en temps réel.

Cette zone permet de voir :

- le monde simulé
- la plateforme de jeu
- le robot
- les obstacles ou objets présents dans l’environnement
- les déplacements du robot pendant l’exécution du programme

C’est également dans cette zone que vous pouvez vérifier si le comportement du robot correspond à ce qui était attendu.

.. figure:: ../images/tp1/zone_centrale_webots.png
   :alt: Zone centrale de Webots

   Figure 2 — Zone centrale de Webots avec le robot sur la plateforme.

7. Contrôleur du robot
~~~~~~~~~~~~~~~~~~~~~~

Le contrôleur est le programme qui donne des instructions au robot.

C’est dans ce fichier que vous écrirez le code permettant au robot de réaliser différentes actions, comme :

- faire tourner les roues
- avancer ou reculer
- lire les valeurs des capteurs
- ouvrir ou fermer la pince
- prendre une décision selon ce que le robot détecte
- enchaîner plusieurs actions pour réaliser une mission complète

Dans ce TP, le contrôleur utilisé sert principalement à vérifier que les éléments attendus ont bien été ajoutés au robot.

Le contrôleur de validation se trouve ici :

``src/controllers/TP1ValidationController/TP1ValidationController.java``

8. Le rôle des nœuds
~~~~~~~~~~~~~~~~~~~~

Dans Webots, tous les éléments de la simulation sont décrits à l’aide de nœuds. Un nœud peut représenter un objet simple, comme une plateforme, mais aussi un élément plus complexe, comme un robot complet.

Chaque nœud possède des champs, c’est-à-dire des paramètres modifiables. Ces champs permettent par exemple de changer :

- la position d’un objet
- sa rotation
- sa taille
- sa couleur
- sa forme
- ses propriétés physiques
- le contrôleur associé à un robot
- le nom d’un moteur ou d’un capteur

Par exemple, le nœud du robot contient un champ indiquant quel contrôleur doit être utilisé. Lorsque la simulation démarre, Webots sait ainsi quel programme lancer pour piloter le robot.

9. Organisation des fichiers du projet
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Un projet Webots est généralement organisé en plusieurs dossiers.

.. code-block:: text

   TER---Projet-Robotique/
   ├── controllers/
   ├── protos/
   └── worlds/

.. list-table::
   :header-rows: 1

   * - Dossier
     - Rôle
   * - ``worlds/``
     - Contient les fichiers de monde Webots, avec l’extension ``.wbt``.
   * - ``controllers/``
     - Contient les programmes qui contrôlent les robots.
   * - ``protos/``
     - Contient les modèles réutilisables Webots.

Dans ce TP, le robot à modifier est directement présent dans le monde Webots. Vous allez donc agir principalement depuis l’interface graphique.

III. Mise en place du projet
----------------------------

10. Récupération du projet
~~~~~~~~~~~~~~~~~~~~~~~~~~

Pour commencer, vous devez télécharger le projet depuis le dépôt Git fourni.

Ouvrez un terminal, placez-vous dans le dossier où vous souhaitez enregistrer le projet, puis exécutez la commande suivante :

.. code-block:: bash

   git clone https://github.com/AlexMarchetto/TER---Projet-Robotique.git

.. note::

   L’adresse du dépôt pourra être modifiée si le projet est déplacé vers un autre dépôt Git.

11. Ouverture du dossier
~~~~~~~~~~~~~~~~~~~~~~~~

Placez-vous ensuite dans le dossier du projet avec la commande :

.. code-block:: bash

   cd TER---Projet-Robotique

Vous pouvez vérifier que le dossier contient bien les éléments principaux du projet :

.. code-block:: text

   worlds/
   controllers/
   protos/

.. figure:: ../images/tp1/vscode_projet_ouvert.png
   :alt: Projet ouvert dans Visual Studio Code

   Figure 8 — Projet ouvert dans Visual Studio Code avec l’arborescence des fichiers.

12. Ouverture du projet dans Webots
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Lancez Webots et ouvrez le fichier monde du TP1 :

``src/worlds/main.wbt``

Une fois le monde ouvert, vous devriez voir apparaître la scène de simulation avec la plateforme et le robot.

Prenez quelques minutes pour :

- vous déplacer dans la scène
- zoomer sur le robot
- observer les différents éléments déjà présents
- développer le nœud ``Robot`` dans l’arborescence

.. figure:: ../images/tp1/ouverture_projet_webots.png
   :alt: Projet ouvert dans Webots

   Figure 9 — Projet ouvert dans Webots avec le monde de simulation chargé.

IV. Manipulations depuis l’interface graphique de Webots
--------------------------------------------------------

Dans cette partie, vous allez réaliser une première modification du robot directement depuis l’interface graphique de Webots.

L’objectif n’est pas encore de programmer un comportement complet, mais de comprendre comment un robot est construit dans Webots, comment ses composants sont organisés dans l’arborescence et comment modifier ses propriétés sans écrire directement dans un fichier de code.

Le robot fourni est volontairement incomplet. Vous devez le finaliser en ajoutant ou en complétant certains éléments depuis l’interface graphique :

- les roues du robot
- les deux capteurs de distance latéraux
- la partie droite de la pince

.. attention::

   Le contrôleur Java récupère les moteurs et les capteurs grâce à leur nom. Si un nom est différent de celui attendu, le contrôleur ne pourra pas trouver l’élément.

Exemple :

.. code-block:: text

   wheel4 ✅
   Wheel4 ❌
   WHEEL4 ❌

13. Observation du robot
~~~~~~~~~~~~~~~~~~~~~~~~

Avant de modifier le robot, observez sa structure dans l’arborescence de Webots.

Dans la partie gauche de l’interface, développez le nœud du robot. Repérez les éléments suivants :

- le corps du robot
- la roue déjà présente, si elle existe
- les emplacements où devront être ajoutées les autres roues
- le bras
- la partie déjà présente de la pince
- les capteurs déjà présents
- le contrôleur associé au robot

Pour chaque élément, notez son nom dans Webots et expliquez rapidement son rôle.

Questions
~~~~~~~~~

#. Quel est le nom du nœud principal du robot ?
#. Quel est le nom de la roue déjà présente ?
#. Quel est le nom du moteur associé à cette roue ?
#. Quels capteurs sont déjà présents ?
#. Où se trouve le bras dans l’arborescence ?
#. Quel contrôleur est associé au robot ?

.. figure:: ../images/tp1/robot_arborescence_ouverte.png
   :alt: Nœud Robot développé dans Webots

   Figure 3 — Nœud Robot développé dans l’arborescence Webots.

14. Comprendre le placement des objets dans Webots
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dans Webots, chaque objet possède plusieurs champs modifiables depuis l’interface graphique.

.. list-table::
   :header-rows: 1

   * - Champ
     - Rôle
   * - ``translation``
     - Définit la position de l’objet.
   * - ``rotation``
     - Définit l’orientation de l’objet.
   * - ``name``
     - Définit le nom d’un moteur, capteur ou ``Solid``.
   * - ``children``
     - Contient les éléments placés dans un nœud.
   * - ``device``
     - Contient les moteurs et capteurs associés à un joint.
   * - ``endPoint``
     - Contient l’objet attaché à un joint.
   * - ``boundingObject``
     - Définit la forme utilisée pour les collisions.
   * - ``physics``
     - Ajoute des propriétés physiques à un objet.

.. figure:: ../images/tp1/champs_noeud_webots.png
   :alt: Champs d’un nœud Webots

   Figure 4 — Exemple de champs modifiables d’un nœud Webots.

Le champ ``translation`` contient trois valeurs :

.. code-block:: text

   translation x y z

Dans ce projet :

- ``x`` permet de placer un élément vers l’avant ou l’arrière du robot
- ``y`` permet de placer un élément vers la gauche ou la droite
- ``z`` permet de placer un élément en hauteur

Exemple :

.. code-block:: text

   translation 0.07 0.07 0.015

Cela signifie que l’objet est placé légèrement vers l’avant, sur un côté du robot, et légèrement au-dessus du sol.

Avant de commencer, sélectionnez la roue déjà présente et observez :

- son champ ``translation``
- le champ ``anchor`` de son ``HingeJoint``
- son champ ``rotation``
- son moteur
- son ``Solid``

V. Ajout des roues du robot depuis l’interface graphique
--------------------------------------------------------

Objectif de l’exercice
~~~~~~~~~~~~~~~~~~~~~~

Dans cet exercice, vous allez ajouter les roues du robot directement depuis l’interface graphique de Webots.

Le robot doit posséder quatre roues pour pouvoir se déplacer correctement. Chaque roue doit être composée de plusieurs éléments.

.. list-table::
   :header-rows: 1

   * - Élément
     - Rôle
   * - ``HingeJoint``
     - Permet à la roue de tourner.
   * - ``RotationalMotor``
     - Permet au contrôleur Java de faire tourner la roue.
   * - ``Solid``
     - Représente la roue dans la simulation.
   * - ``Shape``
     - Rend la roue visible.
   * - ``boundingObject``
     - Permet à Webots de gérer les collisions.
   * - ``Physics``
     - Donne une masse à la roue.

À la fin de cet exercice, votre robot devra posséder les quatre roues suivantes :

.. list-table::
   :header-rows: 1

   * - Position
     - Nom du joint
     - Nom du moteur
     - Nom du ``Solid``
   * - Roue avant gauche
     - ``WHEEL1``
     - ``wheel1``
     - ``wheel1_solid``
   * - Roue avant droite
     - ``WHEEL2``
     - ``wheel2``
     - ``wheel2_solid``
   * - Roue arrière gauche
     - ``WHEEL3``
     - ``wheel3``
     - ``wheel3_solid``
   * - Roue arrière droite
     - ``WHEEL4``
     - ``wheel4``
     - ``wheel4_solid``

.. figure:: ../images/tp1/robot_avant_roues.png
   :alt: Robot avant ajout des roues

   Figure 5 — Robot avant l’ajout des roues manquantes.

15. Ajouter un ``HingeJoint``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dans l’arborescence de Webots, développez le nœud du robot.

Repérez le champ ``children`` du ``Robot``. C’est dans ce champ que les différents composants du robot sont ajoutés.

Faites un clic droit sur ``children``, puis ajoutez un nouveau nœud de type :

.. code-block:: text

   HingeJoint

.. figure:: ../images/tp1/ajout_hingejoint.png
   :alt: Ajout d’un HingeJoint

   Figure 6 — Ajout d’un HingeJoint depuis le champ children du Robot.

Une fois le ``HingeJoint`` ajouté, sélectionnez-le dans l’arborescence. Dans la section ``Node``, renseignez le champ ``DEF``.

.. list-table::
   :header-rows: 1

   * - Roue
     - Valeur du champ ``DEF``
   * - Roue avant gauche
     - ``WHEEL1``
   * - Roue avant droite
     - ``WHEEL2``
   * - Roue arrière gauche
     - ``WHEEL3``
   * - Roue arrière droite
     - ``WHEEL4``

Le ``HingeJoint`` représente l’articulation de la roue. C’est grâce à lui que la roue pourra tourner autour d’un axe.

16. Configurer ``jointParameters``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dans le ``HingeJoint``, développez le champ :

.. code-block:: text

   jointParameters

Dans le champ ``axis``, indiquez :

.. code-block:: text

   0 1 0

Cette valeur signifie que la roue tourne autour de l’axe Y.

Ensuite, modifiez le champ ``anchor``. Le champ ``anchor`` correspond au point de rotation de la roue.

.. list-table::
   :header-rows: 1

   * - Roue
     - Valeur du champ ``anchor``
   * - ``WHEEL1``
     - ``0.07 0.07 0.015``
   * - ``WHEEL2``
     - ``0.07 -0.07 0.015``
   * - ``WHEEL3``
     - ``-0.07 0.07 0.015``
   * - ``WHEEL4``
     - ``-0.07 -0.07 0.015``

.. figure:: ../images/tp1/config_jointparameters.png
   :alt: Configuration des jointParameters

   Figure 7 — Configuration de l’axe et du point d’ancrage d’une roue.

Explication :

- ``x`` place la roue vers l’avant ou vers l’arrière
- ``y`` place la roue à gauche ou à droite
- ``z`` règle la hauteur de la roue

Les roues avant ont une valeur de ``x`` positive. Les roues arrière ont une valeur de ``x`` négative. Les roues gauche et droite ont des valeurs de ``y`` opposées.

17. Ajouter le moteur de la roue
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Chaque roue doit posséder un moteur pour pouvoir tourner.

Dans le ``HingeJoint``, repérez le champ :

.. code-block:: text

   device

Faites un clic droit sur ``device``, puis ajoutez un :

.. code-block:: text

   RotationalMotor

Sélectionnez ensuite le ``RotationalMotor`` ajouté. Dans le champ ``name``, indiquez le nom du moteur correspondant à la roue.

.. list-table::
   :header-rows: 1

   * - Roue
     - Nom du moteur
   * - ``WHEEL1``
     - ``wheel1``
   * - ``WHEEL2``
     - ``wheel2``
   * - ``WHEEL3``
     - ``wheel3``
   * - ``WHEEL4``
     - ``wheel4``

Le nom du moteur est très important. Le contrôleur Java utilise ce nom pour retrouver la roue et lui appliquer une vitesse.

Exemple :

.. code-block:: java

   robot.getMotor("wheel1");

Si le nom du moteur est mal écrit, le contrôleur ne pourra pas le trouver.

.. figure:: ../images/tp1/ajout_moteur_roue.png
   :alt: Ajout du moteur de la roue

   Figure 10 — Ajout d’un RotationalMotor dans le champ device du HingeJoint.

18. Ajouter le ``Solid`` de la roue
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le ``HingeJoint`` définit l’articulation de la roue, mais il faut maintenant ajouter l’objet attaché à cette articulation.

Dans le champ ``endPoint`` du ``HingeJoint``, ajoutez un :

.. code-block:: text

   Solid

Sélectionnez le ``Solid``, puis modifiez son champ ``name``.

.. list-table::
   :header-rows: 1

   * - Roue
     - Nom du ``Solid``
   * - ``WHEEL1``
     - ``wheel1_solid``
   * - ``WHEEL2``
     - ``wheel2_solid``
   * - ``WHEEL3``
     - ``wheel3_solid``
   * - ``WHEEL4``
     - ``wheel4_solid``

Modifiez ensuite le champ ``translation`` du ``Solid``. Cette valeur doit être identique à la valeur du champ ``anchor``.

.. figure:: ../images/tp1/ajout_solid_roue.png
   :alt: Ajout du Solid de la roue

   Figure 11 — Ajout du Solid représentant la roue dans le champ endPoint.

.. list-table::
   :header-rows: 1

   * - Roue
     - Valeur du champ ``translation``
   * - ``WHEEL1``
     - ``0.07 0.07 0.015``
   * - ``WHEEL2``
     - ``0.07 -0.07 0.015``
   * - ``WHEEL3``
     - ``-0.07 0.07 0.015``
   * - ``WHEEL4``
     - ``-0.07 -0.07 0.015``

Enfin, modifiez le champ ``rotation`` du ``Solid`` avec la valeur suivante :

.. code-block:: text

   1 0 0 1.57

Cette rotation permet d’orienter correctement le cylindre qui représentera la roue.

19. Ajouter l’apparence de la roue
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le ``Solid`` existe maintenant, mais il n’est pas encore visible. Pour afficher la roue, il faut lui ajouter une forme.

Dans le ``Solid``, développez le champ :

.. code-block:: text

   children

Faites un clic droit sur ``children``, puis ajoutez un :

.. code-block:: text

   Shape

Dans ce ``Shape``, ajoutez ensuite :

- un ``PBRAppearance`` dans le champ ``appearance``
- un ``Cylinder`` dans le champ ``geometry``

Dans le ``PBRAppearance``, modifiez les champs suivants :

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``baseColor``
     - ``0.02 0.02 0.02``
   * - ``roughness``
     - ``1``

Dans le ``Cylinder``, modifiez les champs suivants :

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``radius``
     - ``0.025``
   * - ``height``
     - ``0.02``

À ce stade, la roue doit être visible dans la zone de simulation.

.. figure:: ../images/tp1/apparence_roue.png
   :alt: Apparence de la roue

   Figure 12 — Configuration de l’apparence de la roue avec un Shape et un Cylinder.

20. Ajouter le ``boundingObject``
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La roue est maintenant visible, mais Webots doit aussi connaître sa forme pour gérer les collisions.

Dans le ``Solid`` de la roue, repérez le champ :

.. code-block:: text

   boundingObject

Ajoutez un :

.. code-block:: text

   Cylinder

Donnez à ce ``Cylinder`` les mêmes dimensions que la roue visible :

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``radius``
     - ``0.025``
   * - ``height``
     - ``0.02``

Le ``boundingObject`` est indispensable pour que Webots prenne correctement la roue en compte dans la physique de la simulation.

.. figure:: ../images/tp1/boundingobject_roue.png
   :alt: BoundingObject de la roue

   Figure 13 — Configuration du boundingObject utilisé pour les collisions de la roue.

21. Ajouter les propriétés physiques
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Pour que la roue soit considérée comme un objet physique, il faut ajouter un nœud ``Physics``.

Dans le ``Solid``, repérez le champ :

.. code-block:: text

   physics

Ajoutez un :

.. code-block:: text

   Physics

Modifiez ensuite les champs suivants :

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``density``
     - ``-1``
   * - ``mass``
     - ``0.05``

La valeur ``density = -1`` indique à Webots qu’il doit utiliser directement la masse donnée dans le champ ``mass``.

.. figure:: ../images/tp1/physics_roue.png
   :alt: Propriétés physiques de la roue

   Figure 14 — Ajout des propriétés physiques de la roue avec une masse de 0.05.

22. Répéter la manipulation pour les autres roues
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Vous devez maintenant répéter cette manipulation pour obtenir les quatre roues du robot.

.. list-table::
   :header-rows: 1

   * - Roue
     - Joint
     - Moteur
     - Solid
     - Anchor / Translation
   * - Avant gauche
     - ``WHEEL1``
     - ``wheel1``
     - ``wheel1_solid``
     - ``0.07 0.07 0.015``
   * - Avant droite
     - ``WHEEL2``
     - ``wheel2``
     - ``wheel2_solid``
     - ``0.07 -0.07 0.015``
   * - Arrière gauche
     - ``WHEEL3``
     - ``wheel3``
     - ``wheel3_solid``
     - ``-0.07 0.07 0.015``
   * - Arrière droite
     - ``WHEEL4``
     - ``wheel4``
     - ``wheel4_solid``
     - ``-0.07 -0.07 0.015``

.. figure:: ../images/tp1/robot_quatre_roues.png
   :alt: Robot avec ses quatre roues

   Figure 15 — Robot complété avec ses quatre roues.

VI. Ajout des capteurs de distance latéraux
-------------------------------------------

Le robot possède déjà un capteur de distance frontal. Vous devez maintenant ajouter deux capteurs de distance latéraux :

- un capteur à droite
- un capteur à gauche

Ces capteurs permettront au robot de détecter ce qui se trouve sur ses côtés.

.. list-table::
   :header-rows: 1

   * - Capteur
     - Nom attendu
   * - Capteur droit
     - ``ds_right``
   * - Capteur gauche
     - ``ds_left``

23. Ajouter le capteur droit
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dans l’arborescence du robot, faites un clic droit sur le champ ``children`` du ``Robot``.

Ajoutez un nouveau nœud de type :

.. code-block:: text

   DistanceSensor

.. figure:: ../images/tp1/ajout_capteur_droit.png
   :alt: Ajout du capteur droit

   Figure 16 — Ajout du capteur de distance droit nommé ds_right.

Sélectionnez le capteur ajouté, puis modifiez ses champs.

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``name``
     - ``ds_right``
   * - ``translation``
     - ``0.09 -0.04 0.04``
   * - ``rotation``
     - ``0 0 1 -0.5``

Le capteur droit doit utiliser la même ``lookupTable`` que le capteur frontal.

.. code-block:: text

   0    1000 0
   0.3  600  0
   1    0    0

24. Ajouter le capteur gauche
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Ajoutez un deuxième nœud de type :

.. code-block:: text

   DistanceSensor

.. figure:: ../images/tp1/ajout_capteur_gauche.png
   :alt: Ajout du capteur gauche

   Figure 17 — Ajout du capteur de distance gauche nommé ds_left.

Sélectionnez ce capteur, puis modifiez ses champs.

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``name``
     - ``ds_left``
   * - ``translation``
     - ``0.09 0.04 0.04``
   * - ``rotation``
     - ``0 0 1 0.5``

Le capteur gauche doit également utiliser la même ``lookupTable`` que le capteur frontal.

25. Visualiser les rayons des capteurs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Pour vérifier l’orientation des capteurs de distance, Webots permet d’afficher leurs rayons.

Dans le menu Webots, activez :

.. code-block:: text

   View → Optional Rendering → Show DistanceSensor Rays

Vous devez voir apparaître les rayons de détection des capteurs.

.. figure:: ../images/tp1/rayons_capteurs_distance.png
   :alt: Rayons des capteurs de distance

   Figure 18 — Visualisation des rayons des capteurs de distance dans Webots.

Vérifiez que :

- ``ds_front`` regarde vers l’avant
- ``ds_right`` regarde légèrement vers la droite
- ``ds_left`` regarde légèrement vers la gauche

VII. Complétion de la pince
---------------------------

Le bras du robot possède déjà une partie de la pince. Pour que la pince soit complète, vous devez ajouter la partie droite de la pince.

La pince est composée de deux parties :

- une partie gauche déjà présente
- une partie droite à ajouter

.. figure:: ../images/tp1/pince_incomplete.png
   :alt: Pince incomplète

   Figure 19 — Pince incomplète avant l’ajout de la partie droite.

La partie droite doit être ajoutée dans le nœud ``arm_solid``, juste après le joint de la pince gauche.

Dans l’arborescence, développez :

.. code-block:: text

   Robot
   └── ARM_JOINT
       └── arm_solid

Repérez ensuite le joint déjà présent de la pince gauche.

26. Ajouter le joint de la pince droite
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dans le champ ``children`` de ``arm_solid``, ajoutez un nouveau nœud de type :

.. code-block:: text

   HingeJoint

Dans la section ``Node``, renseignez le champ ``DEF`` avec :

.. code-block:: text

   GRIPPER_RIGHT_JOINT

Dans le champ ``jointParameters``, modifiez les valeurs suivantes :

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``anchor``
     - ``0.055 -0.030 0``
   * - ``axis``
     - ``0 0 1``

.. figure:: ../images/tp1/joint_pince_droite.png
   :alt: Joint de la pince droite

   Figure 20 — Ajout du HingeJoint correspondant à la pince droite.

27. Ajouter le moteur de la pince droite
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dans le champ ``device`` du ``GRIPPER_RIGHT_JOINT``, ajoutez un :

.. code-block:: text

   RotationalMotor

Modifiez les champs du moteur avec les valeurs suivantes :

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``name``
     - ``gripper_right_motor``
   * - ``minPosition``
     - ``-0.3``
   * - ``maxPosition``
     - ``0.8``
   * - ``maxVelocity``
     - ``1.0``
   * - ``maxTorque``
     - ``5``

Le nom ``gripper_right_motor`` est obligatoire, car le contrôleur Java l’utilise pour ouvrir et fermer la pince.

.. figure:: ../images/tp1/moteur_pince_droite.png
   :alt: Moteur de la pince droite

   Figure 21 — Ajout du moteur gripper_right_motor pour contrôler la pince droite.

28. Ajouter le ``Solid`` de la pince droite
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dans le champ ``endPoint`` du ``GRIPPER_RIGHT_JOINT``, ajoutez un :

.. code-block:: text

   Solid

Modifiez les champs du ``Solid`` avec les valeurs suivantes :

.. list-table::
   :header-rows: 1

   * - Champ
     - Valeur
   * - ``name``
     - ``gripper_right_solid``
   * - ``translation``
     - ``0.055 -0.040 0``

.. figure:: ../images/tp1/solid_pince_droite.png
   :alt: Solid de la pince droite

   Figure 22 — Ajout du Solid représentant la partie droite de la pince.

Dans le champ ``children`` de ce ``Solid``, ajoutez un :

.. code-block:: text

   Shape

Dans le ``Shape``, ajoutez :

- un ``PBRAppearance`` dans ``appearance``
- une ``Box`` dans ``geometry``

Dans la ``Box``, modifiez le champ ``size`` avec :

.. code-block:: text

   0.045 0.012 0.025

Vous pouvez utiliser une couleur noire pour que la pince droite ressemble à la pince gauche.

.. figure:: ../images/tp1/pince_complete.png
   :alt: Pince complète

   Figure 23 — Pince complète avec les parties gauche et droite.

VIII. Comprendre le rôle du contrôleur
--------------------------------------

29. Questions de compréhension sur le contrôleur
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Le fichier ``TP1ValidationController.java`` est le programme associé au robot. Il permet de vérifier que les différents éléments ont bien été ajoutés depuis l’interface graphique de Webots.

Il ne vérifie pas uniquement la présence visuelle des éléments dans Webots. Il essaye surtout de récupérer les moteurs et les capteurs grâce à leur nom.

.. figure:: ../images/tp1/tp1_validation_controller.png
   :alt: Contrôleur de validation dans VS Code

   Figure 24 — Fichier TP1ValidationController.java ouvert dans Visual Studio Code.

Répondez aux questions suivantes :

#. Quelle fonction permet de récupérer un moteur dans Webots ?
#. Quelle fonction permet de récupérer un capteur de distance dans Webots ?
#. Quelle fonction permet de récupérer un capteur de contact dans Webots ?
#. Quelle fonction permet de faire tourner une roue ?
#. Quelle fonction du contrôleur permet de faire tourner les quatre roues en même temps ?
#. Comment récupérer la valeur d’un capteur de distance ?
#. Pourquoi le nom des moteurs et des capteurs est-il important ?
#. Que se passe-t-il si un moteur est visible dans Webots mais que son nom est incorrect ?

30. Première modification du contrôleur
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Dans cette partie, vous allez modifier quelques valeurs simples du contrôleur afin d’observer leur effet sur le robot.

Dans la boucle principale, repérez la partie suivante :

.. code-block:: java

   if (elapsedTime < 2.0) {
     setWheelVelocity(wheels, 3.0);
   }

Cette partie signifie que pendant les deux premières secondes, le robot avance à une vitesse de ``3.0``.

Modifiez la vitesse et le temps de déplacement.

.. list-table::
   :header-rows: 1

   * - Temps
     - Vitesse
   * - ``2.0``
     - ``1.0``
   * - ``2.0``
     - ``5.0``
   * - ``3.0``
     - ``3.0``
   * - ``5.0``
     - ``2.0``

Après chaque modification :

#. sauvegardez le fichier
#. compilez le contrôleur
#. rechargez le monde Webots
#. observez le comportement du robot

IX. Questions de compréhension finales
--------------------------------------

Répondez aux questions suivantes :

#. Quel est le rôle d’un ``HingeJoint`` ?
#. Quel est le rôle d’un ``RotationalMotor`` ?
#. Pourquoi le nom du moteur est-il important ?
#. À quoi sert le champ ``anchor`` ?
#. Pourquoi le champ ``translation`` du ``Solid`` doit-il correspondre au champ ``anchor`` du ``HingeJoint`` ?
#. À quoi sert le ``boundingObject`` ?
#. Pourquoi faut-il ajouter un nœud ``Physics`` ?
#. Quelle différence y a-t-il entre la roue visible et le ``boundingObject`` ?
#. Pourquoi les roues gauche et droite ont-elles des valeurs de ``y`` opposées ?
#. Que peut-il se passer si le moteur s’appelle ``Wheel1`` au lieu de ``wheel1`` ?
#. Pourquoi faut-il respecter le nom ``ds_right`` pour le capteur droit ?
#. À quoi sert l’option ``Show DistanceSensor Rays`` ?
#. Pourquoi la pince droite doit-elle avoir un moteur différent de la pince gauche ?
#. Pourquoi le contrôleur de validation ne se contente-t-il pas de vérifier les objets visibles ?
#. Quel est l’intérêt de modifier le robot depuis l’interface graphique dans ce premier TP ?

X. Validation attendue
----------------------

À la fin du TP, le robot doit contenir :

- quatre roues
- quatre moteurs de roues nommés ``wheel1``, ``wheel2``, ``wheel3`` et ``wheel4``
- un capteur frontal ``ds_front``
- un capteur droit ``ds_right``
- un capteur gauche ``ds_left``
- un capteur de contact ``touch_front``
- une caméra ``color_sensor``
- un bras motorisé avec ``arm_motor``
- un capteur de position du bras ``arm_sensor``
- une pince gauche avec ``gripper_left_motor``
- une pince droite avec ``gripper_right_motor``

Le contrôleur de validation doit afficher des messages ``[OK]`` dans la console Webots.

Lorsque la validation est réussie, le robot doit effectuer une courte démonstration :

#. ouvrir et fermer la pince
#. avancer pendant quelques instants
#. s’arrêter

XI. Annexes
-----------

Annexe A — Noms importants à respecter
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :header-rows: 1

   * - Élément
     - Nom attendu
   * - Roue avant gauche
     - ``WHEEL1``
   * - Roue avant droite
     - ``WHEEL2``
   * - Roue arrière gauche
     - ``WHEEL3``
   * - Roue arrière droite
     - ``WHEEL4``
   * - Moteur roue 1
     - ``wheel1``
   * - Moteur roue 2
     - ``wheel2``
   * - Moteur roue 3
     - ``wheel3``
   * - Moteur roue 4
     - ``wheel4``
   * - Solid roue 1
     - ``wheel1_solid``
   * - Solid roue 2
     - ``wheel2_solid``
   * - Solid roue 3
     - ``wheel3_solid``
   * - Solid roue 4
     - ``wheel4_solid``
   * - Capteur avant
     - ``ds_front``
   * - Capteur droit
     - ``ds_right``
   * - Capteur gauche
     - ``ds_left``
   * - Capteur de contact
     - ``touch_front``
   * - Caméra couleur
     - ``color_sensor``
   * - Moteur du bras
     - ``arm_motor``
   * - Capteur du bras
     - ``arm_sensor``
   * - Moteur pince gauche
     - ``gripper_left_motor``
   * - Moteur pince droite
     - ``gripper_right_motor``

Annexe B — Coordonnées des roues
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :header-rows: 1

   * - Roue
     - ``anchor``
     - ``translation``
   * - ``WHEEL1``
     - ``0.07 0.07 0.015``
     - ``0.07 0.07 0.015``
   * - ``WHEEL2``
     - ``0.07 -0.07 0.015``
     - ``0.07 -0.07 0.015``
   * - ``WHEEL3``
     - ``-0.07 0.07 0.015``
     - ``-0.07 0.07 0.015``
   * - ``WHEEL4``
     - ``-0.07 -0.07 0.015``
     - ``-0.07 -0.07 0.015``

Annexe C — Problèmes fréquents
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :header-rows: 1

   * - Problème
     - Cause possible
     - Solution
   * - La roue n’apparaît pas
     - Aucun ``Shape`` n’a été ajouté
     - Ajouter un ``Shape`` avec un ``Cylinder``.
   * - La roue est visible mais ne touche pas le sol
     - Mauvaise valeur de ``translation``
     - Vérifier la coordonnée ``z``.
   * - La roue ne tourne pas
     - Mauvais nom du moteur
     - Vérifier ``wheel1``, ``wheel2``, ``wheel3``, ``wheel4``.
   * - Le robot ne bouge pas
     - Les moteurs ne sont pas trouvés
     - Vérifier les noms des moteurs.
   * - Le capteur latéral ne fonctionne pas
     - Mauvais nom ou mauvaise rotation
     - Vérifier ``ds_left``, ``ds_right`` et la rotation.
   * - Les rayons des capteurs ne sont pas visibles
     - Option non activée
     - Activer ``Show DistanceSensor Rays``.
   * - La pince droite ne bouge pas
     - Mauvais nom du moteur
     - Vérifier ``gripper_right_motor``.
   * - Le contrôleur affiche ``[ERREUR]``
     - Élément manquant ou mal nommé
     - Lire le message d’erreur et vérifier l’élément indiqué.