package frc.robot.subsystems.intake;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;

public class Intake extends SubsystemBase {
  private final IntakeIO io;
  private final IntakeIOInputsAutoLogged inputs = new IntakeIOInputsAutoLogged();

  public Intake(IntakeIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Intake", inputs);
  }

  public void intake() {
    io.setPercent(IntakeConstants.INTAKE_SPEED);
  }

  public void eject() {
    io.setPercent(IntakeConstants.EJECT_SPEED);
  }

  public void stop() {
    io.setPercent(0);
  }
}
