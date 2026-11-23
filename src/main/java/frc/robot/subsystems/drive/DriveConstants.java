package frc.robot.subsystems.drive;

public final class DriveConstants {
  private DriveConstants() {}

  public static final double AIM_KP = 0.02;
  public static final double AIM_MAX_ROT = 0.5;
  public static final double AIM_TOLERANCE_DEG = 2.0;

  // Stick multipliers while intaking. Full stick = this much output.
  public static final double INTAKE_DRIVE_SCALE = 0.25;
  public static final double INTAKE_TURN_SCALE = 0.5;

  public static final double AUTO_LEAVE_SPEED = 0.4;
}
