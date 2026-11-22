package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import java.util.Set;

public final class Constants {
  private Constants() {}

  public enum Mode { REAL, SIM, REPLAY }

  // Change SIM to REPLAY to re-run a saved log through the code (see README).
  public static final Mode MODE = RobotBase.isReal() ? Mode.REAL : Mode.SIM;

  // CAN IDs. PDP stays on 0.
  public static final int FRONT_LEFT_ID = 1;
  public static final int FRONT_RIGHT_ID = 2;
  public static final int REAR_LEFT_ID = 3;
  public static final int REAR_RIGHT_ID = 4;
  public static final int INTAKE_ID = 5;
  public static final int FEEDER_ID = 6;
  public static final int SHOOTER_LEADER_ID = 7;
  public static final int SHOOTER_FOLLOWER_ID = 8;

  public static final int CONTROLLER_PORT = 0;
  public static final double DEADBAND = 0.08;

  public static final double INTAKE_SPEED = 0.8;
  public static final double FEEDER_SPEED = 0.7;
  public static final double EJECT_SPEED = -0.6;

  // Shooter RPM is motor RPM. Tune kP first with kV fixed.
  public static final double SHOOTER_KV = 12.0 / 5676.0;
  public static final double SHOOTER_KP = 0.0002;
  public static final double SHOOTER_TOLERANCE_RPM = 100;
  public static final double FALLBACK_RPM = 3500;
  public static final double MAX_TUNE_RPM = 5600;
  // {distance meters, RPM}. Fill these in from test shots.
  public static final double[][] SHOT_TABLE = {
    {1.0, 3000},
    {2.0, 3500},
    {3.0, 4000},
    {4.0, 4500},
  };

  // Must match the camera name in the PhotonVision UI.
  public static final String CAMERA_NAME = "limelight";
  public static final double CAMERA_HEIGHT_M = 0.402;
  public static final double CAMERA_PITCH_DEG = 20.0;
  public static final double TAG_HEIGHT_M = 1.124; // measure on the real HUB
  public static final Set<Integer> HUB_TAGS = Set.of(9, 10, 25, 26);

  public static final double AIM_KP = 0.02;
  public static final double AIM_MAX_ROT = 0.5;
  public static final double AIM_TOLERANCE_DEG = 2.0;
  public static final double AIM_YAW_OFFSET_DEG = 0.0;

  public static final double AUTO_LEAVE_SPEED = 0.4;
  public static final double AUTO_LEAVE_SECONDS = 1.0;
}
