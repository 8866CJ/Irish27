# Irish27 robot code (IRISH 2026 BUILT)

## Layout

One folder per mechanism. Each has a hardware layer (AdvantageKit IO pattern), a subsystem, and its own constants:

| Folder | Hardware layer | Subsystem | Constants |
|---|---|---|---|
| `subsystems/drive` | `DriveIO` + `DriveIOTalonSRX` | `Drive` | `DriveConstants` |
| `subsystems/intake` | `IntakeIO` + `IntakeIOTalonSRX` | `Intake` | `IntakeConstants` |
| `subsystems/feeder` | `FeederIO` + `FeederIOSparkMax` | `Feeder` | `FeederConstants` |
| `subsystems/shooter` | `ShooterIO` + `ShooterIOSparkMax` | `Shooter` (shot table) | `ShooterConstants` |
| `subsystems/vision` | `VisionIO` + `VisionIOPhoton` | `Vision` | `VisionConstants` |
| `dashboard` | `DashboardIO` + `DashboardIONT` | `Dashboard` (tuning, alliance) | |
| `superstructure` | none | `Superstructure`: state machine that coordinates intake, feeder and shooter | |

Top level: `Ports` (every CAN ID), `Constants` (robot-wide settings only), `Shift` (BUILT match periods), `RobotContainer` (builds subsystems, button bindings, auto).

- **Hardware layer:** each `*IO` interface has an `Inputs` class marked `@AutoLog`. Everything read from hardware goes in there and is logged every loop. Subsystems read only from inputs, never from hardware directly. That's what makes replay work.
- **Commands:** `Drive` and `Superstructure` hand out named commands (`superstructure.intake()`, `drive.aimAtHub()`) that require the subsystem. Each one holds a state while its button is held and returns to idle when it ends. Pressing a new button cleanly interrupts the old one.
- **Tests:** `src/test` checks the shift table against the game manual and runs the state machine through the scheduler. Run them with `.\gradlew.bat test`.

## Logs

The robot publishes all logs over NetworkTables, and the driver dashboard saves them to the laptop (see the dashboard's `PROJECT_OVERVIEW.md`). There's no USB stick.

## Replay a log

1. Set `MODE` in `Constants.java` to `Mode.REPLAY`.
2. Run `.\gradlew.bat simulateJava` and pick a `.wpilog` when asked (or open it in AdvantageScope first).
3. The code runs against the logged inputs and writes `<name>_replay.wpilog` next to it. Compare the two in AdvantageScope to test a code change against a real match.
4. Set `MODE` back before deploying. On the real robot, it's always `REAL` anyway.
