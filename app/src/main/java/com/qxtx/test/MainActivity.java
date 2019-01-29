package com.qxtx.test;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qxtx.test.animate.IdeaAnimationManager;
import com.qxtx.test.animate.IdeaAnimator;
import com.qxtx.test.animate.IdeaAnimatorManager;
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
    int count = 0;
    int[] pos = new int[2];
    View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.send);
        ImageView img = (ImageView)findViewById(R.id.image);
        TextView text = (TextView)findViewById(R.id.text);
        Path path = setPath();
        view = findViewById(R.id.main);
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        btn.getLocationInWindow(pos);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View[] clone = new View[] {
                inflater.inflate(R.layout.olbbtn, null),
                inflater.inflate(R.layout.olbbtn, null),
                inflater.inflate(R.layout.olbbtn, null),
                inflater.inflate(R.layout.olbbtn, null),
                inflater.inflate(R.layout.olbbtn, null),
                inflater.inflate(R.layout.olbbtn, null)
        };
        for (int i = 0; i < clone.length; i++) {
            ((ViewGroup)view).addView(clone[i]);
            clone[i].getLayoutParams().width = btn.getLayoutParams().width;
            clone[i].getLayoutParams().height = btn.getLayoutParams().height;
            clone[i].setX(pos[0] + btn.getWidth() * (i + 1) + 25 * (i + 1));
            clone[i].setY(pos[1]);
            clone[i].setClickable(false);
        }

        btn.setOnClickListener(v -> {
//            IdeaAnimatorManager.rolling(btn, IdeaUtil.RIGHT, 1).setDuration(1000).start();
            float[][] pointers = new float[][] {
                    {50f, 50f},
                    {150f, 130f},
                    {400f, 200f},
                    {10f, 20f}
            };
            IdeaAnimationManager.rotateY(btn, 360f, true);

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}