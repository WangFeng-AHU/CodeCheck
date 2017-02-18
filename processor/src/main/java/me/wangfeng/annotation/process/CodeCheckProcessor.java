package me.wangfeng.annotation.process;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import me.wangfeng.annotation.Singleton;
import me.wangfeng.annotation.Util;

/**
 * 代码检查相关注解的注解处理器
 *
 * Created by wangfeng on 17/2/16.
 */
public class CodeCheckProcessor extends AbstractProcessor {

    private int round = 0;

    private Set<String> supportAnnotations;

    private Messager messager;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        if (supportAnnotations == null) {
            supportAnnotations = new LinkedHashSet<>();
            supportAnnotations.add(Util.class.getCanonicalName());
            supportAnnotations.add(Singleton.class.getCanonicalName());
        }
        return supportAnnotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        round++;
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(Locale.CHINESE, "第%d轮注解处理", round));
        messager.printMessage(Diagnostic.Kind.NOTE, "当前待处理的注解有：" + annotations.toString());

        if (annotations.isEmpty()) {
            // 说明已经没有需要处理的注解了
            return true;
        }

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            messager.printMessage(Diagnostic.Kind.NOTE,
                    String.format("被%s注解的类型有：%s", annotation.toString(), annotatedElements.toString()));
            process(annotation, annotatedElements);
        }
        return true;
    }

    private void process(TypeElement annotation, Set<? extends Element> annotatedElements) {
        if (annotation.getQualifiedName().contentEquals(Util.class.getCanonicalName())) {
            // 处理@Util注解
            UtilChecker.check(annotatedElements, messager);
        } else if (annotation.getQualifiedName().contentEquals(Singleton.class.getCanonicalName())) {
            // 处理@Singleton注解
            SingletonChecker.check(annotatedElements, messager);
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "不支持的注解：" + annotation.toString());
        }
    }
}
