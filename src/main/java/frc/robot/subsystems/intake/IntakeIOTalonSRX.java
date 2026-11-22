package frc.robot.subsystems.intake;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Ports;

public class IntakeIOTalonSRX implements IntakeIO {
  private final TalonSRX motor = new TalonSRX(Ports.INTAKE.id);
  private final Faults faults = new Faults();
  private final StickyFaults stickyFaults = new StickyFaults();

  public IntakeIOTalonSRX() {
    motor.configFactoryDefault();
    motor.setNeutralMode(NeutralMode.Coast);
    motor.configContinuousCurrentLimit(IntakeConstants.CURRENT_LIMIT_AMPS);
    motor.enableCurrentLimit(true);
  }

  @Override
  public void updateInputs(IntakeIOInputs inputs) {
    inputs.busVolts = motor.getBusVoltage();
    inputs.connected = motor.getLastError() == ErrorCode.OK;
    inputs.appliedPercent = motor.getMotorOutputPercent();
    inputs.statorAmps = motor.getStatorCurrent();
    inputs.supplyAmps = motor.getSupplyCurrent();
    inputs.tempC = motor.getTemperature();
    motor.getFaults(faults);
    inputs.faults = faults.toBitfield();
    motor.getStickyFaults(stickyFaults);
    inputs.stickyFaults = stickyFaults.toBitfield();
  }

  @Override
  public void setPercent(double percent) {
    motor.set(TalonSRXControlMode.PercentOutput, percent);
  }
}
