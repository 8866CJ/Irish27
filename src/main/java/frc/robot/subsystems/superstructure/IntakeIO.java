package frc.robot.subsystems.superstructure;

import org.littletonrobotics.junction.AutoLog;

public interface IntakeIO {
  @AutoLog
  class IntakeIOInputs {
    public boolean connected;
    public double appliedPercent;
    public double statorAmps;
    public double supplyAmps;
    public double tempC;
    public double busVolts;
    public int faults;
    public int stickyFaults;
  }

  default void updateInputs(IntakeIOInputs inputs) {}

  default void setPercent(double percent) {}
}
