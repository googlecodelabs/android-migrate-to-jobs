package com.google.codelabs.migratingtojobs.jobdownloads;

import android.support.annotation.NonNull;

import com.google.codelabs.migratingtojobs.common.BasicApp;
import com.google.codelabs.migratingtojobs.common.BasicBrain;
import com.google.codelabs.migratingtojobs.common.Downloader;

public class App extends BasicApp {
    @NonNull
    @Override
    protected BasicBrain createBrain(Downloader downloader) {
        return new JobSchedulingBrain(downloader, getBaseContext());
    }
}
