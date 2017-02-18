package me.wangfeng.annotation;

/**
 * 单例类注解
 *
 * Created by wangfeng on 17/2/16.
 */

public @interface Singleton {

    /**
     * 获取单例类对象的静态方法名
     * @return 方法名
     */
    String value() default "getInstance";
}
