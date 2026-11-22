package frc.robot.subsystems.superstructure;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.dashboard.DashboardIO;
import frc.robot.dashboard.DashboardIOInputsAutoLogged;
import frc.robot.subsystems.vision.Vision;
import java.util.function.BooleanSupplier;
import org.littletonrobotics.junction.Logger;

/** Intake, feeder and shooter, driven by one state machine. */
public class Superstructure extends SubsystemBase {
  public enum State { IDLE, INTAKING, EJECTING, SPINUP, SHOOTING }

  private final IntakeIO intake;
  private final FeederIO feeder;
  private final ShooterIO shooter;
  private final DashboardIO dashboard;
  private final IntakeIOInputsAutoLogged intakeInputs = new IntakeIOInputsAutoLogged();
  private final FeederIOInputsAutoLogged feederInputs = new FeederIOInputsAutoLogged();
  private final ShooterIOInputsAutoLogged shooterInputs = new ShooterIOInputsAutoLogged();
  private final DashboardIOInputsAutoLogged dashboardInputs = new DashboardIOInputsAutoLogged();
  private final InterpolatingDoubleTreeMap shotTable = new InterpolatingDoubleTreeMap();

  private final Vision vision;
  private final BooleanSupplier aimed;
  private State wanted = State.IDLE;
  private State state = State.IDLE;
  private boolean useVision = true;
  private double targetRpm = 0;

  public Superstructure(
      IntakeIO intake, FeederIO feeder, ShooterIO shooter, DashboardIO dashboard,
      Vision vision, BooleanSupplier aimed) {
    this.intake = intake;
    this.feeder = feeder;
    this.shooter = shooter;
    this.dashboard = dashboard;
    this.vision = vision;
    this.aimed = aimed;
    for (double[] row : Constants.SHOT_TABLE) shotTable.put(row[0], row[1]);
  }

  /** SHOOT requests go to SPINUP; the state machine moves to SHOOTING on its own. */
  public void setWanted(State wanted, boolean useVision) {
    this.wanted = wanted == State.SHOOTING ? State.SPINUP : wanted;
    this.useVision = useVision;
  }

  public void setWanted(State wanted) {
    setWanted(wanted, true);
  }

  public State getState() {
    return state;
  }

  @Override
  public void periodic() {
    intake.updateInputs(intakeInputs);
    feeder.updateInputs(feederInputs);
    shooter.updateInputs(shooterInputs);
    dashboard.updateInputs(dashboardInputs);
    Logger.processInputs("Intake", intakeInputs);
    Logger.processInputs("Feeder", feederInputs);
    Logger.processInputs("Shooter", shooterInputs);
    Logger.processInputs("DashboardInputs", dashboardInputs);

    boolean tuning = dashboardInputs.tuneEnabled;
    if (tuning) {
      targetRpm = MathUtil.clamp(dashboardInputs.tuneRpm, 0, Constants.MAX_TUNE_RPM);
    } else {
      targetRpm = useVision && vision.hasTarget() ? shotTable.get(vision.getDistanceM()) : Constants.FALLBACK_RPM;
    }
    boolean atSpeed = Math.abs(shooterInputs.velocityRpm - targetRpm) < Constants.SHOOTER_TOLERANCE_RPM;
    boolean ready = atSpeed && (!useVision || aimed.getAsBoolean());

    state = switch (wanted) {
      case SPINUP -> ready ? State.SHOOTING : State.SPINUP;
      default -> wanted;
    };

    switch (state) {
      case IDLE -> runMotors(0, 0, false);
      case INTAKING -> runMotors(Constants.INTAKE_SPEED, 0, false);
      case EJECTING -> runMotors(Constants.EJECT_SPEED, Constants.EJECT_SPEED, false);
      case SPINUP -> runMotors(0, 0, true);
      case SHOOTING -> runMotors(Constants.INTAKE_SPEED, Constants.FEEDER_SPEED, true);
    }

    Logger.recordOutput("Super/WantedState", wanted);
    Logger.recordOutput("Super/State", state);
    Logger.recordOutput("Super/TargetRPM", targetRpm);
    Logger.recordOutput("Super/ShooterRPM", shooterInputs.velocityRpm);
    Logger.recordOutput("Super/AtSpeed", atSpeed);
    Logger.recordOutput("Super/Ready", ready);
    Logger.recordOutput("Super/UseVision", useVision);
    Logger.recordOutput("Super/Tuning", tuning);
  }

  private void runMotors(double intakeOut, double feederOut, boolean shooterOn) {
    intake.setPercent(intakeOut);
    feeder.setPercent(feederOut);
    if (shooterOn) {
      shooter.setVelocity(targetRpm);
    } else {
      shooter.stop();
    }
  }
}
