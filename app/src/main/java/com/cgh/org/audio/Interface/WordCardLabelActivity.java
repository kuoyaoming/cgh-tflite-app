package com.cgh.org.audio.Interface;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cgh.org.audio.File.FileManager;
import com.example.patie.pcv1.R;
import com.cgh.org.audio.Recoder.AudioPlayFunc;
import com.cgh.org.audio.Recoder.AudioRecordFunc;
import com.cgh.org.audio.Config.Setup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WordCardLabelActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private final int MAX_RECORD_COUNT = Setup.TIME_RECORD_SECOND * 1000 / Setup.TIME_RECORD_UPDATE;
    private final int MAX_PLAY_COUNT = Setup.TIME_PLAY_SECOND * 1000 / Setup.TIME_PLAY_UPDATE;
    private Bundle bundle;
    public String Date;
    public String Id;
    public RecyclerView mRecyclerView;
    public WordCardLabelActivity.MyAdapter myAdapter;
    public List<WordCardLabelActivity.ButtonObject> btnPlayObject;
    public List<WordCardLabelActivity.ProgressObject> progressObjects;
    private AudioPlayFunc player;
    private AudioRecordFunc recorder;
    private int Update_Count = 0;
    private int play_control_position = -1;
    private List<String> wavfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordcard_label);

        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);

        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

        // 將drawerLayout和toolbar整合，會出現「三」按鈕
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                this.drawerLayout, this.toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // 為navigatin_view設置點擊事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // 點選時收起選單
                drawerLayout.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.record) {
                    Intent intent = new Intent();
                    intent.setClass(WordCardLabelActivity.this,MainActivity.class);

                    startActivity(intent);
                    WordCardLabelActivity.this.finish();
                    return true;
                }
                else if(id == R.id.label){
                    return true;
                }
                else if (id == R.id.upload) {
                    Intent intent = new Intent();
                    intent.setClass(WordCardLabelActivity.this,UploaderActivity.class);

                    startActivity(intent);
                    WordCardLabelActivity.this.finish();
                    return true;
                }
                return false;
            }
        });

        this.bundle = getIntent().getExtras();
        this.Id = this.bundle.getString("id");
        this.Date = this.bundle.getString("date");

        //建立專屬資料夾
        FileManager fileManager = new FileManager();
        String unlabeldirectoryPath = "/Medical_Project/Unlabeled/" + Date + "/" + Id + "/";
        String labeldirectoryPath = "/Medical_Project/Labeled/" + Id + "_" + Date + "/";
        recorder = AudioRecordFunc.getInstance();
        File file = new File(Setup.DEF_STORAGE_PATH + labeldirectoryPath);
        if(!file.exists()) {
            boolean isSuccess = file.mkdirs();
            try {
                fileManager.copyFile(unlabeldirectoryPath + "client_info.txt", labeldirectoryPath + "client_info.txt");
            }catch (IOException e){
                Log.v("WordCardLabelActivity","client_info doesn't exist");
            }
            Log.v("WordCardLabelActivity","dir_path = " + labeldirectoryPath + " Create: " + isSuccess);
        }

        wavfile = new ArrayList<String>();
        String[] wavfiles;
        wavfiles = fileManager.listSpecFile(unlabeldirectoryPath, ".wav");     //list all wav file
        if(wavfiles.length > 0){
            for(int i =0;i<wavfiles.length;i++){
                Log.v("WordCardLabelActivity", "wavfile = " + wavfiles[i]);
                wavfile.add(wavfiles[i]);
            }
        }

        player = AudioPlayFunc.getInstance();
        progressObjects = new ArrayList<>();
        btnPlayObject = new ArrayList<>();
        for(int i=0;i<wavfiles.length;i++){
            progressObjects.add(new WordCardLabelActivity.ProgressObject(i, "position " + i,0));
            btnPlayObject.add(new WordCardLabelActivity.ButtonObject(i,"position " + i,true));
        }
        myAdapter = new WordCardLabelActivity.MyAdapter(wavfile,progressObjects,btnPlayObject);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_wordcard_label);
        mRecyclerView.setItemViewCacheSize(100);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(myAdapter);
    }

    //返回鍵功能
    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event){
        if(KeyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent.setClass(WordCardLabelActivity.this, LabelActivity.class);
            myIntent.putExtras(bundle);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(KeyCode, event);
    }


    public class MyAdapter extends RecyclerView.Adapter<WordCardLabelActivity.MyAdapter.ViewHolder>{
        private List<String> mData;
        private List<WordCardLabelActivity.ButtonObject> mBtnPlayList;
        private List<WordCardLabelActivity.ProgressObject> mProgressObjectList;


        public MyAdapter(List<String> wavfile, List<ProgressObject> progressObject, List<ButtonObject> buttonObject) {
            mProgressObjectList = progressObject;
            mBtnPlayList = buttonObject;
            mData = wavfile;
        }

        public List<String> genMultiFile(List<String> unlabeledFile, int position) {

            String unlabeledFiles   = unlabeledFile.get(position);
            String unlabeledFiles2  = unlabeledFile.get(position);
            String unlabeledFiles3  = unlabeledFile.get(position);

            String file1;
            String file2;
            String file3;
            String containFile = unlabeledFiles.replace("wordcard", "").replace(".wav", "");

            List<String> list = new ArrayList<>();

            switch (containFile) {

                case "06_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("06_1", "29_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "11_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("11_2", "29_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "07_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("07_3", "29_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "20_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("20_1", "30_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "10_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("10_2", "30_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "19_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("19_3", "30_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "03_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("03_1", "31_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "01_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("01_2", "31_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);

                }
                case "06_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("06_3", "31_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "07_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("07_1", "32_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file3 = unlabeledFiles3.replace("07_1", "22_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";;
                    list.add(file1);
                    list.add(file2);
                    list.add(file3);
                    break;
                }
                case "06_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("06_2", "32_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "13_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("13_3", "32_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "11_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("11_1", "33_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "07_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("07_2", "33_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "05_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("05_3", "33_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "14_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("14_1", "34_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "13_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("13_2", "34_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "14_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("14_2", "34_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "02_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("02_1", "35_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;

                }
                case "12_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("12_2", "35_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file3 = unlabeledFiles3.replace("12_2", "22_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";;
                    list.add(file1);
                    list.add(file2);
                    list.add(file3);
                    break;
                }
                case "14_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("14_3", "35_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "04_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("04_1", "36_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "02_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("02_2", "36_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "02_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("02_3", "36_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "12_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("12_3", "22_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "10_1" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("10_1", "23_1").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "09_2" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("09_2", "23_2").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                case "09_3" : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    file2 = unlabeledFiles2.replace("09_3", "23_3").replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    list.add(file2);
                    break;
                }
                default : {
                    file1 = unlabeledFiles.replace("wordcard", "").replace(".wav", "") + "_1.wav";
                    list.add(file1);
                    break;
                }
            }
            return list;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public RelativeLayout mLinearLayout;
            public ImageView image_wc;
            public TextView text_wc;
            public ImageButton btn_play;
            public ProgressBar progressbar;
            public MultiSpinner multiSpinner;
            public Button btn_correct;
            public Button btn_store;
            public int mId;

            public ViewHolder(View v) {
                super(v);
                multiSpinner = new MultiSpinner(WordCardLabelActivity.this);
                image_wc = (ImageView) v.findViewById(R.id.img_wordcard_label);
                text_wc = (TextView) v.findViewById(R.id.text_wordcard_label);
                btn_play = (ImageButton) v.findViewById(R.id.btn_wordcard_label_play);
                progressbar = (ProgressBar) v.findViewById(R.id.progress_wordcard_label);
                multiSpinner = (MultiSpinner) v.findViewById(R.id.spinner_wordcard_label_error_class);
                btn_correct = (Button) v.findViewById(R.id.btn_wordcard_label_correct);
                btn_store = (Button) v.findViewById(R.id.btn_wordcard_label_store);
                mLinearLayout = (RelativeLayout) v.findViewById(R.id.wordcard_label_layout);
            }

            void bind(final WordCardLabelActivity.ProgressObject progressObject, WordCardLabelActivity.ButtonObject playObject){
                mId = progressObject.getId();
                progressbar.setProgress(progressObject.getProgress());
                btn_play.setEnabled(playObject.getStatus());
            }

            public int getId(){
                return mId;
            }
        }

        public void setProgress(int progress,int position){
            mProgressObjectList.get(position).setProgress(progress);
            notifyItemChanged(position,1);
        }

        public void initProgress(){
            for(int i=0;i<mProgressObjectList.size();i++){
                mProgressObjectList.get(i).setProgress(0);
                notifyItemChanged(i,1);
            }
        }

        public void disableBtn(){
            for(int i=0;i<mBtnPlayList.size();i++){
                mBtnPlayList.get(i).setStatus(false);
                notifyItemChanged(i,1);
            }
        }

        public void enableBtn(){
            for(int i=0;i<mBtnPlayList.size();i++){
                mBtnPlayList.get(i).setStatus(true);
                notifyItemChanged(i,1);
            }
        }

        @Override
        public WordCardLabelActivity.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.wordcard_labelcell, parent, false);
            WordCardLabelActivity.MyAdapter.ViewHolder vh = new WordCardLabelActivity.MyAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final WordCardLabelActivity.MyAdapter.ViewHolder holder, final int position) {
            //載入對應字卡以及描述
            final String image_name;
            final Resources resource = getResources();
            final FileManager fileManager = new FileManager();
            final String unlabeldirectoryPath = "/Medical_Project/Unlabeled/" + Date + "/" + Id + "/";
            final String labeldirectoryPath = "/Medical_Project/Labeled/" + Id + "_" + Date + "/";

            image_name = mData.get(position).replace(".wav","");
            Log.v("WordCardLabelActivity", "image name = " + image_name);
            int id = resource.getIdentifier(image_name,"drawable",getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            id = resource.getIdentifier(image_name,"string",getPackageName());
            String str_image = resource.getString(id);
            holder.image_wc.setImageDrawable(drawable);
            holder.text_wc.setText(str_image);

            //設定每個區塊背景, 為標記為亮黑色, 標記為暗黑色
            String[] files = fileManager.searchFileWithName(labeldirectoryPath, image_name.replace("wordcard",""),2);
            if(files != null && files.length > 0){
                Log.v("WordCardLabelActivity", position + " -> files size = " + files.length);
                int color = getResources().getColor(R.color.colorDarkGray);
                holder.mLinearLayout.setBackgroundColor(color);
            }else {
                Log.v("WordCardLabelActivity", position + " -> files null");
                int color = getResources().getColor(R.color.colorLightGray);
                holder.mLinearLayout.setBackgroundColor(color);
            }

            //設定錯誤類別
            holder.multiSpinner.setTitle("錯誤類別選擇");
            List<MultiSpinner.SimpleSpinnerOption> multispinnerList = new ArrayList<>();
            for (int i = 0; i < Setup.ERROR_TYPE.length; i++) {
                MultiSpinner.SimpleSpinnerOption option = new MultiSpinner.SimpleSpinnerOption();
                option.setName(Setup.ERROR_TYPE[i]);
                if(i<15) {
                    option.setValue(i + 1);
                }
                else{
                    option.setValue(i + 2);
                }
                multispinnerList.add(option);
            }
            holder.multiSpinner.setDataList(multispinnerList);

            //設定已標籤錯誤類別選擇
            Set<Object> labelSet= fileManager.searchLabelFileWithName(labeldirectoryPath, image_name.replace("wordcard",""));
            if(labelSet != null){
                Log.v("WordCardLabelActivity", position + " -> label files size = " + labelSet.size());
                if(labelSet.contains(16)){
                    holder.multiSpinner.setText("正確");
                }else{
                    holder.multiSpinner.setCheckedSet(labelSet);
                }
            }else {
                Log.v("WordCardLabelActivity", position + " -> label files null");
            }

            //播放鍵功能
            holder.btn_play.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    initProgress();
                    Update_Count = 0;
                    play_control_position = position;
                    //disableBtn();
                    player.stopPlay();
                    handler.removeMessages(Setup.REQ_PLAY_UPDATE);
                    String filename = Setup.DEF_STORAGE_PATH + unlabeldirectoryPath + mData.get(position);
                    player.startPlayWav(filename);
                    handler.sendEmptyMessageDelayed(Setup.REQ_PLAY_UPDATE,Setup.TIME_PLAY_UPDATE);
                }
            });

            //錯誤類別選擇
            holder.multiSpinner.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    holder.multiSpinner.display(v);
                }
            });

            //正確鍵功能 能夠在正確以及錯誤類別間切換
            holder.btn_correct.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
