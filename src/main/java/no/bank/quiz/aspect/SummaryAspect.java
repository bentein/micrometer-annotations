package no.bank.quiz.aspect;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import no.bank.quiz.annotation.Summary;
import no.bank.quiz.annotation.TimeHistogram;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
class SummaryAspect {

    @Autowired
    private PrometheusMeterRegistry registry;

    @AfterReturning(value = "@annotation(annotation)", returning = "retval")
    public void registerSummaryAnnotation(Object retval, Summary annotation) throws Throwable {
        System.out.println(retval instanceof List);

        DistributionSummary sum = getSummary(annotation);
        sum.record(retval.toString().length());
    }

    private DistributionSummary getSummary(Summary annotation) {
        return DistributionSummary.builder(annotation.value())
                .baseUnit(annotation.unit())
                .scale(annotation.scale())
                .description(annotation.description())
                .register(registry);
    }
}