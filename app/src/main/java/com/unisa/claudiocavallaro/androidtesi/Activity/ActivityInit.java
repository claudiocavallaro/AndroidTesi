package com.unisa.claudiocavallaro.androidtesi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.unisa.claudiocavallaro.androidtesi.R;

public class ActivityInit  extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init);

        final EditText editText = (EditText) findViewById(R.id.editInit);

        Button conferma = (Button) findViewById(R.id.buttonInit);

        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = editText.getText().toString();

                if (result.equals("gym2019")){
                    Intent i = new Intent(ActivityInit.this, ActivityHome.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Password non corretta", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
