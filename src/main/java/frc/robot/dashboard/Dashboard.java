package frc.robot.dashboard;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;

/** Reads dashboard inputs once per loop. Construct before other subsystems so they see fresh values. */
public class Dashboard extends SubsystemBase {
  private final DashboardIO io;
  private final DashboardIOInputsAutoLogged inputs = new DashboardIOInputsAutoLogged();

  public Dashboard(DashboardIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("DashboardInputs", inputs);
    Logger.recordOutput("Alliance/IsRed", isRedAlliance());
    Logger.recordOutput("Alliance/FromDashboard", !inputs.allianceOverride.isEmpty());
  }

  public boolean tuneEnabled() {
    return inputs.tuneEnabled;
  }

  public double tuneRpm() {
    return inputs.tuneRpm;
  }

  /** Use this everywhere alliance matters (shift display now, path flipping later). */
  public boolean isRedAlliance() {
    if (inputs.allianceOverride.equals("Red")) return true;
    if (inputs.allianceOverride.equals("Blue")) return false;
    return DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red;
  }
}
