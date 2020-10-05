package com.cgh.org.audio.Interface;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.TimingLogger;

import androidx.appcompat.app.AppCompatActivity;

import com.cgh.org.audio.TFlite.TFlite;
import com.cgh.org.audio.TFlite.frontEnd;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.patie.pcv1.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InferenceActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inference);

        new Thread(() -> {
            try {
                Thread.sleep(100);
                tflite_run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    void tflite_run() {

        ArrayList<String> stringWav = getIntent().getStringArrayListExtra("stringWav");
        assert stringWav != null;
        int PATH_number = stringWav.size();
        for (int i = 0; i < PATH_number; i++) {
            System.out.println("PATH: " + stringWav.get(i));
        }

        TimingLogger timings = new TimingLogger("MyTag", "KKK");
        initPython();
        timings.addSplit("initPython");
        double[][][] pythonInputData = new frontEnd(stringWav, PATH_number).getPythonInputData();
        float[][] twodimInData = new float[PATH_number][16];


        timings.addSplit("frontEnd");
        System.out.println("pythonInputData: " + Arrays.deepToString(pythonInputData));
        TFlite tFlite = null;
        try {
            AssetManager fileManager = getApplicationContext().getAssets();
            tFlite = new TFlite(fileManager, "EfficientNetB0_0929.tflite", PATH_number, pythonInputData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        timings.addSplit("LoadTFlite");
        assert tFlite != null;
        float[][][] ndTfliteOutputData = tFlite.getNdTfliteOutputData();
        int labelnumber = ndTfliteOutputData[0][0].length;
        System.out.println("ndTfliteOutputData: " + Arrays.deepToString(ndTfliteOutputData));
        timings.addSplit("inference");
        timings.dumpToLog();

        for (int i = 0; i < PATH_number; i++) {
            System.arraycopy(ndTfliteOutputData[i][0], 0, twodimInData[i], 0, labelnumber);
        }
        System.out.println("twodimInData: " + Arrays.deepToString(twodimInData));

        Intent i = new Intent(this, ResultActivity.class);
        Bundle mBundle = new Bundle();

        mBundle.putSerializable("ndTfliteOutputData", twodimInData);

        i.putExtras(mBundle);
        startActivity(i);

    }

    void initPython() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
}
