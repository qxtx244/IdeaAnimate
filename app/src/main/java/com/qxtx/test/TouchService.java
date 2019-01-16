package com.qxtx.test;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class TouchService extends Service {
    private TextView viewFullTouch;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("tag", "服务启动");
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            layoutParams.format = PixelFormat.TRANSPARENT; //可以让悬浮窗显示为透明

            layoutParams.width = 500;
            layoutParams.height = 100;
            layoutParams.gravity = Gravity.TOP;

            viewFullTouch = new TextView(getApplicationContext());
            viewFullTouch.setText("未知应用");
            viewFullTouch.setGravity(Gravity.CENTER);
            viewFullTouch.setBackgroundColor(Color.parseColor("#00ff00"));
//            viewFullTouch.setOnTouchListener(new FingerCheck(2));
            windowManager.addView(viewFullTouch, layoutParams);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 1、当多一个手指按下，就判断是否为双指，如果为双指就立刻记录相对坐标和方向，如果为三指以上就放弃手势处理
     * 2、只要有一个手指抬起，就开始处理手势结果
     * 3、只要方向改变，直接放弃手势处理
     */
    private final class FingerCheck implements View.OnTouchListener {
        private final String TAG = "finger";
        private int startCheckTime = 0;

        private final int ORI_AUTO = -1;
        private final int ORI_ILLEGAL = -2;

        private final int fingerNum;

        /**
         * 两个手指的滑动起始坐标
         */
        private ArrayList<float[]> oldPointer;

        /**
         * 两个手指的当前有效移动距离
         */
        private ArrayList<Float> len;

        private int resultOri = ORI_AUTO;

        public FingerCheck(int fingerNum) {
            this.fingerNum = fingerNum;
            oldPointer = new ArrayList<>();
            len = new ArrayList<>();
            for (int i = 0; i < fingerNum; i++) {
                oldPointer.add(new float[] {0f, 0f});
                len.add(0f);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int pointers = event.getPointerCount();
            Log.e(TAG, "触摸");

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.e("down", "手指被按下了");
                    resultOri = ORI_AUTO;
                    return false;
                case MotionEvent.ACTION_POINTER_DOWN:
                    Log.e("pointer_down", "更多的手指被按下了");
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if (startCheckTime < 10) { //防止起始手指抖动
                        startCheckTime++;
                        return false;
                    }

                    if (pointers != fingerNum) {
                        Log.e("move", "触摸点数不是指定数目，放弃本次手势操作：pointers= " + pointers);
                        return false;
                    }

                    if (startCheckTime == 10) {
                        startCheckTime++;
                        for (int i = 0; i < fingerNum; i++) {
                            oldPointer.get(i)[0] = event.getX(i);
                            oldPointer.get(i)[1] = event.getY(i);
                            Log.e("down", "手指" + i + "起始坐标：" + oldPointer.get(i)[0] + "&" + oldPointer.get(i)[1]);
                        }
                        return true;
                    }

                    //得到本次移动的全局手势方向
                    int cntOri = checkFingerMove(event, 0);
                    for (int i = 1; i < fingerNum; i++) {
                        int oneori = checkFingerMove(event, i);
                        if (oneori != cntOri) {
                            Log.e("ori", "各手指的手势不同向，放弃本次手势操作: pointers= " + pointers + ", oneori= " + oneori + ", cntOri= " + cntOri);
                            endFingerCheck();
                            return false;
                        }
                    }

                    //比较两次移动的全局手势一致性
                    if (resultOri == ORI_AUTO) {
                        Log.e("ori", "第一次移动" + cntOri);
                        resultOri = cntOri;
                    } else if (resultOri != cntOri) {
                        Log.e("ori", "前后两次移动的全局手势不同向，放弃本次手势操作: resultOri= " + resultOri + ", cntOri= " + cntOri);
                        endFingerCheck();
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP: //只要有手指抬起，就结束手势
                    if (startCheckTime < 10) { //手势起始段不做处理，防止手指抖动
                        return false;
                    }

                    if (resultOri == ORI_ILLEGAL) {
                        Log.e("up", "手势处理已被放弃，不做手势处理");
                        return false;
                    }

                    Log.e("TAG", "或许需要开始执行手势操作");
                    DisplayMetrics metrics = new DisplayMetrics();
                    ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
                    float minLenToExecute = Math.min(metrics.widthPixels, metrics.heightPixels) / 30f;

                    float maxPointerLen = -1;
                    for (int i = 0; i < fingerNum; i++) {
                        float oneLen = len.get(i);
                        if (oneLen > maxPointerLen) {
                            maxPointerLen = oneLen;
                        }
                    }

                    Log.e("TAG", "本次手势距离：" + maxPointerLen + ", 执行手势的所需最短距离：" + minLenToExecute +
                            "，此时方向= " + resultOri);
                    boolean moveSuccess = maxPointerLen > minLenToExecute;
                    if (moveSuccess) {
                        String logcat;
                        Intent intent;
                        switch (resultOri) {
                            case 2:
                                logcat = "向上启动微信";
                                intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                                break;
                            case 4:
                                logcat = "向左启动企业微信";
                                intent = getPackageManager().getLaunchIntentForPackage("com.tencent.wework");
                                break;
                            case 6:
                                logcat = "向右启动via浏览器";
                                intent = getPackageManager().getLaunchIntentForPackage("mark.via");
                                break;
                            case 8:
                                logcat = "向下启动QQ";
                                intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq");
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), "没什么好启动的", Toast.LENGTH_SHORT).show();
                                return false;
                        }

                        Toast.makeText(getApplicationContext(), logcat, Toast.LENGTH_SHORT).show();
                        Log.e("result", logcat);
                        startActivity(intent);

                        endFingerCheck();
                        return true;
                    }
                    endFingerCheck();
                    return false;
                case MotionEvent.ACTION_UP:
                    endFingerCheck();
                    return true;
            }

            viewFullTouch.setEnabled(false);
            v.getRootView().performClick();
            viewFullTouch.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewFullTouch.setEnabled(true);
                }
            }, 100);
            return false;
        }

        //返回指定手指的当前手势方向
        private int checkFingerMove(MotionEvent event, int index) {
            float[] oldPos = oldPointer.get(index);
            float[] cntPos = new float[2];
            float cntK;
            int cntOri;

            cntPos[0] = event.getX(index);
            cntPos[1] = event.getY(index);

            float deltaX = cntPos[0] - oldPos[0];
            float deltaY = cntPos[1] - oldPos[1];

            /*
             * 向右划：deltaX > 0，|tanK| < 1
             * 向左划：deltaX < 0，|tanK| < 1
             * 向上划：deltaY < 0，|tanK| > 1 或者垂直
             * 向下划：deltaY > 0，|tanK| > 1 或者垂直
             */
            if (deltaX == 0f) { //垂直
                cntOri = deltaY > 0f ? 8 : 2;
                if (len.get(index) > 0f && deltaY == 0f) {
                    cntOri = resultOri;
                }
                Log.e("vertical", index + "垂直, cntOri= " + cntOri + ", deltaY= " + deltaY);
            } else { //存在斜率
                cntK = Math.abs(deltaY / deltaX);
                if (cntK > 1.0) {
                    cntOri = deltaY > 0f ? 8 : 2;
                } else {
                    /* 此时包含tank < 1和tank = 1（45°方向）时，统一做左右划动处理 */
                    cntOri = deltaX > 0f ? 6 : 4;
                }

                Log.e("k", index + "斜率= " + cntOri + ", deltaX= " + deltaX + ", deltaY= " + deltaY);
            }

            Log.e("pos", "比较：oldPosY= " + oldPointer.get(index)[1] + ", cntPosY= " + cntPos[1] + ", len= " + len.get(index));
            len.set(index, len.get(index) + (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY));
            oldPointer.get(index)[0] = cntPos[0];
            oldPointer.get(index)[1] = cntPos[1];

            return cntOri;
        }

        private void endFingerCheck() {
            for (int i = 0; i < fingerNum; i++) {
                oldPointer.get(i)[0] = 0f;
                oldPointer.get(i)[1] = 0f;
                len.set(i, 0f);
            }

            startCheckTime = 0;
            resultOri = ORI_ILLEGAL;
        }
    }
}
