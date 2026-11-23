package frc.robot.dashboard;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.Objects;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.LoggedDashboardChooser;

/**
 * Everything the driver dashboard sets. Construct before other subsystems so they see fresh values.
 * Choosers show up at /SmartDashboard/&lt;name&gt; for any dashboard's chooser tile, and are logged for replay.
 */
public class Dashboard extends SubsystemBase {
  private static final String USE_DRIVER_STATION = "Driver Station";

  private final DashboardIO io;
  private final DashboardIOInputsAutoLogged inputs = new DashboardIOInputsAutoLogged();
  private final LoggedDashboardChooser<String> alliance = new LoggedDashboardChooser<>("Alliance");

  public Dashboard(DashboardIO io) {
    this.io = io;
    alliance.addDefaultOption(USE_DRIVER_STATION, USE_DRIVER_STATION);
    alliance.addOption("Red", "Red");
    alliance.addOption("Blue", "Blue");
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("DashboardInputs", inputs);
    Logger.recordOutput("Alliance/IsRed", isRedAlliance());
    Logger.recordOutput("Alliance/FromDashboard", !USE_DRIVER_STATION.equals(alliancePick()));
  }

  private String alliancePick() {
    return Objects.requireNonNullElse(alliance.get(), USE_DRIVER_STATION);
  }

  public boolean tuneEnabled() {
    return inputs.tuneEnabled;
  }

  public double tuneRpm() {
    return inputs.tuneRpm;
  }

  /** Use this everywhere alliance matters (shift display now, path flipping later). */
  public boolean isRedAlliance() {
    String pick = alliancePick();
    if ("Red".equals(pick)) return true;
    if ("Blue".equals(pick)) return false;
    return DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red;
  }
}
