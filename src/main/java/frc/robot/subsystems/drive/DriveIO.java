package frc.robot.subsystems.drive;

import org.littletonrobotics.junction.AutoLog;

public interface DriveIO {
  /** Arrays are ordered front-left, front-right, rear-left, rear-right. */
  @AutoLog
  class DriveIOInputs {
    public boolean[] connected = new boolean[4];
    public double[] appliedPercent = new double[4];
    public double[] statorAmps = new double[4];
    public double[] supplyAmps = new double[4];
    public double[] tempC = new double[4];
    public double[] busVolts = new double[4];
    public int[] faults = new int[4];
    public int[] stickyFaults = new int[4];
  }

  default void updateInputs(DriveIOInputs inputs) {}

  default void setOutputs(double frontLeft, double frontRight, double rearLeft, double rearRight) {}
}
