Sensors API
===========

Overview
--------

The ``api.sensors`` package contains the classes used to access and simplify the robot sensors.

The goal of this package is to hide the low-level Webots sensor code behind a clearer API.

Instead of directly manipulating Webots sensors in the behavior classes, the robot can use methods such as:

.. code-block:: java

   bot.sensors().frontDistance();
   bot.sensors().frontDetectsObject(350.0);
   bot.sensors().isFrontTouched();
   bot.sensors().seesRed();

This makes the controller easier to read and easier to maintain.

Package location
----------------

The sensors API is located in:

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

Each file in this package starts with:

.. code-block:: java

   package api.sensors;

This means that the files must be located in:

.. code-block:: text

   api/sensors/

If the package declaration and the folder path do not match, Java will not compile the project correctly.

Main classes
------------

The package contains five main classes:

.. list-table::
   :header-rows: 1

   * - Class
     - Role
   * - ``DistanceSensorWrapper``
     - Simplifies access to a Webots distance sensor.
   * - ``TouchSensorWrapper``
     - Simplifies access to a Webots touch sensor and detects new contacts.
   * - ``ColorSensorWrapper``
     - Uses a Webots camera as a color sensor.
   * - ``RGBColor``
     - Represents a color with red, green, and blue values.
   * - ``SensorManager``
     - Groups all robot sensors into one access point.

General organization
--------------------

The sensor system is organized like this:

.. code-block:: text

   SensorManager
      |
      ├── rightDistanceSensor -> DistanceSensorWrapper -> Webots DistanceSensor "ds_right"
      ├── leftDistanceSensor  -> DistanceSensorWrapper -> Webots DistanceSensor "ds_left"
      ├── frontDistanceSensor -> DistanceSensorWrapper -> Webots DistanceSensor "ds_front"
      ├── colorSensor         -> ColorSensorWrapper    -> Webots Camera "color_sensor"
      └── frontTouchSensor    -> TouchSensorWrapper    -> Webots TouchSensor "touch_front"

The behavior classes should generally use ``SensorManager`` instead of directly creating or accessing each wrapper.

DistanceSensorWrapper
---------------------

Overview
~~~~~~~~

The ``DistanceSensorWrapper`` class wraps a Webots ``DistanceSensor``.

It provides simple methods to:

* enable the sensor;
* read its value;
* check if an object is detected;
* check if the sensor exists.

Class code
~~~~~~~~~~

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

Attribute
~~~~~~~~~

The class contains one attribute:

.. code-block:: java

   private final DistanceSensor sensor;

This attribute stores the Webots distance sensor.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public DistanceSensorWrapper(DistanceSensor sensor, int timeStep) {
     this.sensor = sensor;
     if (this.sensor != null) { this.sensor.enable(timeStep); }
   }

The constructor receives:

* a Webots ``DistanceSensor``;
* the simulation ``timeStep``.

If the sensor exists, it is enabled with:

.. code-block:: java

   this.sensor.enable(timeStep);

In Webots, a sensor must be enabled before its values can be read.

The condition:

.. code-block:: java

   if (this.sensor != null)

prevents the controller from crashing if the sensor is missing or if its name is incorrect in the robot PROTO.

getValue
~~~~~~~~

.. code-block:: java

   public double getValue() {
     return sensor == null ? 0.0 : sensor.getValue();
   }

This method returns the current sensor value.

If the sensor does not exist, it returns ``0.0``.

This makes the API safer because a missing sensor does not immediately crash the program.

detectsObject
~~~~~~~~~~~~~

.. code-block:: java

   public boolean detectsObject(double threshold) {
     return getValue() > threshold;
   }

This method checks if the distance sensor value is higher than a given threshold.

Example:

.. code-block:: java

   if (bot.sensors().frontDistanceSensor().detectsObject(350.0)) {
     bot.motors().stop();
   }

The meaning of the threshold depends on the sensor configuration in Webots, especially the ``lookupTable``.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return sensor != null;
   }

This method returns ``true`` if the Webots sensor was correctly found.

