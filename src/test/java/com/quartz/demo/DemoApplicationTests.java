package com.quartz.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.quartz.demo.job.SampleJob;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = Application.class)
class DemoApplicationTests
    extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private Scheduler scheduler;

    @Test
    void contextLoads() throws SchedulerException, InterruptedException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("id", "test");
        JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
            .storeDurably(true)
            .setJobData(jobDataMap)
            .build();

        Trigger trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .startNow()
            .build();

        scheduler.scheduleJob(jobDetail, trigger);

        Thread.sleep(5000);
    }

    @Test
    public void testJobForOneTimeRunning() throws SchedulerException, InterruptedException {

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("id", 1);
        JobDetail jobDetail = JobBuilder.newJob(SampleJob.class)
            .storeDurably(true)
            .withIdentity("job1", "group1")
            .setJobData(jobDataMap)
            .build();

        Trigger trigger1 = TriggerBuilder.newTrigger()
            .withIdentity("cronTrigger1", "group1")
            .forJob(jobDetail)
            .startAt(DateUtils.addSeconds(new Date(), +1))
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
            .build();

        scheduler.scheduleJob(jobDetail, trigger1);
        Thread.sleep(2000);
        assertEquals(1, scheduler.getJobGroupNames().size());

        scheduler.getJobGroupNames().stream().forEach(groupName -> {
            try {
                scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName)).stream().forEach(
                    jobKey -> {
                        try {
                            if (scheduler.getTriggersOfJob(jobKey).isEmpty()) {
                                scheduler.deleteJob(jobKey);
                            }
                        } catch (SchedulerException e) {
                            e.printStackTrace();
                        }
                    }
                );
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });
        assertEquals(0, scheduler.getJobGroupNames().size());
        scheduler.shutdown();
    }
}
