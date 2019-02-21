package org.qxtx.idea.animate;

import org.qxtx.idea.animate.animation.IdeaAnimationManager;
import org.qxtx.idea.animate.animator.IdeaAnimatorManager;
import org.qxtx.idea.animate.circularReveal.IdeaCircularRevealManager;
import org.qxtx.idea.animate.frame.IdeaFrameManager;
import org.qxtx.idea.animate.vector.IdeaSvgManager;
import org.qxtx.idea.view.IdeaSvgView;

/**
 * Get anyone of animate simply.
 * @CreateDate 2019/02/20 9:09.
 * @Author QXTX-GOSPELL
 */

public class IdeaManager {
    public static final String TAG = "IdeaManager";

    private static final int DURATION = 500;

    public static IdeaAnimatorManager animator;
    public static IdeaAnimationManager animation;
    public static IdeaCircularRevealManager circularReveal;
    public static IdeaFrameManager frame;
    public static IdeaSvgManager svg;
    public static IdeaSvgView svgView;
    public static IdeaUtil util;
}
