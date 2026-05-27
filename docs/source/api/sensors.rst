Sensors API
===========

Overview
--------

The ``api.sensors`` package contains the classes used to read information from the robot sensors.

Sensors are very important because they allow the robot to perceive its environment.

In this project, the robot uses several types of sensors:

* distance sensors to detect objects or walls;
* a touch sensor to detect physical contact;
* a camera used as a color sensor;
* a small ``RGBColor`` class to represent colors.

The goal of this package is to make sensor usage simple and understandable.

For example, instead of directly manipulating Webots sensors, the behavior can write:

.. code-block:: java

   robot.sensors().frontDistance();
   robot.sensors().frontDetectsObject(350.0);
   robot.sensors().isFrontTouched();
   robot.sensors().seesRed();

Why this package exists
-----------------------

Without this API, the behavior code would have to directly retrieve and read every Webots sensor.

For example:

.. code-block:: java

   DistanceSensor dsFront = robot.getDistanceSensor("ds_front");
   dsFront.enable(timeStep);
   double value = dsFront.getValue();

With the API, the same idea becomes:

.. code-block:: java

   double value = robot.sensors().frontDistance();

This makes the behavior easier to read.

The behavior can focus on the robot mission instead of low-level Webots code.

Package location
----------------

The files are located in:

.. code-block:: text

   controllers/
   └── FourWheelsCollisionAvoidanceAPI/
       └── api/
           └── sensors/
               ├── ColorSensorWrapper.java
               ├── DistanceSensorWrapper.java
               ├── RGBColor.java
               ├── SensorManager.java
               └── TouchSensorWrapper.java

Package declaration
-------------------

Each file starts with:

.. code-block:: java

   package api.sensors;

This means that the files must be placed in:

.. code-block:: text

   api/sensors/

If the folder and package name do not match, Java will not compile the project.

General organization
--------------------

The sensor system is organized around the ``SensorManager`` class.

.. code-block:: text

   SensorManager
      |
      ├── DistanceSensorWrapper -> ds_right
      ├── DistanceSensorWrapper -> ds_left
      ├── DistanceSensorWrapper -> ds_front
      ├── ColorSensorWrapper    -> color_sensor
      └── TouchSensorWrapper    -> touch_front

The behavior usually does not access each wrapper directly.

Instead, it uses ``SensorManager`` through:

.. code-block:: java

   robot.sensors()

Main classes
------------

.. list-table::
   :header-rows: 1

   * - Class
     - Role
   * - ``DistanceSensorWrapper``
     - Reads one Webots distance sensor.
   * - ``TouchSensorWrapper``
     - Reads one Webots touch sensor and detects new contacts.
   * - ``ColorSensorWrapper``
     - Uses a Webots camera to read an average RGB color.
   * - ``RGBColor``
     - Stores and analyzes red, green and blue values.
   * - ``SensorManager``
     - Groups all sensors and provides simple access methods.

DistanceSensorWrapper
---------------------

Role of the class
~~~~~~~~~~~~~~~~~

The ``DistanceSensorWrapper`` class represents one distance sensor.

A distance sensor is used to detect objects near the robot.

In this project, distance sensors are used to detect:

* walls;
* obstacles;
* objects in front of the robot;
* objects on the left and right sides.

Source code
~~~~~~~~~~~

.. code-block:: java

   package api.sensors;

   import com.cyberbotics.webots.controller.DistanceSensor;

   public class DistanceSensorWrapper {
     private final DistanceSensor sensor;

     public DistanceSensorWrapper(DistanceSensor sensor, int timeStep) {
       this.sensor = sensor;
       if (this.sensor != null) { this.sensor.enable(timeStep); }
     }

     public double getValue() { return sensor == null ? 0.0 : sensor.getValue(); }
     public boolean detectsObject(double threshold) { return getValue() > threshold; }
     public boolean exists() { return sensor != null; }
   }

Constructor explanation
~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public DistanceSensorWrapper(DistanceSensor sensor, int timeStep) {
     this.sensor = sensor;
     if (this.sensor != null) { this.sensor.enable(timeStep); }
   }

The constructor receives:

* a Webots ``DistanceSensor``;
* the simulation ``timeStep``.

The sensor is stored inside the class.

Then it is enabled.

In Webots, a sensor must be enabled before it can return values.

The condition:

.. code-block:: java

   if (this.sensor != null)

prevents the program from crashing if the sensor was not found.

