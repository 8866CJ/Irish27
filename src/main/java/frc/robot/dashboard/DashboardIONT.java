package frc.robot.dashboard;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Constants;

public class DashboardIONT implements DashboardIO {
  private final BooleanSubscriber tuneEnabled =
      NetworkTableInstance.getDefault().getBooleanTopic("/Dashboard/TuneEnabled").subscribe(false);
  private final DoubleSubscriber tuneRpm =
      NetworkTableInstance.getDefault().getDoubleTopic("/Dashboard/TuneRPM").subscribe(Constants.FALLBACK_RPM);

  @Override
  public void updateInputs(DashboardIOInputs inputs) {
    // exists() goes false when the dashboard disconnects, so tuning can't stay stuck on.
    inputs.tuneEnabled = tuneEnabled.getTopic().exists() && tuneEnabled.get();
    inputs.tuneRpm = tuneRpm.get();
  }
}
