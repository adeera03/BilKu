package com.example.bilku;

import android.database.Cursor;
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

import androidx.appcompat.app.AppCompatActivity;

public class UpdateBillActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText editTextUnit;
    SeekBar seekBarRebate;
    TextView textViewRebate;
    Button buttonUpdate, buttonBack;

    DataHelper dbHelper;
    protected Cursor cursor;

    int billId;

    String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bill);

        dbHelper = new DataHelper(this);

        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        editTextUnit = (EditText) findViewById(R.id.editTextUnit);
        seekBarRebate = (SeekBar) findViewById(R.id.seekBarRebate);
        textViewRebate = (TextView) findViewById(R.id.textViewRebate);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_month, months);

        adapter.setDropDownViewResource(
                R.layout.spinner_month_dropdown);

        spinnerMonth.setAdapter(adapter);

        seekBarRebate.setMax(5);

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

        billId = getIntent().getIntExtra("billId", -1);

        if (billId == -1) {
            Toast.makeText(getApplicationContext(),
                    "Bill not found",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayBill();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBill();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void displayBill() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery(
                "SELECT month, unit, rebate FROM electricity WHERE id = ?",
                new String[]{String.valueOf(billId)});

        if (cursor.moveToFirst()) {

            String month = cursor.getString(0);
            int unit = cursor.getInt(1);
            int rebate = cursor.getInt(2);

            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(month)) {
                    spinnerMonth.setSelection(i);
                    break;
                }
            }

            editTextUnit.setText(String.valueOf(unit));
            seekBarRebate.setProgress(rebate);
            textViewRebate.setText(rebate + "%");

        } else {
            Toast.makeText(getApplicationContext(),
                    "Bill not found",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        cursor.close();
    }

    private void updateBill() {

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

        String month = spinnerMonth.getSelectedItem().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "UPDATE electricity SET month = ?, unit = ?, total = ?, rebate = ?, final = ? WHERE id = ?",
                new Object[]{month, unit, totalCharges,
                        rebate, finalCost, billId}
        );

        Toast.makeText(getApplicationContext(),
                "Bill updated successfully",
                Toast.LENGTH_SHORT).show();

        finish();
    }
}
