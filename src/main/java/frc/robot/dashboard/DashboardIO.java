package frc.robot.dashboard;

import org.littletonrobotics.junction.AutoLog;

/** Shooter tuning values sent from the dashboard's Shooter Tuning tab. */
public interface DashboardIO {
  @AutoLog
  class DashboardIOInputs {
    public boolean tuneEnabled;
    public double tuneRpm;
  }

  default void updateInputs(DashboardIOInputs inputs) {}
}
