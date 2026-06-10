# Correction — TP1

## Projet robotique sur Webots

## Finalisation du robot et prise en main

---

# 1. Questions de compréhension sur le contrôleur

## Question 1 — Quelle fonction permet de récupérer un moteur dans Webots ?

La fonction utilisée pour récupérer un moteur dans Webots est :

```java
robot.getMotor("nom_du_moteur");
```

Exemple :

```java
Motor wheel1 = robot.getMotor("wheel1");
```

Cette fonction permet au contrôleur Java de récupérer un moteur présent dans le robot, à condition que son nom soit correct dans le monde Webots.

---

## Question 2 — Quelle fonction permet de récupérer un capteur de distance dans Webots ?

La fonction utilisée pour récupérer un capteur de distance est :

```java
robot.getDistanceSensor("nom_du_capteur");
```

Exemple :

```java
DistanceSensor dsFront = robot.getDistanceSensor("ds_front");
```

Cette fonction permet d’accéder à un capteur de distance défini dans le robot.

---

## Question 3 — Quelle fonction permet de récupérer un capteur de contact dans Webots ?

La fonction utilisée pour récupérer un capteur de contact est :

```java
robot.getTouchSensor("nom_du_capteur");
```

Exemple :

```java
TouchSensor touchFront = robot.getTouchSensor("touch_front");
```

Le capteur de contact permet de savoir si le robot touche un objet.

---

## Question 4 — Quelle fonction permet de faire tourner une roue ?

Pour faire tourner une roue, il faut utiliser la méthode :

```java
motor.setVelocity(vitesse);
```

Exemple :

```java
wheel1.setVelocity(3.0);
```

Avant cela, le moteur doit généralement être configuré en rotation continue avec :

```java
wheel1.setPosition(Double.POSITIVE_INFINITY);
```

---

## Question 5 — Quelle fonction du contrôleur permet de faire tourner les quatre roues en même temps ?

Dans le contrôleur de validation, la fonction utilisée pour faire tourner les quatre roues en même temps est :

```java
setWheelVelocity(wheels, velocity);
```

Elle parcourt le tableau des roues et applique la même vitesse à chaque moteur.

Exemple :

```java
setWheelVelocity(wheels, 3.0);
```

---

## Question 6 — Comment récupérer la valeur d’un capteur de distance ?

Pour récupérer la valeur d’un capteur de distance, il faut utiliser :

```java
sensor.getValue();
```

Exemple :

```java
double value = dsFront.getValue();
```

Avant de lire sa valeur, le capteur doit être activé avec :

```java
dsFront.enable(timeStep);
```

---

## Question 7 — Pourquoi le nom des moteurs et des capteurs est-il important ?

Le nom des moteurs et des capteurs est important car le contrôleur Java les récupère grâce à ce nom.

Par exemple, si le code contient :

```java
robot.getMotor("wheel1");
```

alors le moteur dans Webots doit obligatoirement s’appeler :

```text
wheel1
```

Si le nom est différent, le contrôleur ne trouvera pas le moteur et le robot ne pourra pas utiliser cet élément.

---

## Question 8 — Que se passe-t-il si un moteur est visible dans Webots mais que son nom est incorrect ?

Si un moteur est visible dans Webots mais que son nom est incorrect, le contrôleur ne pourra pas le récupérer.

Par exemple, si le moteur s’appelle :

```text
Wheel1
```

au lieu de :

```text
wheel1
```

alors cette ligne ne fonctionnera pas correctement :

```java
robot.getMotor("wheel1");
```

Le moteur sera présent visuellement dans Webots, mais inutilisable par le programme. Le contrôleur affichera alors une erreur ou le robot ne bougera pas comme prévu.

---

# 2. Première modification du contrôleur

Dans cette partie, il fallait modifier la durée et la vitesse du déplacement afin d’observer le comportement du robot.

Code de départ :

```java
if (elapsedTime < 2.0) {
  setWheelVelocity(wheels, 3.0);
}
```

---

## Test 1 — Temps : `2.0`, vitesse : `1.0`

