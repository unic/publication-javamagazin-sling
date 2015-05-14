package de.javamagazin.sling.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;

import java.util.concurrent.ExecutorService;

import static de.javamagazin.sling.impl.Constants.JOB_TOPIC_CONTACT_REQUEST;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.apache.sling.event.jobs.consumer.JobConsumer.JobResult.ASYNC;
import static org.apache.sling.event.jobs.consumer.JobConsumer.PROPERTY_TOPICS;

/**
 * Demonstrates how to handle a job for a specific topic. Does nothing but signal successful job completion. It also demonstrates
 * the best-practice of <em>asynchronous job execution</em>. By default, job execution is synchronous - if a job is created
 * by a synchronous event handler (and event handlers are synchronous by default), any cause - such as a JCR resource creation - will
 * block until the job is completed.
 *
 * @author Olaf Otto
 */
@Component
@Service
@Properties({
        @Property(name = PROPERTY_TOPICS, value = JOB_TOPIC_CONTACT_REQUEST)
})
public class ContactRequestNotifier implements JobConsumer {
    ExecutorService executorService = newSingleThreadExecutor();

    @Override
    public JobResult process(Job job) {
        final AsyncHandler handler = (AsyncHandler) job.getProperty(PROPERTY_JOB_ASYNC_HANDLER);

        // Perform whatever operation asynchronously
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                handler.ok();
            }
        });

        return ASYNC;
    }

    @Deactivate
    protected void deactivate() {
        executorService.shutdownNow();
    }
}
