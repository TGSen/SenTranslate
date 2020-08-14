package permission;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;


import com.example.libbase.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

/**
 * 请求框架的封装
 */
public class SPermission {

    private static final int REQUEST_CODE_SETTING = 0;
    public static final String[] CAMERA = new String[]{Permission.CAMERA,
            Permission.WRITE_EXTERNAL_STORAGE};


    private SPermission() {
    }

    private static volatile SPermission permission;


    public static SPermission getInstance() {
        if (permission == null) {
            synchronized (SPermission.class) {
                if (permission == null) {
                    permission = new SPermission();
                }
            }
        }
        return permission;
    }

    public void hasPermissions(Context context, String[] permiss, OnPermissionListener listener) {
        boolean isHas = AndPermission.hasPermissions(context, permiss);
        if (listener != null) {
            if (isHas) {
                listener.onSuccess();
                return;
            }
        }

        getPermission(context, permiss, listener);
    }

    public void getPermission(Context context, String[] permiss, OnPermissionListener listener) {
        AndPermission.with(context)
                .runtime()
                .permission(permiss)
                .rationale(new RuntimeRationale())
                .onGranted(permissions -> {
                    if (listener != null) listener.onSuccess();
                })
                .onDenied(permissions -> {
                    if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                        showSettingDialog(context, permissions);
                    }
                })
                .start();
    }

    public interface OnPermissionListener {
        void onSuccess();

        void onFaild();
    }

    /**
     * Display setting dialog.
     */
    public void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed,
                TextUtils.join("\n", permissionNames));
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle("权限申请")
                .setMessage(message)
                .setPositiveButton("申请", (dialog, which) -> setPermission(context))
                .setNegativeButton("拒绝", (dialog, which) -> {
                }).show();
    }

    /**
     * * Set permissions.
     */
    private void setPermission(Context context) {
        AndPermission.with(context).runtime().setting().start(REQUEST_CODE_SETTING);
    }

    /**
     *
     */
    public class RuntimeRationale implements Rationale<List<String>> {
        @Override
        public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
            List<String> permissionNames = Permission.transformText(context, permissions);
            String message = context.getString(R.string.message_permission_rationale,
                    TextUtils.join("\n", permissionNames));
            new AlertDialog.Builder(context).setCancelable(false)
                    .setTitle("Tips")
                    .setMessage(message)
                    .setPositiveButton("再次申请", (dialog, which) -> executor.execute())
                    .setNegativeButton("取消", (dialog, which) -> executor.cancel())
                    .show();
        }
    }

}
