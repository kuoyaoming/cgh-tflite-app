package com.cgh.org.audio.Interface;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.patie.pcv1.R;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResultActivity extends AppCompatActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        TextView textName1 = findViewById(R.id.textName1);
        TextView textName2 = findViewById(R.id.textName2);
        TextView textName3 = findViewById(R.id.textName3);
        TextView textInfo1 = findViewById(R.id.textInfo1);
        TextView textInfo2 = findViewById(R.id.textInfo2);
        TextView textInfo3 = findViewById(R.id.textInfo3);

        String[] classInformation = {"",
                "這類孩子發音的特徵就是把大部分的聲母，例如ㄉ、ㄊ、ㄓ、ㄔ、ㄕ等用ㄍ、ㄎ來代替，像「弟弟」說成「ㄍㄧˋ ㄍㄧˋ」，「蛋塔」說成「幹卡」。",
                "這類構音異常的孩子常把大部份的音都用ㄉ、ㄊ來代替，例如「哥哥」說成「ㄉㄜ ㄉㄜ」，「公園」說成了「東園」，「褲子」則成了「兔子」。",
                "在聲母中有很多音是屬於送氣音，例如ㄆ、ㄏ、ㄊ、ㄎ、ㄑ、ㄕ、ㄘ、ㄔ、ㄒ…都是有氣流的音，有些孩子在氣流跟音的協調上出了問題，所以「婆婆」發成「伯伯」，「泡泡糖」變成「爆爆檔」。",
                "個案在發音上會有把語音簡單化的傾向，把某一個聲母、韻母省略不發，例如「樓梯」變成「ㄡ ㄧ」；「弟弟」變成「ㄧˋ ㄧˋ」。",
                "5我不知道怎麼解釋",
                "6我不知道怎麼解釋",
                "7我不知道怎麼解釋",
                "8我不知道怎麼解釋",
                "9我不知道怎麼解釋",
                "10我不知道怎麼解釋",
                "11我不知道怎麼解釋",
                "12我不知道怎麼解釋",
                "13我不知道怎麼解釋",
                "14我不知道怎麼解釋",
                "15我不知道怎麼解釋",
                "正確發音"};

        String[] className = {"", "送氣化", "不送氣化", "唇音化", "舌根音化",
                "舌尖音化", "塞音化", "塞擦音化", "擦音化", "不卷舌化",
                "鼻音化", "邊音化", "子音省略", "附韻母省略", "聲隨韻母省略",
                "介音省略", "正確音"};

//        int[] classname = {6, 10, 11, 5, 1, 14, 15, 4, 9, 2, 8, 16, 12, 7, 3, 13};
        int[] classname = {2, 4, 6, 7, 9, 14, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15};

        Object[] objectArray = (Object[]) Objects.requireNonNull(getIntent().getExtras()).getSerializable("ndTfliteOutputData");
        assert objectArray != null;
        float[][] ndTfliteOutputData = new float[objectArray.length][];

        for (int i = 0; i < objectArray.length; i++) {
            ndTfliteOutputData[i] = (float[]) objectArray[i];
        }
        System.out.println("i get: " + Arrays.deepToString(ndTfliteOutputData));

        int pathnumber = ndTfliteOutputData.length;
        int labelnumber = ndTfliteOutputData[0].length;

        float[] addresult = new float[16];
        float[] threelarger;
        String[] percentage = new String[3];
        String[] classout = new String[3];
        String[] classInfo = new String[3];

        //result addition
        for (float[] ndTfliteOutputDatum : ndTfliteOutputData) {
            for (int j = 0; j < labelnumber; j++)
                addresult[j] = addresult[j] + ndTfliteOutputDatum[j];
        }
        // System.out.println("averesult: " + Arrays.toString(addresult));

        //取平均
        for (int j = 0; j < labelnumber; j++) {
            addresult[j] = addresult[j] / pathnumber;
        }
        System.out.println("averesult: " + Arrays.toString(addresult));
        //Map class
        Map<Float, String> map = new HashMap<>();
        Map<Float, String> mapInfo = new HashMap<>();

        for (int i = 0; i < labelnumber; i++) {
            map.put(addresult[i], className[classname[i]]);
            mapInfo.put(addresult[i], classInformation[classname[i]]);
        }

        threelarger = print3largest(addresult);
        DecimalFormat mDecimalFormat = new DecimalFormat("###.#");
        for (int i = 0; i < 3; i++) {
            percentage[i] = mDecimalFormat.format(threelarger[i] * 100);
            classout[i] = map.get(threelarger[i]);
            classInfo[i] = mapInfo.get(threelarger[i]);
        }

        textName1.setText(classout[0] + ": " + percentage[0] + "%" + "\n");
        textName2.setText(classout[1] + ": " + percentage[1] + "%" + "\n");
        textName3.setText(classout[2] + ": " + percentage[2] + "%" + "\n");
        textInfo1.setText(classInfo[0]);
        textInfo2.setText(classInfo[1]);
        textInfo3.setText(classInfo[2]);


    }


    float[] print3largest(float[] arr) {
        float first, second, third;
        float[] threelarger = new float[3];
        third = first = second = 0;
        for (float v : arr) {
            if (v > first) {
                third = second;
                second = first;
                first = v;
            } else if (v > second) {
                third = second;
                second = v;
            } else if (v > third)
                third = v;
        }
        threelarger[0] = first;
        threelarger[1] = second;
        threelarger[2] = third;

        return threelarger;

    }

}
