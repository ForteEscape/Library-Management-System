package com.management.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfiguration implements SchedulingConfigurer {

  private static final int THREAD_POOL_SIZE = 10;

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(THREAD_POOL_SIZE);
    taskScheduler.setThreadNamePrefix("scheduled-task-pool-");
    taskScheduler.initialize();

    taskRegistrar.setTaskScheduler(taskScheduler);
  }
}
