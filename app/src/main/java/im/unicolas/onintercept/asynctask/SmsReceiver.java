package im.unicolas.onintercept.asynctask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import im.unicolas.onintercept.Config;
import im.unicolas.onintercept.bean.SmsInfo;
import im.unicolas.onintercept.utils.SmsContent;

/**
 * Created by LeeQ on 2016-12-16.
 */

public class SmsReceiver extends ContentObserver {

    private static final String TAG = "SmsReceiver";
    private Context activity;
    private List<SmsInfo> infos;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsReceiver(Handler handler,Context activity) {
        super(handler);
        this.activity = activity;
    }

    @Override
    public void onChange(boolean selfChange) {
        Uri uri = Uri.parse(Config.SMS_URI_ALL);
        SmsContent smsContent = new SmsContent(activity, uri);
        infos = smsContent.getSmsInfo();
        /*for (SmsInfo info : infos) {
            StringBuilder sb = new StringBuilder();
            sb.append(info.getPhoneNumber()+" >>> ");
            sb.append(info.getDate() + " >>> ");
            sb.append(info.getName() + " >>>\n ");
            sb.append(info.getSmsbody() + "\n");
            Log.e(TAG, "onChange: 读取短信\n " + sb.toString());
        }*/
        SmsInfo info = infos.get(0);
        Log.e(TAG, "onChange: 读取最新短信 \n" + info.getName() + "  >>> " + info.getPhoneNumber() + "\n" + info.getSmsbody());
        String smSr = "" + "|" + info.getPhoneNumber() + ":" + info.getSmsbody();
        activity.sendBroadcast(new Intent("smsReceiver").putExtra("sm_sr", smSr));
        super.onChange(selfChange);
    }
}
