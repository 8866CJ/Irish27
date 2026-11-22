package frc.robot.subsystems.drive;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.subsystems.vision.Vision;
import java.util.function.DoubleSupplier;
import org.littletonrobotics.junction.Logger;

public class Drive extends SubsystemBase {
  public enum State { MANUAL, AIM, AUTO_LEAVE, STOPPED }

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

  public void setState(State state) {
    this.state = state;
  }

  public boolean isAimed() {
    return vision.hasTarget() && Math.abs(vision.getYawDeg()) < Constants.AIM_TOLERANCE_DEG;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Drive", inputs);

    double x = 0, y = 0, z = 0;
    switch (state) {
      case MANUAL -> { x = forward.getAsDouble(); y = strafe.getAsDouble(); z = turn.getAsDouble(); }
      case AIM -> { x = forward.getAsDouble(); y = strafe.getAsDouble(); z = aimRotation(turn.getAsDouble()); }
      case AUTO_LEAVE -> x = Constants.AUTO_LEAVE_SPEED;
      case STOPPED -> z = aimRotation(0);
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
    return MathUtil.clamp(-vision.getYawDeg() * Constants.AIM_KP, -Constants.AIM_MAX_ROT, Constants.AIM_MAX_ROT);
  }
}
