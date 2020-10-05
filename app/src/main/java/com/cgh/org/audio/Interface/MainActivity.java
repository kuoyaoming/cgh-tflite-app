package com.cgh.org.audio.Interface;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cgh.org.audio.Recoder.ClinetInfo;
import com.example.patie.pcv1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    static int flag = 0;
    //性別表單
    final String[] gender_array = {"生理男", "生理女", "其他", "性別"};
    private final int REQUESTCODE = 101;
    private final int AUDIOREQUESTCODE = 102;
    public ArrayList<String> gender_list = new ArrayList<>();
    String account, password;
    String staff1, staff2;
    ChooseArrayAdapter<String> gender_adapter;
    String date;
    private DrawerLayout drawerLayout;
    private EditText edit_id;
    private EditText edit_case_id;
    private EditText edit_date;
    private EditText edit_tester;
    private EditText edit_doctor;
    private EditText edit_birthday;
    private EditText edit_age;
    private EditText edit_school;
    private EditText edit_name;
    private EditText edit_parent_name;
    private EditText edit_contact_number;
    private String string_id;
    private String string_case_id;
    private String string_date;
    private String string_tester;
    private String string_doctor;
    private String string_name;
    private String string_gender;
    private String string_birthday;
    private String string_age;
    private String string_school;
    private String string_parent_name;
    private String string_contact_number;
    private EditText id, pwd;

    private void showAlertDialog() {
        final Map<String, String> map1 = new HashMap<>();
        final Map<String, String> map2 = new HashMap<>();

        map1.put("staff1", "st00000001");
        map1.put("staff2", "st00000002");
        map1.put("staff3", "st00000003");
        map1.put("staff4", "st00000004");
        map1.put("staff5", "st00000005");
        map1.put("staff6", "st00000006");
        map1.put("staff7", "st00000007");
        map1.put("staff8", "st00000008");
        map1.put("staff9", "st00000009");

        map2.put("staff1", "cgh001");
        map2.put("staff2", "cgh002");
        map2.put("staff3", "cgh003");
        map2.put("staff4", "cgh004");
        map2.put("staff5", "cgh005");
        map2.put("staff6", "cgh006");
        map2.put("staff7", "cgh007");
        map2.put("staff8", "cgh008");
        map2.put("staff9", "cgh009");

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.password);
        dialog.setCancelable(false);
        Button btn_loggin = dialog.findViewById(R.id.login);

        id = dialog.findViewById(R.id.id);
        pwd = dialog.findViewById(R.id.pwd);


        btn_loggin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                account = id.getText().toString();
                password = pwd.getText().toString();


                for (Entry ey : map1.entrySet()) {
                    if (Objects.equals(account, ey.getValue())) {
                        staff1 = (String) ey.getKey();
                    }

                }

                for (Entry ey : map2.entrySet()) {
                    if (Objects.equals(password, ey.getValue())) {
                        staff2 = (String) ey.getKey();
                    }

                }

                if (staff1 == null || staff2 == null)
                    Toast.makeText(MainActivity.this, "帳號或密碼錯誤，請重新輸入！", Toast.LENGTH_SHORT).show();
                else if (staff1.equals(staff2))
                    dialog.dismiss();
                    //Toast.makeText(MainActivity.this, "HAHAH", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, "不同治療師，請重新輸入！", Toast.LENGTH_SHORT).show();

                }

            }
        });
        dialog.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*

        if (flag == 0) {

            showAlertDialog();
            flag = 1;
        }
*/

        //SharedPreferences settings = getSharedPreferences("account", 0);
        //boolean dialogShown = settings.getBoolean("dialogShown", false);
/*
        if (!dialogShown) {
            showAlertDialog();
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.commit();
        }
*/
        this.drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        this.edit_id = findViewById(R.id.id);
        this.edit_case_id = findViewById(R.id.caseid);
        this.edit_date = findViewById(R.id.date);
        this.edit_tester = findViewById(R.id.tester);
        this.edit_doctor = findViewById(R.id.doctor);
        this.edit_name = findViewById(R.id.name);
        Spinner spinner_gender = findViewById(R.id.gender);
        this.edit_birthday = findViewById(R.id.birthday);
        this.edit_age = findViewById(R.id.age);
        this.edit_school = findViewById(R.id.school);
        this.edit_birthday = findViewById(R.id.birthday);
        this.edit_parent_name = findViewById(R.id.parent_name);
        this.edit_contact_number = findViewById(R.id.contact_number);
        Button btn_store = findViewById(R.id.btn_store);

        Toolbar toolbar = findViewById(R.id.toolbar);
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
                    intent.setClass(MainActivity.this, LabelActivity.class);

                    startActivity(intent);
                    MainActivity.this.finish();
                    return true;
                } else if (id == R.id.upload) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, UploaderActivity.class);

                    startActivity(intent);
                    MainActivity.this.finish();
                    return true;
                }
                return false;
            }
        });

