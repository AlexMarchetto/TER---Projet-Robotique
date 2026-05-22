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
    this.supervisor = new Supervisor(); // Objet Webots qui permet de communiquer avec le monde simulé
    this.timeStep = (int) Math.round(supervisor.getBasicTimeStep());
    this.driveBase = new DriveBase(supervisor); // API moteurs globale
    this.sensorManager = new SensorManager(supervisor, timeStep); // API capteurs globale
    this.arm = new Arm(supervisor, timeStep); // API du bras
    this.gripper = new Gripper(supervisor); // API de la pince (au bout du bras)
    this.puckManager = new PuckManager(supervisor, new String[] {"PALET_1", "PALET_2", "PALET_3"}); // API palets
    this.scheduler = new TaskScheduler(); // API des tâches asynchrones simples
    arm.lift();
    gripper.open();
  }

  public Supervisor supervisor() { return supervisor; }
  public int timeStep() { return timeStep; }
  public DriveBase motors() { return driveBase; }
  public SensorManager sensors() { return sensorManager; }
  public Arm arm() { return arm; }
  public Gripper gripper() { return gripper; }
  public PuckManager pucks() { return puckManager; }
  public TaskScheduler scheduler() { return scheduler; }

  public void setBehavior(RobotBehavior behavior) { this.behavior = behavior; }

  public void run() {
    if (behavior != null) { behavior.init(); }
    while (supervisor.step(timeStep) != -1) {
      scheduler.update();
      if (behavior != null) { behavior.update(); }
    }
  }

  public void stop() { driveBase.stop(); }
}
