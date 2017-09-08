package com.example.minhd.demoappimagelock.gallery;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.minhd.demoappimagelock.gallery.utils.PermissionUtils;

public class BaseGalleryActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.checkPermission(this, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
