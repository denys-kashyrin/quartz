package com.quartz.demo.job;

import com.quartz.demo.service.SimpleService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class SampleJob
    implements Job {

    @Autowired
    private SimpleService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        service.hello(jobExecutionContext.getJobDetail().getJobDataMap().getInt("id"));
    }
}