getValue
~~~~~~~~

.. code-block:: java

   public double getValue() {
     return sensor == null ? 0.0 : sensor.getValue();
   }

This method returns the current value of the distance sensor.

If the sensor does not exist, it returns ``0.0``.

This makes the API safer.

Example:

.. code-block:: java

   double frontValue = robot.sensors().frontDistance();

detectsObject
~~~~~~~~~~~~~

.. code-block:: java

   public boolean detectsObject(double threshold) {
     return getValue() > threshold;
   }

This method checks whether the sensor value is greater than a threshold.

Example:

.. code-block:: java

   if (robot.sensors().frontDetectsObject(350.0)) {
     robot.motors().stop();
   }

In this example, the robot stops if the front sensor value is greater than ``350.0``.

Important note: the meaning of the value depends on the Webots sensor configuration.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return sensor != null;
   }

This method checks if the sensor was found in Webots.

Example:

.. code-block:: java

   if (!robot.sensors().frontDistanceSensor().exists()) {
     System.out.println("Front distance sensor not found");
   }

TouchSensorWrapper
------------------

Role of the class
~~~~~~~~~~~~~~~~~

The ``TouchSensorWrapper`` class represents the front touch sensor.

The touch sensor is used to detect physical contact.

For example, the robot can use it when:

* it touches a puck;
* it touches a wall;
* it touches the drop zone;
* it touches another obstacle.

This class stores both the previous and current touch states.

This makes it possible to detect a new contact.

Source code
~~~~~~~~~~~

.. code-block:: java

   package api.sensors;

   import com.cyberbotics.webots.controller.TouchSensor;

   public class TouchSensorWrapper {
     private final TouchSensor sensor;
     private boolean previousPressed;
     private boolean currentPressed;

     public TouchSensorWrapper(TouchSensor sensor, int timeStep) {
       this.sensor = sensor;
       this.previousPressed = false;
       this.currentPressed = false;
       if (this.sensor != null) { this.sensor.enable(timeStep); }
     }

     public void update() { previousPressed = currentPressed; currentPressed = sensor != null && sensor.getValue() > 0.0; }
     public boolean isPressed() { return currentPressed; }
     public boolean wasJustPressed() { return currentPressed && !previousPressed; }
     public boolean wasJustReleased() { return !currentPressed && previousPressed; }
     public boolean exists() { return sensor != null; }
   }

Why previous and current states are needed
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The touch sensor can be pressed for several simulation steps.

If the robot only uses:

.. code-block:: java

   sensor.getValue() > 0.0

then the same contact can be detected many times.

To avoid this, the wrapper stores:

.. code-block:: java

   previousPressed
   currentPressed

This allows the API to know if the sensor:

* is currently pressed;
* has just been pressed;
* has just been released.

update
~~~~~~

.. code-block:: java

   public void update() {
     previousPressed = currentPressed;
     currentPressed = sensor != null && sensor.getValue() > 0.0;
   }

This method must be called at each simulation step.

It updates the touch state.

The order is important:

1. the old current state becomes the previous state;
2. the new Webots value becomes the current state.

In the project, this method is called by ``SensorManager.update()``.

isPressed
~~~~~~~~~

.. code-block:: java

   public boolean isPressed() {
     return currentPressed;
   }

This method returns ``true`` while the sensor is pressed.

Example:

.. code-block:: java

   if (robot.sensors().isFrontTouched()) {
     robot.motors().stop();
   }

wasJustPressed
~~~~~~~~~~~~~~

.. code-block:: java

   public boolean wasJustPressed() {
     return currentPressed && !previousPressed;
   }

This method returns ``true`` only when a new contact begins.

It is useful when an action must happen only once.

Example:

.. code-block:: java

   if (robot.sensors().wasFrontJustTouched()) {
     System.out.println("New contact detected");
   }

wasJustReleased
~~~~~~~~~~~~~~~

.. code-block:: java

   public boolean wasJustReleased() {
     return !currentPressed && previousPressed;
   }

This method returns ``true`` when the sensor was pressed before and is now released.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return sensor != null;
   }

This method checks if the touch sensor was found.

ColorSensorWrapper
------------------

Role of the class
~~~~~~~~~~~~~~~~~

The ``ColorSensorWrapper`` class uses a Webots camera as a color sensor.

The camera takes an image.

The wrapper reads all pixels in the image and computes the average RGB color.

