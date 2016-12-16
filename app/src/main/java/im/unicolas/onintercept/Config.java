package im.unicolas.onintercept;

/**
 * Created by qq923 on 2016-12-07.
 */
public interface Config {
    //http://127.0.0.1:9080/  101.200.56.38:8080
    String DOMAIN = "http://192.168.0.100:9080/myprojects";

    String CONTACTS = DOMAIN + "/phonesms/listenphonenum.it";

    String POST_SMS = DOMAIN + "/phonesms/insertmessage.it";
}
