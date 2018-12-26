package no.bank.quiz.aspect;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import no.bank.quiz.annotation.Counted;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
class CountedAspect {

    @Autowired
    private PrometheusMeterRegistry registry;

    @Before("@annotation(counted)")
    public void registerCountedAnnotation(Counted counted){
        registry.counter(counted.value()).increment();
    }
}