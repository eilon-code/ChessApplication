package com.mygdx.jar;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

public class PermissionManager {
    private static PermissionManager instance = null;
    private Context context;

    private PermissionManager(){

    }

    public static PermissionManager getInstance(Context context){
        if (instance == null){
            instance = new PermissionManager();
        }
        instance.init(context);
        return instance;
    }

    private void init(Context context){
        this.context = context;
    }

    public boolean checkPermissions(String[] permissions){
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public void askPermissions(Activity activity, String[] permissions, int requestCode){
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