In this project, this can be used to detect red objects, such as red pucks.

Source code
~~~~~~~~~~~

.. code-block:: java

   package api.sensors;

   import com.cyberbotics.webots.controller.Camera;

   public class ColorSensorWrapper {
     private final Camera camera;

     public ColorSensorWrapper(Camera camera, int timeStep) {
       this.camera = camera;
       if (this.camera != null) { this.camera.enable(timeStep); }
     }

     public RGBColor getRGB() {
       if (camera == null) { return new RGBColor(0, 0, 0); }

       int[] image = camera.getImage();
       int width = camera.getWidth();
       int height = camera.getHeight();

       if (image == null || width <= 0 || height <= 0) {
         return new RGBColor(0, 0, 0);
       }

       int pixelCount = width * height;
       int sumRed = 0, sumGreen = 0, sumBlue = 0;

       for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
           sumRed += Camera.imageGetRed(image, width, x, y);
           sumGreen += Camera.imageGetGreen(image, width, x, y);
           sumBlue += Camera.imageGetBlue(image, width, x, y);
         }
       }

       return new RGBColor(sumRed / pixelCount, sumGreen / pixelCount, sumBlue / pixelCount);
     }

     public boolean seesRed() { return getRGB().isRed(); }
     public boolean exists() { return camera != null; }
   }

Constructor explanation
~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public ColorSensorWrapper(Camera camera, int timeStep) {
     this.camera = camera;
     if (this.camera != null) { this.camera.enable(timeStep); }
   }

The constructor receives:

* a Webots camera;
* the simulation ``timeStep``.

The camera is enabled if it exists.

In Webots, a camera must be enabled before its image can be read.

getRGB
~~~~~~

.. code-block:: java

   public RGBColor getRGB()

This method returns the average color seen by the camera.

If the camera does not exist, it returns:

.. code-block:: java

   new RGBColor(0, 0, 0)

This corresponds to black.

Image reading
^^^^^^^^^^^^^

The image is read with:

.. code-block:: java

   int[] image = camera.getImage();

The image size is read with:

.. code-block:: java

   int width = camera.getWidth();
   int height = camera.getHeight();

If the image is invalid, the method returns black.

Average color calculation
^^^^^^^^^^^^^^^^^^^^^^^^^

The method loops through all pixels:

.. code-block:: java

   for (int x = 0; x < width; x++) {
     for (int y = 0; y < height; y++) {
       sumRed += Camera.imageGetRed(image, width, x, y);
       sumGreen += Camera.imageGetGreen(image, width, x, y);
       sumBlue += Camera.imageGetBlue(image, width, x, y);
     }
   }

Then it divides by the number of pixels:

.. code-block:: java

   return new RGBColor(sumRed / pixelCount, sumGreen / pixelCount, sumBlue / pixelCount);

This gives the average RGB color of the camera image.

seesRed
~~~~~~~

.. code-block:: java

   public boolean seesRed() {
     return getRGB().isRed();
   }

This method returns ``true`` if the camera sees a color considered red.

The rule is defined in the ``RGBColor`` class.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return camera != null;
   }

This method checks if the camera was found in Webots.

RGBColor
--------

Role of the class
~~~~~~~~~~~~~~~~~

The ``RGBColor`` class represents a color with three values:

* red;
* green;
* blue.

It is mainly used by ``ColorSensorWrapper``.

Source code
~~~~~~~~~~~

.. code-block:: java

   package api.sensors;

   public class RGBColor {
     private final int red;
     private final int green;
     private final int blue;

     public RGBColor(int red, int green, int blue) {
       this.red = red;
       this.green = green;
       this.blue = blue;
     }

     public int red() { return red; }
     public int green() { return green; }
     public int blue() { return blue; }
     public boolean isRed() { return red > 150 && green < 100 && blue < 100; }
     public boolean isDominantRed() { return red > green && red > blue; }

     @Override
     public String toString() {
       return "(" + red + "," + green + "," + blue + ")";
     }
   }

Values
~~~~~~

Each color is stored with three integer values.

.. list-table::
   :header-rows: 1

   * - Value
     - Meaning
   * - ``red``
     - Amount of red.
   * - ``green``
     - Amount of green.
   * - ``blue``
     - Amount of blue.

In most cases, values are between ``0`` and ``255``.

isRed
~~~~~

.. code-block:: java

   public boolean isRed() {
     return red > 150 && green < 100 && blue < 100;
   }

