package frc.robot.subsystems.vision;

import frc.robot.Constants;
import java.util.List;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

public class VisionIOPhoton implements VisionIO {
  private final PhotonCamera camera = new PhotonCamera(Constants.CAMERA_NAME);

  @Override
  public void updateInputs(VisionIOInputs inputs) {
    inputs.connected = camera.isConnected();
    if (!inputs.connected) {
      clearTargets(inputs);
      return;
    }

    List<PhotonPipelineResult> results = camera.getAllUnreadResults();
    if (results.isEmpty()) return; // no new frame this loop, keep the last one

    PhotonPipelineResult latest = results.get(results.size() - 1);
    inputs.timestampSec = latest.getTimestampSeconds();
    inputs.latencyMs = latest.metadata.getLatencyMillis();
    List<PhotonTrackedTarget> targets = latest.getTargets();
    int n = targets.size();
    inputs.tagIds = new int[n];
    inputs.yawsDeg = new double[n];
    inputs.pitchesDeg = new double[n];
    inputs.areas = new double[n];
    inputs.ambiguities = new double[n];
    for (int i = 0; i < n; i++) {
      PhotonTrackedTarget t = targets.get(i);
      inputs.tagIds[i] = t.getFiducialId();
      inputs.yawsDeg[i] = t.getYaw();
      inputs.pitchesDeg[i] = t.getPitch();
      inputs.areas[i] = t.getArea();
      inputs.ambiguities[i] = t.getPoseAmbiguity();
    }
  }

  private static void clearTargets(VisionIOInputs inputs) {
    inputs.tagIds = new int[0];
    inputs.yawsDeg = new double[0];
    inputs.pitchesDeg = new double[0];
    inputs.areas = new double[0];
    inputs.ambiguities = new double[0];
  }
}
