package com.cgh.org.audio.Interface;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import android.os.AsyncTask;

import android.app.ProgressDialog;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;

import android.content.Intent;


import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import com.cgh.org.audio.File.MagicFileChooser;
import com.example.patie.pcv1.R;
import com.cgh.org.audio.Config.Setup;
import com.cgh.org.audio.File.FileManager;
import com.cgh.org.audio.Zip.ZipUtils;
import com.cgh.org.audio.Http.MyHttpEntity;
import com.cgh.org.audio.Http.SSLSocketFactoryImp;

import pub.devrel.easypermissions.EasyPermissions;

public class UploaderActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "http://140.118.115.17:81";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private LinkedList<File> list_compress_file;
    //Button fileBrowseBtn;
    Button uploadBtn;
    Button compressBtn;
    ImageView previewImage;
    TextView fileName;
    TextView barProgress;
    MagicFileChooser chooserX;
    ProgressBar progressBar;
    //Uri fileUri;
    //private File file;
    private static  int flag_internet = 0;
    private Handler mHandler;

    String fileType = "*/*";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploader);

        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        barProgress = findViewById(R.id.barPercentage);
        progressBar = findViewById(R.id.myProgressBar);

        uploadBtn = findViewById(R.id.myButton);
        compressBtn = findViewById(R.id.myButton2);

        fileName = findViewById(R.id.myTextView);

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
                    intent.setClass(UploaderActivity.this,MainActivity.class);

                    startActivity(intent);
                    UploaderActivity.this.finish();
                }
                else if(id == R.id.label){
                    Intent intent = new Intent();
                    intent.setClass(UploaderActivity.this,LabelActivity.class);

                    startActivity(intent);
                    UploaderActivity.this.finish();
                    return true;
                }
                else if (id == R.id.upload) {
                    return true;
                }
                return false;
            }
        });

        chooserX = new MagicFileChooser(this);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooserX.showFileChooser("application/zip", null, true);

                //check if app has permission to access the external storage.
