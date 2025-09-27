package com.cgh.org.audio.Interface;

import android.annotation.SuppressLint;
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

import com.cgh.org.audio.Config.Setup;
import com.cgh.org.audio.File.FileManager;
import com.cgh.org.audio.Recoder.AudioPlayFunc;
import com.cgh.org.audio.Recoder.AudioRecordFunc;
import com.cgh.org.audio.Recoder.ClinetInfo;
import com.example.patie.pcv1.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordCardActivity extends AppCompatActivity {


    public int aphabet_number;
    public RecyclerView mRecyclerView;
    public MyAdapter myAdapter;
    public List<ProgressObject> progressObjects;
    public List<ButtonObject> btnPlayObject;
    public List<ButtonObject> btnRecordObject;
    public ArrayList<ListItem> wordcardsArray;
    public Button btn_Inf;
    public String wordcardsName;
    public String dir_path;
    public String file_path;
    public ArrayList<String> stringWav = new ArrayList<String>();
    public String AUDIO_WAV_DIRE;
    private DrawerLayout drawerLayout;
    private ClinetInfo info;
    private Bundle bundle;
    private String Date;
    private String Id;
    private AudioRecordFunc recorder;
    private AudioPlayFunc player;
    private int Update_Count = 0;
    private int record_control_position = -1;
    private int play_control_position = -1;
    //設定handler處理器
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Setup.REQ_RECORD_UPDATE: {
                    int MAX_RECORD_COUNT = Setup.TIME_RECORD_SECOND * 1000 / Setup.TIME_RECORD_UPDATE;
                    if (Update_Count >= MAX_RECORD_COUNT) {
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        progressObjects.get(record_control_position).setProgress(0);
                        myAdapter.setProgress(Update_Count * 100 / MAX_RECORD_COUNT, record_control_position);
                        recorder.stopRecordAndFile();
                        //myAdapter.enableBtn();
                        Update_Count = 0;
                    } else {
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        Update_Count++;
                        progressObjects.get(record_control_position).setProgress(Update_Count * 100 / MAX_RECORD_COUNT);
                        myAdapter.setProgress(Update_Count * 100 / MAX_RECORD_COUNT, record_control_position);
                        handler.sendEmptyMessageDelayed(Setup.REQ_RECORD_UPDATE, Setup.TIME_RECORD_UPDATE);
                    }
                    break;
                }
                case Setup.REQ_PLAY_UPDATE: {
                    int MAX_PLAY_COUNT = Setup.TIME_PLAY_SECOND * 1000 / Setup.TIME_PLAY_UPDATE;
                    if (Update_Count >= MAX_PLAY_COUNT) {
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        progressObjects.get(play_control_position).setProgress(0);
                        myAdapter.setProgress(Update_Count * 100 / MAX_PLAY_COUNT, play_control_position);
                        player.stopPlay();
                        //myAdapter.enableBtn();
                        Update_Count = 0;
                    } else {
                        //Log.v("WordCardActivity","progressobject " + control_position + " = " + progressObjects.get(control_position).getProgress());
                        Update_Count++;
                        progressObjects.get(play_control_position).setProgress(Update_Count * 100 / MAX_PLAY_COUNT);
                        myAdapter.setProgress(Update_Count * 100 / MAX_PLAY_COUNT, play_control_position);
                        handler.sendEmptyMessageDelayed(Setup.REQ_PLAY_UPDATE, Setup.TIME_PLAY_UPDATE);
                    }
                    break;
                }
            }
        }
    };

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordcard);

        this.drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.btn_Inf = findViewById(R.id.inference_btn);

        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

        // 將drawerLayout和toolbar整合，會出現「三」按鈕
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                this.drawerLayout, toolbar, R.string.open, R.string.close);
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
                    return true;
                } else if (id == R.id.label) {
                    Intent intent = new Intent();
                    intent.setClass(WordCardActivity.this, LabelActivity.class);

                    startActivity(intent);
                    WordCardActivity.this.finish();
                    return true;
                } else if (id == R.id.upload) {
                    Intent intent = new Intent();
                    intent.setClass(WordCardActivity.this, UploaderActivity.class);

                    startActivity(intent);
                    WordCardActivity.this.finish();
                    return true;
                }
                return false;
            }
        });


        bundle = getIntent().getExtras();
        info = (ClinetInfo) getIntent().getSerializableExtra("info");
        aphabet_number = Objects.requireNonNull(getIntent().getExtras()).getInt("alphabet");
        this.Id = info.GetId();
        this.Date = info.GetDate().replace(" / ", "-");
        String string_alphabet = String.valueOf(aphabet_number);
        Log.v("WordCardActivity", "Id = " + info.GetId());
        Log.v("WordCardActivity", "CaseID = " + info.GetCaseId());
        Log.v("WordCardActivity", "Date = " + info.GetDate());
        Log.v("WordCardActivity", "Tester = " + info.GetTester());
        Log.v("WordCardActivity", "Doctor = " + info.GetDoctor());
        Log.v("WordCardActivity", "Name = " + info.GetName());
        Log.v("WordCardActivity", "Gender = " + info.GetGender());
        Log.v("WordCardActivity", "Birthday = " + info.GetBirthday());
        Log.v("WordCardActivity", "Age = " + info.GetAge());
        Log.v("WordCardActivity", "School = " + info.GetSchool());
        Log.v("WordCardActivity", "ParentName = " + info.GetParentName());
        Log.v("WordCardActivity", "ContactNumber = " + info.GetContactNumber());


        //建立專屬資料夾
        // ios -> /Medical_Project/Unlabel/id/
        dir_path = Setup.DEF_STORAGE_PATH + "/Medical_Project/Unlabeled/" + info.GetDate().replace(" / ", "-") + "/" + info.GetId() + "/";
        File file = new File(dir_path);
        if (!file.exists()) {
            boolean isSuccess = file.mkdirs();
            Log.v("WordCardActivity", "dir_path = " + dir_path + " Create: " + isSuccess);
        }

        //建立病患txt檔案
        file_path = Setup.DEF_STORAGE_PATH + "/Medical_Project/Unlabeled/" + info.GetDate().replace(" / ", "-") + "/" + info.GetId() + "/" + "client_info.txt";
        file = new File(file_path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //將病患資料寫入txt檔案
        try {
            int id;
            String heading;
            FileWriter write = new FileWriter(file_path, false);
            BufferedWriter buf = new BufferedWriter(write);

            Resources resource = getResources();
            id = resource.getIdentifier("id", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetId());
            buf.newLine();

            id = resource.getIdentifier("caseid", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetCaseId());
            buf.newLine();

            id = resource.getIdentifier("date", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetDate().replace(" / ", "-"));
            buf.newLine();

            id = resource.getIdentifier("tester", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetTester());
            buf.newLine();

            id = resource.getIdentifier("doctor", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetDoctor());
            buf.newLine();

            id = resource.getIdentifier("name", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetName());
            buf.newLine();

            id = resource.getIdentifier("gender", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetGender());
            buf.newLine();

            id = resource.getIdentifier("birthday", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetBirthday().replace(" / ", "-"));
            buf.newLine();

            id = resource.getIdentifier("age", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetAge());
            buf.newLine();

            id = resource.getIdentifier("school", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetSchool());
            buf.newLine();

            id = resource.getIdentifier("parent_name", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetParentName());
            buf.newLine();

            id = resource.getIdentifier("contact_number", "string", getPackageName());
            heading = getResources().getString(id);
            buf.write(heading + " = " + info.GetContactNumber());
            buf.newLine();

            //寫入並關閉串流
            buf.flush();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder = AudioRecordFunc.getInstance();
        recorder.setContext(this);
        player = AudioPlayFunc.getInstance();

        progressObjects = new ArrayList<>();
        btnPlayObject = new ArrayList<>();
        btnRecordObject = new ArrayList<>();
        wordcardsArray = new ArrayList<>();

        // ㄅ~ㄙ and ㄩ
        int NUM_OF_ALPHABET = 22;
        int NUM_OF_ITEMS = 3;
        for (int i = 0; i < NUM_OF_ALPHABET * NUM_OF_ITEMS + NUM_OF_ALPHABET; i++) {
            progressObjects.add(new ProgressObject(i, "position " + i, 0));
            btnPlayObject.add(new ButtonObject(i, "position " + i, true));
            btnRecordObject.add(new ButtonObject(i, "position " + i, true));
        }

        //建立要顯示的字卡名稱
        for (int alphabet_num = 1; alphabet_num <= Setup.ALPHABETS.length; alphabet_num++) {
            HeaderModel Header_alphabet = new HeaderModel();
            Header_alphabet.setheader(Setup.ALPHABETS[alphabet_num - 1]);
            wordcardsArray.add(Header_alphabet);
            for (int item_num = 1; item_num <= NUM_OF_ITEMS; item_num++) {
                ChildModel Child_item = new ChildModel();
                if (alphabet_num < 10) {
                    wordcardsName = "wordcard0" + alphabet_num + "_" + item_num;
                } else if (alphabet_num == Setup.ALPHABETS.length) {
                    wordcardsName = "wordcard24" + "_" + item_num;
                } else {
                    wordcardsName = "wordcard" + alphabet_num + "_" + item_num;
                }

                Child_item.setChild(wordcardsName);
                wordcardsArray.add(Child_item);
            }
        }

        myAdapter = new MyAdapter(progressObjects, btnPlayObject, btnRecordObject, wordcardsArray);
        mRecyclerView = findViewById(R.id.recycler_wordcard);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(myAdapter);


        //預測鍵功能
        btn_Inf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(WordCardActivity.this, InferenceActivity.class);
                intent.putExtra("stringWav", stringWav);

                startActivity(intent);
//                WordCardActivity.this.finish();
            }
        });
    }


    //返回鍵功能
    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        if (KeyCode == KeyEvent.KEYCODE_BACK) {
            Intent myIntent = new Intent();
            myIntent.setClass(WordCardActivity.this, MainActivity.class);
            myIntent.putExtras(bundle);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(KeyCode, event);
    }


    public interface ListItem {
        boolean isHeader();

        String getName();
    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int LAYOUT_HEADER = 0;
        private static final int LAYOUT_CHILD = 1;
        private List<ProgressObject> mProgressObjectList;
        private List<ButtonObject> mBtnRecordList;
        private List<ButtonObject> mBtnPlayList;
        private ArrayList<ListItem> mData;

        MyAdapter(List<ProgressObject> progressobjects, List<ButtonObject> btnplayobject, List<ButtonObject> btnrecordobject, ArrayList<ListItem> listItems) {
            mProgressObjectList = progressobjects;
            mBtnPlayList = btnplayobject;
            mBtnRecordList = btnrecordobject;
            mData = listItems;
        }

        //提供setprocess的funciotn給外部
        void setProgress(int progress, int position) {
            mProgressObjectList.get(position).setProgress(progress);
            notifyItemChanged(position, 1);
        }

        //提供設置btnplay狀態的function給外部
        public void setBtnPlay(boolean status, int position) {
            mBtnPlayList.get(position).setStatus(status);
            notifyItemChanged(position, 1);
        }

        //提供設置btnplay狀態的function給外部
        public void setBtnRecord(boolean status, int position) {
            mBtnRecordList.get(position).setStatus(status);
            notifyItemChanged(position, 1);
        }

        void initProgress() {
            for (int i = 0; i < mProgressObjectList.size(); i++) {
                mProgressObjectList.get(i).setProgress(0);
                notifyItemChanged(i, 1);
            }
        }

        public void disableBtn() {
            for (int i = 0; i < mBtnRecordList.size(); i++) {
                mBtnPlayList.get(i).setStatus(false);
                mBtnRecordList.get(i).setStatus(false);
                notifyItemChanged(i, 1);
            }
        }

        void enableBtn() {
            for (int i = 0; i < mBtnRecordList.size(); i++) {
                mBtnPlayList.get(i).setStatus(true);
                mBtnRecordList.get(i).setStatus(true);
                notifyItemChanged(i, 1);
            }
        }

        //更新特定頁面
        public void updateViewHolder(int position) {
            notifyItemChanged(position, 1);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder vh;
            if (viewType == LAYOUT_HEADER) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.wordcardcell_header, parent, false);
                vh = new WordCardActivity.MyAdapter.HeaderViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.wordcardcell, parent, false);
                vh = new WordCardActivity.MyAdapter.ChildViewHolder(v);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final FileManager fileManager = new FileManager();
            final String unlabeldirectoryPath = "/Medical_Project/Unlabeled/" + Date + "/" + Id + "/";

            if (holder.getItemViewType() == LAYOUT_HEADER) {
                HeaderViewHolder vaultItemHolder = (HeaderViewHolder) holder;
                vaultItemHolder.mTextView.setText(mData.get(position).getName());

            } else {
                ChildViewHolder vaultItemHolder = (ChildViewHolder) holder;

                //載入對應字卡以及描述
                final Resources resource = getResources();
                //Log.v("WordCardActivity","image_name = " + mData.get(position).getName());
                int id = resource.getIdentifier(mData.get(position).getName(), "drawable", getPackageName());
                Drawable drawable = getResources().getDrawable(id);
                id = resource.getIdentifier(mData.get(position).getName(), "string", getPackageName());
                String str_image = resource.getString(id);
                vaultItemHolder.image_wc.setImageDrawable(drawable);
                vaultItemHolder.text_wc.setText(str_image);

                //設定每個區塊背景為灰色
                //設定每個區塊背景, 為標記為亮黑色, 標記為暗黑色
                String[] files = fileManager.searchFileWithName(unlabeldirectoryPath, mData.get(position).getName().replace("wordcard", ""), 1);
                if (files != null && files.length > 0) {
                    //Log.v("WordCardLabelActivity", position + " -> files size = " + files.length);
                    int color = getResources().getColor(R.color.colorDarkGray);
                    vaultItemHolder.mLinearLayout.setBackgroundColor(color);
                } else {
                    //Log.v("WordCardLabelActivity", position + " -> files null");
                    int color = getResources().getColor(R.color.colorLightGray);
                    vaultItemHolder.mLinearLayout.setBackgroundColor(color);
                }

                //錄音鍵功能
                vaultItemHolder.btn_record.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(WordCardActivity.this,position + "btn_record pressed",Toast.LENGTH_LONG).show();
                        initProgress();
                        //disableBtn();
                        Update_Count = 0;
                        record_control_position = position;
                        recorder.stopRecordAndFile();
                        player.stopPlay();
                        recorder = AudioRecordFunc.getInstance();
                        recorder.setContext(this);
                        handler.removeMessages(Setup.REQ_PLAY_UPDATE);
                        handler.removeMessages(Setup.REQ_RECORD_UPDATE);
                        AudioRecordFunc.AUDIO_WAV_DIR = "/Medical_Project/Unlabeled/" + info.GetDate().replace(" / ", "-") + "/" + info.GetId() + "/";
                        AudioRecordFunc.AUDIO_RAW_FILENAME = mData.get(position).getName() + ".raw";
                        AudioRecordFunc.AUDIO_WAV_FILENAME = mData.get(position).getName() + ".wav";
                        int Result = recorder.startRecordAndFile();
                        handler.sendEmptyMessageDelayed(Setup.REQ_RECORD_UPDATE, Setup.TIME_RECORD_UPDATE);

                        AUDIO_WAV_DIRE = Setup.DEF_STORAGE_PATH + AudioRecordFunc.AUDIO_WAV_DIR + AudioRecordFunc.AUDIO_WAV_FILENAME;
                        stringWav.add(AUDIO_WAV_DIRE);

                        System.out.println("stringWav=" + stringWav);
                    }
                });

                //播放鍵功能
                vaultItemHolder.btn_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initProgress();
                        //disableBtn();
                        Update_Count = 0;
                        play_control_position = position;
                        player.stopPlay();
                        recorder.stopRecordAndFile();
                        handler.removeMessages(Setup.REQ_PLAY_UPDATE);
                        handler.removeMessages(Setup.REQ_RECORD_UPDATE);
                        String directortPath = Setup.DEF_STORAGE_PATH + "/Medical_Project/Unlabeled/";
                        String filename = directortPath + Date + "/" + Id + "/" + mData.get(position).getName() + ".wav";
                        Log.v("WordCardActivity", "filename = " + filename);
                        File audiofile = new File(filename);
                        if (audiofile.exists()) {
                            //player = AudioPlayFunc.getInstance();
                            player.startPlayWav(filename);
                            handler.sendEmptyMessageDelayed(Setup.REQ_PLAY_UPDATE, Setup.TIME_PLAY_UPDATE);
                        } else {
                            enableBtn();
                            Toast.makeText(WordCardActivity.this, "該字卡尚未進行錄音", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                vaultItemHolder.bind_processbar(mProgressObjectList.get(position));
                vaultItemHolder.bind_btnplay(mBtnPlayList.get(position));
                vaultItemHolder.bind_btnrecord(mBtnRecordList.get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mData.get(position).isHeader()) {
                return LAYOUT_HEADER;
            } else {
                return LAYOUT_CHILD;
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ChildViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout mLinearLayout;
            ImageView image_wc;
            TextView text_wc;
            ImageButton btn_record;
            ImageButton btn_play;

            ProgressBar progressbar;
            int mId;

            ChildViewHolder(View v) {
                super(v);
                image_wc = v.findViewById(R.id.image_wordcard1);
                text_wc = v.findViewById(R.id.text_wordcard1);
                btn_record = v.findViewById(R.id.btn_wordcard1_recode);
                btn_play = v.findViewById(R.id.btn_wordcard1_play);

                progressbar = v.findViewById(R.id.progress_wordcard1);
                mLinearLayout = v.findViewById(R.id.wordcard_layout);
            }

            void bind_processbar(final ProgressObject progressObject) {
                mId = progressObject.getId();
                progressbar.setProgress(progressObject.getProgress());
            }

            void bind_btnplay(final ButtonObject buttonObject) {
                mId = buttonObject.getId();
                btn_play.setEnabled(buttonObject.getStatus());
            }

            void bind_btnrecord(final ButtonObject buttonObject) {
                mId = buttonObject.getId();
                btn_record.setEnabled(buttonObject.getStatus());
            }

            public int getId() {
                return mId;
            }
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {
            RelativeLayout mLinearLayout;
            TextView mTextView;

            HeaderViewHolder(View v) {
                super(v);
                mTextView = v.findViewById(R.id.label_header_text);
                mLinearLayout = v.findViewById(R.id.label_header_layout);
            }
        }
    }

    //軟體的progress類型
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

    public class HeaderModel implements ListItem {

        String header;

        void setheader(String header) {
            this.header = header;
        }

        @Override
        public boolean isHeader() {
            return true;
        }

        @Override
        public String getName() {
            return header;
        }
    }

    public class ChildModel implements ListItem {

        String child;

        void setChild(String child) {
            this.child = child;
        }

        @Override
        public boolean isHeader() {
            return false;
        }

        @Override
        public String getName() {
            return child;
        }
    }

}