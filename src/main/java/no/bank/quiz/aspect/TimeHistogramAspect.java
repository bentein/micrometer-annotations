package no.bank.quiz.aspect;

import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import no.bank.quiz.annotation.TimeHistogram;
import no.bank.quiz.annotation.Timed;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
class TimeHistogramAspect {

    @Autowired
    private PrometheusMeterRegistry registry;

    @Around("@annotation(histogram)")
    public Object registerTimeHistogramAnnotation(ProceedingJoinPoint joinPoint, TimeHistogram histogram) throws Throwable {

        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        Timer timer = Timer.builder(histogram.value())
                .publishPercentiles(histogram.percentiles())
                .sla(Duration.ofMillis(histogram.sla()))
                .publishPercentileHistogram()
                .description(histogram.value())
                .register(registry);

        timer.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
        return proceed;
    }
}