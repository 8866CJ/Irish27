package frc.robot.subsystems.superstructure;

import com.revrobotics.PersistMode;
import com.revrobotics.REVLibError;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import frc.robot.Constants;

public class ShooterIOSparkMax implements ShooterIO {
  private final SparkMax leader = new SparkMax(Constants.SHOOTER_LEADER_ID, MotorType.kBrushless);
  private final SparkMax follower = new SparkMax(Constants.SHOOTER_FOLLOWER_ID, MotorType.kBrushless);

  public ShooterIOSparkMax() {
    SparkMaxConfig leaderConfig = new SparkMaxConfig();
    leaderConfig.idleMode(IdleMode.kCoast).smartCurrentLimit(40);
    leaderConfig.closedLoop.p(Constants.SHOOTER_KP).feedForward.kV(Constants.SHOOTER_KV);
    leader.configure(leaderConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    SparkMaxConfig followerConfig = new SparkMaxConfig();
    followerConfig.idleMode(IdleMode.kCoast).smartCurrentLimit(40).follow(leader, true);
    follower.configure(followerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public void updateInputs(ShooterIOInputs inputs) {
    inputs.velocityRpm = leader.getEncoder().getVelocity();
    inputs.busVolts = leader.getBusVoltage();
    inputs.leaderConnected = leader.getLastError() == REVLibError.kOk;
    inputs.leaderAppliedOutput = leader.getAppliedOutput();
    inputs.leaderAmps = leader.getOutputCurrent();
    inputs.leaderTempC = leader.getMotorTemperature();
    inputs.leaderFaults = leader.getFaults().rawBits;
    inputs.leaderStickyFaults = leader.getStickyFaults().rawBits;

    inputs.followerAppliedOutput = follower.getAppliedOutput();
    inputs.followerConnected = follower.getLastError() == REVLibError.kOk;
    inputs.followerAmps = follower.getOutputCurrent();
    inputs.followerTempC = follower.getMotorTemperature();
    inputs.followerFaults = follower.getFaults().rawBits;
    inputs.followerStickyFaults = follower.getStickyFaults().rawBits;
  }

  @Override
  public void setVelocity(double rpm) {
    leader.getClosedLoopController().setSetpoint(rpm, ControlType.kVelocity);
  }

  @Override
  public void stop() {
    leader.stopMotor();
  }
}
