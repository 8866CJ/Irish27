package frc.robot.subsystems.shooter;

import org.littletonrobotics.junction.AutoLog;

public interface ShooterIO {
  @AutoLog
  class ShooterIOInputs {
    public double velocityRpm;
    public double busVolts;
    public boolean leaderConnected;
    public boolean followerConnected;
    public double leaderAppliedOutput;
    public double followerAppliedOutput;
    public double leaderAmps;
    public double followerAmps;
    public double leaderTempC;
    public double followerTempC;
    public int leaderFaults;
    public int followerFaults;
    public int leaderStickyFaults;
    public int followerStickyFaults;
  }

  default void updateInputs(ShooterIOInputs inputs) {}

  default void setVelocity(double rpm) {}

  default void stop() {}
}
