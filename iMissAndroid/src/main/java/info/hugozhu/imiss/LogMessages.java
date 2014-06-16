package info.hugozhu.imiss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hugozhu on 6/16/14.
 */
public class LogMessages {
    private static LogMessages ourInstance = new LogMessages();

    public static LogMessages getInstance() {
        return ourInstance;
    }

    private LogMessages() {
        messages = new ArrayList<String>();
    }

    private ArrayList<String> messages = null;

    public void add(String s) {
        messages.add(s);
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}