package frc.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.dashboard.Dashboard;
import frc.robot.dashboard.DashboardIO;
import frc.robot.subsystems.feeder.Feeder;
import frc.robot.subsystems.feeder.FeederIO;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.intake.IntakeIO;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterIO;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.VisionIO;
import frc.robot.superstructure.Superstructure;
import frc.robot.superstructure.Superstructure.State;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Runs the real state machine through the scheduler with hardware stubbed out (like replay mode). */
class SuperstructureTest {
  private Superstructure superstructure;

  @BeforeEach
  void setUp() {
    HAL.initialize(500, 0);
    DriverStationSim.setEnabled(true);
    DriverStationSim.notifyNewData();
    CommandScheduler.getInstance().cancelAll();
    CommandScheduler.getInstance().unregisterAllSubsystems();
    superstructure = new Superstructure(
        new Intake(new IntakeIO() {}),
        new Feeder(new FeederIO() {}),
        new Shooter(new ShooterIO() {}),
        new Dashboard(new DashboardIO() {}),
        new Vision(new VisionIO() {}),
        () -> false);
  }

  @AfterEach
  void tearDown() {
    CommandScheduler.getInstance().cancelAll();
    CommandScheduler.getInstance().unregisterAllSubsystems();
  }

  private static void run() {
    CommandScheduler.getInstance().run();
  }

  @Test
  void holdingAButtonHoldsItsStateAndReleasingReturnsToIdle() {
    Command intake = superstructure.intake();
    CommandScheduler.getInstance().schedule(intake);
    run();
    assertEquals(State.INTAKING, superstructure.getState());

    intake.cancel();
    run();
    assertEquals(State.IDLE, superstructure.getState());
  }

  @Test
  void shootWaitsInSpinupUntilShooterIsAtSpeed() {
    CommandScheduler.getInstance().schedule(superstructure.fallbackShot());
    run();
    assertEquals(State.SPINUP, superstructure.getState()); // stub shooter never spins up
  }

  @Test
  void releasingAnInterruptedButtonDoesNotStopTheNewAction() {
    Command intake = superstructure.intake();
    Command shoot = superstructure.fallbackShot();
    CommandScheduler.getInstance().schedule(intake);
    run();
    CommandScheduler.getInstance().schedule(shoot); // shoot pressed while intake still held
    run();
    assertEquals(State.SPINUP, superstructure.getState());

    intake.cancel(); // driver lets go of intake
    run();
    assertEquals(State.SPINUP, superstructure.getState());
  }
}
