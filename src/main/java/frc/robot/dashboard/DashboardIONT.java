package frc.robot.dashboard;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringSubscriber;
import frc.robot.subsystems.shooter.ShooterConstants;

public class DashboardIONT implements DashboardIO {
  private final NetworkTableInstance nt = NetworkTableInstance.getDefault();
  private final BooleanSubscriber tuneEnabled = nt.getBooleanTopic("/Dashboard/TuneEnabled").subscribe(false);
  private final DoubleSubscriber tuneRpm = nt.getDoubleTopic("/Dashboard/TuneRPM").subscribe(ShooterConstants.FALLBACK_RPM);
  private final StringSubscriber alliance = nt.getStringTopic("/Dashboard/Alliance").subscribe("");

  // exists() goes false when the dashboard disconnects, so nothing stays stuck on.
  @Override
  public void updateInputs(DashboardIOInputs inputs) {
    inputs.tuneEnabled = tuneEnabled.getTopic().exists() && tuneEnabled.get();
    inputs.tuneRpm = tuneRpm.get();
    inputs.allianceOverride = alliance.getTopic().exists() ? alliance.get() : "";
  }
}
