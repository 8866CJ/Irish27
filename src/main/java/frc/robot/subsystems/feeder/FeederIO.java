package frc.robot.subsystems.feeder;

import org.littletonrobotics.junction.AutoLog;

public interface FeederIO {
  @AutoLog
  class FeederIOInputs {
    public boolean connected;
    public double appliedOutput;
    public double velocityRpm;
    public double amps;
    public double tempC;
    public double busVolts;
    public int faults;
    public int stickyFaults;
  }

  default void updateInputs(FeederIOInputs inputs) {}

  default void setPercent(double percent) {}
}
