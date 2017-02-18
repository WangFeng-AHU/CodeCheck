package me.wangfeng.annotation.process;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import me.wangfeng.annotation.Singleton;

/**
 * {@link me.wangfeng.annotation.Singleton Singleton}注解的类型的合法性检查
 *
 * Created by wangfeng on 17/2/16.
 */

class SingletonChecker {

    private static final String SINGLETON_TAG = Singleton.class.getCanonicalName();

    static void check(Set<? extends Element> annotatedElement, Messager messager) {
        if (annotatedElement.isEmpty()) {
            return;
        }
        for (Element element : annotatedElement) {
            if (!CheckerHelper.isClassElement(element)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s注解的不是单例类：不是合法的类声明",
                                SINGLETON_TAG),
                        element);
                continue;
            }
            TypeElement classElement = (TypeElement) element;
            checkConstructor(classElement, messager);
            checkStaticGetter(classElement, messager);
        }
    }

    private static void checkConstructor(TypeElement element, Messager messager) {
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        if (enclosedElements.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s注解的单例类不规范：没有声明private构造方法",
                            SINGLETON_TAG),
                    element);
            return;
        }
        int constructorCount = 0;
        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                constructorCount++;
                if (constructorCount > 1) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s注解的单例类不规范：声明了多个构造方法",
                                    SINGLETON_TAG),
                            element);
                    return;
                }
                Set<Modifier> modifiers = enclosedElement.getModifiers();
                if (!modifiers.contains(Modifier.PRIVATE)) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("%s注解的单例类不规范：构造方法未声明成private",
                                    SINGLETON_TAG),
                            element);
                    return;
                }
            }
        }
        if (constructorCount == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s注解的单例类不规范：没有声明private构造方法",
                            SINGLETON_TAG),
                    element);
        }
    }

    private static void checkStaticGetter(TypeElement element, Messager messager) {
        String staticGetterName = element.getAnnotation(Singleton.class).value();
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        if (enclosedElements.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s注解的单例类不规范：没有声明静态的%s方法",
                            SINGLETON_TAG, staticGetterName),
                    element);
            return;
        }
        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement methodElement = (ExecutableElement) enclosedElement;
            if (!methodElement.getSimpleName().contentEquals(staticGetterName)) {
                continue;
            }
            Set<Modifier> modifiers = methodElement.getModifiers();
            if (!(modifiers.contains(Modifier.STATIC) && modifiers.contains(Modifier.PUBLIC))) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        String.format("%s注解的单例类不规范：%s方法未声明成public static的",
                                SINGLETON_TAG, staticGetterName),
                        element);
            }
            return;
        }
        messager.printMessage(Diagnostic.Kind.ERROR,
                String.format("%s注解的单例类不规范：没有声明静态的%s方法",
                        SINGLETON_TAG, staticGetterName),
                element);
    }
}
