package no.bank.quiz.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import no.bank.quiz.annotation.Counted;
import no.bank.quiz.annotation.TimeHistogram;
import no.bank.quiz.annotation.Timed;
import org.reflections.Reflections;
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

    @Value("${monitoring.package.base}")
    private String basePackage;

    @Autowired
    private PrometheusMeterRegistry registry;

    @PostConstruct
    public void init() {
        Reflections ref = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(new MethodAnnotationsScanner()));

        registerCountedAnnotations(ref);
        registerTimedAnnotations(ref);
        registerTimeHistogramAnnotations(ref);
    }

    private void registerCountedAnnotations(Reflections ref) {
        List<Counter> counters = ref.getMethodsAnnotatedWith(Counted.class).stream()
                .map(m -> m.getAnnotation(Counted.class))
                .map(c -> Counter.builder(c.value())
                        .description(c.description())
                        .register(registry))
                .collect(Collectors.toList());
    }

    private void registerTimedAnnotations(Reflections ref) {
        List<Timer> timers = ref.getMethodsAnnotatedWith(Timed.class).stream()
                .map(m -> m.getAnnotation(Timed.class))
                .map(c -> Timer.builder(c.value())
                        .description(c.description())
                        .register(registry))
                .collect(Collectors.toList());
    }

    private void registerTimeHistogramAnnotations(Reflections ref) {
        List<Timer> histograms = ref.getMethodsAnnotatedWith(TimeHistogram.class).stream()
                .map(m -> m.getAnnotation(TimeHistogram.class))
                .map(c -> Timer.builder(c.value())
                        .publishPercentiles(c.percentiles())
                        .sla(Duration.ofMillis(c.sla()))
                        .publishPercentileHistogram()
                        .description(c.description())
                        .register(registry))
                .collect(Collectors.toList());
    }
}