//
//        //設定測試日期文字選單點擊時跳出時間介面
//        edit_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Calendar c = Calendar.getInstance();
//                c.setTimeInMillis(System.currentTimeMillis());
//                final int myear = c.get(Calendar.YEAR);
//                final int mmonth = c.get(Calendar.MONTH);
//                int mday = c.get(Calendar.DAY_OF_MONTH);
//
//                new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int day) {
//                        //定義時間格式
//                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd");
//                        Date chooseDate = c.getTime();
//                        try {
//                            chooseDate = sdf.parse(year + " / " + (month + 1) + " / " + day);
//                        } catch (ParseException o) {
//                            Log.v("MainActivity", "Date parse fail");
//                        }
//                        String format = sdf.format(chooseDate);
//
//                        edit_date.setText(format);
//                    }
//
//                }, myear, mmonth, mday).show();
//            }
//        });
//
//        edit_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFoucus) {
//                if (hasFoucus) {
//                    final Calendar c = Calendar.getInstance();
//                    c.setTimeInMillis(System.currentTimeMillis());
//                    final int myear = c.get(Calendar.YEAR);
//                    int mmonth = c.get(Calendar.MONTH);
//                    int mday = c.get(Calendar.DAY_OF_MONTH);
//
//                    new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int month, int day) {
//                            //定義時間格式
//                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd");
//                            Date chooseDate = c.getTime();
//                            try {
//                                chooseDate = sdf.parse(year + " / " + (month + 1) + " / " + day);
//                            } catch (ParseException o) {
//                                Log.v("MainActivity", "Date parse fail");
//                            }
//                            String format = sdf.format(chooseDate);
//                            edit_date.setText(format);
//                        }
//
//                    }, myear, mmonth, mday).show();
//                }
//            }
//        });
//

        //設定性別表單
        gender_list.addAll(Arrays.asList(gender_array));
        gender_adapter = new ChooseArrayAdapter<>(this, R.layout.list, gender_list);
        gender_adapter.setDropDownViewResource(R.layout.list);
