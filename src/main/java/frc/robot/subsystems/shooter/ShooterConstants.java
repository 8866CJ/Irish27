package frc.robot.subsystems.shooter;

public final class ShooterConstants {
  private ShooterConstants() {}

  // RPM is motor RPM. Tune kP first with kV fixed.
  public static final double KV = 12.0 / 5676.0;
  public static final double KP = 0.0002;
  public static final int CURRENT_LIMIT_AMPS = 40;

  public static final double TOLERANCE_RPM = 100;
  public static final double FALLBACK_RPM = 3500;
  public static final double MAX_TUNE_RPM = 5600;

  // {distance meters, RPM}. Fill these in from the dashboard's Shooter Tuning tab.
  public static final double[][] SHOT_TABLE = {
    {1.0, 3000},
    {2.0, 3500},
    {3.0, 4000},
    {4.0, 4500},
  };
}
