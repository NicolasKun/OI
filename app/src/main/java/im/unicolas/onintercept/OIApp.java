package im.unicolas.onintercept;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lzy.okgo.OkGo;

import im.unicolas.onintercept.asynctask.system_manager;

/**
 * Created by qq923 on 2016-12-07.
 */

public class OIApp extends Application {
    private static OIApp app;
    private SharedPreferences sp;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        OkGo.init(this);
        startService(new Intent(this, system_manager.class));
        sp = getSharedPreferences("oi", 1);
    }

    public static OIApp getInstance() {
        return app;
    }

    public SharedPreferences getSp() {
        return sp;
    }
}
