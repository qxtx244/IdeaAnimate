package com.qxtx.test;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MyService extends AccessibilityService {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo info = event.getSource();
        int num = info.getChildCount();

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String packageName = (String)event.getPackageName();
                Log.e("TAG", "packageName= " + packageName);

                try {
                    getMyAppActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        return super.getSystemService(name);
    }

    @Override
    public List<AccessibilityWindowInfo> getWindows() {
        return super.getWindows();
    }

    @Override
    protected boolean onGesture(int gestureId) {
        return super.onGesture(gestureId);
    }

    @Override
    public AccessibilityNodeInfo getRootInActiveWindow() {
        return super.getRootInActiveWindow();
    }

    private void getCustomActivity(String packageName) throws Exception {
        Context findContext = getApplicationContext().createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
        Class<Application> applicationClass = Application.class;
        Field mLoadedApkField = applicationClass.getDeclaredField("mLoadedApk");
        mLoadedApkField.setAccessible(true);
        Object mLoadedApk = mLoadedApkField.get((Application)findContext);
        Class<?> mLoadedApkClass = mLoadedApk.getClass();
        Field mActivityThreadField = mLoadedApkClass.getDeclaredField("mActivityThread");
        mActivityThreadField.setAccessible(true);
        Object mActivityThread = mActivityThreadField.get(mLoadedApk);
        Class<?> mActivityThreadClass = mActivityThread.getClass();
        Field mActivitiesField = mActivityThreadClass.getDeclaredField("mActivities");
        mActivitiesField.setAccessible(true);
        Object mActivities = mActivitiesField.get(mActivityThread);
        if (mActivities instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> arrayMap = (Map<Object, Object>) mActivities;
            for (Map.Entry<Object, Object> entry : arrayMap.entrySet()) {
                Object value = entry.getValue();
                Class<?> activityClientRecordClass = value.getClass();
                Field activityField = activityClientRecordClass.getDeclaredField("activity");
                activityField.setAccessible(true);
                Object o = activityField.get(value);
                Log.e("new_hijack","ACTIVITY"+ o.getClass().getName());
            }
        }
    }

    private void getMyAppActivity() throws Exception {
        Class<?> clazz = Class.forName("android.app.ActivityThread");
        Object cntActivity = clazz.getMethod("currentActivityThread").invoke(null);
        Field acts = clazz.getDeclaredField("mActivities");
        acts.setAccessible(true);
        Map<Object, Object> act = (Map<Object, Object>)acts.get(cntActivity);
        Log.e("getClassss", "得到act集合");
        for (Map.Entry<Object, Object> entry : act.entrySet()) {
            Object i = entry.getValue();
            Field isPause = i.getClass().getDeclaredField("paused");
            isPause.setAccessible(true);
            if (!isPause.getBoolean(i)) {
                Log.e("findActivity", "act= " + i);
                Field findActivity = i.getClass().getDeclaredField("activity");
                findActivity.setAccessible(true);
                Activity a = (Activity)findActivity.get(i);
                Log.e("checkACT", "act名称：" + a.getComponentName());
            }
        }
    }

    //获取应用的所有activity名称
    private void getAllActivity(Context application) {
        Class packageParserClass = null;
        try {
            packageParserClass = Class.forName("android.content.pm.PackageParser");
            Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);
            Object packageParser = packageParserClass.newInstance();
            Object packageObj=  parsePackageMethod.invoke(packageParser, new File(application.getApplicationInfo().sourceDir), PackageManager.GET_ACTIVITIES);
            Field receiverField=packageObj.getClass().getDeclaredField("activities");
            List receivers = (List) receiverField.get(packageObj);

            // 调用generateActivityInfo 方法, 把PackageParser.Activity 转换成generateActivityInfo方法
            Class<?> packageParser$ActivityClass = Class.forName("android.content.pm.PackageParser$Activity");

            Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
            Object defaltUserState= packageUserStateClass.newInstance();
            Method generateReceiverInfo = packageParserClass.getDeclaredMethod("generateActivityInfo",
                    packageParser$ActivityClass, int.class, packageUserStateClass, int.class);

            Class<?> userHandler = Class.forName("android.os.UserHandle");
            Method getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId");
            int userId = (int) getCallingUserIdMethod.invoke(null);

            for (Object activity : receivers) {
                ActivityInfo info = (ActivityInfo) generateReceiverInfo.invoke(packageParser,  activity,0, defaltUserState, userId);
                Log.w("see==", info.name);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {

    }
}
