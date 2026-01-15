package com.example.individualproject.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.obermuhlner.math.big.BigDecimalMath;

public class CalcSession {

    private static final EvalContext CTX = new EvalContext();
    private StringBuilder currentInput = new StringBuilder();
    private final List<ExpressionLine> lines = new ArrayList<>();
    private int nextXIndex = 1;
    private boolean isResultMode = false;
    private String errorMessage = null;

    private boolean isDegrees = true;
    public void setDegreesMode(boolean degrees) {
        this.isDegrees = degrees;
    }
    public String getAngleMode() {
        return isDegrees ? "DEG" : "RAD";
    }

    static class ExpressionLine {
        String leftLabel;
        String expression;
        boolean isFinalNumber;

        ExpressionLine(String left, String expr, boolean isFinal) {
            this.leftLabel = left;
            this.expression = expr;
            this.isFinalNumber = isFinal;
        }

        String getDisplay() {
            return leftLabel + " = " + expression;
        }
    }

    public void appendDigit(String digit) {
        if (errorMessage != null) resetAll(); // Сброс при ошибке
        if (isResultMode) {
            currentInput.setLength(0);
            isResultMode = false;
        }
        if (currentInput.toString().equals("0") && !digit.equals(".")) {
            currentInput.setLength(0);
        }
        currentInput.append(digit);
    }

    public void appendDecimal() {
        if (errorMessage != null) resetAll();
        if (isResultMode) {
            currentInput.setLength(0);
            currentInput.append("0");
            isResultMode = false;
        }
        if (currentInput.length() == 0) {
            currentInput.append("0.");
        } else if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
        }
    }

    public void startBinaryOp(Operator op) {
        BigDecimal leftNum = getCurrentAsNumber();
        String leftLbl = lines.isEmpty() ? "ANS" : "x" + (nextXIndex - 1);
        String rightLbl = "x" + nextXIndex;
        String expr = leftNum.toPlainString() + " " + opToString(op) + " " + rightLbl;

        lines.add(new ExpressionLine(leftLbl, expr, false));
        nextXIndex++;
        currentInput.setLength(0);
        isResultMode = false;
    }

    public void pressEquals() {
        if (lines.isEmpty() || errorMessage != null) return;

        int lastIndex = lines.size() - 1;
        ExpressionLine lastOp = lines.remove(lastIndex);
        BigDecimal rightValue = getCurrentAsNumber();

        String[] parts = lastOp.expression.split(" ");
        if (parts.length >= 2) {
            try {
                BigDecimal leftValue = new BigDecimal(parts[0]);
                Operator op = stringToOp(parts[1]);
                BigDecimal result = apply(leftValue, op, rightValue);

                currentInput.setLength(0);
                currentInput.append(result.toPlainString());

                isResultMode = true;
                nextXIndex--;
            } catch (ArithmeticException e) {
                errorMessage = "Div by 0";
            } catch (Exception e) {
                errorMessage = "Error";
            }
        }
    }

    private BigDecimal getCurrentAsNumber() {
        if (currentInput.length() == 0) return BigDecimal.ZERO;
        try {
            return new BigDecimal(currentInput.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal apply(BigDecimal a, Operator op, BigDecimal b) {
        switch (op) {
            case ADD: return a.add(b, CTX.mc);
            case SUB: return a.subtract(b, CTX.mc);
            case MUL: return a.multiply(b, CTX.mc);
            case DIV:
                if (b.signum() == 0) throw new ArithmeticException("Division by zero");
                return a.divide(b, CTX.mc);
            default:  return b;
        }
    }

    public void applyUnaryOp(Operator op) {
        if (errorMessage != null) return;
        try {
            BigDecimal val = getCurrentAsNumber();
            BigDecimal angle;

            if (isDegrees) {
                BigDecimal pi = BigDecimalMath.pi(CTX.mc);
                angle = val.multiply(pi, CTX.mc)
                        .divide(new BigDecimal("180"), CTX.mc);
            } else {
                angle = val;
            }

            BigDecimal res;
            if (op == Operator.SIN) {
                res = BigDecimalMath.sin(angle, CTX.mc);
            } else {
                res = BigDecimalMath.cos(angle, CTX.mc);
            }

            currentInput.setLength(0);
            currentInput.append(res.stripTrailingZeros().toPlainString());
            isResultMode = true;
        } catch (Exception e) {
            errorMessage = "Error";
        }
    }

    private String opToString(Operator op) {
        switch (op) {
            case ADD: return "+";
            case SUB: return "-";
            case MUL: return "×";
            case DIV: return "÷";
            default:  return "?";
        }
    }

    public List<String> buildDisplay() {
        if (errorMessage != null) {
            return Collections.singletonList("Status = " + errorMessage);
        }

        List<String> result = new ArrayList<>();
        for (ExpressionLine line : lines) {
            result.add(line.leftLabel + " = " + formatExpression(line.expression));
        }

        String currentVar = lines.isEmpty() ? "ANS" : "x" + (nextXIndex - 1);
        String displayVal = currentInput.toString();

        if (isResultMode && displayVal.length() > 0) {
            displayVal = formatForDisplay(new BigDecimal(displayVal));
        } else if (displayVal.length() == 0) {
            displayVal = "0";
        }

        result.add(currentVar + " = " + displayVal);
        return result;
    }

    private String formatExpression(String expr) {
        String[] parts = expr.split(" ");
        if (parts.length >= 1) {
            try {
                parts[0] = formatForDisplay(new BigDecimal(parts[0]));
            } catch (Exception ignored) {}
        }
        if (parts.length >= 3) {
            try {
                parts[2] = formatForDisplay(new BigDecimal(parts[2]));
            } catch (Exception ignored) {}
        }
        return String.join(" ", parts);
    }

    private String formatForDisplay(BigDecimal value) {
        if (value == null) return "0";

        BigDecimal displayValue = value.setScale(12, RoundingMode.HALF_UP);
        displayValue = displayValue.stripTrailingZeros();

        return displayValue.toPlainString();
    }

    private Operator stringToOp(String s) {
        switch (s) {
            case "+": return Operator.ADD;
            case "-": return Operator.SUB;
            case "×": return Operator.MUL;
            case "÷": return Operator.DIV;
            default:  return null;
        }
    }

    public void resetAll() {
        lines.clear();
        currentInput.setLength(0);
        errorMessage = null;
        nextXIndex = 1;
        isResultMode = false;
    }

    public void deleteLastChar() {
        if (errorMessage != null) {
            resetAll();
            return;
        }

        // Если мы только что получили результат, DEL очищает его целиком
        if (isResultMode) {
            currentInput.setLength(0);
            isResultMode = false;
            return;
        }

        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);

            if (currentInput.toString().equals("-")) {
                currentInput.setLength(0);
            }
        }
    }

    public void toggleSign() {
        if (errorMessage != null) return;
        if (isResultMode) {
            isResultMode = false;
        }

        String text = currentInput.toString();
        if (text.isEmpty() || text.equals("0")) return;

        if (text.startsWith("-")) {
            currentInput.deleteCharAt(0);
        } else {
            currentInput.insert(0, "-");
        }
    }

}