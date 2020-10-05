package com.cgh.org.audio.Config;

import android.os.Environment;

public class Setup {
    //google uploader 上傳網址
    public static final String DEF_USER_AGENT = "Mozilla/5.0 (X11; Linux86_64) AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.34 Safari/534.24";
    public static final String DEF_WEBAPP_URL = "https://driveuploader.com/upload/t6hj2e2uH3/";
    public static final String DEF_WEBAPP_NAME = "location";

    //設定系統儲存路徑
    public static final String DEF_STORAGE_PATH = Environment.getExternalStorageDirectory().toString();
    public static final String DEF_AUDIO_PATH = Environment.getExternalStorageDirectory().toString() + "/Medical_Project/Labeled/";
    public static final String DEF_AUDIO_PATH_UN = Environment.getExternalStorageDirectory().toString() + "/Medical_Project/Unlabeled/";
    public static final String AUDIO_RAW_FILENAME = "Temp.raw";
    public static final String AUDIO_WAV_FILENAME = "Temp.wav";

    //WordCardActivity 使用
    public static final int TIME_RECORD_UPDATE = 50;
    public static final int TIME_RECORD_SECOND = 3;
    public static final int TIME_PLAY_UPDATE = 50;
    public static final int TIME_PLAY_SECOND = 3;

    //WordCardActivity 使用
    public static final int REQ_RECORD_UPDATE = 1000;
    public static final int REQ_PLAY_UPDATE = 1001;

    //注音符號
    public  static final String[] ALPHABETS = {   "ㄅ","ㄆ","ㄇ","ㄈ",
                                                    "ㄉ","ㄊ","ㄋ","ㄌ",
                                                    "ㄍ","ㄎ","ㄏ","ㄐ",
                                                    "ㄑ","ㄒ","ㄓ","ㄔ",
                                                    "ㄕ","ㄖ","ㄗ","ㄘ",
                                                    "ㄙ","ㄩ"
    };

    //錯誤類別
    public static final String[] ERROR_TYPE = {   "送氣化",
                                                    "不送氣化",
                                                    "唇音化",
                                                    "舌根音化",
                                                    "舌尖音化",
                                                    "塞音化",
                                                    "塞擦音化",
                                                    "擦音化",
                                                    "不卷舌化",
                                                    "鼻音化",
                                                    "邊音化",
                                                    "子音省略",
                                                    "附韻母省略",
                                                    "聲隨韻母省略",
                                                    "介音省略",
                                                    "齒尖音",
                                                    "歪曲音",
                                                    "添加音",
                                                    "不確定"

    };
}
