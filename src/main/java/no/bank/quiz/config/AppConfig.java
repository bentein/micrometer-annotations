package no.bank.quiz.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import no.bank.quiz.annotation.Counted;
import no.bank.quiz.annotation.Summary;
import no.bank.quiz.annotation.TimeHistogram;
import no.bank.quiz.annotation.Timed;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    @Value("${metrics.package.base}")
    private String basePackage;

    @Autowired
    private PrometheusMeterRegistry registry;

    @PostConstruct
    public void init() {
        Reflections ref = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(new MethodAnnotationsScanner(), new FieldAnnotationsScanner()));

        registerCountedAnnotations(ref);
        registerSummaryAnnotations(ref);
        registerTimedAnnotations(ref);
        registerTimeHistogramAnnotations(ref);
    }

    private void registerCountedAnnotations(Reflections ref) {
        List<Counter> counters = ref.getMethodsAnnotatedWith(Counted.class).stream()
                .map(m -> m.getAnnotation(Counted.class))
                .map(a -> Counter.builder(a.value())
                        .description(a.description())
                        .register(registry))
                .collect(Collectors.toList());
    }

    private void registerSummaryAnnotations(Reflections ref) {
        List<DistributionSummary> methodSummaries = ref.getMethodsAnnotatedWith(Summary.class).stream()
                .map(m -> m.getAnnotation(Summary.class))
                .map(a -> DistributionSummary.builder(a.value())
                        .baseUnit(a.unit())
                        .scale(a.scale())
                        .description(a.description())
                        .register(registry))
                .collect(Collectors.toList());

        List<DistributionSummary> fieldSummary = ref.getFieldsAnnotatedWith(Summary.class).stream()
                .map(m -> m.getAnnotation(Summary.class))
                .map(a -> DistributionSummary.builder(a.value())
                        .baseUnit(a.unit())
                        .scale(a.scale())
                        .description(a.description())
                        .register(registry))
                .collect(Collectors.toList());
    }

    private void registerTimedAnnotations(Reflections ref) {
        List<Timer> timers = ref.getMethodsAnnotatedWith(Timed.class).stream()
                .map(m -> m.getAnnotation(Timed.class))
                .map(a -> Timer.builder(a.value())
                        .description(a.description())
                        .register(registry))
                .collect(Collectors.toList());
    }

    private void registerTimeHistogramAnnotations(Reflections ref) {
        List<Timer> histograms = ref.getMethodsAnnotatedWith(TimeHistogram.class).stream()
                .map(m -> m.getAnnotation(TimeHistogram.class))
                .map(a -> Timer.builder(a.value())
                        .publishPercentiles(a.percentiles())
                        .sla(Duration.ofMillis(a.sla()))
                        .publishPercentileHistogram()
                        .description(a.description())
                        .register(registry))
                .collect(Collectors.toList());
    }
}
