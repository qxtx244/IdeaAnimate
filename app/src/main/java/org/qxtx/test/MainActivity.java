package org.qxtx.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.qxtx.idea.animate.IdeaManager;
import org.qxtx.idea.animate.IdeaUtil;
import org.qxtx.idea.animate.animation.IdeaAnimationManager;
import org.qxtx.idea.animate.vector.IdeaSvgManager;
import org.qxtx.idea.view.IdeaSvgView;

public class MainActivity extends Activity {
    private Button btn;
    private ImageView image;
    private IdeaSvgView ideaVector;
    int count = 0;
    View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.send);
        image = (ImageView) findViewById(R.id.bgIcon);
        ImageView img = (ImageView)findViewById(R.id.image);
        TextView text = (TextView)findViewById(R.id.text);
        ideaVector = (IdeaSvgView) findViewById(R.id.idea);
        view = findViewById(R.id.main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        btn.setOnClickListener(v -> {
//            IdeaAnimatorManager.rolling(btn, IdeaUtil.RIGHT, 1).setDuration(1000).start();
            if (count > 0) {
                IdeaAnimationManager.doorClose(btn, IdeaUtil.RIGHT);

//                ideaVector.startAnimation("M20.5,9.5 c-1.955,0,-3.83,1.268,-4.5,3 " +
//                        "c-0.67,-1.732,-2.547,-3,-4.5,-3 C8.957,9.5,7,11.432,7,14 " +
//                        "c0,3.53,3.793,6.257,9,11.5 c5.207,-5.242,9,-7.97,9,-11.5 " +
//                        "C25,11.432,23.043,9.5,20.5,9.5z");
//                IdeaSvgManager.scale(ideaVector, 5f);
//                ideaVector.setLineColor(Color.RED).setStrokeWidth(6f);
//                ideaVector.startTrimAnimation(60);
                ideaVector.setDuration(1000).startTrimAnimation(false);

                count = 0;
            } else {
                IdeaAnimationManager.doorOpen(btn, IdeaUtil.RIGHT);
//                ideaVector.show("M205,95 c-19.55,0,-38.3,12.68,-45,30 " +
//                        "c-6.7,-17.32,-25.47,-30,-45,-30 C89.57,95,70,114.32,70,140 " +
//                        "c0,35.3,37.93,62.57,90,115 c52.07,-52.42,90,-79.7,90,-115 " +
//                        "C250,114.32,230.43,95,205,95z", false);
                ideaVector.setFillColor(Color.RED);
                ideaVector.setLineColor(Color.WHITE);
                ideaVector.show(IdeaUtil.SVG_STAR, true);
                IdeaSvgManager.scale(ideaVector, 4f);
//                IdeaManager.circularReveal.start(ideaVector);
                count++;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}