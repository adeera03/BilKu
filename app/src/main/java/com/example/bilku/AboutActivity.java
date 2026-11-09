package com.example.bilku;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    TextView textViewWebsite;
    Button buttonBack;


    String websiteUrl = "https://github.com/adeera03/BilKu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textViewWebsite = (TextView) findViewById(R.id.textViewWebsite);
        buttonBack = (Button) findViewById(R.id.buttonBack);

        textViewWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (websiteUrl.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Application website is not available yet",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                    startActivity(intent);
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
