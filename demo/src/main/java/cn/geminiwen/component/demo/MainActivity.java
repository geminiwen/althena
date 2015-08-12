package cn.geminiwen.component.demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.geminiwen.component.althena.Althena;
import cn.geminiwen.component.althena.task.Task;

public class MainActivity extends AppCompatActivity implements Althena.OnDownloadStateUpdateListener{

    private static final String TAG = "Althena";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File dstFile = new File(fileDir, "test.png");
        Task task = new Task("http://ww1.sinaimg.cn/large/7a8aed7bgw1euzko6672oj20go0oz771.jpg", dstFile, System.currentTimeMillis());
        task.setOnStateUpdateListener(this);
        Althena.start(task);
    }

    @Override
    public void onStart(Task task) {
        Log.d(TAG, "start");
    }

    @Override
    public void onInfo(Task task, long contentLength) {
        Log.d(TAG, "info, length = " + contentLength );
    }

    @Override
    public void onPause(Task task) {
        Log.d(TAG, "paused");
    }

    @Override
    public void onStop(Task task) {
        Log.d(TAG, "stop");
    }

    @Override
    public void onRestart(Task task) {
        Log.d(TAG, "restart");
    }

    @Override
    public void onProgressUpdate(Task task, long downloadedBytes, long contentLength) {
        Log.d(TAG, "progress update = " + downloadedBytes);
    }

    @Override
    public void onError(Task task, IOException e) {
        Log.d(TAG, "error", e);
    }

    @Override
    public void onComplete(Task task) {
        Log.d(TAG, "complete");
    }
}
