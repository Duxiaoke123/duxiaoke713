package com.example.x.lianxi712;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.webkit.DownloadListener;

import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;



/**
 * @Author：x
 * @E-mail：
 * @Date：2019/7/12 19:42
 * @Description：描述信息
 */
public class DownLoadFile {
    private static final String SP_NAME = "download_file";
    private static final String CURR_LENGTH = "curr_length";
    private static final int DEFAULT_THREAD_COUNT = 4;//默认下载线程数
    //以下为线程状态
    private static final String DOWNLOAD_INIT = "1";
    private static final String DOWNLOAD_ING = "2";
    private static final String DOWNLOAD_PAUSE = "3";

    private Context mContext;

    private String loadUrl;//网络获取的url
    private String filePath;//下载到本地的path
    private int threadCount = DEFAULT_THREAD_COUNT;//下载线程数

    private int fileLength;//文件总大小
    //使用volatile防止多线程不安全
    private volatile int runningThreadCount;//正在运行的线程数
    private Thread[] mThreads;

    private DownloadListener mDownLoadListener;
    private String stareDownload = DOWNLOAD_ING;

    public DownLoadFile(MainActivity mainActivity, String loadUrl, String filePath, int i) {
        this(mainActivity,loadUrl,filePath,DEFAULT_THREAD_COUNT,null);
    }


    public void  setOnDownLoadListener(DownLoadListener mDowLoadListener){
        this.mDownLoadListener= (DownloadListener) mDowLoadListener;

    }


    interface DownLoadListener{
        void  getProgress(int progress);
        void  onComplete();
        void  onFailure();
    }
    public  DownLoadFile (Context mContext ,String loadUrl,String filePath,int threadCount,DownloadListener mDownLoadListener){
        this.mContext = mContext;
        this.loadUrl = loadUrl;
        this.threadCount = threadCount;
        this.mDownLoadListener = mDownLoadListener;
        runningThreadCount = 0;
    }
    protected  void downLoad(){
        new Thread(new Runnable() {

            private int currLength;
            private int fileLength;

            @Override
            public void run() {
                try {
                    if(mThreads==null){
                        mThreads = new Thread[threadCount];
                        URL url = new URL(loadUrl);
                       URLConnection con = url.openConnection();
                        con.setConnectTimeout(5000);
                        con.setRequestMethod("GET");
                        int code = con.getResponseCode();
                        if(code == 200){
                            fileLength = con.getContentLength();
                            RandomAccessFile raf = new RandomAccessFile(filePath,"rwd");
                            raf.setLength(fileLength);
                            raf.close();
                            int blockLength = fileLength / threadCount;
                            SharedPreferences sp = mContext.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
                            currLength = sp.getInt(CURR_LENGTH, 0);
                            for(int i = 0;i<threadCount;i++){
                                int startPosition = sp.getInt(SP_NAME+(i+1),i*blockLength);
                                int endPosition = (i+1)*blockLength-1;
                                if ((i+1)==threadCount){

                                    endPosition = fileLength-1;
                                    mThreads[i] = new DownThread(i+1,startPosition,endPosition);
                                    mThreads[i].start();
                                }
                            }
                        }else {


                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();


                }
            }
        }).start();

    }

    public void cancle() {
        if(mThreads !=null){
        if(stareDownload.equals(DOWNLOAD_PAUSE)){
            onStart();
        }
        for(Thread dt : mThreads){
            ((DownThread) dt).cancel();
            
        }
    }
}
    public void onPause() {
        if(mThreads!=null){
            stareDownload = DOWNLOAD_PAUSE;
        }
    }

    public void onStart() {
        if(mThreads!=null){
            synchronized (DOWNLOAD_PAUSE){

                stateDownload = DOWNLOAD_ING;
                DOWNLOAD_PAUSE.notifyAll();
            }

        }
    }



    public void onDestroy() {
        if(mThreads!=null){
            mThreads = null;

        }
        
    }

    private final  int SUCCESS = 0x00000101;
    private final  int FAILURE = 0x00000102;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {


        }
    };


}
