package com.example.minhd.demoappimagelock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

import io.paperdb.Paper;

import static com.example.minhd.demoappimagelock.MainActivity.checkPat;
import static com.example.minhd.demoappimagelock.MainActivity.save_pat;

public class PatternActivity extends Activity {

    private static final int REQUEST_CODE_GALLERY = 123;
    String save_pattern_key = "pattern_code";
    String final_pattern = "";
    PatternLockView mPatternLockView;
    private boolean checkPatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);
        final String save_pattern = Paper.book().read(save_pattern_key);
        if(save_pattern != null && !save_pattern.equals("null"))
        {
            setContentView(R.layout.pattern_screen);

            checkPatt = true ;

            mPatternLockView = (PatternLockView)findViewById(R.id.pattern_lock_view);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView,pattern);
                    if(final_pattern.equals(save_pattern)) {
                        Toast.makeText(PatternActivity.this, "Password Correct!", Toast.LENGTH_SHORT).show();

                    }
                    else
                        Toast.makeText(PatternActivity.this, "Password incorrect!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCleared() {

                }
            });
        }
        else
        {

            setContentView(R.layout.activity_pattern);
            mPatternLockView = (PatternLockView)findViewById(R.id.pattern_lock_view);
            mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {

                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                    final_pattern = PatternLockUtils.patternToString(mPatternLockView,pattern);

                }

                @Override
                public void onCleared() {

                }
            });




            Button btnSetup = (Button)findViewById(R.id.btnSetPattern);
            btnSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Paper.book().write(save_pattern_key, final_pattern);
                    Toast.makeText(PatternActivity.this, "Save pattern okay!", Toast.LENGTH_SHORT).show();
                    finish();
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.save_pat = null;
        MainActivity.save_pin = null;
        MainActivity.checkPat = "null" ;
        Paper.book().write(save_pat, checkPat);
        Paper.book().write(save_pattern_key, checkPat);
        Intent intent1 = new Intent(PatternActivity.this, MainActivity.class) ;
        startActivity(intent1);
        finish();
    }
}
