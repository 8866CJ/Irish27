package frc.robot.subsystems.shooter;

import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;

public class Shooter extends SubsystemBase {
  private final ShooterIO io;
  private final ShooterIOInputsAutoLogged inputs = new ShooterIOInputsAutoLogged();
  private final InterpolatingDoubleTreeMap shotTable = new InterpolatingDoubleTreeMap();

  public Shooter(ShooterIO io) {
    this.io = io;
    for (double[] row : ShooterConstants.SHOT_TABLE) shotTable.put(row[0], row[1]);
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Shooter", inputs);
  }

  public double rpmForDistance(double meters) {
    return shotTable.get(meters);
  }

  public void spinTo(double rpm) {
    io.setVelocity(rpm);
  }

  public void stop() {
    io.stop();
  }

  public double getVelocityRpm() {
    return inputs.velocityRpm;
  }

  public boolean isAtSpeed(double targetRpm) {
    return Math.abs(inputs.velocityRpm - targetRpm) < ShooterConstants.TOLERANCE_RPM;
  }
}
