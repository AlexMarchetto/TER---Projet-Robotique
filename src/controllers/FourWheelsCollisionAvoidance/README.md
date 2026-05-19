# FourWheelsCollisionAvoidance

Dossier de controller Webots factorisé en plusieurs classes Java.

## Installation

Place ce dossier ici :

controllers/FourWheelsCollisionAvoidance/

Le fichier principal doit rester :

FourWheelsCollisionAvoidance.java

Le robot Webots peut garder :

controller "FourWheelsCollisionAvoidance"

## Fichiers

- FourWheelsCollisionAvoidance.java : point d'entrée du controller
- TERBot.java : logique principale du robot et machine à états
- RobotMode.java : enum des états
- Wheel.java : représentation d'une roue
- DriveBase.java : gestion des 4 roues
- RobotSensors.java : gestion des capteurs
- Arm.java : gestion du bras et de la pince
- PuckManager.java : gestion des palets
- MathUtils.java : fonctions mathématiques utiles
