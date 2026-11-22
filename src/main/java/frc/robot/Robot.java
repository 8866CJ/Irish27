package frc.robot;

import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedPowerDistribution;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

public class Robot extends LoggedRobot {
  private final RobotContainer container;
  private Command autoCommand;

  public Robot() {
    Logger.recordMetadata("Project", "Irish27");
    Logger.recordMetadata("Mode", Constants.MODE.name());
    switch (Constants.MODE) {
      // No USB stick: everything goes over NetworkTables and the dashboard laptop saves it.
      case REAL -> {
        Logger.addDataReceiver(new NT4Publisher());
        LoggedPowerDistribution.getInstance(Ports.PDP.id, ModuleType.kCTRE);
      }
      case SIM -> Logger.addDataReceiver(new NT4Publisher());
      case REPLAY -> {
        setUseTiming(false);
        String logPath = LogFileUtil.findReplayLog();
        Logger.setReplaySource(new WPILOGReader(logPath));
        Logger.addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_replay")));
      }
    }
    Logger.start();

    container = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    container.logMatchState();
  }

  @Override
  public void autonomousInit() {
    autoCommand = container.getAutonomousCommand();
    CommandScheduler.getInstance().schedule(autoCommand);
  }

  @Override
  public void teleopInit() {
    if (autoCommand != null) autoCommand.cancel();
  }

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }
}
