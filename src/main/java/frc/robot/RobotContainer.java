package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.dashboard.DashboardIO;
import frc.robot.dashboard.DashboardIONT;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.DriveIO;
import frc.robot.subsystems.drive.DriveIOTalonSRX;
import frc.robot.subsystems.superstructure.FeederIO;
import frc.robot.subsystems.superstructure.FeederIOSparkMax;
import frc.robot.subsystems.superstructure.IntakeIO;
import frc.robot.subsystems.superstructure.IntakeIOTalonSRX;
import frc.robot.subsystems.superstructure.ShooterIO;
import frc.robot.subsystems.superstructure.ShooterIOSparkMax;
import frc.robot.subsystems.superstructure.Superstructure;
import frc.robot.subsystems.superstructure.Superstructure.State;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionIO;
import frc.robot.subsystems.vision.VisionIOPhoton;
import org.littletonrobotics.junction.Logger;

public class RobotContainer {
  private final CommandXboxController xbox = new CommandXboxController(Constants.CONTROLLER_PORT);
  private final Vision vision;
  private final Drive drive;
  private final Superstructure superstructure;

  public RobotContainer() {
    // REPLAY uses empty IO so the logged inputs drive the code instead of hardware.
    boolean replay = Constants.MODE == Constants.Mode.REPLAY;
    vision = new Vision(replay ? new VisionIO() {} : new VisionIOPhoton());
    drive = new Drive(
        replay ? new DriveIO() {} : new DriveIOTalonSRX(),
        vision,
        () -> -deadband(xbox.getLeftY()),
        () -> -deadband(xbox.getLeftX()),
        () -> -deadband(xbox.getRightX()));
    superstructure = new Superstructure(
        replay ? new IntakeIO() {} : new IntakeIOTalonSRX(),
        replay ? new FeederIO() {} : new FeederIOSparkMax(),
        replay ? new ShooterIO() {} : new ShooterIOSparkMax(),
        replay ? new DashboardIO() {} : new DashboardIONT(),
        vision,
        drive::isAimed);

    configureBindings();
    logCommands();
  }

  private void configureBindings() {
    // Left trigger: intake. B: eject jams.
    xbox.leftTrigger().whileTrue(hold("Intake", () -> superstructure.setWanted(State.INTAKING)));
    xbox.b().whileTrue(hold("Eject", () -> superstructure.setWanted(State.EJECTING)));

    // Right trigger: auto-aim at HUB, RPM from distance, feeds when ready.
    xbox.rightTrigger().whileTrue(Commands.startEnd(
        () -> { drive.setState(Drive.State.AIM); superstructure.setWanted(State.SHOOTING, true); },
        () -> { drive.setState(Drive.State.MANUAL); superstructure.setWanted(State.IDLE); })
        .withName("AimAndShoot"));

    // Right bumper: no-vision backup shot at FALLBACK_RPM.
    xbox.rightBumper().whileTrue(hold("FallbackShot", () -> superstructure.setWanted(State.SHOOTING, false)));
  }

  private Command hold(String name, Runnable start) {
    return Commands.startEnd(start, () -> superstructure.setWanted(State.IDLE)).withName(name);
  }

  private static void logCommands() {
    CommandScheduler s = CommandScheduler.getInstance();
    s.onCommandInitialize(c -> Logger.recordOutput("Commands/" + c.getName(), true));
    s.onCommandFinish(c -> Logger.recordOutput("Commands/" + c.getName(), false));
    s.onCommandInterrupt(c -> Logger.recordOutput("Commands/" + c.getName(), false));
  }

  private static double deadband(double value) {
    return MathUtil.applyDeadband(value, Constants.DEADBAND);
  }

  public void resetStates() {
    drive.setState(Drive.State.MANUAL);
    superstructure.setWanted(State.IDLE);
  }

  /** Leave the starting line, then aim and shoot preloads for the rest of AUTO. */
  public Command getAutonomousCommand() {
    return Commands.sequence(
        Commands.runOnce(() -> drive.setState(Drive.State.AUTO_LEAVE)),
        Commands.waitSeconds(Constants.AUTO_LEAVE_SECONDS),
        Commands.runOnce(() -> {
          drive.setState(Drive.State.STOPPED);
          superstructure.setWanted(State.SHOOTING, true);
        }))
        .withName("Auto");
  }

  // Shift timing is from the BUILT manual, Table 6-2.
  // Teleop timer: TRANSITION 140-130, SHIFT 1-4 every 25 s, END GAME 30-0.
  public void updateShiftDashboard() {
    double t = DriverStation.getMatchTime();
    boolean opposite = DriverStation.isTeleop()
        && (t > 130 || (t <= 105 && t > 80) || (t <= 55 && t > 30));
    double shiftEnd = t > 130 ? 130 : t > 30 ? 30 + 25 * Math.floor((t - 30.001) / 25) : 0;
    Logger.recordOutput("Shift/ScoreOwnSide", !opposite);
    Logger.recordOutput("Shift/SecondsLeft", Math.max(0, t - shiftEnd));
    Logger.recordOutput("Shift/MatchTime", Math.max(0, t));
    Logger.recordOutput("Shift/IsRedAlliance",
        DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red);
  }
}
