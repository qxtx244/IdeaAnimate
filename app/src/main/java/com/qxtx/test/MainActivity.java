package com.qxtx.test;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qxtx.test.animate.IdeaAnimation;
import com.qxtx.test.animate.IdeaAnimationManager;
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
    private LinearLayout layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.send);
        layout = (LinearLayout) findViewById(R.id.btnLayout);
        ImageView img = (ImageView)findViewById(R.id.image);
        TextView text = (TextView)findViewById(R.id.text);
        Path path = setPath();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IdeaAnimationManager.translate(btn, 500f, 700f).start();
                IdeaAnimatorManager.doorOpen(btn, IdeaUtil.RIGHT).start();
            }
        });
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