This method checks if the color is clearly red.

The rule is:

.. code-block:: text

   red > 150
   green < 100
   blue < 100

This is useful to detect a strong red object.

isDominantRed
~~~~~~~~~~~~~

.. code-block:: java

   public boolean isDominantRed() {
     return red > green && red > blue;
   }

This method checks if red is the strongest color component.

It is less strict than ``isRed``.

toString
~~~~~~~~

.. code-block:: java

   public String toString() {
     return "(" + red + "," + green + "," + blue + ")";
   }

This method is useful for debug logs.

Example output:

.. code-block:: text

   (180,45,30)

SensorManager
-------------

Role of the class
~~~~~~~~~~~~~~~~~

The ``SensorManager`` class is the main class of the sensors API.

It groups all sensors in one object.

This makes the rest of the code much easier to understand.

Instead of using each wrapper directly, the behavior can use:

.. code-block:: java

   robot.sensors().frontDistance();
   robot.sensors().isFrontTouched();
   robot.sensors().seesRed();

Source code
~~~~~~~~~~~

.. code-block:: java

   package api.sensors;

   import com.cyberbotics.webots.controller.Supervisor;

   public class SensorManager {
     private final DistanceSensorWrapper rightDistanceSensor;
     private final DistanceSensorWrapper leftDistanceSensor;
     private final DistanceSensorWrapper frontDistanceSensor;
     private final ColorSensorWrapper colorSensor;
     private final TouchSensorWrapper frontTouchSensor;

     public SensorManager(Supervisor robot, int timeStep) {
       this.rightDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_right"), timeStep);
       this.leftDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_left"), timeStep);
       this.frontDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_front"), timeStep);
       this.colorSensor = new ColorSensorWrapper(robot.getCamera("color_sensor"), timeStep);
       this.frontTouchSensor = new TouchSensorWrapper(robot.getTouchSensor("touch_front"), timeStep);
     }

     public void update() { frontTouchSensor.update(); }
     public double rightDistance() { return rightDistanceSensor.getValue(); }
     public double leftDistance() { return leftDistanceSensor.getValue(); }
     public double frontDistance() { return frontDistanceSensor.getValue(); }
     public boolean frontDetectsObject(double threshold) { return frontDistanceSensor.detectsObject(threshold); }
     public boolean leftDetectsObject(double threshold) { return leftDistanceSensor.detectsObject(threshold); }
     public boolean rightDetectsObject(double threshold) { return rightDistanceSensor.detectsObject(threshold); }
     public boolean isFrontTouched() { return frontTouchSensor.isPressed(); }
     public boolean wasFrontJustTouched() { return frontTouchSensor.wasJustPressed(); }
     public RGBColor color() { return colorSensor.getRGB(); }
     public boolean seesRed() { return colorSensor.seesRed(); }
     public DistanceSensorWrapper frontDistanceSensor() { return frontDistanceSensor; }
     public DistanceSensorWrapper leftDistanceSensor() { return leftDistanceSensor; }
     public DistanceSensorWrapper rightDistanceSensor() { return rightDistanceSensor; }
     public TouchSensorWrapper frontTouchSensor() { return frontTouchSensor; }
     public ColorSensorWrapper colorSensor() { return colorSensor; }
   }

Sensor names
~~~~~~~~~~~~

The ``SensorManager`` retrieves sensors using their Webots names.

.. list-table::
   :header-rows: 1

   * - Webots name
     - API attribute
     - Role
   * - ``ds_right``
     - ``rightDistanceSensor``
     - Detects objects on the right.
   * - ``ds_left``
     - ``leftDistanceSensor``
     - Detects objects on the left.
   * - ``ds_front``
     - ``frontDistanceSensor``
     - Detects objects in front.
   * - ``color_sensor``
     - ``colorSensor``
     - Reads the color in front of the robot.
   * - ``touch_front``
     - ``frontTouchSensor``
     - Detects front physical contact.

These names must match the names in the Webots robot model.

update
~~~~~~

.. code-block:: java

   public void update() {
     frontTouchSensor.update();
   }

This method updates the touch sensor state.

It should be called at each simulation step.

In the full behavior, this is done with:

.. code-block:: java

   robot.sensors().update();

Distance access methods
~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public double rightDistance()
   public double leftDistance()
   public double frontDistance()

These methods return the current values of the distance sensors.

Example:

.. code-block:: java

   double front = robot.sensors().frontDistance();

