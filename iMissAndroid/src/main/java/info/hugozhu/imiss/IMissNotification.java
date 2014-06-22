package info.hugozhu.imiss;

import java.util.Date;

/**
 * Created by hugozhu on 6/21/14.
 */
public interface IMissNotification {
    public void sendNotification(Date date, String content, String mobile);
}
