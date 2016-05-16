package com.google.codelabs.migratingtojobs.common;

import java.util.concurrent.ThreadFactory;

public class PriorityThreadFactory implements ThreadFactory {
    private final int mPriority;

    public PriorityThreadFactory(int priority) {
        mPriority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(mPriority);
        return t;
    }
}
