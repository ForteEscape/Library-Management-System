package com.management.library.batch;

import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.RentalStatus;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final EntityManagerFactory entityManagerFactory;

  @Bean
  public Job myBatchJob(){
    return jobBuilderFactory.get("myBatchJob")
        .start(step())
        .build();
  }

  @Bean
  @JobScope
  public Step step(){
    return stepBuilderFactory.get("step")
        .<Rental, Rental>chunk(100)
        .reader(itemReader())
        .processor(itemProcessor())
        .writer(itemWriter())
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Rental> itemReader(){
    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("standardDate", LocalDate.now());
    parameterValues.put("rentalStatus", RentalStatus.PROCEEDING);

    return new JpaPagingItemReaderBuilder<Rental>()
        .pageSize(100)
        .parameterValues(parameterValues)
        .queryString("select r from Rental r "
            + "where r.rentalStatus = :rentalStatus and "
            + "r.rentalEndDate > :standardDate")
        .entityManagerFactory(entityManagerFactory)
        .name("JpaPagingItemReader")
        .build();
  }

  @Bean
  @StepScope
  public ItemProcessor<Rental, Rental> itemProcessor(){
    return rental -> {
      rental.changeRentalStatus(RentalStatus.OVERDUE);
      return rental;
    };
  }

  @Bean
  @StepScope
  public JpaItemWriter<Rental> itemWriter(){
    return new JpaItemWriterBuilder<Rental>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }
}
