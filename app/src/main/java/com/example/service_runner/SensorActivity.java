package com.example.service_runner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;

public class SensorActivity extends AppCompatActivity implements SensorEventListener, LocationTracker.LocationUpdateListener, LocationTracker {

    private String nome, idade, altura, peso;
    //declara variaveis
    private static final String TAG = "SENSORES";
    private SensorManager sensorManager;
    SensorManager multiSensor;
    Sensor sensores;
    File csvFile;
    OutputStreamWriter outputWriter;
    private boolean isRunning;
    private LocationTracker.LocationUpdateListener listener;
    private LocationClass gps, net;
    private GoogleMap mMap;
    Location lastLoc;
    long lastTime;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        //Captura dados que vem da MainActivity
        Intent intent = this.getIntent();
        nome = intent.getStringExtra("nome");
        idade = intent.getStringExtra("idade");
        altura = intent.getStringExtra("altura");
        peso = intent.getStringExtra("peso");

        //Requisição de permissão para usuários (Escrita, localização e internet)
        if(Permissoes.validarPermissoes(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }, this, 101)){

        }
        //instancia GPS
        FallbackLocationTracker(this);
    }
    public void iniciaSensor(View view){
        //inicializa sensores
        multiSensor = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor msensor = multiSensor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor asensor = multiSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gsensor = multiSensor.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //registra o listener dos sensores
        if(multiSensor.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_NORMAL)){
            Toast.makeText(getApplicationContext(), "Gyroscópio OK", Toast.LENGTH_SHORT).show();
        }
        if(multiSensor.registerListener(this, asensor, SensorManager.SENSOR_DELAY_NORMAL)){
            Toast.makeText(getApplicationContext(), "Acelerômetro OK", Toast.LENGTH_SHORT).show();
        }
        if(multiSensor.registerListener(this, msensor, SensorManager.SENSOR_DELAY_NORMAL)){
            Toast.makeText(getApplicationContext(), "Magnetômetro OK", Toast.LENGTH_SHORT).show();
        }
        //cria o arquivo que será usado no metodo onSensorChanged para escrita dos dados
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        csvFile = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/Export_"+timestamp+".csv");
        try {
            csvFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(csvFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        outputWriter = new OutputStreamWriter(outputStream);

        //inicia gps
        this.start();
    }
    public void paraSensor(View view){
        try{
            multiSensor.unregisterListener(SensorActivity.this);
            multiSensor = null;
            if(multiSensor == null){
                Toast.makeText(getApplicationContext(), "Finalizado", Toast.LENGTH_SHORT).show();
            }
        }catch(NullPointerException e){
            //erro de null pointer, então não está registrado, mostrar mensagem
        }

        try {
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onCreate: Serviços parados");
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            if (sensorEvent.sensor.getType() == sensores.TYPE_ACCELEROMETER) {
                try {
                    outputWriter.append(
                            "TYPE_ACCELEROMETER;"
                                    .concat(String.valueOf(sensorEvent.values[0])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[1])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[2])).concat(";")
                                    .concat(String.valueOf(sensorEvent.timestamp)).concat(";")
                                    .concat( idade).concat(";")
                                    .concat( peso)
                                    .concat(";").concat(altura)
                    );
                    outputWriter.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, (String.valueOf(sensorEvent.values[0])).concat(";")
                    .concat(String.valueOf(sensorEvent.values[1])).concat(";")
                    .concat(String.valueOf(sensorEvent.values[2])).concat(";")
                    .concat(String.valueOf(sensorEvent.timestamp)).concat(";")
                    .concat( idade).concat(";")
                    .concat( peso)
                    .concat(";").concat(altura));
            if (sensorEvent.sensor.getType() == sensores.TYPE_MAGNETIC_FIELD) {
                try {
                    outputWriter.append(
                            "TYPE_MAGNETIC_FIELD;"
                                    .concat(String.valueOf(sensorEvent.values[0])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[1])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[2])).concat(";")
                                    .concat(String.valueOf(sensorEvent.timestamp)).concat(";")
                                    .concat( idade).concat(";")
                                    .concat( peso)
                                    .concat(";").concat(altura)
                    );
                    outputWriter.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (sensorEvent.sensor.getType() == sensores.TYPE_GYROSCOPE) {
                try {
                    outputWriter.append(
                            "TYPE_GYROSCOPE;"
                                    .concat(String.valueOf(sensorEvent.values[0])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[1])).concat(";")
                                    .concat(String.valueOf(sensorEvent.values[2])).concat(";")
                                    .concat(String.valueOf(sensorEvent.timestamp)).concat(";")
                                    .concat( idade).concat(";")
                                    .concat( peso)
                                    .concat(";").concat(altura)
                    );
                    outputWriter.append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (NullPointerException  e){
            Toast.makeText(getApplicationContext(), "NullPointerException: Por favor reinicie a aplicação", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensorEvent, int i) {
        //codar
    }
    public void FallbackLocationTracker(Context context) {
        gps = new LocationClass(context, LocationClass.ProviderType.GPS);
        net = new LocationClass(context, LocationClass.ProviderType.NETWORK);
    }

    public void start(){
        if(isRunning){
            //Already running, do nothing
            return;
        }

        //Start both
        gps.start(this);
        net.start(this);
        isRunning = true;
    }

    public void start(LocationTracker.LocationUpdateListener update) {
        start();
        listener = update;
    }


    public void stop(){
        if(isRunning){
            gps.stop();
            net.stop();
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation(){
        //If either has a location, use it
        return gps.hasLocation() || net.hasLocation();
    }

    public boolean hasPossiblyStaleLocation(){
        //If either has a location, use it
        return gps.hasPossiblyStaleLocation() || net.hasPossiblyStaleLocation();
    }

    public Location getLocation(){
        Location ret = gps.getLocation();
        if(ret == null){
            ret = net.getLocation();
        }
        return ret;
    }

    public Location getPossiblyStaleLocation(){
        Location ret = gps.getPossiblyStaleLocation();
        if(ret == null){
            ret = net.getPossiblyStaleLocation();
        }
        return ret;
    }

    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
        boolean update = false;
        Log.d(TAG, "onUpdateGPS: ");
        //We should update only if there is no last location, the provider is the same, or the provider is more accurate, or the old location is stale
        if(lastLoc == null){
            update = true;
        }
        else if(lastLoc != null && lastLoc.getProvider().equals(newLoc.getProvider())){
            update = true;
        }
        else if(newLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
            update = true;
        }
        else if (newTime - lastTime > 5 * 60 * 1000){
            update = true;
        }

        if(update){
            if(listener != null){
                listener.onUpdate(lastLoc, lastTime, newLoc, newTime);
            }
            lastLoc = newLoc;
            lastTime = newTime;
        }

        if(getLocation() != null && outputWriter != null) {
            try {
                outputWriter.append(
                        "GPS;"
                                .concat(String.valueOf(getLocation().getLatitude())).concat(";")
                                .concat(String.valueOf(getLocation().getLongitude())).concat(";")
                                .concat(String.valueOf(getLocation().getSpeed())).concat(";")
                                .concat(String.valueOf(getLocation().getTime())).concat(";")
                                .concat( idade).concat(";")
                                .concat( peso)
                                .concat(";").concat(altura)
                );
                Log.d(TAG, "onUpdateGPS: GRAVOU NO ARQUIVO");
                outputWriter.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}