package io.github.classgraph.issues.issue897;

import static java.lang.annotation.ElementType.TYPE_USE;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Target;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;

/**
 * Issue897.
 */
public class Issue897Test {
    /**
     * Inner class that uses another inner class.
     */
    private class Inner1 {
        public Inner1(@Anno Inner2 i) {}
    }

    /**
     * Other inner class.
     */
    public class Inner2 {
    }

    /**
     * Type-use anntotation.
     */
    @Target(TYPE_USE)
    @interface Anno {}

    /**
     * Test that the annotation is attached to the first "source-code" parameter of the Inner1
     * constructor, not to the compiler-generated parameter for the enclosing class.
     */
    @Test
    public void annotationOnInnerClassConstructor() {
        try (ScanResult scanResult = new ClassGraph().acceptClasses(Inner1.class.getName())
                .ignoreClassVisibility().enableMethodInfo().enableAnnotationInfo().scan()) {
            final ClassInfo classInfo = scanResult.getClassInfo(Inner1.class.getName());
            final MethodInfo methodInfo = classInfo.getDeclaredConstructorInfo().get(0);
            // TODO: Attach the annotation to the source-code parameter instead of crashing.
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> methodInfo.getTypeSignatureOrTypeDescriptor());
        }
    }
}
