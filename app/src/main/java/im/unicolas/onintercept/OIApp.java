package im.unicolas.onintercept;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.lzy.okgo.OkGo;

import java.io.File;
import java.io.IOException;

import im.unicolas.onintercept.asynctask.system_manager;

/**
 * Created by qq923 on 2016-12-07.
 */

public class OIApp extends Application {
    private static OIApp app;
    private SharedPreferences sp;
    private File file;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        OkGo.init(this);
        //createNewLogFile();
        startService(new Intent(this, system_manager.class));
        sp = getSharedPreferences("oi", 1);
    }

    private void createNewLogFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Download/", "oiLogs.txt");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public File getLogsFile() {
        return file;
    }

    public static OIApp getInstance() {
        return app;
    }

    public SharedPreferences getSp() {
        return sp;
    }
}
