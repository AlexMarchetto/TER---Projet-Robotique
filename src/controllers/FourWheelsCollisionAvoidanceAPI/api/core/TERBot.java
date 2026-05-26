package api.core;

import com.cyberbotics.webots.controller.Supervisor;

import api.actuators.Arm;
import api.actuators.Gripper;
import api.behavior.RobotBehavior;
import api.motors.DriveBase;
import api.sensors.SensorManager;
import api.tasks.TaskScheduler;
import api.world.PuckManager;

public class TERBot {
  private final Supervisor supervisor;
  private final int timeStep;

  private final DriveBase driveBase;
  private final SensorManager sensorManager;
  private final Arm arm;
  private final Gripper gripper;
  private final PuckManager puckManager;
  private final TaskScheduler scheduler;

  private RobotBehavior behavior;

  public TERBot() {
    /*
     * Main Webots object.
     * It allows the controller to communicate with the simulated world.
     */
    this.supervisor = new Supervisor();

    /*
     * Webots simulation time step.
     */
    this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

    /*
     * Initialization of the robot APIs.
     */
    this.driveBase = new DriveBase(supervisor); // Motor API
    this.sensorManager = new SensorManager(supervisor, timeStep); // Sensor API
    this.arm = new Arm(supervisor, timeStep); // Arm API
    this.gripper = new Gripper(supervisor); // Gripper API

    /*
     * Puck API.
     * Automatically finds all objects whose DEF name starts with PALET_.
     * Example: PALET_1, PALET_2, PALET_3, PALET_4, etc.
     */
    this.puckManager = PuckManager.findAllWithPrefix(supervisor, "PALET_");

    /*
     * Simple asynchronous task API.
     */
    this.scheduler = new TaskScheduler();

    /*
     * Initial arm and gripper position.
     */
    arm.lift();
    gripper.open();
  }

  public Supervisor supervisor() {
    return supervisor;
  }

  public int timeStep() {
    return timeStep;
  }

  public DriveBase motors() {
    return driveBase;
  }

  public SensorManager sensors() {
    return sensorManager;
  }

  public Arm arm() {
    return arm;
  }

  public Gripper gripper() {
    return gripper;
  }

  public PuckManager pucks() {
    return puckManager;
  }

  public TaskScheduler scheduler() {
    return scheduler;
  }

  public void setBehavior(RobotBehavior behavior) {
    this.behavior = behavior;
  }

  public void run() {
    if (behavior != null) {
      behavior.init();
    }

    while (supervisor.step(timeStep) != -1) {
      scheduler.update();

      if (behavior != null) {
        behavior.update();
      }
    }
  }

  public void stop() {
    driveBase.stop();
  }
}