It is useful for debugging.

Example:

.. code-block:: java

   if (!bot.sensors().frontDistanceSensor().exists()) {
     System.out.println("Front distance sensor not found");
   }

TouchSensorWrapper
------------------

Overview
~~~~~~~~

The ``TouchSensorWrapper`` class wraps a Webots ``TouchSensor``.

It is used to detect physical contact.

Unlike a simple direct read of the sensor value, this wrapper stores both the previous and current state of the sensor.

This allows the API to detect:

* if the sensor is currently pressed;
* if it has just been pressed;
* if it has just been released.

Class code
~~~~~~~~~~

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

     public void update() {
       previousPressed = currentPressed;
       currentPressed = sensor != null && sensor.getValue() > 0.0;
     }

     public boolean isPressed() { return currentPressed; }
     public boolean wasJustPressed() { return currentPressed && !previousPressed; }
     public boolean wasJustReleased() { return !currentPressed && previousPressed; }
     public boolean exists() { return sensor != null; }
   }

Attributes
~~~~~~~~~~

The class contains three attributes:

.. code-block:: java

   private final TouchSensor sensor;
   private boolean previousPressed;
   private boolean currentPressed;

``sensor``
^^^^^^^^^^

Stores the Webots touch sensor.

``previousPressed``
^^^^^^^^^^^^^^^^^^^

Stores the touch state from the previous simulation step.

``currentPressed``
^^^^^^^^^^^^^^^^^^

Stores the touch state from the current simulation step.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public TouchSensorWrapper(TouchSensor sensor, int timeStep) {
     this.sensor = sensor;
     this.previousPressed = false;
     this.currentPressed = false;
     if (this.sensor != null) { this.sensor.enable(timeStep); }
   }

The constructor stores the sensor, initializes the states to ``false``, and enables the sensor if it exists.

update
~~~~~~

.. code-block:: java

   public void update() {
     previousPressed = currentPressed;
     currentPressed = sensor != null && sensor.getValue() > 0.0;
   }

This method must be called at each simulation step.

It updates the previous and current contact states.

The order is important:

1. the old current value becomes the previous value;
2. the new sensor value becomes the current value.

This makes it possible to detect a new contact.

isPressed
~~~~~~~~~

.. code-block:: java

   public boolean isPressed() {
     return currentPressed;
   }

This method returns ``true`` while the sensor is currently pressed.

It is useful when the robot must react as long as it is touching something.

Example:

.. code-block:: java

   if (bot.sensors().isFrontTouched()) {
     bot.motors().stop();
   }

wasJustPressed
~~~~~~~~~~~~~~

.. code-block:: java

   public boolean wasJustPressed() {
     return currentPressed && !previousPressed;
   }

This method returns ``true`` only at the moment where the sensor changes from not pressed to pressed.

This is useful to trigger an action only once when contact begins.

Example:

.. code-block:: java

   if (bot.sensors().wasFrontJustTouched()) {
     System.out.println("New contact detected");
   }

wasJustReleased
~~~~~~~~~~~~~~~

.. code-block:: java

   public boolean wasJustReleased() {
     return !currentPressed && previousPressed;
   }

This method returns ``true`` only when the sensor was pressed before and is no longer pressed.

This is useful to detect when the robot stops touching an object.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return sensor != null;
   }

This method checks whether the touch sensor was correctly found.

Important update rule
~~~~~~~~~~~~~~~~~~~~~

The ``TouchSensorWrapper`` only works correctly if its ``update`` method is called once per simulation step.

In this API, this is done through the ``SensorManager``:

.. code-block:: java

   public void update() {
     frontTouchSensor.update();
   }

A behavior should make sure that the sensor manager is updated regularly.

ColorSensorWrapper
------------------

Overview
~~~~~~~~

The ``ColorSensorWrapper`` class uses a Webots ``Camera`` as a simple color sensor.

It reads the camera image and computes the average RGB color.

This is useful to detect colored objects, such as red pucks.

Class code
~~~~~~~~~~

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

