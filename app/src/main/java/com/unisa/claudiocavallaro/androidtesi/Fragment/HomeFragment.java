package com.unisa.claudiocavallaro.androidtesi.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.Rating;
import android.os.Bundle;
import android.os.Environment;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.acrcloud.rec.ACRCloudClient;
import com.acrcloud.rec.ACRCloudConfig;
import com.acrcloud.rec.ACRCloudResult;
import com.acrcloud.rec.IACRCloudListener;
import com.acrcloud.rec.utils.ACRCloudLogger;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.unisa.claudiocavallaro.androidtesi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class HomeFragment extends Fragment implements IACRCloudListener {

    private final static String TAG = "MainActivity";

    private TextView mVolume, mResult, tv_time, tv_title, tv_artist;

    private boolean mProcessing = false;
    private boolean mAutoRecognizing = false;
    private boolean initState = false;

    private RatingBar ratingBar;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPlaying = false;

    private String path = "";

    private long startTime = 0;
    private long stopTime = 0;

    private final int PRINT_MSG = 1001;

    private ACRCloudConfig mConfig = null;
    private ACRCloudClient mClient = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, null);

        path = Environment.getExternalStorageDirectory().toString()
                + "/acrcloud";
        Log.e(TAG, path);

        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }

        mVolume = (TextView) view.findViewById(R.id.textVol);
        mResult = (TextView) view.findViewById(R.id.textRes);
        tv_time = (TextView) view.findViewById(R.id.textElapsedTime);

        tv_artist = (TextView) view.findViewById(R.id.musicArtist);
        tv_title = (TextView) view.findViewById(R.id.musicTitle);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingbar);


        ImageButton button = (ImageButton) view.findViewById(R.id.ico_mic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        verifyPermissions();

        this.mConfig = new ACRCloudConfig();

        this.mConfig.acrcloudListener = this;
        this.mConfig.context = this.getContext();

        // Please create project in "http://console.acrcloud.cn/service/avr".
        this.mConfig.host = "identify-eu-west-1.acrcloud.com";
        this.mConfig.accessKey = "d4d377f0c6947376606087d7c765a228";
        this.mConfig.accessSecret = "IHA8k41R2Iv24nmEtRTABtcRH2x1Fbw5IQ57fJwO";

        this.mConfig.recorderConfig.isVolumeCallback = true;

        this.mClient = new ACRCloudClient();
        ACRCloudLogger.setLog(true);

        this.initState = this.mClient.initWithConfig(this.mConfig);

        return view;
    }

    public void start() {
        if (!this.initState) {
            Toast.makeText(this.getContext(), "init error", Toast.LENGTH_SHORT).show();
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
        mProcessing = false;
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

        try {
            JSONObject j = new JSONObject(result);
            JSONObject j1 = j.getJSONObject("status");
            int j2 = j1.getInt("code");
            //il codice è zero quando trova la canzone
            if(j2 == 0){
                JSONObject metadata = j.getJSONObject("metadata");
                //
                if (metadata.has("music")) {
                    JSONArray musics = metadata.getJSONArray("music");

                    // metto 1 per limitare il risultato
                    // dovrei rimettere musics.lenght()
                    for(int i=0; i< 1; i++) {
                        JSONObject tt = (JSONObject) musics.get(i);
                        String title = tt.getString("title");
                        JSONArray artistt = tt.getJSONArray("artists");
                        JSONObject art = (JSONObject) artistt.get(0);
                        String artist = art.getString("name");



                        JSONObject obj_meta = tt.getJSONObject("external_metadata");
                        JSONObject spotify = obj_meta.getJSONObject("spotify");
                        JSONObject track = spotify.getJSONObject("track");
                        //JSONObject idJSON = track.getJSONObject("id");

                        System.out.println("--------" + track.getString("id"));

                        tres = tres + (i+1) + ".  Title: " + title + "    Artist: " + artist + "\n";

                        // CREAZIONE OGGETTO

                        tv_title.setText(title);
                        tv_artist.setText(artist);


                    }
                }

                tres = tres + "\n\n" + result;
            }else{
                Toast.makeText(this.getContext(), "Nessuna canzone trovata", Toast.LENGTH_LONG).show();
                tres = result;
            }
        } catch (JSONException e) {
            Toast.makeText(this.getContext(), "Nessuna canzone trovata", Toast.LENGTH_LONG).show();
            tres = result;
            e.printStackTrace();
        }

        //mResult.setText(tres);
        System.out.println("*******" + tres);
        startTime = System.currentTimeMillis();



        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                System.out.println((int)rating);
            }
        });
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
            int permission = ActivityCompat.checkSelfPermission(this.getContext(), PERMISSIONS[i]);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.getActivity(), PERMISSIONS,
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
