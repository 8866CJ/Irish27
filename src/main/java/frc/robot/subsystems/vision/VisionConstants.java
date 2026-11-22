package frc.robot.subsystems.vision;

import java.util.Set;

public final class VisionConstants {
  private VisionConstants() {}

  // Must match the camera name in the PhotonVision UI.
  public static final String CAMERA_NAME = "limelight";
  public static final double CAMERA_HEIGHT_M = 0.4;
  public static final double CAMERA_PITCH_DEG = 20.0;
  public static final double TAG_HEIGHT_M = 1.124; // measure on the real HUB
  public static final Set<Integer> HUB_TAGS = Set.of(9, 10, 25, 26);
  public static final double AIM_YAW_OFFSET_DEG = 0.0;
}
