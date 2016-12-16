package im.unicolas.onintercept.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by qq923 on 2016-12-07.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    public void loadDataByOkgo(final String reqTag, String url, HashMap<String, String> params,
                               final Handler handler, final int what) {
        OkGo.post(url)
                .tag(reqTag)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e(TAG, reqTag+"\nonSuccess: 请求成功 " + s);
                        if (response.code() == 200) {
                            if (s != null && !s.equals("")) {
                                Message msg = handler.obtainMessage();
                                msg.what = what;
                                msg.obj = s;
                                handler.sendMessage(msg);
                            }
                        } else {
                            Log.e(TAG, "onSuccess: 请求数据错误 " + s);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e(TAG, reqTag+"\nonError: " + e.getMessage());
                    }
                });
    }
}
