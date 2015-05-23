package com.github.kjrz.highlights2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.grafika.ContinuousCaptureActivity;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button camButton = (Button) findViewById(R.id.camButton);
        camButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent camIntent = new Intent(Dashboard.this, CameraActivity.class);
                Dashboard.this.startActivity(camIntent);
            }
        });

        Button noiseButton = (Button) findViewById(R.id.noiseButton);
        noiseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "YO!", Toast.LENGTH_SHORT).show();
            }
        });

        Button grafikaButton = (Button) findViewById(R.id.grafikaButton);
        grafikaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent grafikaIntent = new Intent(Dashboard.this, ContinuousCaptureActivity.class);
                Dashboard.this.startActivity(grafikaIntent);
            }
        });

        Button cam2Button = (Button) findViewById(R.id.cam2Button);
        cam2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cam2Intent = new Intent(Dashboard.this, CameraActivity2.class);
                Dashboard.this.startActivity(cam2Intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
