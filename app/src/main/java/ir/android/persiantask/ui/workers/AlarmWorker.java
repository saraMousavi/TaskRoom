package ir.android.persiantask.ui.workers;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import ir.android.persiantask.ui.activity.AlarmActivity;

public class AlarmWorker extends Worker {
    public static Ringtone ringtone;
    public AlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Intent i = new Intent(getApplicationContext(), AlarmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("alarmTitle", getInputData().getString("alarmTitle"));
        getApplicationContext().startActivity(i);
        Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        ringtone.play();
//        PowerManager powerManager = (PowerManager) getApplicationContext()
//                .getSystemService(getApplicationContext().POWER_SERVICE);
//        PowerManager.WakeLock  wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
//                PowerManager.ACQUIRE_CAUSES_WAKEUP |
//                PowerManager.ON_AFTER_RELEASE , "appname::WakeLock");
//        turnScreenOn();

        //acquire will turn on the display
//        wakeLock.acquire(2*60*1000L /*2 minutes*/);

        //release will release the lock from CPU, in case of that, screen will go back to sleep mode in defined time bt device settings
//        wakeLock.release();
        return Result.success();
    }

}
