package std.neomind.brainmanager.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;

import std.neomind.brainmanager.R;

import androidx.core.app.ActivityCompat;

public final class PermissionManager {

    private Activity mActivity;

    private boolean granted;

    public PermissionManager(Activity activity) {
        mActivity = activity;
        granted = false;
    }

    public void request() {
        if (granted) return;
        requestPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.CAMERA,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET);
    }

    public boolean checkGranted() {
        boolean denied = getPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getText(R.string.PermissionManager_permissionText_readExternalStorage));
        denied = denied || getPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getText(R.string.PermissionManager_permissionText_writeExternalStorage));
        denied = denied || getPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED, getText(R.string.PermissionManager_permissionText_receiveBootCompleted));
        denied = denied || getPermission(Manifest.permission.CAMERA, getText(R.string.PermissionManager_permissionText_camera));
        denied = denied || getPermission(Manifest.permission.WAKE_LOCK, getText(R.string.PermissionManager_permissionText_wakeLock));
        denied = denied || getPermission(Manifest.permission.ACCESS_NETWORK_STATE, getText(R.string.PermissionManager_accessNetworkState));
        denied = denied || getPermission(Manifest.permission.INTERNET, getText(R.string.PermissionManager_permissionText_internet));
        return !denied;
    }

    private boolean getPermission(final String permission, String permissionText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = mActivity.checkSelfPermission(permission);
            return permissionResult == PackageManager.PERMISSION_GRANTED;
            //if (permissionResult == PackageManager.PERMISSION_DENIED) {
            /*
             * 해당 권한이 거부된 적이 있는지 유무 판별 해야함.
             * 거부된 적이 있으면 true, 거부된 적이 없으면 false 리턴
             */
                /*
                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                    dialog.setTitle(getText(R.string.PermissionManager_acquirePermissionDialogTitle))
                            .setMessage(String.format(getText(R.string.PermissionManager_acquirePermissionDialogMsg), permissionText))
                            .setPositiveButton(getText(R.string.AlertDialog_button_yes), (dialog12, which) -> requestPermission(permission))
                            .setNegativeButton(getText(R.string.AlertDialog_button_no), (dialog1, which) -> mActivity.finish()).create().show();
                } else {
                    requestPermission(permission);
                }
                return true;

            }*/
        }
        return true;    // SDK M 이하에서는 설치 시 퍼미션 허용
    }

    private void requestPermissions(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(permissions, mActivity.getResources().getInteger(
                    R.integer.PERMISSIONS_REQUEST_CODE));
        }
    }

    private String getText(int id) {
        return mActivity.getResources().getString(id);
    }
}
