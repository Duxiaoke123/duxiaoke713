package com.example.x.lianxi712;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;

public class MainActivity extends AppCompatActivity {
    private String loadUrl = "http://gdown.baidu.com/data/wisegame/d2fbbc8e64990454/wangyiyunyinle_87.apk";
    private String filePath = Environment.getExternalStorageDirectory()+"/"+"网易云音乐.apk";
    private DownLoadFile downLoadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImmersionBar.with(this).transparentBar().statusBarDarkFont(true).init();
        final TextView tvprogress = (TextView) findViewById(R.id.progressTv);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        downLoadFile = new DownLoadFile(this, loadUrl, filePath, 3);
        downLoadFile.setOnDownLoadListener(new DownLoadFile.DownLoadListener(){
            @Override
            public void getProgress(int progress) {
                tvprogress.setText("当前进度："+progress+"%");
                progressBar.setProgress(progress);
            }

            @Override
            public void onComplete() {
                Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
            }
        });
            findViewById(R.id.progressTv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downLoadFile.downLoad();
                }
            });
            findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downLoadFile.onPause();
                }
            });
            findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downLoadFile.onStart();
                }
            });

    }

    @Override
    protected void onDestroy() {
        System.out.println("ondestroy");
        downLoadFile.cancle();
        downLoadFile.onDestroy();
        super.onDestroy();
    }
}
