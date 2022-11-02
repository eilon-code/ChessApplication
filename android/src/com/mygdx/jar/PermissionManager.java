package com.mygdx.jar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

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

    public void handlePermissionResult(Activity activity, int requestCode, String[] permissions,
                                    int[] grantResults){
        boolean isAllPermissionsGranted = true;

        if (grantResults.length > 1){
            for (int i = 0; i < grantResults.length; i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(activity, "Permission " + permissions[i] + " denied.", Toast.LENGTH_SHORT).show();
                    showPermissionsRational(activity, requestCode, permissions, permissions[i]);
                    isAllPermissionsGranted = false;
                    return;
                }
            }
            Toast.makeText(activity, "Permission granted.", Toast.LENGTH_SHORT).show();
        }
        else {
            isAllPermissionsGranted = false;
        }


        // return isAllPermissionsGranted;
    }

    public void showPermissionsRational(Activity activity, int requestCode, String[] permissions,
                                        String deniedPermission){
        if (activity.shouldShowRequestPermissionRationale(deniedPermission)){
            showMessageOkCancel("You need to allow access to the permission(s)!",
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            askPermissions(activity, permissions, requestCode);
                        }
                    });
        }
    }

    public void showMessageOkCancel(String msg, DialogInterface.OnClickListener onClickListener){
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("Ok", onClickListener)
                .setNegativeButton("Cancel", onClickListener)
                .create()
                .show();
    }
}
