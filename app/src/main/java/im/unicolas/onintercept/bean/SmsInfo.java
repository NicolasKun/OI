package im.unicolas.onintercept.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qq923 on 2016-12-11.
 */

public class SmsInfo implements Parcelable {
    /**
     * 短信内容
     */
    private String smsbody;
    /**
     * 发送短信的电话号码
     */
    private String phoneNumber;
    /**
     * 发送短信的日期和时间
     */
    private String date;
    /**
     * 发送短信人的姓名
     */
    private String name;
    /**
     * 短信类型1是接收到的，2是已发出
     */
    private String type;

    public SmsInfo() {
    }

    protected SmsInfo(Parcel in) {
        smsbody = in.readString();
        phoneNumber = in.readString();
        date = in.readString();
        name = in.readString();
        type = in.readString();
    }

    public static final Creator<SmsInfo> CREATOR = new Creator<SmsInfo>() {
        @Override
        public SmsInfo createFromParcel(Parcel in) {
            return new SmsInfo(in);
        }

        @Override
        public SmsInfo[] newArray(int size) {
            return new SmsInfo[size];
        }
    };

    public String getSmsbody() {
        return smsbody;
    }

    public void setSmsbody(String smsbody) {
        this.smsbody = smsbody;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(smsbody);
        dest.writeString(phoneNumber);
        dest.writeString(date);
        dest.writeString(name);
        dest.writeString(type);
    }
}
