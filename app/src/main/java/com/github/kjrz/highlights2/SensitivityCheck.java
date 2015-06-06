package com.github.kjrz.highlights2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import be.hogent.tarsos.dsp.onsets.OnsetHandler;

public class SensitivityCheck extends Activity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "Noise";

    private final ClapCatcher clapper = new ClapCatcher();

    private int sensitivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        setSensitivity();
        setClapHandler();
    }

    private void setSensitivity() {
        sensitivity = ClapCatcher.getSensitivityFromPreferences(this);
        SeekBar bar = (SeekBar) findViewById(R.id.sensitivity);
        bar.setProgress(sensitivity);
        bar.setOnSeekBarChangeListener(this);
    }

    private void setClapHandler() {
        clapper.onCreate(sensitivity, new OnsetHandler() {
            @Override
            public void handleOnset(double time, double salience) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clapHeard();
                    }
                });
            }
        });
    }

    private void clapHeard() {
        Log.i(TAG, getString(R.string.clapNoted));
        Toast.makeText(this, getString(R.string.clapNoted), Toast.LENGTH_SHORT).show();
    }

    private void setLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_noise);
    }

    @Override
    public void onResume() {
        super.onResume();
        clapper.onResume();
    }

    @Override
    public void onPause() {
        clapper.onPause();
        super.onPause();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        showSensitivity(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        showSensitivity(seekBar.getProgress());
    }

    private void showSensitivity(int progress) {
        TextView value = (TextView) findViewById(R.id.value);
        value.setText("Sensitivity: " + progress);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        hideSensitivity();
        sensitivity = seekBar.getProgress();
        clapper.setSensitivity(sensitivity);
        clapper.onPause();
        clapper.onResume();
        ClapCatcher.saveSensitivityInPreferences(this, sensitivity);
    }

    private void hideSensitivity() {
        TextView value = (TextView) findViewById(R.id.value);
        value.setText("");
    }
}
