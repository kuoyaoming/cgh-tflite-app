package com.cgh.org.audio.Interface;



import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import com.cgh.org.audio.Recoder.ClinetInfo;
import com.example.patie.pcv1.R;

public class AlphabetActivity extends AppCompatActivity {
    //public ArrayList<String> Alphabet = new ArrayList<>();
    //初始化ListView頁面
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ClinetInfo info;
    private Bundle bundle;
    public RecyclerView mRecyclerView;
    public MyAdapter myAdapter;

    public final String[] Alphabet = {"ㄅ", "ㄆ", "ㄇ", "ㄈ", "ㄉ", "ㄊ", "ㄋ", "ㄌ", "ㄍ", "ㄎ", "ㄏ", "ㄐ",
            "ㄑ", "ㄒ", "ㄓ", "ㄔ", "ㄕ", "ㄖ", "ㄗ", "ㄘ", "ㄙ", "ㄧ", "ㄨ", "ㄩ", "ㄞ", "ㄟ", "ㄠ", "ㄡ",
            "ㄢ", "ㄣ", "ㄤ", "ㄥ"};
    public ArrayList<String> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alphabet);
        bundle = getIntent().getExtras();

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
                    return true;
                }
                else if(id == R.id.label){
                    Intent intent = new Intent();
                    intent.setClass(AlphabetActivity.this,LabelActivity.class);

                    startActivity(intent);
                    AlphabetActivity.this.finish();
                    return true;
                }
                else if (id == R.id.upload) {
                    Intent intent = new Intent();
                    intent.setClass(AlphabetActivity.this,UploaderActivity.class);

                    startActivity(intent);
                    AlphabetActivity.this.finish();
                    return true;
                }
                return false;
            }
        });



        for(int i=0;i<Alphabet.length;i++) {list.add(Alphabet[i]);}
        myAdapter = new MyAdapter(list);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(myAdapter);

        Toast.makeText(AlphabetActivity.this, "請點選注音進入字卡選單", Toast.LENGTH_LONG).show();
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> mData;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public RelativeLayout mLinearLayout;
            public ImageView mImageView;
            public TextView mTextView;
            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.info_text);
                mImageView = (ImageView) v.findViewById(R.id.info_img);
                mLinearLayout = (RelativeLayout) v.findViewById(R.id.alphabet_layout);
            }
        }

        public MyAdapter(List<String> data) {
            mData = data;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
//            holder.mTextView.setText(mData.get(position));
            //載入對應注音
            String image_name;
            Resources resource = getResources();
            if (position < 9){
                image_name = "alphabet0" + Integer.toString(position+1);
            }
            else{
                if (position<24) {
                    image_name = "alphabet" + Integer.toString(position+1);
                }
                else{
                    image_name = "alphabet" + Integer.toString(position+5);
                }
            }
            //Log.v("AlphabetActivity","imagename =" + image_name);
            int id = resource.getIdentifier(image_name,"drawable",getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            holder.mImageView.setImageDrawable(drawable);

            //設定背景為藍白交錯
            if(position % 2 == 1){
                int color = getResources().getColor(R.color.colorMediumSeagreen);
                holder.mLinearLayout.setBackgroundColor(color);
            }
            else{
                int color = getResources().getColor(R.color.colorYellowGreen);
                holder.mLinearLayout.setBackgroundColor(color);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳過ㄚㄛㄜㄝ的編號
                    if(position <24) {
                        bundle.putInt("alphabet", position+1);
                    }
                    else{
                        bundle.putInt("alphabet",position+5);
                    }

                    Intent intent = new Intent();
                    intent.setClass(AlphabetActivity.this,WordCardActivity.class);

                    intent.putExtras(bundle);
                    startActivity(intent);
                    AlphabetActivity.this.finish();
                }
            });

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    //返回鍵功能
    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event){
        if(KeyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent.setClass(AlphabetActivity.this, MainActivity.class);
            myIntent.putExtras(bundle);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(KeyCode, event);
    }

}