//                    if (holder.multiSpinner.getText().toString().equals("正確")) {
//                        holder.multiSpinner.showSelectedContent();
//                    } else {
//                        holder.multiSpinner.setText("正確");
//                    }
                    holder.multiSpinner.setText("正確");
                }
            });

            //儲存鍵功能
            holder.btn_store.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //刪除已標記檔案
                    List<String> unlabledFiles = new ArrayList<>();
                    Log.v("WordCardLabelActivity", "labeldirectory = " + labeldirectoryPath + "  filestring = " + mData.get(position).replace("wordcard","").replace(".wav",""));

                    unlabledFiles = genMultiFile(mData, position);

                    for (int k = 0; k < unlabledFiles.size(); k++) {
                        //String[] files = fileManager.searchFileWithName(labeldirectoryPath, mData.get(position).replace("wordcard", "").replace(".wav", ""));
                        String[] files = fileManager.searchFileWithName(labeldirectoryPath, unlabledFiles.get(k),2);

                        if (files != null) {
                            for (int i = 0; i < files.length; i++) {
                                Log.v("WordCardLabelActivity", "delete file name = " + files[i]);
                                File file = new File(Setup.DEF_STORAGE_PATH + labeldirectoryPath + files[i]);
                                boolean status = file.delete();
                            }
                        }
                    }

                    recorder = AudioRecordFunc.getInstance();
                    String filename = "";
                    String diagnosis = holder.multiSpinner.getText().toString();
                    if (diagnosis.equals("")) {
                        Toast.makeText(WordCardLabelActivity.this, "請選擇診斷結果", Toast.LENGTH_LONG).show();
                    } else if (diagnosis.equals("正確")) {
                        //檔案名稱為 類別_注音符號_字卡_第幾人
                        unlabledFiles = genMultiFile(mData, position);
                        for (int i = 0; i < unlabledFiles.size(); i++) {
                            //filename = "16_" + mData.get(position).replace("wordcard", "").replace(".wav","") + "_1.wav";
                            filename = "16_" + unlabledFiles.get(i);
                            try {
                                Log.v("WordCardLabelActivity","from wav dir = " + unlabeldirectoryPath + mData.get(position));
                                Log.v("WordCardLabelActivity","to wav dir = " + labeldirectoryPath + filename);
                                fileManager.copyFile(unlabeldirectoryPath + mData.get(position), labeldirectoryPath + filename);
                                notifyItemChanged(position,1);
                                Toast.makeText(WordCardLabelActivity.this,"儲存成功",Toast.LENGTH_SHORT).show();
                            }catch (IOException e){
                                Log.v("WordCardLabelActivity","audio file doesn't exist");
                            }
                        }
                    } else {
                        //檔案名稱為 類別_注音符號_字卡_第幾人
                        unlabledFiles = genMultiFile(mData, position);
                        List<MultiSpinner.SimpleSpinnerOption> obs = new ArrayList<>();
                        obs = holder.multiSpinner.getCheckedOptions();
                        for (int i = 0; i < unlabledFiles.size(); i++) {
                            for (MultiSpinner.SimpleSpinnerOption option : obs) {
                                    filename = option.getValue().toString() + "_" + unlabledFiles.get(i);
                                try {
                                    fileManager.copyFile(unlabeldirectoryPath + mData.get(position), labeldirectoryPath + filename);
                                    notifyItemChanged(position,1);
                                    Toast.makeText(WordCardLabelActivity.this, "儲存成功", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Log.v("WordCardLabelActivity", "client_info doesn't exist");
                                }
                            }
                        }
                    }
                }
            });

            holder.bind(mProgressObjectList.get(position), mBtnPlayList.get(position));
        }

        @Override
        public int getItemCount() {
            if(mData != null)
                return mData.size();
            else
                return 0;
        }
    }


    private class ProgressObject {
        private int mId;
        private String mTitle;
        private int mProgress;

        ProgressObject(int id, String title, int progress) {
            mId = id;
            mTitle = title;
            mProgress = progress;
        }

        int getId() {
            return mId;
        }

        void setId(int id) {
            mId = id;
        }

        String getTitle() {
            return mTitle;
        }

        void setTitle(String title) {
            mTitle = title;
        }

        int getProgress() {
            return mProgress;
        }

        void setProgress(int progress) {
            mProgress = progress;
        }
    }

    //軟體的button類型
    private class ButtonObject {
        private int mId;
        private String mTitle;
        private Boolean mStatus;

        ButtonObject(int id, String title, Boolean status) {
            mId = id;
            mTitle = title;
            mStatus = status;
        }

        int getId() {
            return mId;
        }

        void setId(int id) {
            mId = id;
        }

        String getTitle() {
            return mTitle;
        }

        void setTitle(String title) {
            mTitle = title;
        }

        Boolean getStatus() {
            return mStatus;
        }

        void setStatus(Boolean status) {
            mStatus = status;
        }
    }

    //軟體的bool類型
    private class BooleanObject {
        private int mId;
        private String mTitle;
        private Boolean mStatus;

        BooleanObject(int id, String title, Boolean status) {
            mId = id;
            mTitle = title;
            mStatus = status;
        }

        int getId() {
            return mId;
        }

        void setId(int id) {
            mId = id;
        }

        String getTitle() {
            return mTitle;
        }

        void setTitle(String title) {
            mTitle = title;
        }

        Boolean getStatus() {
            return mStatus;
        }

        void setStatus(Boolean status) {
            mStatus = status;
        }
    }

    //設定handler處理器
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case Setup.REQ_PLAY_UPDATE:
                {
                    if(Update_Count == MAX_PLAY_COUNT){
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        //myAdapter.enableBtn();
                        progressObjects.get(play_control_position).setProgress(0);
                        myAdapter.setProgress(0,play_control_position);
                        player.stopPlay();
                        Update_Count = 0;
                    }else{
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        Update_Count++;
                        progressObjects.get(play_control_position).setProgress(Update_Count*100/MAX_PLAY_COUNT);
                        myAdapter.setProgress(Update_Count*100/MAX_PLAY_COUNT,play_control_position);
                        handler.sendEmptyMessageDelayed(Setup.REQ_PLAY_UPDATE,Setup.TIME_PLAY_UPDATE);
                    }
                    break;
                }
            }
        }
    };
}
