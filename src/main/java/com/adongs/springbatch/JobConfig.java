package com.adongs.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * @Author yudong
 * @Date 2019/8/16 上午11:11
 * @Version 1.0
 */
@Configuration
public class JobConfig {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 配置Job
     * @return
     */
    @Bean
    public Job footballJob(Step step){
        return this.jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(sampleListener())
                .start(step).build();
    }

    /**
     * 构建step
     * @param reader
     * @param processor
     * @param flatFileItemWriter
     * @return
     */
    @Bean
    public Step reconciliation(FlatFileItemReader reader,
                               ItemProcessor processor,
                               FlatFileItemWriter flatFileItemWriter){

        return  stepBuilderFactory.get("reconciliation")
                .<Account,Account> chunk(2)
                .reader(reader)
                .processor(processor)
                .writer(flatFileItemWriter)
                .build();
    }

    /**
     * 读取文件
     * @return
     */
    @Bean
    public FlatFileItemReader flatFileItemReader(){
        FlatFileItemReader flatFileItemReader = new FlatFileItemReader();
        flatFileItemReader.setResource(new ClassPathResource("user-1-account.csv"));
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setNames("name","date","money");
        BeanWrapperFieldSetMapper beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper();
        beanWrapperFieldSetMapper.setBeanFactory(applicationContext);
        beanWrapperFieldSetMapper.setPrototypeBeanName("account");
        DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
        defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        flatFileItemReader.setLineMapper(defaultLineMapper);
        return flatFileItemReader;
    }


    /**
     * 处理器
     * @return
     */
    @Bean
    public ItemProcessor creditBillProcessor(){
        return new ItemProcessor<Account,Account>(){
            @Override
            public Account process(Account item) throws Exception {
                System.out.println(item.toString());
                return item;
            }
        };
    }

    /**
     * 写入文件
     * @return
     */
    @Bean
    public FlatFileItemWriter flatFileItemWriter(){
        BeanWrapperFieldExtractor beanWrapperFieldExtractor = new BeanWrapperFieldExtractor();
        beanWrapperFieldExtractor.setNames(new String[]{"name","date","money"});
        DelimitedLineAggregator delimitedLineAggregator = new DelimitedLineAggregator();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
        FlatFileItemWriter flatFileItemWriter = new FlatFileItemWriter();
        flatFileItemWriter.setResource(new ClassPathResource("user-1-account-result.csv"));
        flatFileItemWriter.setLineAggregator(delimitedLineAggregator);
        return flatFileItemWriter;
    }



    /**
     * 监听
     * @return
     */
    @Bean
    public JobExecutionListener sampleListener(){
        return new JobExecutionListener(){
            @Override
            public void beforeJob(JobExecution jobExecution) {
                System.out.println("JobExecutionListener.beforeJob");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                System.out.println("JobExecutionListener.afterJob");
            }
        };
    }



}
