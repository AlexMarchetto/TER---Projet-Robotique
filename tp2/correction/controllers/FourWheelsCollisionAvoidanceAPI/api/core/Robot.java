package api.core;

import com.cyberbotics.webots.controller.Supervisor;

import api.actuators.Arm;
import api.actuators.Gripper;
import api.behavior.RobotBehavior;
import api.motors.DriveBase;
import api.sensors.SensorManager;
import api.tasks.TaskScheduler;

public class Robot {
  private final Supervisor supervisor;
  private final int timeStep;

  private final DriveBase driveBase;
  private final SensorManager sensorManager;
  private final Arm arm;
  private final Gripper gripper;
  private final TaskScheduler scheduler;

  private RobotBehavior behavior;

  public Robot() {
    /*
     * Objet principal de Webots.
     * Il permet au contrôleur de communiquer avec le monde simulé.
     */
    this.supervisor = new Supervisor();

    /*
     * Pas de temps de la simulation Webots.
     */
    this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());

    /*
     * Initialisation des différentes API du robot.
     */
    this.driveBase = new DriveBase(supervisor); // API des moteurs
    this.sensorManager = new SensorManager(supervisor, timeStep); // API des capteurs
    this.arm = new Arm(supervisor, timeStep); // API du bras
    this.gripper = new Gripper(supervisor); // API de la pince

    /*
     * API simple pour gérer des tâches asynchrones.
     */
    this.scheduler = new TaskScheduler();

    /*
     * Position initiale du bras et de la pince.
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