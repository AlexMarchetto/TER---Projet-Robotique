#####
Robot
#####

********
Hardware
********

Structure of the robot
======================

Components
==========

Sensors
=======

********
Software
********

This part is intended to provide information about the software side of the robot, especially the controller used to control its behavior in Webots. It describes the main functions used by the controller, their purpose, and their role in the robot's logic.

Functions
=========

getRedValue
-----------

The ``getRedValue`` function is used to read the red component detected by the robot's color sensor.

This function gets the image captured by the camera and calculates the average red value of all pixels. If the camera image contains only one pixel, the function directly returns the red value of this pixel.

This function is mainly used to help the robot detect whether the object in front of it is red.

.. code-block:: java

   public static int getRedValue(Camera colorSensor) {
     int[] image = colorSensor.getImage();
     int w = colorSensor.getWidth();
     int h = colorSensor.getHeight();

     if (w == 1 && h == 1) {
       return Camera.imageGetRed(image, w, 0, 0);
     }

     int total = 0;
     int count = 0;

     for (int x = 0; x < w; x++) {
       for (int y = 0; y < h; y++) {
         total += Camera.imageGetRed(image, w, x, y);
         count++;
       }
     }

     return count == 0 ? 0 : total / count;
   }


getGreenValue
-------------

The ``getGreenValue`` function is used to read the green component detected by the robot's color sensor.

It works in the same way as ``getRedValue``. It gets the image from the camera, reads the green value of each pixel, and returns the average green value.

This function is used together with the red and blue values to determine the color of an object.

.. code-block:: java

   public static int getGreenValue(Camera colorSensor) {
     int[] image = colorSensor.getImage();
     int w = colorSensor.getWidth();
     int h = colorSensor.getHeight();

     if (w == 1 && h == 1) {
       return Camera.imageGetGreen(image, w, 0, 0);
     }

     int total = 0;
     int count = 0;

     for (int x = 0; x < w; x++) {
       for (int y = 0; y < h; y++) {
         total += Camera.imageGetGreen(image, w, x, y);
         count++;
       }
     }

     return count == 0 ? 0 : total / count;
   }


getBlueValue
------------

The ``getBlueValue`` function is used to read the blue component detected by the robot's color sensor.

It gets the image captured by the camera and calculates the average blue value of all pixels. This value is then used with the red and green values to identify the detected color.

In the controller, this function helps to check if the detected object is red by verifying that the blue value remains low.

.. code-block:: java

   public static int getBlueValue(Camera colorSensor) {
     int[] image = colorSensor.getImage();
     int w = colorSensor.getWidth();
     int h = colorSensor.getHeight();

     if (w == 1 && h == 1) {
       return Camera.imageGetBlue(image, w, 0, 0);
     }

     int total = 0;
     int count = 0;

     for (int x = 0; x < w; x++) {
       for (int y = 0; y < h; y++) {
         total += Camera.imageGetBlue(image, w, x, y);
         count++;
       }
     }

     return count == 0 ? 0 : total / count;
   }


attachPuckToRobot
-----------------

The ``attachPuckToRobot`` function is used to virtually attach the puck to the robot during transport.

When the robot has grabbed the puck, this function moves the puck in front of the robot at each simulation step. This makes the transport more stable and avoids problems caused by the physics engine, such as the puck shaking, slipping, or being ejected from the gripper.

The function first checks if the puck exists. Then it gets the robot position and orientation. After that, it calculates a position slightly in front of the robot and updates the puck position in the Webots world.

The ``resetPhysics`` method is used to stabilize the puck after moving it.

