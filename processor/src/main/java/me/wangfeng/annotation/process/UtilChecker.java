package me.wangfeng.annotation.process;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import me.wangfeng.annotation.Util;

/**
 * {@link Util Util}注解的类型的合法性检查
 *
 * Created by wangfeng on 17/2/16.
 */

class UtilChecker {
    private static final String UTIL_TAG = Util.class.getCanonicalName();

    static void check(Set<? extends Element> annotatedElements, Messager messager) {
        if (annotatedElements.isEmpty()) {
            return;
        }

        for (Element annotatedElement : annotatedElements) {
            if (!CheckerHelper.isClassElement(annotatedElement)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s注解的不是工具类：不是合法的类声明",
                                UTIL_TAG),
                        annotatedElement);
                continue;
            }
            TypeElement classElement = (TypeElement) annotatedElement;
            Element enclosingElement = classElement.getEnclosingElement();
            if (enclosingElement.getKind() != ElementKind.PACKAGE) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s注解的工具类不规范：工具类不能为嵌套类",
                                UTIL_TAG),
                        classElement);
            }
            checkConstructor(classElement, messager);
            checkMethod(classElement, messager);
            checkField(classElement, messager);
        }
    }

    private static void checkConstructor(TypeElement classElement, Messager messager) {
        List<? extends Element> enclosedElements = classElement.getEnclosedElements();
        if (enclosedElements.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s注解的工具类不规范：没有声明private构造方法",
                            UTIL_TAG),
                    classElement);
        }
        int constructorCount = 0;
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                constructorCount++;
                if (constructorCount > 1) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s注解的工具类不规范：声明了多个构造方法",
                                    UTIL_TAG),
                            classElement);
                    return;
                }
                Set<Modifier> constructorModifiers = element.getModifiers();
                if (!constructorModifiers.contains(Modifier.PRIVATE)) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s注解的工具类不规范：构造方法未声明成private",
                                    UTIL_TAG),
                            classElement);
                    return;
                }
            }
        }
        if (constructorCount == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s注解的工具类不规范：没有声明private构造方法",
                            UTIL_TAG),
                    classElement);
        }
    }

    private static void checkMethod(TypeElement classElement, Messager messager) {
        List<? extends Element> enclosedElements = classElement.getEnclosedElements();
        if (enclosedElements.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s注解的工具类不规范：没有工具方法",
                            UTIL_TAG),
                    classElement);
        }

        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.METHOD) {
                Set<Modifier> modifiers = element.getModifiers();
                if (!modifiers.contains(Modifier.STATIC)) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s注解的工具类不规范：包含了非静态方法%s",
                                    UTIL_TAG, element.getSimpleName().toString()),
                            classElement);
                }
            }
        }
    }

    private static void checkField(TypeElement classElement, Messager messager) {
        List<? extends Element> enclosedElements = classElement.getEnclosedElements();
        if (enclosedElements.isEmpty()) {
            return;
        }
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.FIELD) {
                Set<Modifier> modifiers = element.getModifiers();
                if (!modifiers.contains(Modifier.STATIC)) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s注解的工具类不规范：包含了非静态变量%s",
                                    UTIL_TAG, element.getSimpleName().toString()),
                            classElement);
                }
            }
        }
    }
}
