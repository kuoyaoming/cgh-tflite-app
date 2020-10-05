package com.cgh.org.audio.TFlite;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.ArrayList;

public class frontEnd {
    private int wavNumber;
    private double[][][] pythonInputData;
    private ArrayList<String> wavPath;
    int GRAPH_X = 257;
    int GRAPH_y = 500;


    public frontEnd(ArrayList<String> inwavPath, int inwavNumber) {
        wavPath = inwavPath;
        wavNumber = inwavNumber;
        pythonInputData = new double[wavNumber][GRAPH_X][GRAPH_y];
    }

    public double[][][] getPythonInputData() {
        for (int i = 0; i < wavNumber; i++) {
            double[][] pythonData = new double[GRAPH_X][GRAPH_y];

            try {
                pythonData = callPythonCode(wavPath.get(i));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("PATH ERROR ");
            }

            pythonInputData[i] = pythonData;
            System.out.println("wavPath: " + wavPath.get(i));
        }

        return pythonInputData;
    }


    private double[][] callPythonCode(String path) {
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("mfcc").callAttr("mfcc", path);
        JavaBean javaBean = pyObject.toJava(JavaBean.class);
        return javaBean.getData();
    }
}
