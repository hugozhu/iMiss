package info.hugozhu.imiss;

import info.hugozhu.imiss.ui.ApplicationLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by hugozhu on 6/16/14.
 */
public class LogMessages {
    private static LogMessages ourInstance = new LogMessages();
    private IMissObserver handler;
    SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");


    public static LogMessages getInstance() {
        return ourInstance;
    }

    private LogMessages() {
        messages = new ArrayList<String>();
    }

    private ArrayList<String> messages = null;


    public void register(IMissObserver notification) {
        this.handler = notification;
    }

    public void unregister() {
        this.handler = null;
    }

    public synchronized void add(String s) {
        messages.add(format.format(new Date())+": "+s);
        handler.onMissing();
    }

    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}