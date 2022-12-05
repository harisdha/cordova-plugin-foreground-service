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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class ForegroundService extends Service {

    private static boolean isServiceStarted = false;
    private boolean isWorkerRun = false;
    // private static String token = "";
    // private String packageName = "";

    private void setToken(String mytoken)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Editor editor = prefs.edit();
        editor.putString("MYTOKEN", mytoken);
        editor.commit();
    }

    private String getToken()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getString("MYTOKEN", "NO_TOKEN");
    }

    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
        if(intent != null)
        {
            String action = (String) intent.getAction();
            if(action == null || action.isEmpty() || action.trim().isEmpty())
            {
                action = "";
            }

            if (action.equals("stop")) 
            {
                // Stop the service
                isServiceStarted = false;
                stopForeground(true);
                stopSelf();
            } 
            else if(action.equals("start"))
            {
                // Start the service
                startPluginForegroundService(intent);
            }
            else if(action.equals("restart"))
            {
                
                Bundle extras = intent.getExtras();
                String myToken = (String) extras.get("token");

                this._set_restart_service(myToken);
                isServiceStarted = false;
                // stopForeground(true);
                // stopSelf();
            }
        }

        return START_STICKY;
        
    }

    @TargetApi(26)
    private void startPluginForegroundService(Intent intent) 
    {
        
        //if already running
        if (isServiceStarted)
        {
            Log.e("service", "already running..!");
            // stopSelf();
            return;  
        }

        isServiceStarted = true;

        Bundle extras = intent.getExtras();

        this.setToken((String) extras.get("token"));
        String tempToken = this.getToken();

        Context context = getApplicationContext();

        ForegroundService tempService = this;
        
        this._showNotif(context, (String) extras.get("icon"));

        new Thread(
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    try
                    {
                        Log.e("Service", "worker running..");

                        String myToken = tempToken;
                        
                        PackageManager pm = context.getPackageManager();

                        Intent launchIntent = pm.getLaunchIntentForPackage(getPackageName()); //"com.webskrip.foreground_service"

                        // context.startActivity(launchIntent);
                        
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
                        
                        while (isServiceStarted) 
                        {
                            Log.e("Service", "Worker Loop is running...");

                            isWorkerRun = true;
                            counter++;
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification.Builder builder = new Notification.Builder(tempService, CHANNELID2)
                                        .setContentText("token: " + myToken)
                                        .setContentTitle("counter : " + counter)
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(icon2 == 0 ? 17301514 : icon2);
                                        
                            manager.notify(counter, builder.build());


                            try 
                            {
                                Thread.sleep(10000);
                            } 
                            catch (InterruptedException e) 
                            {
                                e.printStackTrace();
                            }
                        }

                        isWorkerRun = false;
                        

                    }
                    catch(Exception e)
                    {
                        Log.e("Work loop exception", e.toString());
                    }
                    
                }
            }
        ).start();

    }

    @Override
    public void onDestroy()
    {
        Log.e("Service", "onDestroy..");
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

        stopForeground(true);
        stopSelf();

    }

    private void _set_restart_service(String myToken)
    {

        Log.d("Service", "try restarting app..");
        Log.d("Service", "try restarting app..");
        Log.d("Service", "try restarting app..");

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        // Tell the service we want to start it
        restartServiceIntent.setAction("start");

        // Pass the notification title/text/icon to the service
        restartServiceIntent.putExtra("token", myToken)
            .putExtra("packageName", getPackageName())
            .putExtra("icon", "")
            .putExtra("importance", 3)
            .putExtra("id", "");
            

        Log.e("Service", restartServiceIntent.toString());

        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        try
        {
            Log.e("Service", "onTaskRemoved running.." + getToken());

            super.onTaskRemoved(rootIntent);

            Log.e("Service", "onTaskRemoved running.." + getToken());

            String myToken = this.getToken();
            if(myToken != null)
            {
                Log.e("Service", myToken);
                this._set_restart_service(myToken);
            }
            
            // isServiceStarted = false;
            Log.e("Service", "onTaskRemoved finish..");

        }
        catch(Exception e)
        {
            Log.e("onTaskRemoved", e.toString());
        }

    }
    
    
    private void _showNotif(Context context, String myIcon)
    {
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );
        
        int icon = getResources().getIdentifier(myIcon, "drawable", context.getPackageName());

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Helpdesk")
                .setContentTitle("Service enabled")
                .setOngoing(true)
                .setSmallIcon(icon == 0 ? 17301514 : icon);

        startForeground(1001, notification.build());
    }


}
