package com.unisa.claudiocavallaro.androidtesi.notused;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

import com.unisa.claudiocavallaro.androidtesi.Fragment.HomeFragment;
import com.unisa.claudiocavallaro.androidtesi.R;
import com.unisa.claudiocavallaro.androidtesi.Fragment.RecordFragment;

public class MainNav extends AppCompatActivity {
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    fragment = new HomeFragment();
                    return loadFragment(fragment);
                case R.id.navigation_record:
                    fragment = new RecordFragment();
                    return loadFragment(fragment);
            }
            return false;
        }
    };

    private boolean loadFragment(Fragment fragment){

        if (fragment != null){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new HomeFragment());
    }

}
