# FourWheelsCollisionAvoidanceAPI

Controller Webots Java avec une API propre pour programmer le robot.

## Installation

Option 1, garder le nom actuel du controller :

```text
controllers/FourWheelsCollisionAvoidance/
```

Dans ce cas, garde `FourWheelsCollisionAvoidance.java` comme fichier principal et mets dans Webots :

```text
controller "FourWheelsCollisionAvoidance"
```

Option 2, utiliser le nom du dossier fourni :

```text
controllers/FourWheelsCollisionAvoidanceAPI/
```

Dans ce cas, mets dans Webots :

```text
controller "FourWheelsCollisionAvoidanceAPI"
```

Le fichier `FourWheelsCollisionAvoidanceAPI.java` est déjà fourni pour ce cas.

## Exemple simple

```java
TERBot robot = new TERBot();
robot.setBehavior(new SimpleAvoidObstacleBehavior(robot));
robot.run();
```

## API disponible

```java
robot.motors().forward(4.0);
robot.motors().backward(2.0);
robot.motors().turnLeft(3.0);
robot.motors().turnRight(3.0);
robot.motors().setSpeed(2.0, 4.0);
robot.motors().stop();

robot.sensors().frontDistance();
robot.sensors().leftDistance();
robot.sensors().rightDistance();
robot.sensors().isFrontTouched();
robot.sensors().wasFrontJustTouched();
robot.sensors().color();
robot.sensors().seesRed();

robot.arm().lift();
robot.arm().lower();
robot.arm().moveTo(0.2);

robot.gripper().open();
robot.gripper().close();

robot.pucks().findNearestAvailablePuck();
robot.pucks().findBestAvailablePuck(0.35);
robot.pucks().attachPuckToRobot(index);
robot.pucks().dropPuck(index, -0.9, 0.0, 0.095);
```

## Asynchrone simple

```java
robot.scheduler().add(new TimedTask(50,
    () -> robot.motors().forward(3.0),
    () -> robot.motors().stop()
));
```
