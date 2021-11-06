package com.abhi41.restapimvvmretrofit;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {

    private static AppExecutors instance;

    public static AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors();
        }
        return instance;
    }


    //what is ScheduledExecutorService?
    //It is an executor service that can schedule command to run after delay.
    private final ScheduledExecutorService mNetworkIO = Executors.newScheduledThreadPool(3);

    public ScheduledExecutorService getmNetworkIO() {
        return mNetworkIO;
    }

    //what is executor?
    //It is a thing that is used to execute runnable task. you can do this on main thread or background thread.
    //e.g

    //it will give me a single thread used to execute task
    private Executor mBackgroundExecutor = Executors.newSingleThreadExecutor();
    //It will give multiple thread used to execute task
    private Executor multipleExecutor = Executors.newFixedThreadPool(3);


    /*public AppExecutors() {
        multipleExecutor.execute(new Runnable() {
            @Override
            public void run() {

            }
        });

        multipleExecutor.execute(new Runnable() {
            @Override
            public void run() {

            }
        });

        multipleExecutor.execute(new Runnable() {
            @Override
            public void run() {

            }
        });

    }*/
}
