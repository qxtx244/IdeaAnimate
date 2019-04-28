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

import com.qxtx.idea.animate.IdeaUtil;
import com.qxtx.idea.animate.animation.IdeaAnimationManager;
import com.qxtx.idea.animate.svg.IdeaSvgManager;
import com.qxtx.idea.animate.view.IdeaSvgView;

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

        String SVG_GOS = "M326 744 c-167 -88 -253 -291 -197 -471 c34 -113 136 -222 240 -258 c68 -23 217 -20 286 6 c75 28 165 112 205 191 c28 57 30 68 30 175 c0 102 -3 120 -25 168 c-36 75 -103 146 -173 183 c-58 31 -63 32 -188 32 c-118 -1 -133 -3 -178 -26z "
                 + "M368 755 c-93 -34 -173 -111 -222 -215 c-28 -58 -31 -75 -31 -156 c0 -84 2 -95 38 -166 c42 -85 102 -147 183 -188 c44 -23 61 -25 169 -25 c107 0 125 3 170 24 c68 33 155 123 188 193 c24 50 27 69 27 162 c0 90 -4 113 -24 161 c-34 76 -115 159 -192 195 c-57 28 -70 30 -165 29 c-62 0 -119 -6 -141 -14z "
                 + "m207 -36 c-123 -18 -224 -90 -280 -199 c-29 -58 -38 -98 -37 -179 c1 -110 52 -211 135 -273 c32 -23 36 -29 17 -23 c-73 21 -159 107 -184 183 c-64 195 12 401 177 478 c44 20 66 24 142 23 z "
                 + "m-288 -65 c-51 -50 -89 -122 -106 -202 c-18 -86 -13 -137 23 -247 c3 -11 -6 5 -21 35 c-24 48 -27 67 -28 145 c0 82 3 96 33 157 c21 43 51 83 83 112 c66 59 76 59 16 0z "
                 + "m421 10 c39 -16 87 -56 104 -87 c8 -16 6 -16 -20 3 c-116 86 -316 3 -368 -153 c-22 -65 -15 -177 14 -232 c39 -77 116 -134 180 -136 c24 0 25 -1 7 -9 c-68 -29 -188 9 -245 78 c-146 178 -74 462 137 538 c52 18 146 17 191 -2z "
                 + "m4 -151 c37 -33 19 -72 -49 -107 c-18 -9 -40 -27 -49 -41 c-20 -31 -20 -109 1 -141 c35 -54 116 -80 168 -53 c20 11 20 10 -3 -16 c-39 -43 -77 -58 -131 -53 c-154 15 -227 246 -116 367 c54 59 139 80 179 44z "
                 + "m144 -198 c-14 -53 -91 -86 -124 -53 c-23 23 -12 45 48 98 c41 35 63 62 68 84 c5 24 8 16 10 -39 c2 -38 1 -79 -2 -90z "
                 + "M390 761 c-54 -17 -115 -55 -162 -102 c-172 -172 -144 -459 60 -597 c79 -54 120 -65 238 -60 c80 2 108 8 146 27 c79 41 143 105 182 183 c34 67 36 76 36 173 c0 97 -2 106 -35 172 c-42 84 -102 143 -188 184 c-56 26 -72 29 -157 28 c-52 0 -106 -4 -120 -8z ";

        btn.setOnClickListener(v -> {
            if (count > 0) {
                IdeaAnimationManager.doorClose(btn, IdeaUtil.RIGHT);

//                IdeaSvgManager.showWithAnim(ideaVector, IdeaUtil.SVG_HEART_2_BRIDE);
//                IdeaSvgManager.trimDstAnim(ideaVector, 500);
//                IdeaSvgManager.trimFullyAnim(ideaVector, false);
                new Thread(() -> {
                    for (int i = 0; i < 10; i++) {
                        num = i;
                        ideaVector.post(() -> {
                            IdeaSvgManager.numAnim(ideaVector, num);
                            if (num > 3) {
                                ideaVector.stopAnimateSafely();
                            }
                        });
                        SystemClock.sleep(ideaVector.getDuration() * 3);
                    }
                }).start();

                count = 0;
            } else {
                IdeaAnimationManager.doorOpen(btn, IdeaUtil.RIGHT);

                ideaVector.setDuration(500);
                ideaVector.setFillColor(Color.WHITE, Color.RED, Color.RED, Color.RED, Color.RED, Color.RED)
                        .setLineColor(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.WHITE, Color.CYAN, Color.MAGENTA);

//                boolean isValid = IdeaSvgManager.checkSvgData(IdeaUtil.SVG_NUMBER_8);
//                IdeaSvgManager.numAnim(ideaVector, 2);
                ideaVector.showSvg(IdeaUtil.SVG_NUMBER_8, IdeaUtil.PAINT_FILL);
                ideaVector.scale(2f);
                count++;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}