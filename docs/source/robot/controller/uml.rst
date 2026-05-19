Diagramme UML
=============

Cette page présente la modélisation orientée objet du contrôleur du robot.

La modélisation repose principalement sur la composition :

- ``TERBot`` possède les sous-systèmes principaux du robot.
- ``DriveBase`` possède quatre roues.
- ``Wheel`` encapsule un moteur Webots.
- ``RobotSensors`` encapsule les capteurs.
- ``Arm`` encapsule les moteurs du bras et de la pince.
- ``PuckManager`` encapsule la gestion des palets.

Diagramme PlantUML
------------------

Le diagramme suivant peut être copié dans un outil compatible PlantUML.

.. code-block:: plantuml

   @startuml
   skinparam classAttributeIconSize 0

   class FourWheelsCollisionAvoidance {
     +main(String[] args) : void
   }

   class TERBot {
     -supervisor : Supervisor
     -timeStep : int
     -driveBase : DriveBase
     -sensors : RobotSensors
     -arm : Arm
     -puckManager : PuckManager
     -mode : RobotMode
     -currentPuckIndex : int
     -puckAttached : boolean
     +TERBot(supervisor : Supervisor)
     +run() : void
     -update() : void
     -handleContact(touched : boolean) : void
     -updateSearch() : double[]
     -updateTouchAvoid() : double[]
     -updateApproachPuck() : double[]
     -updateGoToDropZone(touched : boolean) : double[]
     -updateBackAndTurnAfterDrop() : double[]
     -resetSearch() : void
     -printDebug(touched : boolean) : void
   }

   enum RobotMode {
     SEARCH
     TOUCH_AVOID
     APPROACH_PUCK
     LOWER_ARM
     CLOSE_GRIPPER
     LIFT_ARM
     GO_TO_DROP_ZONE
     DROP_PUCK
     LIFT_ARM_AFTER_DROP
     BACK_AND_TURN_AFTER_DROP
   }

   class DriveBase {
     -frontLeft : Wheel
     -frontRight : Wheel
     -rearLeft : Wheel
     -rearRight : Wheel
     +DriveBase(robot : Supervisor)
     +setSpeed(leftSpeed : double, rightSpeed : double) : void
     +stop() : void
   }

   class Wheel {
     -motor : Motor
     +Wheel(motor : Motor)
     +setVelocity(velocity : double) : void
     +stop() : void
   }

   class RobotSensors {
     -dsRight : DistanceSensor
     -dsLeft : DistanceSensor
     -dsFront : DistanceSensor
     -colorSensor : Camera
     -touchFront : TouchSensor
     +RobotSensors(robot : Supervisor, timeStep : int)
     +getRightDistance() : double
     +getLeftDistance() : double
     +getFrontDistance() : double
     +isTouched() : boolean
     +getAverageColor() : int[]
     +isRedDetected() : boolean
   }

   class Arm {
     -armMotor : Motor
     -gripperLeftMotor : Motor
     -gripperRightMotor : Motor
     -armSensor : PositionSensor
     +Arm(robot : Supervisor, timeStep : int)
     +lift() : void
     +lower() : void
     +openGripper() : void
     +closeGripper() : void
   }

   class PuckManager {
     -robot : Supervisor
     -puckNames : String[]
     -puckNodes : Node[]
     -puckTranslationFields : Field[]
     -puckDelivered : boolean[]
     +PuckManager(robot : Supervisor, puckNames : String[])
     +findNearestAvailablePuck() : int
     +getPuckNode(index : int) : Node
     +getDistanceToPuck(index : int) : double
     +isDelivered(index : int) : boolean
     +getPuckName(index : int) : String
     +attachPuckToRobot(index : int) : void
     +dropPuck(index : int, dropX : double, dropY : double, dropZ : double) : void
   }

   class MathUtils <<utility>> {
     +normalizeAngle(angle : double) : double
     +getRobotYaw(robot : Supervisor) : double
     +distance2D(a : double[], b : double[]) : double
   }

   class Supervisor
   class Motor
   class DistanceSensor
   class Camera
   class TouchSensor
   class PositionSensor
   class Node
   class Field

   FourWheelsCollisionAvoidance --> TERBot : cree
   TERBot *-- DriveBase : possede
   TERBot *-- RobotSensors : possede
   TERBot *-- Arm : possede
   TERBot *-- PuckManager : possede
   TERBot --> RobotMode : utilise
   TERBot --> MathUtils : utilise
   TERBot --> Supervisor : utilise
   DriveBase *-- "4" Wheel : possede
   Wheel --> Motor : encapsule
   RobotSensors --> DistanceSensor : utilise
   RobotSensors --> Camera : utilise
   RobotSensors --> TouchSensor : utilise
   Arm --> Motor : utilise
   Arm --> PositionSensor : utilise
   PuckManager --> Supervisor : utilise
   PuckManager --> Node : utilise
   PuckManager --> Field : utilise
   PuckManager --> MathUtils : utilise

   @enduml

Relations principales
---------------------

``FourWheelsCollisionAvoidance`` crée ``TERBot``.

``TERBot`` possède ``DriveBase``, ``RobotSensors``, ``Arm`` et ``PuckManager``.

``DriveBase`` possède quatre ``Wheel``. ``Wheel`` encapsule un ``Motor`` Webots. ``RobotSensors`` encapsule les capteurs Webots. ``Arm`` encapsule les moteurs du bras et de la pince. ``PuckManager`` encapsule les données liées aux palets.