Object detection methods
~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public boolean frontDetectsObject(double threshold)
   public boolean leftDetectsObject(double threshold)
   public boolean rightDetectsObject(double threshold)

These methods check whether a distance sensor value is greater than a threshold.

Example:

.. code-block:: java

   if (robot.sensors().frontDetectsObject(350.0)) {
     robot.motors().turnLeft(3.0);
   }

Touch methods
~~~~~~~~~~~~~

.. code-block:: java

   public boolean isFrontTouched()
   public boolean wasFrontJustTouched()

``isFrontTouched`` returns ``true`` while the sensor is pressed.

``wasFrontJustTouched`` returns ``true`` only when a new contact begins.

Color methods
~~~~~~~~~~~~~

.. code-block:: java

   public RGBColor color()
   public boolean seesRed()

``color`` returns the average color seen by the camera.

``seesRed`` returns ``true`` if this color is considered red.

Example:

.. code-block:: java

   RGBColor color = robot.sensors().color();
   System.out.println("RGB = " + color);

   if (robot.sensors().seesRed()) {
     System.out.println("Red object detected");
   }

How sensors are used in the robot mission
-----------------------------------------

The sensors are used in several parts of the robot behavior.

.. list-table::
   :header-rows: 1

   * - Sensor
     - Used for
   * - ``ds_front``
     - Detecting an object in front of the robot.
   * - ``ds_left``
     - Detecting obstacles or walls on the left.
   * - ``ds_right``
     - Detecting obstacles or walls on the right.
   * - ``touch_front``
     - Detecting physical contact with a puck or obstacle.
   * - ``color_sensor``
     - Detecting colors, especially red objects.

Example in a simple behavior
----------------------------

A very simple obstacle avoidance behavior can use the sensors like this:

.. code-block:: java

   robot.sensors().update();

   if (robot.sensors().frontDetectsObject(350.0)) {
     robot.motors().turnLeft(3.0);
   } else {
     robot.motors().forward(4.0);
   }

This means:

.. code-block:: text

   If something is detected in front:
       turn left
   Otherwise:
       move forward

Example in the puck collection behavior
---------------------------------------

In the full collection behavior, the sensors are used to:

* detect walls;
* detect obstacles;
* detect contact with a puck;
* detect contact with the drop zone;
* print debug information.

For example, contact detection uses:

.. code-block:: java

   robot.sensors().isFrontTouched();

Distance values are also printed in the debug logs:

.. code-block:: java

   robot.sensors().leftDistance()
   robot.sensors().rightDistance()
   robot.sensors().frontDistance()

Naming convention
-----------------

The sensor API depends on the names of the sensors in Webots.

The robot must contain:

.. code-block:: text

   ds_right
   ds_left
   ds_front
   color_sensor
   touch_front

If one of these names is wrong, the API will not be able to find the corresponding sensor.

Debugging
---------

If a distance sensor does not work, check:

* the sensor exists in the Webots robot model;
* the sensor name is correct;
* the sensor is enabled by the wrapper;
* the controller has been recompiled;
* the sensor direction is correct;
* the threshold value is adapted to the map.

If the touch sensor does not work, check:

* the sensor is named ``touch_front``;
* the sensor has a correct position on the robot;
* the sensor is physically able to touch objects;
* the touched object has a collision shape;
* ``robot.sensors().update()`` is called at each step.

If the color sensor always returns ``(0,0,0)``, check:

* the camera is named ``color_sensor``;
* the camera is enabled;
* the camera is oriented toward the object;
* the object is visible by the camera.

To visualize distance sensor rays in Webots, you can enable:

.. code-block:: text

   View -> Optional Rendering -> Show DistanceSensor Rays

This helps understand what the sensors are detecting.

Summary
-------

The ``api.sensors`` package makes sensor use easier and clearer.

It contains:

* ``DistanceSensorWrapper`` to read distance sensors;
* ``TouchSensorWrapper`` to detect physical contact;
* ``ColorSensorWrapper`` to read RGB color from a camera;
* ``RGBColor`` to store and analyze color values;
* ``SensorManager`` to group all sensors in one place.

The behavior can then use simple methods such as:

.. code-block:: java

   robot.sensors().frontDistance();
   robot.sensors().frontDetectsObject(350.0);
   robot.sensors().isFrontTouched();
   robot.sensors().seesRed();

This makes the robot behavior easier to read and easier to understand.