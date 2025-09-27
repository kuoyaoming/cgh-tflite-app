package com.cgh.org.audio.Interface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cgh.org.audio.File.FileManager;
import com.example.patie.pcv1.R;

import java.util.ArrayList;

public class LabelActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Bundle bundle;

    private String[] DateDirs;
    private String[][] IdDirs;
    public RecyclerView mRecyclerView;
    public LabelActivity.MyAdapter myAdapter;
    private ArrayList<ListItem> Directory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

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
                    intent.setClass(LabelActivity.this,MainActivity.class);

                    startActivity(intent);
                    LabelActivity.this.finish();
                    return true;
                }
                else if(id == R.id.label){
                    return true;
                }
                else if (id == R.id.upload) {
                    Intent intent = new Intent();
                    intent.setClass(LabelActivity.this,UploaderActivity.class);

                    startActivity(intent);
                    LabelActivity.this.finish();
                    return true;
                }
                return false;
            }
        });


        //find all directory in Unlabeled
        this.mRecyclerView = findViewById(R.id.recycler_label);

        String directortPath = "/Medical_Project/Unlabeled/";

        FileManager fileManager = new FileManager();
        DateDirs = fileManager.listDir(directortPath);      //list all Date Directory
        if(DateDirs!= null && DateDirs.length > 0){
            this.Directory = new ArrayList<>();
            IdDirs = new String[DateDirs.length][];
            for(int i=0;i<DateDirs.length;i++){
                Log.v("LabelActivity", "DataDir = " + DateDirs[i]);

                HeaderModel Header_date = new HeaderModel();
                Header_date.setheader(DateDirs[i]);
                Directory.add(Header_date);
                IdDirs[i] = fileManager.listDir(directortPath + DateDirs[i] + "/");
                for(int j=0;j<IdDirs[i].length;j++){
                    Log.v("LabelActivity", "IdDir = " + IdDirs[i][j]);

                    ChildModel Child_id = new ChildModel();
                    Child_id.setChild(IdDirs[i][j]);
                    Directory.add(Child_id);
                }
            }
        }

        // create viewholder and set it in recyclerView
        myAdapter = new MyAdapter(this, this.Directory);
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
    }

    public interface ListItem {
        boolean isHeader();
        String getName();
    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int LAYOUT_HEADER = 0;
        private static final int LAYOUT_CHILD = 1;

        private  LayoutInflater inflater;
        private Context context;
        private ArrayList<LabelActivity.ListItem> mData;

        public MyAdapter(Context context, ArrayList<LabelActivity.ListItem> listItems) {
            this.inflater = LayoutInflater.from(context);
            this.context = context;
            this.mData = listItems;
        }

        public class ChildViewHolder extends RecyclerView.ViewHolder {
            public RelativeLayout mLinearLayout;
            public ImageView mImageView;
            public TextView mTextView;

            public ChildViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.label_text);
                mImageView = (ImageView) v.findViewById(R.id.label_img);
                mLinearLayout = (RelativeLayout) v.findViewById(R.id.label_layout);
            }
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder{
            public RelativeLayout mLinearLayout;
            public TextView mTextView;

            public HeaderViewHolder(View v){
                super(v);
                mTextView = (TextView) v.findViewById(R.id.label_header_text);
                mLinearLayout = (RelativeLayout) v.findViewById(R.id.label_header_layout);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            if(viewType == LAYOUT_HEADER) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.labelcell_header, parent, false);
                 vh = new LabelActivity.MyAdapter.HeaderViewHolder(v);
            }
            else {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.labelcell, parent, false);
                vh = new LabelActivity.MyAdapter.ChildViewHolder(v);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(holder.getItemViewType()== LAYOUT_HEADER)
            {
                HeaderViewHolder vaultItemHolder = (HeaderViewHolder) holder;
                vaultItemHolder.mTextView.setText(mData.get(position).getName());
            }
            else {
                ChildViewHolder vaultItemHolder = (ChildViewHolder) holder;
                vaultItemHolder.mTextView.setText(mData.get(position).getName());

                //set tap action when item clicked
                vaultItemHolder.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        bundle = new Bundle();
                        bundle.putString("id", mData.get(position).getName());
                        int header_index = 0;
                        do{
                            if(mData.get(position - header_index).isHeader()){
                                bundle.putString("date", mData.get(position - header_index).getName());
                                break;
                            }
                            header_index = header_index + 1;
                        }while(true);

                        Intent intent = new Intent();
                        intent.setClass(LabelActivity.this,WordCardLabelActivity.class);

                        intent.putExtras(bundle);
                        startActivity(intent);
                        LabelActivity.this.finish();
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position){
            if(mData.get(position).isHeader()){
                return LAYOUT_HEADER;
            }else {
                return LAYOUT_CHILD;
            }
        }

        @Override
        public int getItemCount() {
            if (mData != null)
                return mData.size();
            else
                return 0;
        }
    }

    public class HeaderModel implements LabelActivity.ListItem{

        String header;

        public void setheader(String header) {
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

    public class ChildModel implements LabelActivity.ListItem{

        String child;

        public void setChild(String child) {
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
