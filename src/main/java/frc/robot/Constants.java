package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/** Robot-wide settings. Mechanism settings live in each subsystem's own Constants file. */
public final class Constants {
  private Constants() {}

  public enum Mode { REAL, SIM, REPLAY }

  // Change SIM to REPLAY to re-run a saved log through the code (see README).
  public static final Mode MODE = RobotBase.isReal() ? Mode.REAL : Mode.SIM;

  public static final int CONTROLLER_PORT = 0;
  public static final double DEADBAND = 0.08;

  public static final double AUTO_LEAVE_SECONDS = 1.0;
}