//        spinner_gender.setAdapter(gender_adapter);
//        spinner_gender.setSelection(gender_array.length - 1);
//
//        //性別選單切換時的動作
//        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                //取得目前清單
//                String lang = ((TextView) view).getText().toString();
//                int color = getResources().getColor(R.color.colorSlateGray);
//                ((TextView) view).setTextColor(color);
//                string_gender = lang;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        //設定生日文字選單點擊時跳出時間介面
//        edit_birthday.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View v) {
//                final Calendar c = Calendar.getInstance();
//                c.setTimeInMillis(System.currentTimeMillis());
//                final int myear = c.get(Calendar.YEAR);
//                int mmonth = c.get(Calendar.MONTH);
//                int mday = c.get(Calendar.DAY_OF_MONTH);
//
//                new CalendarDialog(MainActivity.this, new DatePicker.OnDateChangedListener() {
//                    @Override
//                    public void onDateChanged(DatePicker view, int year, int month, int day) {
//                        year = view.getYear();
//                        month = view.getMonth();
//                        day = view.getDayOfMonth();
//                        //定義時間格式
//                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd");
//                        Date chooseDate = c.getTime();
//                        try {
//                            chooseDate = sdf.parse(year + " / " + (month + 1) + " / " + day);
//                        } catch (ParseException o) {
//                            Log.v("MainActivity", "Date parse fail");
//                        }
//                        String format = sdf.format(chooseDate);
//                        edit_birthday.setText(format);
//                        Log.v("MainActivity", "year = " + year);
//                        Log.v("MainActivity", "month = " + month);
//                        Log.v("MainActivity", "day = " + day);
//
//                        Date date_birthday = chooseDate;
//
//                        //自動計算出年齡
//                        int age = 0;
//                        try {
//                            age = getAge(date_birthday);
//                        } catch (Exception e) {
//                            Log.e("MainActivity", "Birthday is before now");
//                        }
//                        edit_age.setText(String.valueOf(age));
//                    }
//                }).show();
//            }
//        });
//
//        edit_birthday.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    final Calendar c = Calendar.getInstance();
//                    c.setTimeInMillis(System.currentTimeMillis());
//                    final int myear = c.get(Calendar.YEAR);
//                    int mmonth = c.get(Calendar.MONTH);
//                    int mday = c.get(Calendar.DAY_OF_MONTH);
//
//                    new CalendarDialog(MainActivity.this, new DatePicker.OnDateChangedListener() {
//                        @Override
//                        public void onDateChanged(DatePicker view, int year, int month, int day) {
//                            year = view.getYear();
//                            month = view.getMonth();
//                            day = view.getDayOfMonth();
//
//                            //定義時間格式
//                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd");
//                            Date chooseDate = c.getTime();
//                            try {
//                                chooseDate = sdf.parse(year + " / " + (month + 1) + " / " + day);
//                            } catch (ParseException o) {
//                                Log.v("MainActivity", "Date parse fail");
//                            }
//                            String format = sdf.format(chooseDate);
//                            edit_birthday.setText(format);
//                            Log.v("MainActivity", "year = " + year);
//                            Log.v("MainActivity", "month = " + month);
//                            Log.v("MainActivity", "day = " + day);
//                            Date date_birthday = chooseDate;
//
//                            //自動計算出年齡
//                            int age = 0;
//                            try {
//                                age = getAge(date_birthday);
//                            } catch (Exception e) {
//                                Log.e("MainActivity", "Birthday is before now");
//                            }
//                            edit_age.setText(String.valueOf(age));
//                        }
//                    }).show();
//                }
//            }
//        });


        btn_store.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                string_id = "";
                string_case_id = "";
                string_date = "";
                string_tester = "";
                string_doctor = "";
                string_name = "";
                string_birthday = "";
                string_age = "";
                string_school = "";
                string_parent_name = "";
                string_contact_number = "";


                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WordCardActivity.class);

                //新增一個 bundle物件，將要傳送的資料傳入
                ClinetInfo info = new ClinetInfo();
                info.SetId(string_id);
                info.SetCaseId(string_case_id);
                info.SetDate(string_date);
                info.SetTester(string_tester);
                info.SetDoctor(string_doctor);
                info.SetName(string_name);
                info.SetGender(string_gender);
                info.SetBirthday(string_birthday);
                info.SetAge(string_age);
                info.SetSchool(string_school);
                info.SetParentName(string_parent_name);
                info.SetContactNumber(string_contact_number);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", info);

                //將Bundle物件傳給intent
                intent.putExtras(bundle);

                startActivity(intent);
                MainActivity.this.finish();


            }
        });

        request_permission();

    }

    public void request_permission() {
        List<String> permissionList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkSelfPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            checkSelfPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (checkSelfPermission == PackageManager.PERMISSION_DENIED) {
                permissionList.add(Manifest.permission.RECORD_AUDIO);
            }

            if (!permissionList.isEmpty()) {
                requestPermissions(permissionList.toArray(new String[0]), REQUESTCODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUESTCODE) {
//            //询问用户权限
//            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0]
//                    == PackageManager.PERMISSION_GRANTED) {
//            } else {
//            }
//        }
//        else if(requestCode == AUDIOREQUESTCODE){
//            if (permissions[0].equals(Manifest.permission.RECORD_AUDIO) && grantResults[0]
//                    == PackageManager.PERMISSION_GRANTED) {
//            } else {
//            }
//        }
        if (requestCode == REQUESTCODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    // PERMISSION_DENIED 这个值代表是没有授权，我们可以把被拒绝授权的权限显示出来
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(MainActivity.this, permissions[i] + "权限被拒绝了", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    //日期顯示格式
    public String setDataFormat(int year, int monthOfYear, int dayOfMonth) {
        return year + " / " + monthOfYear + " / " + dayOfMonth;
    }

    //計算年齡函式
    public int getAge(Date birthday) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        if (cal.before(birthday)) {
            throw new IllegalAccessException("birthday is before Now");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthday);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (dayOfMonthNow < dayOfMonthBirth)
                age--;
        }
        return age;
    }

    //設定日期選單選單對話框
    public class CalendarDialog extends AlertDialog implements OnClickListener, DatePicker.OnDateChangedListener {
//    public class CalendarDialog extends AlertDialog implements OnClickListener{

        private Context mContext;
        private DatePicker mDatePicker;
        private DatePicker.OnDateChangedListener mCallback;
        private int mYear;
        private int mMonth;
        private int mDay;


        //        @RequiresApi(api = Build.VERSION_CODES.O)
        CalendarDialog(Context context, DatePicker.OnDateChangedListener callback) {
            super(context, 0);

            mCallback = callback;
            setIcon(0);
            setButton(BUTTON_POSITIVE, "確認", this);
            setButton(BUTTON_NEGATIVE, "取消", this);
            Context themContext = getContext();
            Calendar c = Calendar.getInstance();

            LayoutInflater inflater = (LayoutInflater) themContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.calendar, null);
            setView(view);

            mDatePicker = view.findViewById(R.id.calender);
//            mDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
//                @Override
//                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                    Log.v("MainActivity function","year =" +year);
//                    mDatePicker = view;
//                    mYear = year;
//                    mMonth = monthOfYear;
//                    mDay = dayOfMonth;
//                }
//            });

            mDatePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Log.v("MainActivity function", "year =" + year);
                    mDatePicker = view;
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                }
            });
        }

        @Override
        public void onDateChanged(DatePicker picker, int year, int month, int day) {
            Log.v("MainActivity function", "year =" + year);
            this.mDatePicker = picker;
            this.mYear = year;
            this.mMonth = month;
            this.mDay = day;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                    mCallback.onDateChanged(mDatePicker, mYear, mMonth, mDay);
                    dismiss();
                    break;
                case BUTTON_NEGATIVE:
                    dismiss();
                    break;
            }
        }
    }

    //具有提示的清單容器
    public class ChooseArrayAdapter<T> extends ArrayAdapter {
        //建構方法
        ChooseArrayAdapter(Context context, int resource, List<T> objects) {
            super(context, resource, objects);
        }

        //將List最後一項用來顯示hint
        @Override
        public int getCount() {
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }
    }

}