```java
if (elapsedTime < 2.0) {
  setWheelVelocity(wheels, 1.0);
}
```

Le robot avance pendant 2 secondes, mais plus lentement qu’avec la vitesse initiale de `3.0`.

---

## Test 2 — Temps : `2.0`, vitesse : `5.0`

```java
if (elapsedTime < 2.0) {
  setWheelVelocity(wheels, 5.0);
}
```

Le robot avance pendant 2 secondes, mais beaucoup plus rapidement.
Il parcourt donc une distance plus grande pendant le même temps.

---

## Test 3 — Temps : `3.0`, vitesse : `3.0`

```java
if (elapsedTime < 3.0) {
  setWheelVelocity(wheels, 3.0);
}
```

Le robot garde la même vitesse que dans l’exemple initial, mais il avance pendant plus longtemps.
Il parcourt donc une distance plus grande.

---

## Test 4 — Temps : `5.0`, vitesse : `2.0`

```java
if (elapsedTime < 5.0) {
  setWheelVelocity(wheels, 2.0);
}
```

Le robot avance moins vite qu’avec une vitesse de `3.0`, mais pendant plus longtemps.
Son déplacement est donc plus lent et plus progressif.

---

# 3. Questions de compréhension finales

## Question 1 — Quel est le rôle d’un `HingeJoint` ?

Un `HingeJoint` permet de créer une articulation en rotation entre deux éléments.

Dans le cas du robot, il sert principalement à fixer les roues, le bras ou la pince tout en leur permettant de tourner autour d’un axe.

Exemple : une roue est reliée au corps du robot avec un `HingeJoint`.

---

## Question 2 — Quel est le rôle d’un `RotationalMotor` ?

Un `RotationalMotor` permet de contrôler la rotation d’un élément relié à un `HingeJoint`.

Il peut être utilisé pour :

* faire tourner une roue ;
* lever ou baisser un bras ;
* ouvrir ou fermer une pince.

Exemple :

```java
Motor wheel1 = robot.getMotor("wheel1");
wheel1.setVelocity(3.0);
```

---

## Question 3 — Pourquoi le nom du moteur est-il important ?

Le nom du moteur est important car le contrôleur utilise ce nom pour le récupérer.

Si le moteur s’appelle `wheel1` dans le code, il doit aussi s’appeler `wheel1` dans Webots.

Sinon, le contrôleur ne pourra pas le trouver.

---

## Question 4 — À quoi sert le champ `anchor` ?

Le champ `anchor` indique le point autour duquel l’articulation tourne.

Pour une roue, l’`anchor` correspond au centre de rotation de la roue.

Exemple :

```webots
anchor 0.07 0.07 0.015
```

Cela signifie que l’axe de rotation de la roue est placé à cette position.

---

## Question 5 — Pourquoi le champ `translation` du `Solid` doit-il correspondre au champ `anchor` du `HingeJoint` ?

Le champ `translation` du `Solid` indique où se trouve visuellement et physiquement la roue.

Le champ `anchor` indique où se trouve l’axe de rotation.

Si ces deux valeurs ne correspondent pas, la roue peut tourner autour d’un mauvais point.
Cela peut donner un comportement anormal, par exemple une roue qui tourne autour d’un axe décalé.

---

## Question 6 — À quoi sert le `boundingObject` ?

Le `boundingObject` définit la forme utilisée pour les collisions physiques.

Même si un objet est visible dans Webots, il ne peut pas forcément entrer en collision avec d’autres objets sans `boundingObject`.

Pour une roue, le `boundingObject` peut être un cylindre :

```webots
boundingObject Cylinder {
  radius 0.025
  height 0.02
}
```

---

## Question 7 — Pourquoi faut-il ajouter un nœud `Physics` ?

Le nœud `Physics` permet à Webots de simuler le comportement physique d’un objet.

Il permet notamment de définir :

* la masse ;
* les collisions ;
* les interactions avec le sol ;
* les mouvements réalistes.

Sans `Physics`, un objet peut être visible mais ne pas se comporter correctement dans la simulation.