Attribute
~~~~~~~~~

The class contains one attribute:

.. code-block:: java

   private final Camera camera;

This attribute stores the Webots camera used as a color sensor.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public ColorSensorWrapper(Camera camera, int timeStep) {
     this.camera = camera;
     if (this.camera != null) { this.camera.enable(timeStep); }
   }

The constructor enables the camera if it exists.

A Webots camera must be enabled before its image can be read.

getRGB
~~~~~~

.. code-block:: java

   public RGBColor getRGB()

This method returns the average RGB color seen by the camera.

If the camera does not exist, it returns black:

.. code-block:: java

   new RGBColor(0, 0, 0)

If the image is not valid, it also returns black.

Image reading
^^^^^^^^^^^^^

The image is retrieved with:

.. code-block:: java

   int[] image = camera.getImage();

The image dimensions are retrieved with:

.. code-block:: java

   int width = camera.getWidth();
   int height = camera.getHeight();

The method checks that the image is valid:

.. code-block:: java

   if (image == null || width <= 0 || height <= 0) {
     return new RGBColor(0, 0, 0);
   }

Average color computation
^^^^^^^^^^^^^^^^^^^^^^^^^

The method loops over all pixels:

.. code-block:: java

   for (int x = 0; x < width; x++) {
     for (int y = 0; y < height; y++) {
       sumRed += Camera.imageGetRed(image, width, x, y);
       sumGreen += Camera.imageGetGreen(image, width, x, y);
       sumBlue += Camera.imageGetBlue(image, width, x, y);
     }
   }

Then it returns the average color:

.. code-block:: java

   return new RGBColor(sumRed / pixelCount, sumGreen / pixelCount, sumBlue / pixelCount);

This means that the returned color represents the average color of the whole image.

seesRed
~~~~~~~

.. code-block:: java

   public boolean seesRed() {
     return getRGB().isRed();
   }

This method returns ``true`` if the average color is considered red.

The actual red detection rule is defined in the ``RGBColor`` class.

exists
~~~~~~

.. code-block:: java

   public boolean exists() {
     return camera != null;
   }

This method checks whether the camera was correctly found.

RGBColor
--------

Overview
~~~~~~~~

The ``RGBColor`` class represents a color using three components:

* red;
* green;
* blue.

It is mainly used by ``ColorSensorWrapper``.

Class code
~~~~~~~~~~

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

Attributes
~~~~~~~~~~

.. code-block:: java

   private final int red;
   private final int green;
   private final int blue;

These attributes store the three color components.

Each value is generally between ``0`` and ``255``.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public RGBColor(int red, int green, int blue) {
     this.red = red;
     this.green = green;
     this.blue = blue;
   }

The constructor creates a color from three integer values.

Access methods
~~~~~~~~~~~~~~

.. code-block:: java

   public int red() { return red; }
   public int green() { return green; }
   public int blue() { return blue; }

These methods return each color component.

Example:

.. code-block:: java

   RGBColor color = bot.sensors().color();

   System.out.println(color.red());
   System.out.println(color.green());
   System.out.println(color.blue());

isRed
~~~~~

.. code-block:: java

   public boolean isRed() {
     return red > 150 && green < 100 && blue < 100;
   }

This method checks if the color is clearly red.

The rule is:

.. code-block:: text

   red must be greater than 150
   green must be lower than 100
   blue must be lower than 100

This works well for detecting strong red objects, such as red pucks.

isDominantRed
~~~~~~~~~~~~~

.. code-block:: java

   public boolean isDominantRed() {
     return red > green && red > blue;
   }

This method checks if red is the dominant color.

It is less strict than ``isRed``.

For example, a color can be dominant red without being strongly red.

toString
~~~~~~~~

.. code-block:: java

   @Override
   public String toString() {
     return "(" + red + "," + green + "," + blue + ")";
   }

This method returns the color as text.

Example output:

.. code-block:: text

   (180,45,30)

This is useful for debugging.

SensorManager
-------------

Overview
~~~~~~~~

