package api.state;

public enum RobotMode {
  // Search and obstacle handling.
  SEARCH,
  TOUCH_AVOID,

  // Puck pickup sequence.
  APPROACH_PUCK,
  LOWER_ARM,
  CLOSE_GRIPPER,
  LIFT_ARM,

  // Drop zone sequence.
  GO_TO_DROP_ZONE,
  DROP_PUCK,
  LIFT_ARM_AFTER_DROP,
  BACK_AND_TURN_AFTER_DROP,

  // Final state.
  FINISHED
}