.. code-block:: java

   public static void attachPuckToRobot(Supervisor robot, Node puckNode, Field puckTranslationField) {
     if (puckNode == null || puckTranslationField == null) {
       return;
     }

     Node self = robot.getSelf();

     double[] robotPosition = self.getPosition();
     double[] orientation = self.getOrientation();

     double localX = 0.15;
     double localY = 0.0;
     double localZ = 0.06;

     double worldX = robotPosition[0]
         + orientation[0] * localX
         + orientation[1] * localY
         + orientation[2] * localZ;

     double worldY = robotPosition[1]
         + orientation[3] * localX
         + orientation[4] * localY
         + orientation[5] * localZ;

     double worldZ = robotPosition[2]
         + orientation[6] * localX
         + orientation[7] * localY
         + orientation[8] * localZ;

     puckTranslationField.setSFVec3f(new double[] {worldX, worldY, worldZ});

     // Stabilise le palet à chaque step
     puckNode.resetPhysics();
   }


main
----

The ``main`` function is the main part of the controller. It initializes the robot, the sensors, the motors, and the variables used to control the robot behavior.

It also contains the main control loop of the robot. This loop is executed at each simulation step. Inside this loop, the robot reads its sensors, detects the puck, avoids obstacles, controls the arm and the gripper, and updates the wheel speeds.

The robot behavior is organized using several modes:

* ``SEARCH``: the robot moves forward and searches for the red puck.
* ``APPROACH_PUCK``: the robot moves slowly toward the detected puck.
* ``LOWER_ARM``: the robot stops and lowers its arm.
* ``CLOSE_GRIPPER``: the robot closes its gripper to grab the puck.
* ``LIFT_ARM``: the robot lifts its arm with the puck.
* ``TRANSPORT``: the robot moves forward while carrying the puck.

The ``main`` function is therefore the central function of the controller.

