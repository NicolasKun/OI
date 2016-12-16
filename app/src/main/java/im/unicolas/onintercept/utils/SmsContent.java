package im.unicolas.onintercept.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import im.unicolas.onintercept.bean.SmsInfo;

/**
 * Created by LeeQ on 2016-12-16.
 */
public class SmsContent{
    private Context activity;//这里有个activity对象，不知道为啥以前好像不要，现在就要了。自己试试吧。
    private Uri uri;
    List<SmsInfo> infos;

    public SmsContent(Context activity, Uri uri) {
        infos = new ArrayList<SmsInfo>();
        this.activity = activity;
        this.uri = uri;
    }

    /**
     * Role:获取短信的各种信息 <BR>
     */
    public List<SmsInfo> getSmsInfo() {
        String[] projection = new String[] { "_id", "address", "person",
                "body", "date", "type" };
        Cursor cusor = activity.getContentResolver().query(uri, projection, null, null,
                "date desc");
        int nameColumn = cusor.getColumnIndex("person");
        int phoneNumberColumn = cusor.getColumnIndex("address");
        int smsbodyColumn = cusor.getColumnIndex("body");
        int dateColumn = cusor.getColumnIndex("date");
        int typeColumn = cusor.getColumnIndex("type");
        if (cusor != null) {
            while (cusor.moveToNext()) {
                SmsInfo smsinfo = new SmsInfo();
                smsinfo.setName(cusor.getString(nameColumn));
                smsinfo.setDate(cusor.getString(dateColumn));
                smsinfo.setPhoneNumber(cusor.getString(phoneNumberColumn));
                smsinfo.setSmsbody(cusor.getString(smsbodyColumn));
                smsinfo.setType(cusor.getString(typeColumn));
                infos.add(smsinfo);
            }
            cusor.close();
        }
        return infos;
    }
}
