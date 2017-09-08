package com.example.minhd.demoappimagelock.gallery.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    public static boolean checkPermissionStore(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        List<String> pers = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (SharfUtils.getNumberDeniedNotAgainPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) > 0) {
                return false;
            }
            pers.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (SharfUtils.getNumberDeniedNotAgainPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) > 0) {
                return false;
            }
            pers.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (pers.size() == 0) {
            return true;
        }
        String arrs[] = new String[pers.size()];
        int index = 0;
        for (String per : pers) {
            arrs[index] = per;
            index++;
        }
        ActivityCompat.requestPermissions(activity, arrs, requestCode);
        return false;
    }

    public static void checkPermission(Activity activity, String[] pernissions, int[] granted) {
        for (int i = 0; i < granted.length; i++) {
            if (granted[i] == PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, pernissions[i])) {
                    SharfUtils.increateDeniedNotAgainPermission(activity, pernissions[i], 1);
                }
            } else {
                SharfUtils.saveDeniedNotAgainPermission(activity, pernissions[i], 0);
            }
        }
    }

    public static boolean checkPermissionStoreMediaNotShowDialog(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        boolean check = true;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            check = false;
        } else {
            SharfUtils.saveDeniedNotAgainPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            check = false;
        } else {
            SharfUtils.saveDeniedNotAgainPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, 0);
        }
        return check;
    }
}
