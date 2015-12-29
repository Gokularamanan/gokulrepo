package com.fleshkart.app.map.mapsapi;

import android.os.Handler;

public class BufferDelayedTasker {
    private static final String TAG = "BufferDelayedTasker";

    private long mLastTaskId;
    private long mDelayMilliSec;

    private Runnable mQueuedTask;
    private Handler mHandler = new Handler();

    /**
     * BufferDelayedTasker  Squash redundant multiple tasks within delayed buffer into one task.
     * @param delayMilliSec
     */
    public BufferDelayedTasker(long delayMilliSec) {
        mDelayMilliSec = delayMilliSec;
    }

    /**
     * pushTask
     * @param task  Optional. If null, just requests a new task id.
     * @return  Task id.
     */
    public long pushTask(final DelayedRunnableTask task) {
        if ( mQueuedTask != null ) {
            mHandler.removeCallbacks( mQueuedTask );
            mQueuedTask = null;
        }

        mLastTaskId = System.currentTimeMillis();

        if ( task != null ) {
            task.setTaskId( mLastTaskId );
            mQueuedTask = new Runnable() {
                @Override
                public void run() {
                    mQueuedTask = null;
                    task.runTask();
                }
            };
            mHandler.postDelayed( mQueuedTask, mDelayMilliSec );
        }

        return mLastTaskId;
    }

    public boolean isTaskStale(long taskId) {
        return ( taskId > 0 && taskId < mLastTaskId );
    }

    /****************************************************************
     * DelayedRunnableTask
     ****************************************************************/

    public interface DelayedRunnableTask {
        public void runTask();
        public long getTaskId();
        public void setTaskId(long taskId);
    }

}