//                if (EasyPermissions.hasPermissions(UploaderActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    chooserX.showFileChooser("application/zip", null, true);
//
//                } else {
//                    //If permission is not present request for the same.
//                    EasyPermissions.requestPermissions(UploaderActivity.this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
//                }
            }
        });

        compressBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){

                compressZIP();

            }
        });
    }

    private void deletePath(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            deletePath(files[i]);
        }
        file.delete();

    }

    private void deleteFilesnPath(File file) {
        String parseString = file.getName();


        String patient = parseString.split("_")[0];
        String data = (parseString.split("_")[1]).split("\\.")[0];



        Log.v(TAG, "patient is = " + patient);
        Log.v(TAG, "data is = " + data);

        String labeledPath = Setup.DEF_AUDIO_PATH + parseString.split("\\.")[0];
        String unlabeledPath = Setup.DEF_AUDIO_PATH_UN + data + "/" + patient;
        String unlabeledPathUpper = Setup.DEF_AUDIO_PATH_UN + data;


        File labeledFile = new File(labeledPath);
        File unlabeledFile = new File(unlabeledPath);
        File unlabeledFileUpper = new File(unlabeledPathUpper);

        if (file.exists()) {
            //Log.v(TAG, "uploaded name is = " + file.getName());
            //Log.v(TAG, "uploaded path is = " + file.getPath());

            if (labeledFile.isDirectory()) {
                /* Delete "Labeled directory!!" */
                Log.v(TAG, "Labeled path exist!!!!!");
                //labeledFile.delete();
                deletePath(labeledFile);
            }

            if (unlabeledFile.isDirectory()) {
                /* Delete "Unlabeled directory!!" */
                Log.v(TAG, "Unlabeled path exist!!!!!");
                //unlabeledFile.delete();
                deletePath(unlabeledFile);

                if (unlabeledFileUpper.isDirectory() && unlabeledFileUpper.list().length == 0) {
                    /* Delete "Unlabeled date directory!!" */
                    Log.v(TAG, "Unlabeled patient path is empty!!!");
                    unlabeledFileUpper.delete();
                }


            }

        }
        /* Delete zip file!!!!! */
        file.delete();
    }

    private void scanZIPnDelete(String path) {
        FileManager fileManagerX = new FileManager();
        String[] zipFiles = fileManagerX.listSpecFile(path, ".zip");

        if (zipFiles == null) {
            Log.v(TAG, " There are no zip files in this path!!");

            return;
        }

        for (String zipfiles : zipFiles) {
            String fullZipPath = path + zipfiles;

            if (new File(fullZipPath).exists()) {
                //Log.v(TAG, "Exist!!!!!");
                new File(fullZipPath).delete();
            }
            //else {
            //    Log.v(TAG, fullZipPath + " doesn't exist!!!!!");
            //}
        }
        fileName.setText("更新完畢 !!");
    }

    private void compressZIP() {
        scanZIPnDelete("Medical_Project/Labeled/");

        if (new File(Setup.DEF_AUDIO_PATH).list() != null) {

            list_compress_file = new LinkedList<>();
            File dir_audio = new File(Setup.DEF_AUDIO_PATH);
            File[] folder_audio = dir_audio.listFiles();
            for (int i = 0; i < folder_audio.length; i++) {
                if (folder_audio[i].isDirectory()) {
                    list_compress_file.add(folder_audio[i]);
                    try {
                        ZipUtils.ZipFolder(folder_audio[i].toString(), Setup.DEF_AUDIO_PATH + folder_audio[i].getName() + ".zip");
                        String onProgress = folder_audio[i].getName() + ".zip" + " 已壓縮";
                        fileName.setText(onProgress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //Toast.makeText(MainActivity.this,"壓縮完成",Toast.LENGTH_SHORT).show();
            fileName.setText("所有檔案已壓縮成功 !!");
        }
        else {
            fileName.setText("請先進行錄音程序 !!");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        chooserX.onActivityResult(requestCode, resultCode, data);
        File[] fileX = chooserX.getChosenFiles();

        Log.v(TAG, " lenghth of File = " + fileX.length);

        if (fileX.length == 0) {
            fileName.setText("請選擇檔案 !!");
        }

        for (File file : fileX) {


            if (file != null) {
                //UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(MainActivity.this, file);
                //uploadAsyncTask.execute();

                HttpsPostThread thread = new HttpsPostThread(mHandler,"https://ib115-17.et.ntust.edu.tw/test?password=lps703703", file, 200);
                thread.execute();

                initData();



            } else {
                Toast.makeText(getApplicationContext(),
                        "Please select a file first", Toast.LENGTH_LONG).show();

            }
        }
    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String result = (String) msg.obj;
                switch (msg.what) {
                    case 200:
                        // 請求成功
                        Log.e("TAG", "返回參數===" + result);
                        break;
                    case 404:
                        // 請求失敗
                        Log.e("TAG", "請求失敗!");
                        break;
                }

            }
        };
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, UploaderActivity.this);
    }




    /**
     * Background network task to handle file upload.
     */
    private class HttpsPostThread extends AsyncTask<Void, Integer, String> {


        //HttpClient httpClient = new DefaultHttpClient();
        //private Context context;
        private Exception exception;
        private ProgressDialog progressDialog;
        //private  File file;
        /*
        private UploadAsyncTask(Context context, File file) {
            this.context = context;
            this.file = file;
        }
        */

        private Handler handler;
        private String httpUrl;

        private File file;
        private int mWhat;

        public static final int ERROR = 404;
        public static final int SUCCESS = 200;

        public HttpsPostThread(Handler handler, String httpUrl,
                               File file, int what) {
            super();
            this.handler = handler;
            this.httpUrl = httpUrl;
            this.file = file;
            this.mWhat = what;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = null;
            try {
                HttpParams httpParameters = new BasicHttpParams();
                // 設置連接管理器的超時
                ConnManagerParams.setTimeout(httpParameters, 10000);
                // 設置連接超時
                HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
                // 設置socket超時
                HttpConnectionParams.setSoTimeout(httpParameters, 10000);
                HttpClient hc = getHttpClient(httpParameters);

                HttpPost post = new HttpPost(httpUrl);
                MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                multipartEntityBuilder.addPart("file", new FileBody(file));

                // Progress listener - updates task's progress


                MyHttpEntity.ProgressListener progressListener =


                        new MyHttpEntity.ProgressListener() {
                            @Override
                            public void transferred(float progress) {
                                publishProgress((int) progress);

                            }
                        };
                //post.setEntity(new UrlEncodedFormEntity(valueList, HTTP.UTF_8));

                post.setEntity(new MyHttpEntity(multipartEntityBuilder.build(),
                        progressListener));

                post.setParams(httpParameters);
                HttpResponse response = null;
                try {
                    response = hc.execute(post);
                } catch (UnknownHostException e) {
                    throw new Exception("Unable to access "
                            + e.getLocalizedMessage());
                } catch (SocketException e) {
                    throw new Exception(e.getLocalizedMessage());
                }
                int sCode = response.getStatusLine().getStatusCode();
                if (sCode == HttpStatus.SC_OK) {
                    result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
                    if (handler != null) {
                        flag_internet = 1;
                        handler.sendMessage(Message.obtain(handler, mWhat, result)); // 請求成功
                    }
                } else {
                    result = "請求失敗" + sCode; // 請求失敗
                    flag_internet = 0;
                    // 404 - 未找到
                    if (handler != null) {

                        handler.sendMessage(Message.obtain(handler, ERROR, result));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (handler != null) {
                    flag_internet = 0;
                    result = "請求失敗,異常退出";
                    handler.sendMessage(Message.obtain(handler, ERROR, result));
                }
            }
            return result;
        }

        public  HttpClient getHttpClient(HttpParams params) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore
                        .getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
                HttpProtocolParams.setUseExpectContinue(params, true);

                // 設置http https支持
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory
                        .getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 443));// SSL/TSL的認證過程，端口為443
                ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                        params, registry);
                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                return new DefaultHttpClient(params);
            }
        }

        @Override
        protected void onPreExecute() {

            // Init and show dialog
            progressBar.setProgress(0);
            /*
            this.progressDialog = new ProgressDialog(MainActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
            */
        }

        @Override
        protected void onPostExecute(String result) {

            // Close dialog
            //this.progressDialog.dismiss();


            if (result == null || flag_internet == 0) {
                Log.v(TAG, "fail = " + result);
                Log.v(TAG, "fail flag = " + flag_internet);
                barProgress.setVisibility(View.INVISIBLE);
                progressBar.setProgress(100);

                String onProgress = "上傳失敗，請連線網路 !!";
                fileName.setText(onProgress);


            }
            else {
                Log.v(TAG, "succ = " + result);
                Log.v(TAG, "succ flag = " + flag_internet);
                barProgress.setVisibility(View.INVISIBLE);
                progressBar.setProgress(100);
                String onProgress = "上傳成功 !!";
                fileName.setText(onProgress);

                deleteFilesnPath(file);
            }

            //Toast.makeText(getApplicationContext(),
            //       result, Toast.LENGTH_LONG).show();
            //showFileChooser();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);

            String percentage = progress[0] + "%";
            barProgress.setText(percentage);

            String onProgress = "正在上傳 " + file.getName();
            fileName.setText(onProgress);

            //this.progressDialog.setProgress((int) progress[0]);
        }
    }
}
