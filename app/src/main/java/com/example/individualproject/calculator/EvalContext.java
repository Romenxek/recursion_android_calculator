package com.example.individualproject.calculator;

import java.math.MathContext;
import java.math.RoundingMode;

public class EvalContext {
    // Внутренняя точность для расчетов (очень высокая)
    public final MathContext mc = new MathContext(100, RoundingMode.HALF_EVEN);

    // Точность для вывода пользователю (чуть меньше, чтобы скрыть "хвосты")
    public final int displayScale = 20;
}