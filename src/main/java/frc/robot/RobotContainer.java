package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.dashboard.Dashboard;
import frc.robot.dashboard.DashboardIO;
import frc.robot.dashboard.DashboardIONT;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.DriveIO;
import frc.robot.subsystems.drive.DriveIOTalonSRX;
import frc.robot.subsystems.feeder.Feeder;
import frc.robot.subsystems.feeder.FeederIO;
import frc.robot.subsystems.feeder.FeederIOSparkMax;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.IntakeIO;
import frc.robot.subsystems.intake.IntakeIOTalonSRX;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterIO;
import frc.robot.subsystems.shooter.ShooterIOSparkMax;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionIO;
import frc.robot.subsystems.vision.VisionIOPhoton;
import frc.robot.superstructure.Superstructure;
import org.littletonrobotics.junction.Logger;

public class RobotContainer {
  private final CommandXboxController xbox = new CommandXboxController(Constants.CONTROLLER_PORT);
  private final Dashboard dashboard;
  private final Vision vision;
  private final Drive drive;
  private final Superstructure superstructure;

  public RobotContainer() {
    // REPLAY uses empty IO so the logged inputs drive the code instead of hardware.
    // Subsystems run in the order they're created: inputs first, Superstructure last.
    boolean replay = Constants.MODE == Constants.Mode.REPLAY;
    dashboard = new Dashboard(replay ? new DashboardIO() {} : new DashboardIONT());
    vision = new Vision(replay ? new VisionIO() {} : new VisionIOPhoton());
    drive = new Drive(
        replay ? new DriveIO() {} : new DriveIOTalonSRX(),
        vision,
        () -> -deadband(xbox.getLeftY()),
        () -> -deadband(xbox.getLeftX()),
        () -> -deadband(xbox.getRightX()));
    Intake intake = new Intake(replay ? new IntakeIO() {} : new IntakeIOTalonSRX());
    Feeder feeder = new Feeder(replay ? new FeederIO() {} : new FeederIOSparkMax());
    Shooter shooter = new Shooter(replay ? new ShooterIO() {} : new ShooterIOSparkMax());
    superstructure = new Superstructure(intake, feeder, shooter, dashboard, vision, drive::isAimed);

    configureBindings();
    logCommands();
  }

  private void configureBindings() {
    xbox.leftTrigger().whileTrue(superstructure.intake());
    xbox.b().whileTrue(superstructure.eject());
    xbox.rightTrigger().whileTrue(aimAndShoot());
    xbox.rightBumper().whileTrue(superstructure.fallbackShot());
  }

  private Command aimAndShoot() {
    return Commands.parallel(drive.aimAtHub(), superstructure.shoot()).withName("AimAndShoot");
  }

  /** Leave the starting line, then aim and shoot preloads for the rest of AUTO. */
  public Command getAutonomousCommand() {
    return Commands.sequence(
        drive.leaveStartingLine().withTimeout(Constants.AUTO_LEAVE_SECONDS),
        Commands.parallel(drive.holdAndAim(), superstructure.shoot()))
        .withName("Auto");
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

  public void logMatchState() {
    double t = DriverStation.getMatchTime();
    Shift shift = Shift.current();
    Logger.recordOutput("Shift/Current", shift);
    Logger.recordOutput("Shift/ScoreOwnSide", shift.scoreOwnSide());
    Logger.recordOutput("Shift/SecondsLeft", shift.secondsLeft(t));
    Logger.recordOutput("Shift/MatchTime", Math.max(0, t));
    Logger.recordOutput("Shift/IsRedAlliance", dashboard.isRedAlliance());
  }
}
