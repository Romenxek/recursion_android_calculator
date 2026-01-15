package com.example.individualproject.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.individualproject.R;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalcSession session;
    private TreeAdapter adapter;
    private TextView tvAngleMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        session = new CalcSession();
        tvAngleMode = findViewById(R.id.tvAngleMode);

        RecyclerView rv = findViewById(R.id.treeList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreeAdapter();
        rv.setAdapter(adapter);

        // Настройка кнопок цифр
        setupDigit(R.id.btn0, "0");
        setupDigit(R.id.btn1, "1");
        setupDigit(R.id.btn2, "2");
        setupDigit(R.id.btn3, "3");
        setupDigit(R.id.btn4, "4");
        setupDigit(R.id.btn5, "5");
        setupDigit(R.id.btn6, "6");
        setupDigit(R.id.btn7, "7");
        setupDigit(R.id.btn8, "8");
        setupDigit(R.id.btn9, "9");

        // Настройка операций
        setupOp(R.id.btnAdd, Operator.ADD);
        setupOp(R.id.btnSub, Operator.SUB);
        setupOp(R.id.btnMul, Operator.MUL);
        setupOp(R.id.btnDiv, Operator.DIV);

        // Кнопка равно
        Button eq = findViewById(R.id.btnEq);
        eq.setOnClickListener(v -> {
            session.pressEquals();
            refreshUI();
        });

        // Кнопка очистки
        Button clear = findViewById(R.id.btnClear);
        clear.setOnClickListener(v -> {
            session.resetAll();
            refreshUI();
        });

        findViewById(R.id.btnDel).setOnClickListener(v -> {
            session.deleteLastChar();
            refreshUI();
        });

        // Точка
        findViewById(R.id.btnDot).setOnClickListener(v -> {
            session.appendDecimal();
            refreshUI();
        });

        // Тригонометрия
        findViewById(R.id.btnSin).setOnClickListener(v -> {
            session.applyUnaryOp(Operator.SIN);
            refreshUI();
        });

        findViewById(R.id.btnCos).setOnClickListener(v -> {
            session.applyUnaryOp(Operator.COS);
            refreshUI();
        });

        findViewById(R.id.btnDeg).setOnClickListener(v -> {
            session.setDegreesMode(true);
            refreshUI();
        });

        findViewById(R.id.btnRad).setOnClickListener(v -> {
            session.setDegreesMode(false);
            refreshUI();
        });

        findViewById(R.id.btnSign).setOnClickListener(v -> {
            session.toggleSign();
            refreshUI();
        });

        refreshUI();
    }

    private void setupDigit(int id, String d) {
        findViewById(id).setOnClickListener(v -> {
            session.appendDigit(d);
            refreshUI();
        });
    }

    private void setupOp(int id, Operator op) {
        findViewById(id).setOnClickListener(v -> {
            session.startBinaryOp(op);
            refreshUI();
        });
    }

    private void refreshUI() {
        tvAngleMode.setText(session.getAngleMode());
        List<String> lines = session.buildDisplay();
        Collections.reverse(lines);
        adapter.setData(lines);
    }
}