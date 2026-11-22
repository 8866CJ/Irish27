package frc.robot.dashboard;

import org.littletonrobotics.junction.AutoLog;

/** Values sent from the driver dashboard. */
public interface DashboardIO {
  @AutoLog
  class DashboardIOInputs {
    public boolean tuneEnabled;
    public double tuneRpm;
    /** "Red", "Blue", or "" to use the Driver Station. */
    public String allianceOverride = "";
  }

  default void updateInputs(DashboardIOInputs inputs) {}
}
