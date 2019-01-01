package no.bank.quiz.aspect;

import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import no.bank.quiz.annotation.Timed;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
class TimedAspect {

    @Autowired
    private PrometheusMeterRegistry registry;

    @Around("@annotation(timed)")
    public Object registerTimedAnnotation(ProceedingJoinPoint joinPoint, Timed timed) throws Throwable {

        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        Timer timer = Timer.builder(timed.value())
                .description(timed.value())
                .register(registry);

        timer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);

        return proceed;
    }
}