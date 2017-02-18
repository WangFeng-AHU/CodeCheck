package me.wangfeng.annotation.process;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * 辅助工具类
 *
 * Created by wangfeng on 17/2/18.
 */

class CheckerHelper {

    static boolean isClassElement(Element element) {
        // 注：不能用element.isClass()方法判断，因为枚举类型该方法也会返回true
        return element.getKind() == ElementKind.CLASS;
    }
}
