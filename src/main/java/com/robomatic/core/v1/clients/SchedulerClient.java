package com.robomatic.core.v1.clients;

import com.robomatic.core.v1.models.JobCreatedModel;
import com.robomatic.core.v1.models.JobListModel;
import com.robomatic.core.v1.models.JobModel;

import java.util.List;

public interface SchedulerClient {

    JobCreatedModel createJob(final JobModel job);

    JobModel getJobById(final String jobId);

    JobListModel getJobs();

    JobCreatedModel deleteJob(final String jobId);

}