The ``SensorManager`` class groups all robot sensors.

It is the main class used by the rest of the API to access sensor values.

Instead of using each wrapper directly, behaviors can use:

.. code-block:: java

   bot.sensors().frontDistance();
   bot.sensors().isFrontTouched();
   bot.sensors().seesRed();

Class code
~~~~~~~~~~

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

Attributes
~~~~~~~~~~

The class stores five sensor wrappers:

.. code-block:: java

   private final DistanceSensorWrapper rightDistanceSensor;
   private final DistanceSensorWrapper leftDistanceSensor;
   private final DistanceSensorWrapper frontDistanceSensor;
   private final ColorSensorWrapper colorSensor;
   private final TouchSensorWrapper frontTouchSensor;

Each wrapper corresponds to one Webots sensor.

Constructor
~~~~~~~~~~~

.. code-block:: java

   public SensorManager(Supervisor robot, int timeStep) {
     this.rightDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_right"), timeStep);
     this.leftDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_left"), timeStep);
     this.frontDistanceSensor = new DistanceSensorWrapper(robot.getDistanceSensor("ds_front"), timeStep);
     this.colorSensor = new ColorSensorWrapper(robot.getCamera("color_sensor"), timeStep);
     this.frontTouchSensor = new TouchSensorWrapper(robot.getTouchSensor("touch_front"), timeStep);
   }

The constructor receives:

* the Webots ``Supervisor``;
* the simulation ``timeStep``.

It retrieves all sensors by name.

Sensor names
^^^^^^^^^^^^

The robot PROTO must contain sensors with exactly these names:

.. list-table::
   :header-rows: 1

   * - Webots sensor name
     - API wrapper
     - Role
   * - ``ds_right``
     - ``rightDistanceSensor``
     - Measures distance on the right side.
   * - ``ds_left``
     - ``leftDistanceSensor``
     - Measures distance on the left side.
   * - ``ds_front``
     - ``frontDistanceSensor``
     - Measures distance in front of the robot.
   * - ``color_sensor``
     - ``colorSensor``
     - Reads the color in front of the robot.
   * - ``touch_front``
     - ``frontTouchSensor``
     - Detects front physical contact.

If one of these names is different in the PROTO file, the corresponding sensor may not work.

update
~~~~~~

.. code-block:: java

   public void update() {
     frontTouchSensor.update();
   }

This method updates sensors that need memory between simulation steps.

Currently, it updates the front touch sensor.

This is necessary to detect events like:

* just pressed;
* just released.

In a behavior, this method should be called at each simulation step.

Distance access methods
~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public double rightDistance() { return rightDistanceSensor.getValue(); }
   public double leftDistance() { return leftDistanceSensor.getValue(); }
   public double frontDistance() { return frontDistanceSensor.getValue(); }

These methods return the current values of the distance sensors.

Example:

.. code-block:: java

   double front = bot.sensors().frontDistance();

   if (front > 350.0) {
     bot.motors().stop();
   }

Object detection methods
~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: java

   public boolean frontDetectsObject(double threshold) {
     return frontDistanceSensor.detectsObject(threshold);
   }

   public boolean leftDetectsObject(double threshold) {
     return leftDistanceSensor.detectsObject(threshold);
   }

   public boolean rightDetectsObject(double threshold) {
     return rightDistanceSensor.detectsObject(threshold);
   }

These methods return ``true`` if the sensor value is above the given threshold.

Example:

.. code-block:: java

   if (bot.sensors().frontDetectsObject(350.0)) {
     bot.motors().turnLeft(2.0);
   }

Touch methods
~~~~~~~~~~~~~

.. code-block:: java

   public boolean isFrontTouched() {
     return frontTouchSensor.isPressed();
   }

   public boolean wasFrontJustTouched() {
     return frontTouchSensor.wasJustPressed();
   }

``isFrontTouched`` returns ``true`` as long as the touch sensor is pressed.

``wasFrontJustTouched`` returns ``true`` only when a new contact begins.

Example:

