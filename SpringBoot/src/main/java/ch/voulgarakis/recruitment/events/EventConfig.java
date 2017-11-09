package ch.voulgarakis.recruitment.events;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.support.TaskUtils;

@Configuration
public class EventConfig {
    /**
     * Enable this Bean if you want only asynchronous event handling.
     * Disable both beans if you want the standard synchronous event handling.
     */
    // @Bean
    public ApplicationEventMulticaster asyncApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        eventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
        return eventMulticaster;
    }

    /**
     * Enable this Bean if you want dual (asynchronous & synchronous) event handling. You can differentiate between
     * tasks by using the @AsyncListenter annotation.
     */
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        DualEventMulticaster eventMulticaster = new DualEventMulticaster();
        eventMulticaster.setAsyncEventMulticaster(asyncApplicationEventMulticaster());
        eventMulticaster.setSyncEventMulticaster(new SimpleApplicationEventMulticaster());
        return eventMulticaster;
    }
}