---

## Question 8 — Quelle différence y a-t-il entre la roue visible et le `boundingObject` ?

La roue visible correspond à l’apparence graphique de l’objet.
Elle est définie avec un `Shape`.

Le `boundingObject`, lui, correspond à la forme utilisée par Webots pour calculer les collisions.

En résumé :

| Élément          | Rôle                 |
| ---------------- | -------------------- |
| `Shape`          | Affichage visuel     |
| `boundingObject` | Collisions physiques |

---

## Question 9 — Pourquoi les roues gauche et droite ont-elles des valeurs de `y` opposées ?

Dans Webots, l’axe `y` permet de placer les objets sur la gauche ou sur la droite du robot.

Une roue placée à gauche peut avoir une valeur positive en `y`, tandis qu’une roue placée à droite peut avoir une valeur négative.

Exemple :

```webots
WHEEL1 : 0.07 0.07 0.015
WHEEL2 : 0.07 -0.07 0.015
```

Les valeurs opposées permettent de placer les roues de chaque côté du robot.

---

## Question 10 — Que peut-il se passer si le moteur s’appelle `Wheel1` au lieu de `wheel1` ?

Le contrôleur ne pourra pas trouver le moteur, car les noms sont sensibles à la casse.

`Wheel1` et `wheel1` ne sont pas considérés comme identiques.

Si le code cherche :

```java
robot.getMotor("wheel1");
```

mais que le moteur s’appelle :

```text
Wheel1
```

alors le moteur ne sera pas récupéré correctement.

---

## Question 11 — Pourquoi faut-il respecter le nom `ds_right` pour le capteur droit ?

Il faut respecter le nom `ds_right` car le contrôleur utilise ce nom pour récupérer le capteur droit.

Exemple :

```java
DistanceSensor dsRight = robot.getDistanceSensor("ds_right");
```

Si le capteur porte un autre nom dans Webots, le contrôleur ne pourra pas le trouver.

---

## Question 12 — À quoi sert l’option `Show DistanceSensor Rays` ?

L’option `Show DistanceSensor Rays` permet d’afficher les rayons des capteurs de distance dans la simulation.

Cela permet de vérifier :

* l’orientation des capteurs ;
* leur position ;
* leur direction de détection ;
* s’ils pointent vers la bonne zone.

C’est utile pour repérer une erreur de rotation ou de placement.

---

## Question 13 — Pourquoi la pince droite doit-elle avoir un moteur différent de la pince gauche ?

La pince droite doit avoir un moteur différent de la pince gauche car chaque côté de la pince doit pouvoir être contrôlé séparément.

La pince gauche utilise :

```text
gripper_left_motor
```

La pince droite utilise :

```text
gripper_right_motor
```

Cela permet d’ouvrir et de fermer correctement les deux côtés de la pince.

---

## Question 14 — Pourquoi le contrôleur de validation ne se contente-t-il pas de vérifier les objets visibles ?

Le contrôleur de validation ne vérifie pas seulement les objets visibles, car un objet peut être visible mais mal configuré.

Par exemple :

* une roue peut être visible mais avoir un mauvais nom de moteur ;
* un capteur peut être visible mais ne pas être récupérable par le code ;
* une pince peut exister visuellement mais ne pas être motorisée correctement.

Le contrôleur vérifie donc que les éléments sont bien utilisables par le programme Java.

---

## Question 15 — Quel est l’intérêt de modifier le robot depuis l’interface graphique dans ce premier TP ?

Modifier le robot depuis l’interface graphique permet de mieux comprendre la structure d’un robot dans Webots.

Cela permet de visualiser directement :

* les nœuds ;
* les moteurs ;
* les capteurs ;
* les positions ;
* les axes de rotation ;
* les formes physiques.

Ce premier TP sert donc à comprendre comment le robot est construit avant de passer à une programmation plus avancée dans les TP suivants.

---

# 4. Validation attendue

À la fin du TP1, le robot doit contenir :

