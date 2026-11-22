package frc.robot;

/** Every CAN ID in one place, so two devices can never share an ID by accident. */
public enum Ports {
  PDP(0),
  DRIVE_FRONT_LEFT(1),
  DRIVE_FRONT_RIGHT(2),
  DRIVE_REAR_LEFT(3),
  DRIVE_REAR_RIGHT(4),
  INTAKE(5),
  FEEDER(6),
  SHOOTER_LEADER(7),
  SHOOTER_FOLLOWER(8);

  public final int id;

  Ports(int id) {
    this.id = id;
  }
}
