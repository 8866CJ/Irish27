package frc.robot.subsystems.feeder;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;

public class Feeder extends SubsystemBase {
  private final FeederIO io;
  private final FeederIOInputsAutoLogged inputs = new FeederIOInputsAutoLogged();

  public Feeder(FeederIO io) {
    this.io = io;
  }

  @Override
  public void periodic() {
    io.updateInputs(inputs);
    Logger.processInputs("Feeder", inputs);
  }

  public void feed() {
    io.setPercent(FeederConstants.FEED_SPEED);
  }

  public void eject() {
    io.setPercent(FeederConstants.EJECT_SPEED);
  }

  public void stop() {
    io.setPercent(0);
  }
}
