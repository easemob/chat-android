package com.hyphenate.easeim.common.manager;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.hyphenate.easeui.utils.RomUtils;
import com.hyphenate.util.EMLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 用于Float window的权限检查及权限请求
 */
public class FloatWindowManager {

    private static final String TAG = FloatWindowManager.class.getSimpleName();

    public static boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return checkOps(context);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean checkOps(Context context) {
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject1);
            return m == AppOpsManager.MODE_ALLOWED || !RomUtils.isDomesticSpecialRom();
        } catch (Exception ignore) {
        }
        return false;
    }

    public static boolean tryJumpToPermissionPage(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            switch (RomUtils.getRomName()) {
                case RomUtils.ROM_MIUI:
                    return applyMiuiPermission(context);
                case RomUtils.ROM_EMUI:
                    return applyHuaweiPermission(context);
                case RomUtils.ROM_VIVO:
                    return applyVivoPermission(context);
                case RomUtils.ROM_OPPO:
                    return applyOppoPermission(context);
                case RomUtils.ROM_QIKU:
                    return apply360Permission(context);
                case RomUtils.ROM_SMARTISAN:
                    return applySmartisanPermission(context);
                case RomUtils.ROM_COOLPAD:
                    return applyCoolpadPermission(context);
                case RomUtils.ROM_ZTE:
                    return applyZTEPermission(context);
                case RomUtils.ROM_LENOVO:
                    return applyLenovoPermission(context);
                case RomUtils.ROM_LETV:
                    return applyLetvPermission(context);
                default:
                    return true;
            }
        } else {
            if (RomUtils.isMeizuRom()) {
                return getAppDetailSettingIntent(context);
            } else if (RomUtils.isVivoRom()) {
                return applyVivoPermission(context);
            } else if (RomUtils.isMiuiRom()) {
                return applyMiuiPermission(context) || getAppDetailSettingIntent(context);
            } else {
                return applyCommonPermission(context);
            }
        }
    }

    private static boolean startActivitySafely(Intent intent, Context context) {
        try {
            if (isIntentAvailable(intent, context)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            EMLog.e(TAG, "启动Activity失败！");
            return false;
        }
    }

    public static boolean isIntentAvailable(Intent intent, Context context) {
        return intent != null && context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    private static boolean applyCommonPermission(Context context) {
        try {
            Class clazz = Settings.class;
            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
            Intent intent = new Intent(field.get(null).toString());
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            startActivitySafely(intent, context);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean applyCoolpadPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.yulong.android.seccenter",
                "com.yulong.android.seccenter.dataprotection.ui.AppListActivity");
        return startActivitySafely(intent, context);
    }

    private static boolean applyLenovoPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.lenovo.safecenter",
                "com.lenovo.safecenter.MainTab.LeSafeMainActivity");
        return startActivitySafely(intent, context);
    }

    private static boolean applyZTEPermission(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.zte.heartyservice.intent.action.startActivity.PERMISSION_SCANNER");
        return startActivitySafely(intent, context);
    }

    private static boolean applyLetvPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.letv.android.letvsafe",
                "com.letv.android.letvsafe.AppActivity");
        return startActivitySafely(intent, context);
    }

    private static boolean applyVivoPermission(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        intent.setAction("com.vivo.permissionmanager");
        intent.setClassName("com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
        ComponentName componentName1 = intent.resolveActivity(context.getPackageManager());
        if (componentName1 != null) {
            return startActivitySafely(intent, context);
        }

        intent.setAction("com.iqoo.secure");
        intent.setClassName("com.iqoo.secure",
                "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
        ComponentName componentName2 = intent.resolveActivity(context.getPackageManager());
        if (componentName2 != null) {
            return startActivitySafely(intent, context);
        }

        intent.setAction("com.iqoo.secure");
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainActivity");
        ComponentName componentName3 = intent.resolveActivity(context.getPackageManager());
        if (componentName3 != null) {
            return startActivitySafely(intent, context);
        }

        return startActivitySafely(intent, context);
    }

    private static boolean applyOppoPermission(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setAction("com.oppo.safe");
        intent.setClassName("com.oppo.safe",
                "com.oppo.safe.permission.PermissionAppListActivity");
        if (!startActivitySafely(intent, context)) {
            intent.setAction("com.color.safecenter");
            intent.setClassName("com.color.safecenter",
                    "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
            if (!startActivitySafely(intent, context)) {
                intent.setAction("com.coloros.safecenter");
                intent.setClassName("com.coloros.safecenter",
                        "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
                return startActivitySafely(intent, context);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private static boolean apply360Permission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings",
                "com.android.settings.Settings$OverlaySettingsActivity");
        if (!startActivitySafely(intent, context)) {
            intent.setClassName("com.qihoo360.mobilesafe",
                    "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            return startActivitySafely(intent, context);
        } else {
            return true;
        }
    }

    private static boolean applyMiuiPermission(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.APP_PERM_EDITOR");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("extra_pkgname", context.getPackageName());
        return startActivitySafely(intent, context);
    }

    public static boolean getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return startActivitySafely(localIntent, context);
    }

    private static boolean applyMeizuPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe",
                "com.meizu.safe.security.AppSecActivity");
        intent.putExtra("packageName", context.getPackageName());
        return startActivitySafely(intent, context);
    }

    private static boolean applyHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
            intent.setComponent(comp);
            if (!startActivitySafely(intent, context)) {
                comp = new ComponentName("com.huawei.systemmanager",
                        "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
                intent.setComponent(comp);
                context.startActivity(intent);
                return true;
            } else {
                return true;
            }
        } catch (SecurityException e) {
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName comp = new ComponentName("com.huawei.systemmanager",
                        "com.huawei.permissionmanager.ui.MainActivity");
                intent.setComponent(comp);
                context.startActivity(intent);
                return true;
            } catch (Exception e1) {
                EMLog.e(TAG, "Huawei跳转失败" + e1);
                return getAppDetailSettingIntent(context);
            }
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName comp = new ComponentName("com.Android.settings",
                        "com.android.settings.permission.TabItem");
                intent.setComponent(comp);
                context.startActivity(intent);
                return true;
            } catch (Exception e2) {
                EMLog.e(TAG, "Huawei跳转失败" + e);
                return getAppDetailSettingIntent(context);
            }
        } catch (Exception e) {
            return getAppDetailSettingIntent(context);
        }
    }

    private static boolean applySmartisanPermission(Context context) {
        Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS_NEW");
        intent.setClassName("com.smartisanos.security",
                "com.smartisanos.security.SwitchedPermissions");
        intent.putExtra("index", 17); //有版本差异,不一定定位正确
        if (startActivitySafely(intent, context)) {
            return true;
        } else {
            intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS");
            intent.setClassName("com.smartisanos.security",
                    "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("permission", new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});

            return startActivitySafely(intent, context);
        }
    }
}

