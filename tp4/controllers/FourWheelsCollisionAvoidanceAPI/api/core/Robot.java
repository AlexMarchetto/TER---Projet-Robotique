package api.core;

import com.cyberbotics.webots.controller.Supervisor;

import api.actuators.Arm;
import api.actuators.Gripper;
import api.behavior.RobotBehavior;
import api.motors.DriveBase;
import api.sensors.SensorManager;
import api.tasks.TaskScheduler;
import api.world.PuckManager;

public class Robot {
  private final Supervisor supervisor;
  private final int timeStep;

  private final DriveBase driveBase;
  private final SensorManager sensorManager;
  private final Arm arm;
  private final Gripper gripper;
  private final PuckManager puckManager;
  private final TaskScheduler scheduler;

  private RobotBehavior behavior;

  public Robot() {
    // Main Webots object used to control the robot and access the simulation.
    this.supervisor = new Supervisor();

    // Simulation step duration used by Webots.
    this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

    // Initialize the main robot APIs.
    this.driveBase = new DriveBase(supervisor);
    this.sensorManager = new SensorManager(supervisor, timeStep);
    this.arm = new Arm(supervisor, timeStep);
    this.gripper = new Gripper(supervisor);

    // Automatically load every puck whose DEF name starts with "PALET_".
    this.puckManager = PuckManager.findAllWithPrefix(supervisor, "PALET_");

    // Scheduler used to run delayed or timed actions.
    this.scheduler = new TaskScheduler();

    // Set the robot to a safe initial state.
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

    // Main simulation loop.
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