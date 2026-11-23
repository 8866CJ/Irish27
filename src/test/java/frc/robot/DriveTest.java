package frc.robot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.drive.Drive;
import frc.robot.subsystems.drive.DriveConstants;
import frc.robot.subsystems.drive.DriveIO;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Driver holds the stick full forward; checks what actually reaches the wheels. */
class DriveTest {
  private final double[] wheels = new double[4];
  private boolean tagVisible;
  private Drive drive;

  @BeforeEach
  void setUp() {
    HAL.initialize(500, 0);
    DriverStationSim.setEnabled(true);
    DriverStationSim.notifyNewData();
    CommandScheduler.getInstance().cancelAll();
    CommandScheduler.getInstance().unregisterAllSubsystems();
    Vision vision = new Vision(new VisionIO() {
      @Override
      public void updateInputs(VisionIOInputs inputs) {
        inputs.tagIds = tagVisible ? new int[] {9} : new int[0];
        inputs.yawsDeg = tagVisible ? new double[] {0} : new double[0];
        inputs.pitchesDeg = tagVisible ? new double[] {10} : new double[0];
      }
    });
    drive = new Drive(new DriveIO() {
      @Override
      public void setOutputs(double fl, double fr, double rl, double rr) {
        wheels[0] = fl; wheels[1] = fr; wheels[2] = rl; wheels[3] = rr;
      }
    }, vision, () -> 1.0, () -> 0.0, () -> 0.0);
  }

  @AfterEach
  void tearDown() {
    CommandScheduler.getInstance().cancelAll();
    CommandScheduler.getInstance().unregisterAllSubsystems();
  }

  private void runWith(edu.wpi.first.wpilibj2.command.Command command) {
    CommandScheduler.getInstance().schedule(command);
    CommandScheduler.getInstance().run();
  }

  @Test
  void aimingWithATagLocksOutTheDriver() {
    tagVisible = true;
    runWith(drive.aimAtHub());
    assertArrayEquals(new double[] {0, 0, 0, 0}, wheels, 1e-9); // tag dead center, so no turn either
  }

  @Test
  void seeingATagWhileJustDrivingDoesNotLock() {
    tagVisible = true;
    CommandScheduler.getInstance().run(); // no shoot button held
    assertArrayEquals(new double[] {1, 1, 1, 1}, wheels, 1e-9);
  }

  @Test
  void aimingWithoutATagDrivesNormally() {
    tagVisible = false;
    runWith(drive.aimAtHub());
    assertArrayEquals(new double[] {1, 1, 1, 1}, wheels, 1e-9);
  }

  @Test
  void intakingScalesTheDriveDown() {
    runWith(drive.intakeSpeed());
    double s = DriveConstants.INTAKE_DRIVE_SCALE;
    assertArrayEquals(new double[] {s, s, s, s}, wheels, 1e-9);
  }
}
