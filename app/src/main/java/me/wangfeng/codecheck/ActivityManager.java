package me.wangfeng.codecheck;

import me.wangfeng.annotation.Singleton;

/**
 * Activity管理类
 * 用于测试{@link me.wangfeng.annotation.Singleton Singleton}注解
 *
 * Created by wangfeng on 17/2/18.
 */

@Singleton
public class ActivityManager {

    private static ActivityManager sInstance;

//    public ActivityManager() {
//    }

    private ActivityManager() {
    }

//    private ActivityManager(Context context) {
//    }

    public static ActivityManager getInstance() {
        if (sInstance == null) {
            sInstance = new ActivityManager();
        }
        return sInstance;
    }
}