.. code-block:: java

   if (bot.sensors().wasFrontJustTouched()) {
     System.out.println("New contact detected");
   }

Color methods
~~~~~~~~~~~~~

.. code-block:: java

   public RGBColor color() {
     return colorSensor.getRGB();
   }

   public boolean seesRed() {
     return colorSensor.seesRed();
   }

``color`` returns the average RGB color seen by the camera.

``seesRed`` returns ``true`` if this color is considered red.

Example:

.. code-block:: java

   RGBColor color = bot.sensors().color();

   System.out.println("RGB = " + color);

   if (bot.sensors().seesRed()) {
     System.out.println("Red object detected");
   }

Wrapper accessors
~~~~~~~~~~~~~~~~~

The class also provides access to the wrapper objects:

.. code-block:: java

   public DistanceSensorWrapper frontDistanceSensor() { return frontDistanceSensor; }
   public DistanceSensorWrapper leftDistanceSensor() { return leftDistanceSensor; }
   public DistanceSensorWrapper rightDistanceSensor() { return rightDistanceSensor; }
   public TouchSensorWrapper frontTouchSensor() { return frontTouchSensor; }
   public ColorSensorWrapper colorSensor() { return colorSensor; }

These methods are useful when more advanced access is needed.

Example:

.. code-block:: java

   if (!bot.sensors().colorSensor().exists()) {
     System.out.println("Color sensor not found");
   }

How sensors are used in behaviors
---------------------------------

The sensors API is mainly used inside robot behaviors.

Example of obstacle detection:

.. code-block:: java

   if (bot.sensors().frontDetectsObject(350.0)) {
     bot.motors().turnLeft(2.0);
   } else {
     bot.motors().forward(2.0);
   }

Example of contact detection:

.. code-block:: java

   bot.sensors().update();

   if (bot.sensors().wasFrontJustTouched()) {
     bot.motors().stop();
   }

Example of color detection:

.. code-block:: java

   if (bot.sensors().seesRed()) {
     System.out.println("The robot sees a red puck");
   }

Important naming convention
---------------------------

The sensors API depends on the names of the sensors in the Webots robot.

The robot PROTO must contain:

.. code-block:: text

   ds_front
   ds_left
   ds_right
   touch_front
   color_sensor

Example:

.. code-block:: text

   DistanceSensor {
     name "ds_front"
   }

If a sensor has another name, the API will not be able to retrieve it correctly.

Debugging
---------

If a sensor does not work, check the following points:

* the sensor exists in the robot PROTO;
* the sensor name is correct;
* the sensor is placed correctly on the robot;
* the controller has been recompiled;
* the sensor is enabled with the correct ``timeStep``;
* the sensor wrapper ``exists`` method returns ``true``.

Example:

.. code-block:: java

   if (!bot.sensors().frontDistanceSensor().exists()) {
     System.out.println("Front distance sensor missing");
   }

If the color sensor always returns ``(0,0,0)``, check that:

* the camera exists;
* the camera name is ``color_sensor``;
* the camera is enabled;
* the camera is facing the correct direction;
* the object is visible in the camera image.

If ``wasFrontJustTouched`` never becomes ``true``, check that:

* ``bot.sensors().update()`` is called regularly;
* the touch sensor exists;
* the touch sensor has a ``boundingObject``;
* the robot really touches the object physically.

Summary
-------

The ``api.sensors`` package provides a clean API for reading robot sensors.

``DistanceSensorWrapper`` simplifies distance sensor access.

``TouchSensorWrapper`` detects current and new physical contacts.

``ColorSensorWrapper`` uses a camera to compute the average RGB color.

``RGBColor`` stores and analyzes color values.

``SensorManager`` groups all sensors and provides a simple interface for behaviors.

This package allows the rest of the controller to use clear commands such as:

.. code-block:: java

   bot.sensors().frontDistance();
   bot.sensors().frontDetectsObject(350.0);
   bot.sensors().isFrontTouched();
   bot.sensors().wasFrontJustTouched();
   bot.sensors().seesRed();