package tanja.db.de.pedometer;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

/**
 * Created by Thomas on 23.03.2015.
 */
public class SensorService extends Service implements SensorEventListener{
    SensorManager smAccel, linSm, rotatSm, gyroSM;
    float[] unAccel= {0, 0, 0};
    float[] corrected_acceleration = {0, 0, 0};
    float[] gyroskope = {0, 0, 0};
    float[] matrix1 = new float[9];//rotationMatrix
    float[] rotationVectorXYZ = {0, 0, 0};
    SensorEventListener sel;
    public static final String TAG = "SensorService";
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
    private PowerManager.WakeLock mWakeLock = null;

    public void onCreate() {
        Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();

        // Erstelle Sensore Manager//
        smAccel = (SensorManager) getSystemService(SENSOR_SERVICE);
        linSm =(SensorManager) getSystemService(SENSOR_SERVICE);
        rotatSm = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroSM =(SensorManager) getSystemService(SENSOR_SERVICE);

        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(Process.myPid() , new Notification());
        registerListener();
        mWakeLock.acquire();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterListener();
        mWakeLock.release();
        stopForeground(true);
        Toast.makeText(this, "Service destroyed ...",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(" + intent + ")");
            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };
            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };

    private void registerListener() {
        // Register Sensor Manager//
        smAccel.registerListener(this,
                smAccel.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        linSm.registerListener(this,
                linSm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_FASTEST);
        rotatSm.registerListener(this,
                rotatSm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_FASTEST);
        gyroSM.registerListener(this,
                gyroSM.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

    }
    /*
    * Un-register this as a sensor event listener.
    */
    private void unregisterListener() {
        // Unregister Sensor Manager
        smAccel.unregisterListener(this);
        linSm.unregisterListener(this);
        rotatSm.unregisterListener(this);
        gyroSM.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            unAccel[0] = event.values[0];
            unAccel[1] = event.values[1];
            unAccel[2] = event.values[2];
            String valuesAcc = unAccel[0] + ";" + unAccel[1] + ";"
                    + unAccel[2] + ";" + event.timestamp;
            MainActivity.dataUnAcc.add(valuesAcc);
            //MainActivity.showData.setText((new Float(unAccel[0])).toString());
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            corrected_acceleration[0] = event.values[0];
            corrected_acceleration[1] = event.values[1];
            corrected_acceleration[2] = event.values[2];
            String valueCoAcc = corrected_acceleration[0] + ";" + corrected_acceleration[1] + ";"
                    + corrected_acceleration[2] + ";"+ event.timestamp;
            MainActivity.dataCoAcc.add(valueCoAcc);
            //MainActivity.showData.setText((new Float(corrected_acceleration[0])).toString());
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(matrix1,event.values);
            float rotationVectorX = event.values[0];
            float rotationVectorY = event.values[1];
            float rotationVectorZ = event.values[2];
            float scalarOfRotation = event.values[3];
            rotationVectorXYZ[0] = event.values[0];
            rotationVectorXYZ[1] = event.values[1];
            rotationVectorXYZ[2] = event.values[2];
            String valueRot =  rotationVectorX +";"+ rotationVectorY +";" + rotationVectorZ +";"
                    + scalarOfRotation +";" +event.timestamp;
            MainActivity.dataRot.add(valueRot);
            //MainActivity.showData.setText((new Float(rotationVectorXYZ[0])).toString());
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyroskope[0] = event.values[0];
            gyroskope[1] = event.values[1];
            gyroskope[2] = event.values[2];
            String valueGyro = gyroskope[0]+";"+ gyroskope[1] +";"+ gyroskope[2] +";"+event.timestamp;
            MainActivity.dataGyro.add(valueGyro);
            MainActivity.showData.setText((new Float(gyroskope[0])).toString());
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
