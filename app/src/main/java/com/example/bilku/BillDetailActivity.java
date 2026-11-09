package com.example.bilku;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class BillDetailActivity extends AppCompatActivity {

    TextView textViewMonth, textViewUnit, textViewTotal;
    TextView textViewRebate, textViewFinal;
    Button buttonEdit, buttonDelete, buttonBack;

    DataHelper dbHelper;
    protected Cursor cursor;

    int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        dbHelper = new DataHelper(this);

        textViewMonth = (TextView) findViewById(R.id.textViewMonth);
        textViewUnit = (TextView) findViewById(R.id.textViewUnit);
        textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewRebate = (TextView) findViewById(R.id.textViewRebate);
        textViewFinal = (TextView) findViewById(R.id.textViewFinal);

        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        billId = getIntent().getIntExtra("billId", -1);

        if (billId == -1) {
            Toast.makeText(getApplicationContext(),
                    "Bill not found",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayBill();

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        BillDetailActivity.this,
                        UpdateBillActivity.class);

                intent.putExtra("billId", billId);
                startActivity(intent);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBill();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (billId != -1) {
            displayBill();
        }
    }

    private void displayBill() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery(
                "SELECT * FROM electricity WHERE id = ?",
                new String[]{String.valueOf(billId)});

        if (cursor.moveToFirst()) {

            String month = cursor.getString(1);
            int unit = cursor.getInt(2);
            double total = cursor.getDouble(3);
            int rebate = cursor.getInt(4);
            double finalCost = cursor.getDouble(5);

            textViewMonth.setText(month);
            textViewUnit.setText(unit + " kWh");

            textViewTotal.setText(
                    String.format(Locale.US, "RM %.2f", total));

            textViewRebate.setText(rebate + "%");

            textViewFinal.setText(
                    String.format(Locale.US, "RM %.2f", finalCost));

        } else {
            Toast.makeText(getApplicationContext(),
                    "Bill not found",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        cursor.close();
    }

    private void deleteBill() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                BillDetailActivity.this, R.style.BilKuAlertDialog);

        builder.setTitle("Delete Bill");

        builder.setMessage(
                "Are you sure you want to delete this electricity bill?");

        builder.setPositiveButton("DELETE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        db.execSQL(
                                "DELETE FROM electricity WHERE id = ?",
                                new Object[]{billId}
                        );

                        Toast.makeText(getApplicationContext(),
                                "Bill deleted successfully",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });

        builder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        int butterYellow = ContextCompat.getColor(
                BillDetailActivity.this, R.color.pastel_yellow);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(butterYellow);

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(butterYellow);
    }
}
