package com.dev.bins.note.service;

import android.app.IntentService;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.WindowManager;

import com.dev.bins.note.model.Category;
import com.dev.bins.note.model.Note;

import org.litepal.crud.DataSupport;

import java.util.Date;


public class CopyService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {

    private WindowManager windowManager;
    private ClipboardManager clipboardManager;

    private boolean viewIsAdd = false;



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onPrimaryClipChanged() {
        if (clipboardManager.hasPrimaryClip()) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            String data = clipData.getItemAt(0).getText().toString();
            if (TextUtils.isEmpty(data))
                return;
            Note note = new Note();
            note.setTitle("无标题");
            note.setContent(data);
            note.setDate(new Date());
            note.setShow(true);
            note.save();

            Category category = DataSupport.find(Category.class, Category.CLIPBOARD);
            category.getNotes().add(note);
            category.save();
        }
    }

    @Override
    public void onDestroy() {
        clipboardManager.removePrimaryClipChangedListener(this);
        super.onDestroy();
    }
}