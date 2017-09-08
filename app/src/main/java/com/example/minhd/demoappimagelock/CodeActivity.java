package com.example.minhd.demoappimagelock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import io.paperdb.Paper;

import static com.example.minhd.demoappimagelock.MainActivity.check;

public class CodeActivity extends Activity {

    public static final String TAG = "PinLockView";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private String final_pin ;
    private PinLockListener mPinLockListener;
    private java.lang.String save_pin = "save_pin";
    private static final int REQUEST_CODE_GALLERY = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Paper.init(this);

        final String save_pattern = Paper.book().read(save_pin);

        if (save_pattern != null && ! save_pattern.equals("null")) {

            setContentView(R.layout.code_screen);


            mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
            mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

            mPinLockView.attachIndicatorDots(mIndicatorDots);

            mPinLockView.setPinLength(4);
            mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

            mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

            mPinLockListener = new PinLockListener() {
                @Override
                public void onComplete(String pin) {
                    if (pin.equals(save_pattern)) {
                        Toast.makeText(CodeActivity.this, "Pass correct", Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(CodeActivity.this, Main2Activity.class) ;
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(CodeActivity.this, "Incorrect", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("TAG", save_pattern + "\t" + pin ) ;

                }

                @Override
                public void onEmpty() {
                    Log.d(TAG, "Pin empty");

                }

                @Override
                public void onPinChange(int pinLength, String intermediatePin) {
                    Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);

                }
            };
            mPinLockView.setPinLockListener(mPinLockListener);

        } else {

            setContentView(R.layout.activity_code);

            mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view_set_up);
            mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots_setup);

            mPinLockView.attachIndicatorDots(mIndicatorDots);

            mPinLockView.setPinLength(4);
            mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

            mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);


            mPinLockListener = new PinLockListener() {
                @Override
                public void onComplete(String pin) {
                    Log.d(TAG, "Pin complete: " + pin);
                    final_pin = pin;

                }

                @Override
                public void onEmpty() {
                    Log.d(TAG, "Pin empty");
                }

                @Override
                public void onPinChange(int pinLength, String intermediatePin) {
                    Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
                }
            };

            mPinLockView.setPinLockListener(mPinLockListener);

            findViewById(R.id.btnSetPin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(CodeActivity.this, "Save Pin Success", Toast.LENGTH_SHORT).show();
                    Paper.book().write(save_pin, final_pin) ;
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
        Paper.book().write(save_pin, "null") ;
        MainActivity.check = "null" ;
        Paper.book().write(MainActivity.save_pin, check);
        Intent intent = new Intent(CodeActivity.this, MainActivity.class) ;
        startActivity(intent);
        finish();
    }
}
