package com.davidbriglio.foreground;

import android.content.Intent;
import android.content.Context;
import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.IBinder;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.util.Log;
import android.os.SystemClock;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;

public class ForegroundService extends Service {

    private boolean isServiceStarted = false;
    private boolean isWorkerRun = false;

    @Override
    public void onDestroy()
    {
        Log.e("Service", "onDestroy..");
        // this._stop_worker();
        // this._restart_me();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.e("Service", "onBind..");
        Log.e("Service", intent.toString());
        return null;
    }

    // Client is Unbinding via the unbindService() call
    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.e("Service", "onUnbind..");
        Log.e("Service", intent.toString());
        return super.onUnbind(intent);
    }

    private void _stop_worker()
    {
        Log.e("Service", "stop worker..");

        isServiceStarted = false;
        int timeout = 10;
        while(isWorkerRun && timeout > 0)
        {
            //wait
            try {
                timeout--;
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(timeout > 0)
        {
            Log.e("Service", "stop worker done..");
        }
        else
        {
            Log.e("Service", "stop worker timeout..");
        }
    }

    private void _restart_me()
    {
        Log.e("Service", "try restarting app..");

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        // Tell the service we want to start it
        restartServiceIntent.setAction("start");

        // Pass the notification title/text/icon to the service
        restartServiceIntent.putExtra("title", "")
            .putExtra("text", "")
            .putExtra("icon", "")
            .putExtra("importance", 3)
            .putExtra("id", "");
            

        Log.e("Service", restartServiceIntent.toString());

        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);


        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 10000, restartServicePendingIntent);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.e("Service", "onTaskRemoved running..");
        this._stop_worker();
        this._restart_me();

        int counter = 121;
        String CHANNELID3 = "Helpdesk Notification";
        NotificationChannel channel3 = new NotificationChannel(
                CHANNELID3,
                CHANNELID3,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel3);
        
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this, CHANNELID3)
                    .setContentText("Service restarted..")
                    .setContentTitle("counter : " + counter)
                    .setSmallIcon(17301514);
                    

        manager.notify(counter, builder.build());

        


        super.onTaskRemoved(rootIntent);

        // 
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals("start")) {
            // Start the service
            startPluginForegroundService(intent);
        } else {
            // Stop the service
            isServiceStarted = false;
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
        
    }

    @TargetApi(26)
    private void startPluginForegroundService(Intent intent) {
        
        //if already running
        if (isServiceStarted) return;

        Bundle extras = intent.getExtras();


        isServiceStarted = true;

        Context context = getApplicationContext();


        // // Delete notification channel if it already exists
        // NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // manager.deleteNotificationChannel("foreground.service.channel");

        // // Get notification channel importance
        // Integer importance;

        // try {
        //     importance = Integer.parseInt((String) extras.get("importance"));
        // } catch (NumberFormatException e) {
        //     importance = 1;
        // }

        // switch(importance) {
        //     case 2:
        //         importance = NotificationManager.IMPORTANCE_DEFAULT;
        //         break;
        //     case 3:
        //         importance = NotificationManager.IMPORTANCE_HIGH;
        //         break;
        //     default:
        //         importance = NotificationManager.IMPORTANCE_LOW;
        //     // We are not using IMPORTANCE_MIN because we want the notification to be visible
        // }

        // // Create notification channel
        // NotificationChannel channel = new NotificationChannel("foreground.service.channel", "Background Services", importance);
        // channel.setDescription("Enables background processing.");
        // getSystemService(NotificationManager.class).createNotificationChannel(channel);

        // // Get notification icon
        // int icon = getResources().getIdentifier((String) extras.get("icon"), "drawable", context.getPackageName());

        // // Make notification
        // Notification notification = new Notification.Builder(context, "foreground.service.channel")
        //     .setContentTitle((CharSequence) extras.get("title"))
        //     .setContentText((CharSequence) extras.get("text"))
        //     .setOngoing(true)
        //     .setSmallIcon(icon == 0 ? 17301514 : icon) // Default is the star icon
        //     .build();

        // // Get notification ID
        // Integer id;
        // try {
        //     id = Integer.parseInt((String) extras.get("id"));
        // } catch (NumberFormatException e) {
        //     id = 0;
        // }

        // // Put service in foreground and show notification (id of 0 is not allowed)
        // startForeground(id != 0 ? id : 197812504, notification);

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        
        int icon = getResources().getIdentifier((String) extras.get("icon"), "drawable", context.getPackageName());

            
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Helpdesk")
                .setContentTitle("Service enabled")
                .setOngoing(true)
                .setSmallIcon(icon == 0 ? 17301514 : icon);

        startForeground(1001, notification.build());
        


        ForegroundService dsf = this;

        new Thread(
             new Runnable() {
                 @Override
                 public void run() {

                    
                    // String anewactivity="Zero";
                    // Class activityClass = Class.forName("com.webskrip.foreground_service"); //com.something.Zero extras.get("parent_intent")
                    // Intent NewActivity = new Intent(context, activityClass.class);

                    PackageManager pm = context.getPackageManager();
                    Intent launchIntent = pm.getLaunchIntentForPackage("com.webskrip.foreground_service");
                    // context.startActivity(launchIntent);
                    
                    Log.e("Service", 'worker runing..');
                    Log.e("Service", launchIntent.toString());
                    Log.e("Service", context.toString());

                    PendingIntent pendingIntent = PendingIntent.getActivity(
                            context, 
                            0, 
                            launchIntent, 
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                            
                    int counter = 9999;
                    final String CHANNELID2 = "Helpdesk Notification";
                    NotificationChannel channel2 = new NotificationChannel(
                            CHANNELID2,
                            CHANNELID2,
                            NotificationManager.IMPORTANCE_LOW
                    );

                    int icon2 = getResources().getIdentifier((String) extras.get("icon"), "drawable", "Notification");
                    getSystemService(NotificationManager.class).createNotificationChannel(channel2);
                    
                     while (isServiceStarted) {
                        isWorkerRun = true;
                        counter++;
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification.Builder builder = new Notification.Builder(dsf, CHANNELID2)
                                    .setContentText("Helpdesk")
                                    .setContentTitle("counter : " + counter)
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(icon2 == 0 ? 17301514 : icon2);
                                    

                        manager.notify(counter, builder.build());

                        Log.e("Service", "Service is running...");
                         try {
                             Thread.sleep(10000);
                         } catch (InterruptedException e) {
                              e.printStackTrace();
                         }
                     }

                     isWorkerRun = false;
                 }
             }
     ).start();



    //  return super.onStartCommand(intent, flags, startId);
    }

    // @Override
    // public IBinder onBind(Intent intent) {
    //     throw new UnsupportedOperationException("Not yet implemented");
    // }
}
