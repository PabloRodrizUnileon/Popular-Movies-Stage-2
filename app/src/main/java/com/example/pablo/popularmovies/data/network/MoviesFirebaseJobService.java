package com.example.pablo.popularmovies.data.network;

import android.util.Log;

import com.example.pablo.popularmovies.Utils.InjectorUtils;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

public class MoviesFirebaseJobService extends JobService {

    private static final String TAG = MoviesFirebaseJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: jobParameters service for movies started");
        MoviesNetworkDataSource moviesNetworkDataSource = InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        moviesNetworkDataSource.fetchMovies();

        jobFinished(jobParameters, false);

        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