.. code-block:: java

   public static void main(String[] args) {

     Supervisor robot = new Supervisor();
     int timeStep = 64;

     Node puckNode = robot.getFromDef("PALET");
     Field puckTranslationField = null;

     if (puckNode != null) {
       puckTranslationField = puckNode.getField("translation");
       System.out.println("Palet PALET trouve par le Supervisor.");
     } else {
       System.out.println("ATTENTION : aucun DEF PALET trouve dans le monde.");
       System.out.println("Ajoute DEF PALET devant le Solid du palet dans Robot.wbt.");
     }

     DistanceSensor[] ds = new DistanceSensor[2];
     String[] dsNames = {"ds_right", "ds_left"};

     for (int i = 0; i < 2; i++) {
       ds[i] = robot.getDistanceSensor(dsNames[i]);
       ds[i].enable(timeStep);
     }

     DistanceSensor dsFront = robot.getDistanceSensor("ds_front");
     dsFront.enable(timeStep);

     Camera colorSensor = robot.getCamera("color_sensor");
     colorSensor.enable(timeStep);

     TouchSensor touchFront = robot.getTouchSensor("touch_front");
     touchFront.enable(timeStep);

     Motor[] wheels = new Motor[4];
     String[] wheelNames = {"wheel1", "wheel2", "wheel3", "wheel4"};

     for (int i = 0; i < 4; i++) {
       wheels[i] = robot.getMotor(wheelNames[i]);
       wheels[i].setPosition(Double.POSITIVE_INFINITY);
       wheels[i].setVelocity(0.0);
     }

     Motor armMotor = robot.getMotor("arm_motor");
     Motor gripperLeftMotor = robot.getMotor("gripper_left_motor");
     Motor gripperRightMotor = robot.getMotor("gripper_right_motor");

     PositionSensor armSensor = robot.getPositionSensor("arm_sensor");
     armSensor.enable(timeStep);

     double ARM_UP = -0.65;
     double ARM_DOWN = 0.35;

     double GRIPPER_OPEN_LEFT = 0.2;
     double GRIPPER_OPEN_RIGHT = -0.2;

     double GRIPPER_CLOSED_LEFT = -0.55;
     double GRIPPER_CLOSED_RIGHT = 0.55;

     armMotor.setPosition(ARM_UP);
     gripperLeftMotor.setPosition(GRIPPER_OPEN_LEFT);
     gripperRightMotor.setPosition(GRIPPER_OPEN_RIGHT);

     int avoidObstacleCounter = 0;
     int stepCounter = 0;
     int actionCounter = 0;

     boolean puckDetected = false;
     boolean puckTouched = false;
     boolean previousTouched = false;
     boolean puckAttached = false;

     String robotMode = "SEARCH";
     String lastMode = "";

     while (robot.step(timeStep) != -1) {

       stepCounter++;

       double leftSpeed = 1.0;
       double rightSpeed = 1.0;

       double rightSensorValue = ds[0].getValue();
       double leftSensorValue = ds[1].getValue();
       double frontValue = dsFront.getValue();

       int red = getRedValue(colorSensor);
       int green = getGreenValue(colorSensor);
       int blue = getBlueValue(colorSensor);

       boolean redDetected = red > 150 && green < 100 && blue < 100;

       double touchValue = touchFront.getValue();
       boolean touched = touchValue > 0.0;
       boolean newContact = touched && !previousTouched;

       boolean objectAt30cm = frontValue >= 500.0;

       if (robotMode.equals("SEARCH") && !puckDetected && objectAt30cm && redDetected) {
         puckDetected = true;
         robotMode = "APPROACH_PUCK";
       }

       if (stepCounter > 50 && newContact && !puckTouched) {
         puckDetected = true;
         puckTouched = true;
         robotMode = "LOWER_ARM";
         actionCounter = 0;
       }

       if (robotMode.equals("SEARCH")) {
         leftSpeed = 1.0;
         rightSpeed = 1.0;

         if (avoidObstacleCounter > 0) {
           avoidObstacleCounter--;
           leftSpeed = 1.0;
           rightSpeed = -1.0;
         } else {
           if (rightSensorValue > 500.0 || leftSensorValue > 500.0) {
             avoidObstacleCounter = 100;
           }
         }
       }

       else if (robotMode.equals("APPROACH_PUCK")) {
         leftSpeed = 0.5;
         rightSpeed = 0.5;
       }

       else if (robotMode.equals("LOWER_ARM")) {
         leftSpeed = 0.0;
         rightSpeed = 0.0;

         armMotor.setPosition(ARM_DOWN);
         gripperLeftMotor.setPosition(GRIPPER_OPEN_LEFT);
         gripperRightMotor.setPosition(GRIPPER_OPEN_RIGHT);

         actionCounter++;

         if (actionCounter > 60) {
           robotMode = "CLOSE_GRIPPER";
           actionCounter = 0;
         }
       }

       else if (robotMode.equals("CLOSE_GRIPPER")) {
         leftSpeed = 0.0;
         rightSpeed = 0.0;

         gripperLeftMotor.setPosition(GRIPPER_CLOSED_LEFT);
         gripperRightMotor.setPosition(GRIPPER_CLOSED_RIGHT);

         actionCounter++;

         if (actionCounter > 60) {
           puckAttached = true;

           robotMode = "LIFT_ARM";
           actionCounter = 0;
         }
       }

       else if (robotMode.equals("LIFT_ARM")) {
         leftSpeed = 0.0;
         rightSpeed = 0.0;

         armMotor.setPosition(ARM_UP);

         actionCounter++;

         if (actionCounter > 80) {
           robotMode = "TRANSPORT";
           actionCounter = 0;
         }
       }

       else if (robotMode.equals("TRANSPORT")) {
         leftSpeed = 0.5;
         rightSpeed = 0.5;
       }

       if (puckAttached) {
         attachPuckToRobot(robot, puckNode, puckTranslationField);
       }

       if (!robotMode.equals(lastMode)) {
         System.out.println("Changement de mode : " + lastMode + " -> " + robotMode);
         lastMode = robotMode;
       }

       wheels[0].setVelocity(leftSpeed);
       wheels[1].setVelocity(rightSpeed);
       wheels[2].setVelocity(leftSpeed);
       wheels[3].setVelocity(rightSpeed);

       previousTouched = touched;
     }

     robot.delete();
   }


