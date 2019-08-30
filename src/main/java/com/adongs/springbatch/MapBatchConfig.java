package com.adongs.springbatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;

/**
 * 基于内存处理
 * @Author yudong
 * @Date 2019/8/16 上午10:09
 * @Version 1.0
 */
@Configuration
public class MapBatchConfig implements BatchConfigurer {

    private static final Log logger = LogFactory.getLog(MapBatchConfig.class);

    /**
     * 事务管理器
     */
    private PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;
    private MapJobRepositoryFactoryBean factoryBean;

    public MapBatchConfig() {}


    @Override
    @Bean
    public JobRepository getJobRepository() {
        return jobRepository;
    }

    @Bean
    public MapJobRegistry mapJobRegistry(){
        return new MapJobRegistry();
    }

    /**
     * 作业构建工厂
     * @return
     */
    @Bean
    public JobBuilderFactory jobBuilderFactory(){
        return new  JobBuilderFactory(jobRepository);
    }

    /**
     * 作业步构建工厂
     * @return
     */
    @Bean
    public StepBuilderFactory stepBuilderFactory(){
        return new StepBuilderFactory(jobRepository,transactionManager);
    }

    @Override
    @Bean
    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    @Bean
    public JobLauncher getJobLauncher() {
        return jobLauncher;
    }

    @Override
    @Bean
    public JobExplorer getJobExplorer() {
        return jobExplorer;
    }

    /**
     * 初始化项目
     */
    @PostConstruct
    public void initialize() {
        try {
            this.jobRepository = createJobRepository();
            this.jobExplorer = createJobExplorer();
            this.jobLauncher = createJobLauncher();
        } catch (Exception e) {
            throw new BatchConfigurationException(e);
        }
    }

    /**
     * 创建调度器
     * @return
     * @throws Exception
     */
    public JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }




    /**
     * 创建基于内存JobRepository
     * @return
     * @throws Exception
     */
    public JobRepository createJobRepository() throws Exception {
        factoryBean = new MapJobRepositoryFactoryBean();
        factoryBean.setTransactionManager(getTransactionManager());
        factoryBean.afterPropertiesSet();
        JobRepository jobRepository = factoryBean.getObject();
        return jobRepository;
    }

    /**
     * 创建基于内存的JobExplorer
     * @return
     * @throws Exception
     */
    public JobExplorer createJobExplorer() throws Exception {
        MapJobExplorerFactoryBean mapJobExplorerFactoryBean = new MapJobExplorerFactoryBean(factoryBean);
        mapJobExplorerFactoryBean.afterPropertiesSet();
        return mapJobExplorerFactoryBean.getObject();
    }
}
