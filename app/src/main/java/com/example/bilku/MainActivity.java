package com.example.bilku;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText editTextUnit;
    SeekBar seekBarRebate;
    TextView textViewRebate, textViewTotal, textViewFinal;
    Button buttonCalculate, buttonHistory, buttonAbout;

    DataHelper dbHelper;

    String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};

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

        dbHelper = new DataHelper(this);

        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        editTextUnit = (EditText) findViewById(R.id.editTextUnit);
        seekBarRebate = (SeekBar) findViewById(R.id.seekBarRebate);
        textViewRebate = (TextView) findViewById(R.id.textViewRebate);
        textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewFinal = (TextView) findViewById(R.id.textViewFinal);
        buttonCalculate = (Button) findViewById(R.id.buttonCalculate);
        buttonHistory = (Button) findViewById(R.id.buttonHistory);
        buttonAbout = (Button) findViewById(R.id.buttonAbout);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_month, months);

        adapter.setDropDownViewResource(
                R.layout.spinner_month_dropdown);

        spinnerMonth.setAdapter(adapter);

        seekBarRebate.setMax(5);
        seekBarRebate.setProgress(0);
        textViewRebate.setText("0%");

        seekBarRebate.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress,
                                                  boolean fromUser) {
                        textViewRebate.setText(progress + "%");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateBill();
            }
        });

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,
                        HistoryActivity.class);
                startActivity(intent);
            }
        });

        buttonAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,
                        AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void calculateBill() {

        String unitInput = editTextUnit.getText().toString().trim();

        if (unitInput.isEmpty()) {
            editTextUnit.setError("Please enter electricity usage");
            editTextUnit.requestFocus();
            return;
        }

        int unit;

        try {
            unit = Integer.parseInt(unitInput);
        } catch (NumberFormatException e) {
            editTextUnit.setError("Please enter a valid number");
            editTextUnit.requestFocus();
            return;
        }

        if (unit < 1 || unit > 1000) {
            editTextUnit.setError("Units must be between 1 and 1000");
            editTextUnit.requestFocus();
            return;
        }

        double totalCharges;

        if (unit <= 200) {
            totalCharges = unit * 0.218;
        } else if (unit <= 300) {
            totalCharges = (200 * 0.218)
                    + ((unit - 200) * 0.334);
        } else if (unit <= 600) {
            totalCharges = (200 * 0.218)
                    + (100 * 0.334)
                    + ((unit - 300) * 0.516);
        } else {
            totalCharges = (200 * 0.218)
                    + (100 * 0.334)
                    + (300 * 0.516)
                    + ((unit - 600) * 0.546);
        }

        int rebate = seekBarRebate.getProgress();

        double finalCost = totalCharges
                - (totalCharges * rebate / 100.0);

        textViewTotal.setText(
                String.format(Locale.US, "RM %.2f", totalCharges));

        textViewFinal.setText(
                String.format(Locale.US, "RM %.2f", finalCost));

        String month = spinnerMonth.getSelectedItem().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "INSERT INTO electricity(month, unit, total, rebate, final) VALUES(?, ?, ?, ?, ?)",
                new Object[]{month, unit, totalCharges, rebate, finalCost}
        );

        Toast.makeText(getApplicationContext(),
                "Bill calculated and saved successfully",
                Toast.LENGTH_SHORT).show();
    }
}