* quatre roues ;
* quatre moteurs de roues nommés `wheel1`, `wheel2`, `wheel3` et `wheel4` ;
* un capteur frontal nommé `ds_front` ;
* un capteur droit nommé `ds_right` ;
* un capteur gauche nommé `ds_left` ;
* un capteur de contact nommé `touch_front` ;
* une caméra nommée `color_sensor` ;
* un bras motorisé avec `arm_motor` ;
* un capteur de position du bras nommé `arm_sensor` ;
* une pince gauche avec `gripper_left_motor` ;
* une pince droite avec `gripper_right_motor`.

Le contrôleur de validation doit afficher des messages `[OK]` dans la console Webots.

Lorsque la validation est réussie, le robot doit effectuer une courte démonstration :

1. avancer pendant quelques instants ;
2. s’arrêter ;
3. ouvrir et fermer la pince ;
4. afficher les valeurs des capteurs de distance.

---

# 5. Correction du world attendu

Dans le fichier `main.wbt`, le robot peut être directement écrit dans le monde sans passer par un fichier `PROTO`.

Le bloc `Robot { ... }` doit contenir au minimum :

* le corps du robot ;
* les quatre roues ;
* les trois capteurs de distance ;
* la caméra ;
* le capteur de contact ;
* le bras ;
* la pince gauche ;
* la pince droite ;
* le `boundingObject` du robot ;
* le nœud `Physics`.

Le contrôleur attendu est :

```webots
controller "TP1ValidationController"
```

Le robot doit aussi être en mode superviseur :

```webots
supervisor TRUE
```

---

# 6. Noms importants à respecter

| Élément             | Nom attendu           |
| ------------------- | --------------------- |
| Roue avant gauche   | `WHEEL1`              |
| Roue avant droite   | `WHEEL2`              |
| Roue arrière gauche | `WHEEL3`              |
| Roue arrière droite | `WHEEL4`              |
| Moteur roue 1       | `wheel1`              |
| Moteur roue 2       | `wheel2`              |
| Moteur roue 3       | `wheel3`              |
| Moteur roue 4       | `wheel4`              |
| Solid roue 1        | `wheel1_solid`        |
| Solid roue 2        | `wheel2_solid`        |
| Solid roue 3        | `wheel3_solid`        |
| Solid roue 4        | `wheel4_solid`        |
| Capteur avant       | `ds_front`            |
| Capteur droit       | `ds_right`            |
| Capteur gauche      | `ds_left`             |
| Capteur de contact  | `touch_front`         |
| Caméra couleur      | `color_sensor`        |
| Moteur du bras      | `arm_motor`           |
| Capteur du bras     | `arm_sensor`          |
| Moteur pince gauche | `gripper_left_motor`  |
| Moteur pince droite | `gripper_right_motor` |

---

# 7. Problèmes fréquents et corrections

| Problème                                     | Cause possible                               | Correction                                               |
| -------------------------------------------- | -------------------------------------------- | -------------------------------------------------------- |
| Le robot ne bouge pas                        | Les moteurs ne sont pas trouvés              | Vérifier les noms `wheel1`, `wheel2`, `wheel3`, `wheel4` |
| Une roue est visible mais ne tourne pas      | Le moteur est mal nommé ou absent            | Vérifier le `RotationalMotor` de la roue                 |
| Une roue tourne mal                          | Mauvais `anchor` ou mauvaise `translation`   | Faire correspondre `anchor` et `translation`             |
| Le robot tombe ou glisse bizarrement         | Mauvais `boundingObject` ou `Physics` absent | Ajouter ou corriger les propriétés physiques             |
| Un capteur ne répond pas                     | Mauvais nom du capteur                       | Vérifier `ds_front`, `ds_left`, `ds_right`               |
| Les rayons des capteurs ne sont pas visibles | Option non activée                           | Activer `Show DistanceSensor Rays`                       |
| La pince droite ne bouge pas                 | Moteur droit absent ou mal nommé             | Vérifier `gripper_right_motor`                           |
| Le contrôleur affiche `[ERREUR]`             | Élément manquant ou mal nommé                | Lire le message exact dans la console                    |
