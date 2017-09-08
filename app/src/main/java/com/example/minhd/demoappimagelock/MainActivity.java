package com.example.minhd.demoappimagelock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import net.soulwolf.widget.dialogbuilder.DialogBuilder;
import net.soulwolf.widget.dialogbuilder.Gravity;
import net.soulwolf.widget.dialogbuilder.MasterDialog;
import net.soulwolf.widget.dialogbuilder.OnItemClickListener;
import net.soulwolf.widget.dialogbuilder.dialog.AlertMasterDialog;

import io.paperdb.Paper;

public class MainActivity extends Activity implements OnItemClickListener {

    public static String check;
    public static String checkPat;
    public static java.lang.String save_pin = "save_pinn";
    public static java.lang.String save_pat = "save_patt";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Paper.init(this);

        final String save_pinn = Paper.book().read(this.save_pin);
        final String save_pattern = Paper.book().read(this.save_pat);

        if (save_pinn != null && !save_pinn.equals("null") && save_pinn.equals("true")) {
            Intent intent = new Intent(MainActivity.this, CodeActivity.class);
            startActivity(intent);
        }
        if (save_pattern != null && !save_pattern.equals("null") && save_pattern.equals("true")) {
            Intent intent1 = new Intent(MainActivity.this, PatternActivity.class);
            startActivity(intent1);
        }

        try {
            if (save_pattern == null && save_pinn == null || save_pattern.equals("null")
                    && save_pinn.equals("null")) {
                DialogBuilder builder = new DialogBuilder(this)
                        .setOnItemClickListener(this)
                        .setCancelable(false)
                        .setAnimation(R.anim.da_fade_in_center, R.anim.da_fade_out_center)
                        //.setIgnoreStatusBar(false)
                        .setGravity(Gravity.CENTER);
                AlertMasterDialog dialog = new AlertMasterDialog(builder);
                dialog.setTitle("Select type of Security");
                dialog.setButton1("Pattern");
                dialog.setButton2("Pin");
                dialog.show();
            }
        } catch (NullPointerException e) {

        }

    }

    @Override
    public void onItemClick(MasterDialog dialog, View view, int position) {

        if (position == 1) {
            check = "true";
            checkPat = "false";
            Paper.book().write(save_pin, check);
            Intent intent = new Intent(MainActivity.this, CodeActivity.class);
            startActivity(intent);
            finish();
        } else {
            checkPat = "true";
            check = "false";
            Paper.book().write(save_pin, checkPat);
            Intent intent1 = new Intent(MainActivity.this, PatternActivity.class);
            startActivity(intent1);
            finish();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}