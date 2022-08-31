package com.example.final_project.Common;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class TimerWorker extends Worker {
    public static Thread t;
    public static boolean flag=true;
    public TimerWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {

        super(context, params);
        setProgressAsync(new Data.Builder().putString("time", "0").build());
    }

    @Override
    public Result doWork() {

        Data.Builder data = new Data.Builder();
        long millisUntilFinished= (long) Integer.parseInt(getInputData().getString("timerSet")) *60*1000;

        t= new Thread(new Runnable() {

            @Override
            public void run() {

            }
        });
        t.run();
        flag=true;
        while (millisUntilFinished>-1000&&flag) {
            try {
                long minutes = (millisUntilFinished/1000)/60;
                long seconds = (millisUntilFinished/1000)%60;
                StringBuilder time=new StringBuilder();
                if(seconds<10)
                    time.append(minutes+":0"+seconds);
                else time.append(minutes+":"+seconds);
                if(time.toString().equals("0:00")){
                    setProgressAsync(data.putString("time", time.toString()).build());
                    setProgressAsync(data.putString("timeFinished", "finished").build());
                }

                setProgressAsync(data.putString("time", time.toString()).build());

                t.sleep(1000);
                millisUntilFinished=millisUntilFinished-1000;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setProgressAsync(data.putString("time", "finish").build());

        return Result.success();
    }
    public static void killTime(){
       flag=false;
        }
}
