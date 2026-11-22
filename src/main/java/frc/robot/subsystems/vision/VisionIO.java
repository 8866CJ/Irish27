package frc.robot.subsystems.vision;

import org.littletonrobotics.junction.AutoLog;

public interface VisionIO {
  /** One entry per AprilTag seen in the latest frame. */
  @AutoLog
  class VisionIOInputs {
    public boolean connected;
    public double timestampSec;
    public double latencyMs;
    public int[] tagIds = new int[0];
    public double[] yawsDeg = new double[0];
    public double[] pitchesDeg = new double[0];
    public double[] areas = new double[0];
    public double[] ambiguities = new double[0];
  }

  default void updateInputs(VisionIOInputs inputs) {}
}
