package general.lang.reflect;


class AnnotatedElement {

    public array<Annotation> getAnnotations();

    public final Annotation getAnnotation(Class annotationClass) {
        for (Annotation annotation : getAnnotations()) {
            if (annotation.getClass() == annotationClass)
                return a;
        }
        return null;
    }

}