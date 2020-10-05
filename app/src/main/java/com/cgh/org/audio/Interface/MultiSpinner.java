package com.cgh.org.audio.Interface;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.patie.pcv1.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//錯誤類別選單
public class MultiSpinner extends AppCompatTextView implements View.OnClickListener,DialogInterface.OnClickListener {

    private ListView listView;

    private Context context;

    private String title;

    private List<SimpleSpinnerOption> dataList;

    private List<Set<Object>> mCheckSetList;

    private int position = 0;

    private MultiSpinner.Adapter adapter;

    private Set<Object> checkedSet;

    private int selectCount = -1;

    private boolean isEmpty() {
        return dataList == null ? true : dataList.isEmpty();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCheckedSet(Set<Object> checkedSet) {
        this.checkedSet = checkedSet;
        showSelectedContent();
    }

    public List<SimpleSpinnerOption> getDataList() {
        return dataList;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public List<SimpleSpinnerOption> getCheckedOptions(){
        List<SimpleSpinnerOption> listoption = new ArrayList<>();
        if(checkedSet!= null) {
            for (Object o : checkedSet) {
                String i = o.toString();
                // 編號上跳過正確
                if(Integer.valueOf(i) < 16)
                    listoption.add(dataList.get(Integer.valueOf(i) - 1));
                else
                    listoption.add(dataList.get(Integer.valueOf(i) - 2));
            }
            return listoption;
        }
        else{
            return null;
        }
    }

    public void setDataList(List<SimpleSpinnerOption> dataList) {
        this.dataList = dataList;
        if (adapter == null) {
            adapter = new MultiSpinner.Adapter(dataList);
            this.listView.setAdapter(adapter);
        } else {
            adapter.setList(dataList);
            adapter.notifyDataSetChanged();
        }
    }

    public MultiSpinner(Context context) {
        super(context, null);
    }

    public MultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setOnClickListener(this);
        listView = new ListView(context);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        adapter = new MultiSpinner.Adapter(null);
        this.listView.setAdapter(adapter);
    }

    public MultiSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCheckSetList(List<Set<Object>> SetList){
        this.mCheckSetList = SetList;
    }

    public void setPosition(int position){
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        ViewGroup parent = (ViewGroup) listView.getParent();
        if (parent != null) {
            parent.removeView(listView);
            Log.d("MultiSpinner", "remove listView");
        }
        if (dataList == null) {
            Log.d("MultiSpinner", "no data to show");
        }
        Log.d("MultiSpinner", "alertdialog");
        adapter.setCheckedSet(checkedSet);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("确定", this)
                .setNegativeButton("取消", this)
                .setView(listView).show();
    }

    public void display(View view) {
        ViewGroup parent = (ViewGroup) listView.getParent();
        if (parent != null) {
            parent.removeView(listView);
            Log.d("MultiSpinner", "remove listView");
        }
        if (dataList == null) {
            Log.d("MultiSpinner", "no data to show");
        }
        adapter.setCheckedSet(checkedSet);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("确定", this)
                .setNegativeButton("取消", this)
                .setView(listView).show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i){
            case -1:
                this.checkedSet=adapter.getCheckedSet();
                showSelectedContent();
                break;
        }
    }

    public void showSelectedContent(){
        StringBuilder sb=new StringBuilder();
        if(getCheckedOptions()!= null) {
            for (SimpleSpinnerOption option : getCheckedOptions()) {
                sb.append(option.getName()).append(",");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            setText(sb.toString());
        }
        else{
            setText("");
        }
    }

    class Adapter extends BaseAdapter implements OnClickListener {

        private List<SimpleSpinnerOption> list;

        private Set<Object> checkedSet;

        public Adapter(List<SimpleSpinnerOption> list){
            this.list=list;
            checkedSet=new HashSet<Object>();
        }

        public void setList(List<SimpleSpinnerOption> list) {
            this.list = list;
        }

        public Set<Object> getCheckedSet(){
            return this.checkedSet;
        }

        public void setCheckedSet(Set<Object> checkedSet) {
            this.checkedSet=new HashSet<Object>();
            if(checkedSet!=null){
                this.checkedSet.addAll(checkedSet);
            }
        }

        @Override
        public int getCount() {
            return this.list==null?0:this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list==null?null:this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SimpleSpinnerOption mul=(SimpleSpinnerOption)this.getItem(position);
            MultiSpinner.Adapter.Wrapper wrapper=null;
            if(convertView==null){
//                convertView = LayoutInflater.from(MultiSpinner.this.getContext()).inflate(R.layout.item3,null);
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item3,null);
                //convertView = LayoutInflater.from(context).inflate(R.layout.item3,null);
                wrapper=new MultiSpinner.Adapter.Wrapper();
                wrapper.textView=(TextView)convertView.findViewById(R.id.item3_text_errorclass);
                wrapper.checkBox=(CheckBox)convertView.findViewById(R.id.item3_checkbox);
                wrapper.checkBox.setOnClickListener(this);
                convertView.setTag(wrapper);
                Log.v("MultiSpinner","create converView");
            }else {
                wrapper = (MultiSpinner.Adapter.Wrapper) convertView.getTag();
            }
            wrapper.textView.setText(mul.getName());

            if(checkedSet!=null){
                if(checkedSet.contains(mul.getValue())){
                    wrapper.checkBox.setChecked(true);
                }else{
                    wrapper.checkBox.setChecked(false);
                }
            }
            wrapper.checkBox.setTag(position);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            CheckBox checkBox=(CheckBox)v;
            Integer position=(Integer)checkBox.getTag();
            if(position==null){
                return;
            }
            SimpleSpinnerOption op=(SimpleSpinnerOption)getItem(position);
            if(checkBox.isChecked()){
                int maxCount= getSelectCount();
                if(maxCount>-1&&checkedSet.size()>=maxCount){
                    checkBox.setChecked(false);
//                    Toast.makeText(MultiSpinner.Con, String.format("最多只能选择 %s 个", selectCount), Toast.LENGTH_SHORT).show();
                    return;
                }
                checkedSet.add(op.getValue());
            }else{
                checkedSet.remove(op.getValue());
            }
        }

        class Wrapper{
            public TextView textView;
            public CheckBox checkBox;
        }
    }

    public static class SimpleSpinnerOption {

        private String name;
        private Object value;

        public SimpleSpinnerOption(){
            this.name="";
            this.value="";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

}

