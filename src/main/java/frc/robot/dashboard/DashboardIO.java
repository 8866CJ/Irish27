package frc.robot.dashboard;

import org.littletonrobotics.junction.AutoLog;

/** Values sent from the driver dashboard (Shooter Tuning tab). */
public interface DashboardIO {
  @AutoLog
  class DashboardIOInputs {
    public boolean tuneEnabled;
    public double tuneRpm;
  }

  default void updateInputs(DashboardIOInputs inputs) {}
}
