package com.example.bilku;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    ListView listViewHistory;
    Button buttonBack;
    DataHelper dbHelper;
    protected Cursor cursor;

    ArrayList<String> months;
    ArrayList<String> costs;
    ArrayList<Integer> billIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = (ListView) findViewById(R.id.listViewHistory);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        dbHelper = new DataHelper(this);

        months = new ArrayList<String>();
        costs = new ArrayList<String>();
        billIds = new ArrayList<Integer>();

        RefreshList();

        listViewHistory.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id) {

                        int selectedBillId = billIds.get(position);

                        Intent intent = new Intent(
                                HistoryActivity.this,
                                BillDetailActivity.class);

                        intent.putExtra("billId", selectedBillId);
                        startActivity(intent);
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
        RefreshList();
    }

    public void RefreshList() {

        months.clear();
        costs.clear();
        billIds.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery(
                "SELECT id, month, final FROM electricity ORDER BY id DESC",
                null);

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);
            String month = cursor.getString(1);
            double finalCost = cursor.getDouble(2);

            billIds.add(id);
            months.add(month);
            costs.add(String.format(Locale.US, "RM %.2f", finalCost));
        }

        cursor.close();

        BillListAdapter adapter = new BillListAdapter(
                this, months, costs);

        listViewHistory.setAdapter(adapter);
    }
}
