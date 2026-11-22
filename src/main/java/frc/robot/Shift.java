package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * BUILT match periods (game manual Table 6-2/6-3). Both alliances can always score;
 * which side of the HUB counts for you swaps every shift.
 */
public enum Shift {
  DISABLED(0, true),
  AUTO(0, true),
  TRANSITION(130, false),
  SHIFT_1(105, true),
  SHIFT_2(80, false),
  SHIFT_3(55, true),
  SHIFT_4(30, false),
  END_GAME(0, true);

  /** Teleop timer value when this period ends. */
  private final double endsAt;
  private final boolean scoreOwnSide;

  Shift(double endsAt, boolean scoreOwnSide) {
    this.endsAt = endsAt;
    this.scoreOwnSide = scoreOwnSide;
  }

  public static Shift current() {
    if (!DriverStation.isEnabled()) return DISABLED;
    if (DriverStation.isAutonomous()) return AUTO;
    return duringTeleop(DriverStation.getMatchTime());
  }

  /** Teleop timer counts down 140 -> 0. */
  public static Shift duringTeleop(double matchTime) {
    for (Shift s : new Shift[] {TRANSITION, SHIFT_1, SHIFT_2, SHIFT_3, SHIFT_4}) {
      if (matchTime > s.endsAt) return s;
    }
    return END_GAME;
  }

  public boolean scoreOwnSide() {
    return scoreOwnSide;
  }

  public double secondsLeft(double matchTime) {
    return Math.max(0, matchTime - endsAt);
  }
}
