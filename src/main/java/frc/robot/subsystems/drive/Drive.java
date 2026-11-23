package frc.robot.subsystems.drive;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.vision.Vision;
import java.util.function.DoubleSupplier;
import org.littletonrobotics.junction.Logger;

public class Drive extends SubsystemBase {
  public enum State { MANUAL, AIM, INTAKE, AUTO_LEAVE, HOLD_AIM }

  private final DriveIO io;
  private final DriveIOInputsAutoLogged inputs = new DriveIOInputsAutoLogged();
  private final Vision vision;
  private final DoubleSupplier forward, strafe, turn;
  private State state = State.MANUAL;

  public Drive(DriveIO io, Vision vision, DoubleSupplier forward, DoubleSupplier strafe, DoubleSupplier turn) {
    this.io = io;
    this.vision = vision;
    this.forward = forward;
    this.strafe = strafe;
    this.turn = turn;
  }

  // Commands hold a state while scheduled and return to MANUAL when they end or are interrupted.

  /** Tag visible: driver is locked out and vision only rotates onto the HUB. No tag: normal driving. */
  public Command aimAtHub() {
    return hold(State.AIM).withName("AimAtHub");
  }

  /** Slow driving so the intake gets the current and we don't overrun fuel. */
  public Command intakeSpeed() {
    return hold(State.INTAKE).withName("IntakeSpeed");
  }

  /** Auto only: drive straight forward off the starting line. */
  public Command leaveStartingLine() {
    return hold(State.AUTO_LEAVE).withName("LeaveStartingLine");
  }

  /** Auto only: stay in place and rotate onto the HUB. */
  public Command holdAndAim() {
    return hold(State.HOLD_AIM).withName("HoldAndAim");
  }

  private Command hold(State state) {
    return startEnd(() -> this.state = state, () -> this.state = State.MANUAL);
  }

  public boolean isAimed() {
    return vision.hasTarget() && Math.abs(vision.getYawDeg()) < DriveConstants.AIM_TOLERANCE_DEG;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Drive", inputs);

    double x = 0, y = 0, z = 0;
    switch (state) {
      case MANUAL -> { x = forward.getAsDouble(); y = strafe.getAsDouble(); z = turn.getAsDouble(); }
      case AIM -> {
        if (vision.hasTarget()) z = aimRotation(0);
        else { x = forward.getAsDouble(); y = strafe.getAsDouble(); z = turn.getAsDouble(); }
      }
      case INTAKE -> {
        x = forward.getAsDouble() * DriveConstants.INTAKE_DRIVE_SCALE;
        y = strafe.getAsDouble() * DriveConstants.INTAKE_DRIVE_SCALE;
        z = turn.getAsDouble() * DriveConstants.INTAKE_TURN_SCALE;
      }
      case AUTO_LEAVE -> x = DriveConstants.AUTO_LEAVE_SPEED;
      case HOLD_AIM -> z = aimRotation(0);
    }
    var wheels = MecanumDrive.driveCartesianIK(x, y, z);
    io.setOutputs(wheels.frontLeft, wheels.frontRight, wheels.rearLeft, wheels.rearRight);

    Logger.recordOutput("Drive/State", state);
    Logger.recordOutput("Drive/Aimed", isAimed());
    Logger.recordOutput("Drive/Command", new double[] {x, y, z});
    Logger.recordOutput("Drive/WheelSetpoints",
        new double[] {wheels.frontLeft, wheels.frontRight, wheels.rearLeft, wheels.rearRight});
  }

  // Photon yaw is positive-right, drive rotation is positive-CCW. No tag: driver keeps turn control.
  private double aimRotation(double fallback) {
    if (!vision.hasTarget()) return fallback;
    return MathUtil.clamp(-vision.getYawDeg() * DriveConstants.AIM_KP, -DriveConstants.AIM_MAX_ROT, DriveConstants.AIM_MAX_ROT);
  }
}
