package frc.robot.subsystems.feeder;

import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.Ports;

public class FeederIOSparkMax implements FeederIO {
  private final SparkMax motor = new SparkMax(Ports.FEEDER.id, MotorType.kBrushless);

  public FeederIOSparkMax() {
    SparkMaxConfig config = new SparkMaxConfig();
    config.idleMode(IdleMode.kBrake).smartCurrentLimit(FeederConstants.CURRENT_LIMIT_AMPS);
    motor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public void updateInputs(FeederIOInputs inputs) {
    inputs.busVolts = motor.getBusVoltage();
    inputs.connected = motor.getLastError() == REVLibError.kOk;
    inputs.appliedOutput = motor.getAppliedOutput();
    inputs.velocityRpm = motor.getEncoder().getVelocity();
    inputs.amps = motor.getOutputCurrent();
    inputs.tempC = motor.getMotorTemperature();
    inputs.faults = motor.getFaults().rawBits;
    inputs.stickyFaults = motor.getStickyFaults().rawBits;
  }

  @Override
  public void setPercent(double percent) {
    motor.set(percent);
  }
}
