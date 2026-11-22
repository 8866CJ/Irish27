package frc.robot.subsystems.vision;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;
import org.photonvision.PhotonUtils;

public class Vision extends SubsystemBase {
  private final VisionIO io;
  private final VisionIOInputsAutoLogged inputs = new VisionIOInputsAutoLogged();
  private boolean hasTarget = false;
  private double yawDeg = 0;
  private double distanceM = 0;

  public Vision(VisionIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Vision", inputs);

    // Average every visible HUB tag so we aim at the face's center.
    double yawSum = 0, pitchSum = 0;
    int count = 0;
    for (int i = 0; i < inputs.tagIds.length; i++) {
      if (!VisionConstants.HUB_TAGS.contains(inputs.tagIds[i])) continue;
      yawSum += inputs.yawsDeg[i];
      pitchSum += inputs.pitchesDeg[i];
      count++;
    }
    hasTarget = count > 0;
    if (hasTarget) {
      yawDeg = yawSum / count + VisionConstants.AIM_YAW_OFFSET_DEG;
      distanceM = PhotonUtils.calculateDistanceToTargetMeters(
          VisionConstants.CAMERA_HEIGHT_M,
          VisionConstants.TAG_HEIGHT_M,
          Units.degreesToRadians(VisionConstants.CAMERA_PITCH_DEG),
          Units.degreesToRadians(pitchSum / count));
    }

    Logger.recordOutput("Vision/HasTarget", hasTarget);
    Logger.recordOutput("Vision/YawDeg", yawDeg);
    Logger.recordOutput("Vision/DistanceM", distanceM);
    Logger.recordOutput("Vision/HubTagCount", count);
  }

  public boolean hasTarget() {
    return hasTarget;
  }

  public double getYawDeg() {
    return yawDeg;
  }

  public double getDistanceM() {
    return distanceM;
  }
}
