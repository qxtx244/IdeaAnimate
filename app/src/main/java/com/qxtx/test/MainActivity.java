package com.qxtx.test;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qxtx.test.animate.IdeaAnimator;
import com.qxtx.test.animate.IdeaAnimatorManager;
import com.qxtx.test.animate.IdeaAnimatorSet;
import com.qxtx.test.animate.IdeaUtil;

import java.util.concurrent.ScheduledExecutorService;


//金山词霸翻译测试：http://fy.iciba.com/ajax.php?a=fy&f=auto&t=auto&w=hello%20world

public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private ScheduledExecutorService executorService;
    private Intent intent, intent1;
    private Button btn;
    private IdeaAnimator idea;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.send);
        ImageView img = (ImageView)findViewById(R.id.image);
        TextView text = (TextView)findViewById(R.id.text);
        Path path = setPath();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdeaAnimatorManager.bounce(btn, 30f).start();
            }
        });
//        new Handler().postDelayed(() -> {
//            IdeaAnimator idea2 = IdeaAnimatorManager.shake(btn, 1000, IdeaUtil.HORIZONTAL, 2);
//            idea2.start();
//        }, 2000);

//        AnimationDrawable drawable = new AnimationDrawable();
//        drawable.addFrame(getResources().getDrawable(R.mipmap.alias), 100);
//        drawable.addFrame(getResources().getDrawable(R.mipmap.alias2), 100);
//        drawable.addFrame(getResources().getDrawable(R.mipmap.gohome), 100);
//        drawable.addFrame(getResources().getDrawable(R.mipmap.ic_launcher), 100);
//        img.setImageDrawable(drawable);
//        ((AnimationDrawable)img.getDrawable()).start();

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