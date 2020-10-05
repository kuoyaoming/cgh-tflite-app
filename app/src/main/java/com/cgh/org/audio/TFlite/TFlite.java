package com.cgh.org.audio.TFlite;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFlite {
    private int wavNumber;
    private Interpreter tflite = null;
    private float[][][] ndTfliteOutputData;
    int GRAPH_X = 257;
    int GRAPH_y = 500;
    int CLASSES = 5;

    public TFlite(AssetManager fileDescriptor, String modelFilename, int wavNumber, double[][][] pythonInputData) throws IOException {
        this.wavNumber = wavNumber;
        ndTfliteOutputData = new float[this.wavNumber][1][CLASSES];
        load_model(fileDescriptor, modelFilename);


        for (int i = 0; i < wavNumber; i++) {

            float[][][][] tfliteInputData = reshape(pythonInputData[i]);
            tflite.run(tfliteInputData, ndTfliteOutputData[i]);
        }
    }

    public float[][][] getNdTfliteOutputData() {
        return ndTfliteOutputData;
    }

    private MappedByteBuffer loadModelFile(AssetFileDescriptor fileDescriptor) throws IOException {
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void load_model(AssetManager assets, String modelFilename) throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        try {

            tflite = new Interpreter(loadModelFile(fileDescriptor));
            tflite.setNumThreads(4);

        } catch (IOException e) {
            System.out.println("load model error");
            e.printStackTrace();
        }
    }

    private float[][][][] reshape(double[][] inputData) {

        float[][] temData = new float[GRAPH_X][GRAPH_y];
        float[][][][] return_data = new float[1][GRAPH_X][GRAPH_y][1];

        for (int i = 0; i < GRAPH_X; i++) {
            for (int j = 0; j < GRAPH_y; j++) {
                temData[i][j] = (float) inputData[i][j];
            }
        }

        for (int i = 0; i < GRAPH_X; i++) {
            for (int j = 0; j < GRAPH_y; j++) {
                return_data[0][i][j][0] = temData[i][j];
            }
        }

        return return_data;
    }
}
