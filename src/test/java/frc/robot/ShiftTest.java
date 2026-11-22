package frc.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Checks the shift table against the BUILT game manual (Table 6-2/6-3). */
class ShiftTest {
  @Test
  void periodsMatchManual() {
    assertEquals(Shift.TRANSITION, Shift.duringTeleop(140));
    assertEquals(Shift.TRANSITION, Shift.duringTeleop(130.5));
    assertEquals(Shift.SHIFT_1, Shift.duringTeleop(130));
    assertEquals(Shift.SHIFT_1, Shift.duringTeleop(106));
    assertEquals(Shift.SHIFT_2, Shift.duringTeleop(105));
    assertEquals(Shift.SHIFT_3, Shift.duringTeleop(80));
    assertEquals(Shift.SHIFT_4, Shift.duringTeleop(55));
    assertEquals(Shift.END_GAME, Shift.duringTeleop(30));
    assertEquals(Shift.END_GAME, Shift.duringTeleop(0));
  }

  @Test
  void scoringSideAlternates() {
    assertFalse(Shift.TRANSITION.scoreOwnSide());
    assertTrue(Shift.SHIFT_1.scoreOwnSide());
    assertFalse(Shift.SHIFT_2.scoreOwnSide());
    assertTrue(Shift.SHIFT_3.scoreOwnSide());
    assertFalse(Shift.SHIFT_4.scoreOwnSide());
    assertTrue(Shift.END_GAME.scoreOwnSide());
    assertTrue(Shift.AUTO.scoreOwnSide());
  }

  @Test
  void secondsLeftCountsToShiftEnd() {
    assertEquals(10, Shift.TRANSITION.secondsLeft(140), 1e-9);
    assertEquals(15, Shift.SHIFT_1.secondsLeft(120), 1e-9);
    assertEquals(30, Shift.END_GAME.secondsLeft(30), 1e-9);
    assertEquals(0, Shift.DISABLED.secondsLeft(-1), 1e-9);
  }
}
