package frc.robot.subsystems.drive;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants;

public class DriveIOTalonSRX implements DriveIO {
  private final TalonSRX[] motors = {
    new TalonSRX(Constants.FRONT_LEFT_ID),
    new TalonSRX(Constants.FRONT_RIGHT_ID),
    new TalonSRX(Constants.REAR_LEFT_ID),
    new TalonSRX(Constants.REAR_RIGHT_ID),
  };
  private final Faults faults = new Faults();
  private final StickyFaults stickyFaults = new StickyFaults();

  public DriveIOTalonSRX() {
    for (TalonSRX m : motors) {
      m.configFactoryDefault();
      m.setNeutralMode(NeutralMode.Brake);
    }
    motors[1].setInverted(true);
    motors[3].setInverted(true);
  }

  @Override
  public void updateInputs(DriveIOInputs inputs) {
    for (int i = 0; i < motors.length; i++) {
      TalonSRX m = motors[i];
      inputs.busVolts[i] = m.getBusVoltage();
      inputs.connected[i] = m.getLastError() == ErrorCode.OK;
      inputs.appliedPercent[i] = m.getMotorOutputPercent();
      inputs.statorAmps[i] = m.getStatorCurrent();
      inputs.supplyAmps[i] = m.getSupplyCurrent();
      inputs.tempC[i] = m.getTemperature();
      m.getFaults(faults);
      inputs.faults[i] = faults.toBitfield();
      m.getStickyFaults(stickyFaults);
      inputs.stickyFaults[i] = stickyFaults.toBitfield();
    }
  }

  @Override
  public void setOutputs(double frontLeft, double frontRight, double rearLeft, double rearRight) {
    motors[0].set(TalonSRXControlMode.PercentOutput, frontLeft);
    motors[1].set(TalonSRXControlMode.PercentOutput, frontRight);
    motors[2].set(TalonSRXControlMode.PercentOutput, rearLeft);
    motors[3].set(TalonSRXControlMode.PercentOutput, rearRight);
  }
}
