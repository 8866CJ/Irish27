# Irish27 robot code (IRISH 2026 BUILT)

## Layout

Each mechanism is split into a hardware layer and a logic layer (AdvantageKit's IO pattern):

| Folder | Hardware layer (talks to motors/camera) | Logic layer (decides what to do) |
|---|---|---|
| `subsystems/drive` | `DriveIO` + `DriveIOTalonSRX` | `Drive` |
| `subsystems/superstructure` | `IntakeIO`/`IntakeIOTalonSRX`, `FeederIO`/`FeederIOSparkMax`, `ShooterIO`/`ShooterIOSparkMax` | `Superstructure` (state machine) |
| `subsystems/vision` | `VisionIO` + `VisionIOPhoton` | `Vision` |
| `dashboard` | `DashboardIO` + `DashboardIONT` (Shooter Tuning values) | used by `Superstructure` |

Each `*IO` interface has an `Inputs` class marked `@AutoLog`. Everything read from hardware goes in there and is logged every loop. The logic layer reads only from inputs, never from hardware directly. That's what makes replay work.

## Logs

The robot publishes all logs over NetworkTables, and the driver dashboard saves them to the laptop (see the dashboard's `PROJECT_OVERVIEW.md`). There's no USB stick.

## Replay a log

1. Set `MODE` in `Constants.java` to `Mode.REPLAY`.
2. Run `.\gradlew.bat simulateJava` and pick a `.wpilog` when asked (or open it in AdvantageScope first).
3. The code runs against the logged inputs and writes `<name>_replay.wpilog` next to it. Compare the two in AdvantageScope to test a code change against a real match.
4. Set `MODE` back before deploying. On the real robot, it's always `REAL` anyway.
