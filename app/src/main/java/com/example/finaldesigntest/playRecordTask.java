package com.example.finaldesigntest;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 达明 on 2018/3/29.
 */

public class playRecordTask extends AsyncTask<String,Integer,String> {

    private String TAG = "playRecordTask";
    private File file;
    @Override
    protected String doInBackground(String... strings) {
        String fileName = strings[0].substring(strings[0].indexOf("&")+1);
        String setTime = strings[0].substring(0,strings[0].indexOf("&")-1);
        final String startTime = Calendar.getInstance().get(Calendar.YEAR)+"-"
                            +Calendar.getInstance().get(Calendar.MONTH)+"-"
                            +Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+" "
                            +setTime.substring(0,setTime.indexOf(":")-1)+":"
                            +setTime.substring(setTime.indexOf(".")+1,setTime.lastIndexOf(":")-1);
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/savedRecord/";
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Toast.makeText(MyApplication.getContext(),"I should play now at "+startTime,
                        Toast.LENGTH_SHORT).show();
            }
        };
        Timer timer = new Timer(true);
        timer.schedule(task,strToDateLong(startTime));
        return null;
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
}
