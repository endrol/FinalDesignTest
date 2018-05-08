package com.example.finaldesigntest.helper;

public class socketHelper {
    //单例化
    private socketHelper(){}
    private static final socketHelper SOCKET_HELPER = new socketHelper();
    public static socketHelper getSocketHelper(){
        return SOCKET_HELPER;
    }
    //全局操作
    private boolean thisSocket = false;
    public void setThisSocket(boolean thisSocket) {
        this.thisSocket = thisSocket;
    }

    public boolean isThisSocket() {
        return thisSocket;
    }
}
