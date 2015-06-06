package com.github.kjrz.highlights2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button galleryButton = (Button) findViewById(R.id.galleryButton);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_VIEW,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivity(galleryIntent);
            }
        });

        Button noiseButton = (Button) findViewById(R.id.noiseButton);
        noiseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent noiseIntent = new Intent(Dashboard.this, SensitivityCheck.class);
                startActivity(noiseIntent);
            }
        });

        Button cam2Button = (Button) findViewById(R.id.cam2Button);
        cam2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cam2Intent = new Intent(Dashboard.this, CameraActivity2.class);
                startActivity(cam2Intent);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        return id == R.id.action_settings || super.onOptionsItemSelected(item);
//    }
}
