package com.hucanhui.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;


import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


/**
 * 键盘弹出/收起监听
 * Created by hucanhui on 18/11/14.
 */
public class KeyboardVisibilityObserver {

    private final static int KEYBOARD_VISIBLE_THRESHOLD_DP = 100;
    private static volatile KeyboardVisibilityObserver mInstance;

    public static KeyboardVisibilityObserver getInstance() {
        if (mInstance == null) {
            synchronized (KeyboardVisibilityObserver.class) {
                if (mInstance == null) {
                    mInstance = new KeyboardVisibilityObserver();
                }
            }
        }
        return mInstance;
    }


    private KeyboardVisibilityObserver(){

    }

    public void init(final Application application){
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            private boolean wasOpened = false;
            private Map<String, ViewTreeObserver.OnGlobalLayoutListener> layoutListenerMap = new HashMap<>();
            @Override
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                try {
                    final View activityRoot = getActivityRoot(activity);
                    if (activityRoot == null) {
                        return;
                    }
                    ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

                        private final Rect r = new Rect();
                        private final int visibleThreshold = Math.round(dp2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));

                        @Override
                        public void onGlobalLayout() {
                            activityRoot.getWindowVisibleDisplayFrame(r);
                            int heightDiff = activityRoot.getRootView().getHeight() - r.height();
                            boolean isOpen = heightDiff > visibleThreshold;

                            if (isOpen == wasOpened) {
                                // keyboard state has not changed
                                return;
                            }
                            wasOpened = isOpen;
                            EventBus.getDefault().post(new KeyboardVisibleEvent(isOpen));
                        }
                    };

                    activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
                    layoutListenerMap.put(activity.getClass().getName(), layoutListener);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                try {
                    ViewTreeObserver.OnGlobalLayoutListener layoutListener = layoutListenerMap.get(activity.getClass().getName());
                    if (layoutListener != null){
                        if (wasOpened){
                            EventBus.getDefault().post(new KeyboardVisibleEvent(!wasOpened));
                        }
                        View activityRoot = getActivityRoot(activity);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            activityRoot.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(layoutListener);
                        } else {
                            activityRoot.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(layoutListener);
                        }
                        layoutListenerMap.remove(activity.getClass().getName());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Determine if keyboard is visible
     *
     * @param activity Activity
     * @return Whether keyboard is visible or not
     */
    public boolean isKeyboardVisible(Activity activity) {
        Rect r = new Rect();

        View activityRoot = getActivityRoot(activity);
        int visibleThreshold =
                Math.round(dp2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));

        activityRoot.getWindowVisibleDisplayFrame(r);

        int heightDiff = activityRoot.getRootView().getHeight() - r.height();

        return heightDiff > visibleThreshold;
    }

    private View getActivityRoot(Activity activity) {
        return activity.getWindow().getDecorView();
    }


    /**
     *
     * @param context
     * @param dipValue
     * @return
     */
    private int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
