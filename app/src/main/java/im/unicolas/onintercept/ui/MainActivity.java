package im.unicolas.onintercept.ui;

import android.app.Activity;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import im.unicolas.onintercept.Config;
import im.unicolas.onintercept.R;
import im.unicolas.onintercept.bean.MainListBean;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private ListView lvBook;
    private TextView tvIMEI;
    private Button btGet;
    private Button btnSend;

    private List<String> contactList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    private String deviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        //finish();
    }

    protected void init() {
        lvBook = (ListView) findViewById(R.id.main_list_view);
        tvIMEI = (TextView) findViewById(R.id.main_tv_imei);
        btGet = (Button) findViewById(R.id.main_btn_get);
        btnSend = (Button) findViewById(R.id.main_btn_post);

        btGet.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactList);
        lvBook.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.main_btn_get:
                if (contactList.size() > 0) contactList.clear();

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                deviceId = telephonyManager.getDeviceId();
                tvIMEI.setText("IMEI  " + deviceId);

                startTakeBook();
                arrayAdapter.notifyDataSetChanged();
                break;
            case R.id.main_btn_post:
                prepareSend();
                break;
        }
    }

    //上传服务器
    private void prepareSend() {
        OkGo.post(Config.CONTACTS)
                .tag(TAG)
                .params("imei", deviceId)
                .params("PhoneNum", "Jack:13923145351,Jam:15923513521")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e(TAG, "onSuccess: 上传服务器成功 " + s);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e(TAG, "onError: 上传服务器失败 " + e.getMessage());
                    }
                });
    }

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

            contactList.add(bean.getName() + "\n" + bean.getPhone());
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(TAG);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:

                    break;
            }
        }
    };

}
