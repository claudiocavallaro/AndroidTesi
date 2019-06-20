package com.unisa.claudiocavallaro.androidtesi.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.acrcloud.rec.ACRCloudClient;
import com.acrcloud.rec.ACRCloudConfig;
import com.acrcloud.rec.ACRCloudResult;
import com.acrcloud.rec.IACRCloudListener;
import com.acrcloud.rec.utils.ACRCloudLogger;
import com.unisa.claudiocavallaro.androidtesi.Model.ApiKeys;
import com.unisa.claudiocavallaro.androidtesi.Model.Preferenza;
import com.unisa.claudiocavallaro.androidtesi.Persistence.Communicator;
import com.unisa.claudiocavallaro.androidtesi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ActivityHome extends AppCompatActivity implements IACRCloudListener {

    private final static String TAG = "MainActivity";

    private TextView mVolume, mResult, tv_time, tv_title, tv_artist;

    private boolean mProcessing = false;
    private boolean initState = false;

    private Preferenza p;

    private Button conferma;

    private Communicator communicator;

    private RatingBar ratingBar;

    private String path = "";

    private long startTime = 0;
    private long stopTime = 0;

    public Preferenza getP() {
        return p;
    }

    public void setP(Preferenza p) {
        this.p = p;
    }


    private ACRCloudConfig mConfig = null;
    private ACRCloudClient mClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        communicator = new Communicator();

        communicator.getKey(this);

        path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud";
        Log.e(TAG, path);

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        mVolume = (TextView) findViewById(R.id.textVol);
        mResult = (TextView) findViewById(R.id.textRes);
        tv_time = (TextView) findViewById(R.id.textElapsedTime);

        tv_artist = (TextView) findViewById(R.id.musicArtist);
        tv_title = (TextView) findViewById(R.id.musicTitle);

        ratingBar = (RatingBar) findViewById(R.id.ratingbar);

        conferma = (Button) findViewById(R.id.button);
        conferma.setVisibility(View.INVISIBLE);

        ImageButton button = (ImageButton) findViewById(R.id.ico_mic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        verifyPermissions();

        this.mConfig = new ACRCloudConfig();

        this.mConfig.acrcloudListener = this;
        this.mConfig.context = this;

        // Please create project in "http://console.acrcloud.cn/service/avr".


        this.mConfig.recorderConfig.isVolumeCallback = true;

        this.mClient = new ACRCloudClient();
        ACRCloudLogger.setLog(true);

        this.initState = this.mClient.initWithConfig(this.mConfig);

    }


    public void start() {
        if (!this.initState) {
            Toast.makeText(this, "init error", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            mVolume.setText("");
            mResult.setText("");
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
                mResult.setText("start error!");
            }
            startTime = System.currentTimeMillis();
        }
    }

    public void reset() {
        tv_time.setText("");
        mResult.setText("");
        tv_artist.setText("");
        tv_title.setText("");
        ratingBar.setRating(0);
        mProcessing = false;
    }

    public void setErrorView(){
        this.setContentView(R.layout.activity_error);
    }

    public void setKey(ApiKeys apiKeys){
        this.mConfig.host = apiKeys.getHost();
        this.mConfig.accessKey = apiKeys.getAccessKey();
        this.mConfig.accessSecret = apiKeys.getAccessSecret();
    }

    @Override
    public void onResult(ACRCloudResult results) {
        this.reset();

        // If you want to save the record audio data, you can refer to the following codes.
	/*
	byte[] recordPcm = results.getRecordDataPCM();
        if (recordPcm != null) {
            byte[] recordWav = ACRCloudUtils.pcm2Wav(recordPcm, this.mConfig.recorderConfig.rate, this.mConfig.recorderConfig.channels);
            ACRCloudUtils.createFileWithByte(recordWav, path + "/" + "record.wav");
        }
	*/

        String result = results.getResult();

        String tres = "\n";

        String title = "";
        String artist = "";
        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            p = new Preferenza();
            int j2 = j1.getInt("code");
            //il codice è zero quando trova la canzone
            if(j2 == 0){
                JSONObject metadata = j.getJSONObject("metadata");

                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");

                    // metto 1 per limitare il risultato
                    // dovrei rimettere musics.lenght()
                    for(int i=0; i< 1; i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        title = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        artist = art.getString("name");



                        JSONObject obj_meta = tt.getJSONObject("external_metadata");
                        JSONObject spotify = obj_meta.getJSONObject("spotify");
                        JSONObject track = spotify.getJSONObject("track");
                        //JSONObject idJSON = track.getJSONObject("id");

                        System.out.println("--------" + track.getString("id"));

                        p.setId(track.getString("id"));
                        tres = tres + (i+1) + ".  Title: " + title + "    Artist: " + artist + "\n";

                        // CREAZIONE OGGETTO

                        tv_title.setText(title);
                        tv_artist.setText(artist);


                    }
                }

                tres = tres + "\n\n" + result;


                ratingBar.setVisibility(View.VISIBLE);
                conferma.setVisibility(View.VISIBLE);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                        p.setRatingString(String.valueOf(rating));


                    }
                });

                conferma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = p.getId();
                        String rating = p.getRatingString();
                        useGet(id, rating);

                    }
                });

            }else{
                Toast.makeText(this, "Nessuna canzone trovata", Toast.LENGTH_LONG).show();
                tres = result;
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Nessuna canzone trovata", Toast.LENGTH_LONG).show();
            tres = result;
            e.printStackTrace();
        }

        //mResult.setText(tres);
        System.out.println("*******" + tres);
        startTime = System.currentTimeMillis();




    }

    private void useGet(String id, String rating) {
        communicator.getPref(this, id, rating);
        Toast.makeText(this, "Grazie per aver votato", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVolumeChanged(double volume) {
        long time = (System.currentTimeMillis() - startTime) / 1000;
        //mVolume.setText("Volume : " + volume + "\n\nTime: " + time + " s");
        tv_time.setText("Time: " + time + " s");
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };
    public void verifyPermissions() {
        for (int i=0; i<PERMISSIONS.length; i++) {
            int permission = ActivityCompat.checkSelfPermission(this, PERMISSIONS[i]);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS,
                        REQUEST_EXTERNAL_STORAGE);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("MainActivity", "release");
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }
}
