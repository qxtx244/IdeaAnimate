package com.qxtx.test;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.qxtx.test.animate.Constant;
import com.qxtx.test.animate.IdeaAnimator;
import com.qxtx.test.animate.IdeaAnimatorManager;

import java.util.concurrent.ScheduledExecutorService;


//金山词霸翻译测试：http://fy.iciba.com/ajax.php?a=fy&f=auto&t=auto&w=hello%20world

public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private ScheduledExecutorService executorService;
    private Intent intent, intent1;
    private MyButton btn;
    private IdeaAnimator idea;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (MyButton)findViewById(R.id.send);
        ImageView img = (ImageView)findViewById(R.id.image);

        Path path = setPath();
        idea = new IdeaAnimator("test", img)
                .setDuration(2000)
                .setPropertyName("alpha")
                .setFloatValues(0f, 1f)
                .setRepeat(Constant.REPEAT_INFINITE, Constant.REPEAT_REVERSE);
        idea.start();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(btn, "x", "y", path);
    }

    private Path setPath() {
        Path path = new Path();
        int[] pos = new int[2];
        btn.getLocationOnScreen(pos);
        path.moveTo(pos[0], pos[1]);
        path.lineTo(60f, 600f);
        path.lineTo(1200f, 60f);
        return path;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}