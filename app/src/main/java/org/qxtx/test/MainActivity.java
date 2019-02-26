package org.qxtx.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.qxtx.idea.animate.IdeaUtil;
import org.qxtx.idea.animate.animation.IdeaAnimationManager;
import org.qxtx.idea.animate.vector.IdeaSvgManager;
import org.qxtx.idea.animate.view.IdeaSvgView;

public class MainActivity extends Activity {
    private Button btn;
    private ImageView image;
    private IdeaSvgView ideaVector;
    int count = 0;
    View view;
    int num = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.send);
        TextView text = (TextView)findViewById(R.id.text);
        ideaVector = (IdeaSvgView) findViewById(R.id.idea);
        view = findViewById(R.id.main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        btn.setOnClickListener(v -> {
            if (count > 0) {
                IdeaAnimationManager.doorClose(btn, IdeaUtil.RIGHT);

//                IdeaSvgManager.showWithAnim(ideaVector, IdeaUtil.SVG_HEART_2_BRIDE);
//                IdeaSvgManager.trimDstAnim(ideaVector, 500);
//                IdeaSvgManager.trimFullyAnim(ideaVector, false);
                new Thread(() -> {
                    for (int i = 0; i < 9; i++) {
                        num = i;
                        ideaVector.post(() -> {
                            IdeaSvgManager.numAnim(ideaVector, num);
                        });
                        SystemClock.sleep(ideaVector.getDuration() * 2);
                    }
                }).start();

                count = 0;
            } else {
                IdeaAnimationManager.doorOpen(btn, IdeaUtil.RIGHT);
                ideaVector.setFillColor(Color.WHITE, Color.RED)
                        .setLineColor(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.WHITE, Color.CYAN, Color.MAGENTA);

//                boolean isValid = IdeaSvgManager.checkSvgData(IdeaUtil.SVG_NUMBER_8);
//                IdeaSvgManager.numAnim(ideaVector, 2);
                ideaVector.showSvg(IdeaUtil.SVG_NUMBER_8, IdeaUtil.PAINT_FILL);
//                ideaVector.scale(5f);
                count++;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}