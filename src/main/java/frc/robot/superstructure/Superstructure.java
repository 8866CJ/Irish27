package frc.robot.superstructure;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.dashboard.Dashboard;
import frc.robot.subsystems.feeder.Feeder;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterConstants;
import frc.robot.subsystems.vision.Vision;
import java.util.function.BooleanSupplier;
import org.littletonrobotics.junction.Logger;

/** State machine that coordinates the intake, feeder and shooter. Owns no hardware itself. */
public class Superstructure extends SubsystemBase {
  public enum State { IDLE, INTAKING, EJECTING, SPINUP, SHOOTING }

  private final Intake intake;
  private final Feeder feeder;
  private final Shooter shooter;
  private final Dashboard dashboard;
  private final Vision vision;
  private final BooleanSupplier aimed;

  private State wanted = State.IDLE;
  private State state = State.IDLE;
  private boolean useVision = true;
  private double targetRpm = 0;

  public Superstructure(
      Intake intake, Feeder feeder, Shooter shooter, Dashboard dashboard, Vision vision, BooleanSupplier aimed) {
    this.intake = intake;
    this.feeder = feeder;
    this.shooter = shooter;
    this.dashboard = dashboard;
    this.vision = vision;
    this.aimed = aimed;
  }

  // Commands hold a state while scheduled and return to IDLE when they end or are interrupted.

  public Command intake() {
    return hold(State.INTAKING, true).withName("Intake");
  }

  public Command eject() {
    return hold(State.EJECTING, true).withName("Eject");
  }

  /** Aims with vision, sets RPM from distance, and feeds once at speed and aimed. */
  public Command shoot() {
    return hold(State.SPINUP, true).withName("Shoot");
  }

  /** No vision: fixed RPM, feeds as soon as the shooter is at speed. */
  public Command fallbackShot() {
    return hold(State.SPINUP, false).withName("FallbackShot");
  }

  private Command hold(State state, boolean useVision) {
    return startEnd(
        () -> { wanted = state; this.useVision = useVision; },
        () -> wanted = State.IDLE);
  }

  public State getState() {
    return state;
  }

  @Override
  public void periodic() {
    boolean tuning = dashboard.tuneEnabled();
    if (tuning) {
      targetRpm = MathUtil.clamp(dashboard.tuneRpm(), 0, ShooterConstants.MAX_TUNE_RPM);
    } else if (useVision && vision.hasTarget()) {
      targetRpm = shooter.rpmForDistance(vision.getDistanceM());
    } else {
      targetRpm = ShooterConstants.FALLBACK_RPM;
    }
    boolean atSpeed = shooter.isAtSpeed(targetRpm);
    boolean ready = atSpeed && (!useVision || aimed.getAsBoolean());

    state = wanted == State.SPINUP && ready ? State.SHOOTING : wanted;

    switch (state) {
      case IDLE -> { intake.stop(); feeder.stop(); shooter.stop(); }
      case INTAKING -> { intake.intake(); feeder.stop(); shooter.stop(); }
      case EJECTING -> { intake.eject(); feeder.eject(); shooter.stop(); }
      case SPINUP -> { intake.stop(); feeder.stop(); shooter.spinTo(targetRpm); }
      case SHOOTING -> { intake.intake(); feeder.feed(); shooter.spinTo(targetRpm); }
    }

    Logger.recordOutput("Super/WantedState", wanted);
    Logger.recordOutput("Super/State", state);
    Logger.recordOutput("Super/TargetRPM", targetRpm);
    Logger.recordOutput("Super/ShooterRPM", shooter.getVelocityRpm());
    Logger.recordOutput("Super/AtSpeed", atSpeed);
    Logger.recordOutput("Super/Ready", ready);
    Logger.recordOutput("Super/UseVision", useVision);
    Logger.recordOutput("Super/Tuning", tuning);
  }
}
