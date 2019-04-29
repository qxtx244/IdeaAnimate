package com.qxtx.idea;

import com.qxtx.idea.animation.IdeaAnimationManager;
import com.qxtx.idea.animator.IdeaAnimatorManager;
import com.qxtx.idea.circularReveal.IdeaCircularRevealManager;
import com.qxtx.idea.frame.IdeaFrameManager;
import com.qxtx.idea.svg.IdeaSvgManager;
import com.qxtx.idea.view.IdeaSvgView;

/**
 * CreatedDate   2019/02/20 9:09.
 * Author  QXTX-GOSPELL
 *
 * Get anyone of animate simply. You can see all the animate tool class of current package
 *  but it maybe bad way when you need to use it's class member to do some animate
 *  because it can't to show any help for method in Android Studio's editor,
 *  also it doesn't matter if you remember the method name which you need to call.
 *  You can get some help while you use this class to take some animate from Android Studio's editor.
 *  Each fast-animate util class will name start with "Idea" and end with "Manager" include the custom view.
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
