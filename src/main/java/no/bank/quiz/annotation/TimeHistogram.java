package no.bank.quiz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeHistogram {
    String value();
    String description() default "";
    double[] percentiles() default {0.05,0.95};
    long sla() default 100;
}
