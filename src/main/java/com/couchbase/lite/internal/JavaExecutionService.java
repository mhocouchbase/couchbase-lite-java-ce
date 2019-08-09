//
// JavaExecutionService.java
//
// Copyright (c) 2017 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.lite.internal;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.couchbase.lite.internal.utils.Preconditions;

/**
 * ExecutionService for Java.
 */
public class JavaExecutionService extends AbstractExecutionService {
    private static class CancellableTask implements Cancellable {
        private Future future;

        private CancellableTask(@NonNull Future future) {
            Preconditions.checkArgNotNull(future, "future");
            this.future = future;
        }

        @Override
        public void cancel() { future.cancel(false); }
    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(@NonNull Runnable r) { return new Thread(r, "CBL#" + mCount.getAndIncrement()); }
    };

    private static final Executor THREAD_POOL_EXECUTOR;
    static {
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), THREAD_FACTORY);
    }

    private final Executor mainExecutor;

    private final ScheduledExecutorService scheduler;

    public JavaExecutionService() {
        mainExecutor = Executors.newSingleThreadExecutor();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @NonNull
    @Override
    public Executor getThreadPoolExecutor() {
        return THREAD_POOL_EXECUTOR;
    }

    @NonNull
    @Override
    public Executor getMainExecutor() {
        return mainExecutor;
    }

    @Override
    public Cancellable postDelayedOnExecutor(long delayMs, @NonNull Executor executor, @NonNull Runnable task) {
        Preconditions.checkArgNotNull(executor, "executor");
        Preconditions.checkArgNotNull(task, "task");
        final Runnable delayedTask = () -> {
            try {
                executor.execute(task);
            }
            catch (RejectedExecutionException ignored) { }
        };

        final Future future = scheduler.schedule(delayedTask, delayMs, TimeUnit.MILLISECONDS);
        return new CancellableTask(future);
    }

    @Override
    public void cancelDelayedTask(@NonNull Cancellable cancellableTask) {
        Preconditions.checkArgNotNull(cancellableTask, "future");
        cancellableTask.cancel();
    }
}