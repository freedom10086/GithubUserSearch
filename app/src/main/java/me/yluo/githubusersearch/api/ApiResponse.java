package me.yluo.githubusersearch.api;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;

/**
 * Created by yang on 2016/9/22.
 * API 返回handler 用于回调
 */

public abstract class ApiResponse {

    static final int MSG_SUCCESS = 0;
    static final int MSG_FAILER = -1;

    private Handler handler;
    private Looper looper = null;

    protected ApiResponse() {
        this(null);
    }

    private ApiResponse(Looper looper) {
        this.looper = (looper == null ? Looper.getMainLooper() : looper);
        handler = new ApiResHandler(this, this.looper);
    }

    public abstract void onFailer(String s);
    public abstract void onSuccess(List o);


    private void handleMessage(Message msg) {
        switch (msg.what){
            case MSG_SUCCESS:
                onSuccess((List) msg.obj);
                break;
            case MSG_FAILER:
                onFailer((String) msg.obj);
                break;
        }
    }

    final void sendMsg(int type, Object o){
        handler.sendMessage(obtainMessage(type, o));
    }

    private Message obtainMessage(int responseMessageId, Object responseMessageData) {
        return Message.obtain(handler, responseMessageId, responseMessageData);
    }

    private static class ApiResHandler extends Handler {
        private final ApiResponse mResponder;

        ApiResHandler(ApiResponse mResponder, Looper looper) {
            super(looper);
            this.mResponder = mResponder;
        }

        @Override
        public void handleMessage(Message msg) {
            mResponder.handleMessage(msg);
        }
    }
}
