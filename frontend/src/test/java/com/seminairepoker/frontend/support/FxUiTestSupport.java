package com.seminairepoker.frontend.support;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class FxUiTestSupport {
    private static final AtomicBoolean FX_RUNTIME_STARTED = new AtomicBoolean(false);

    @BeforeAll
    static void shouldStartJavaFxRuntime_whenTestSuiteBootstraps() throws InterruptedException {
        if (FX_RUNTIME_STARTED.compareAndSet(false, true)) {
            CountDownLatch startupLatch = new CountDownLatch(1);
            Platform.startup(startupLatch::countDown);
            startupLatch.await();
        }
    }

    protected <T> T onFxThread(Callable<T> action) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return action.call();
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> valueReference = new AtomicReference<>();
        AtomicReference<Throwable> errorReference = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                valueReference.set(action.call());
            } catch (Throwable throwable) {
                errorReference.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        Throwable throwable = errorReference.get();
        if (throwable != null) {
            if (throwable instanceof Exception exception) {
                throw exception;
            }
            throw new RuntimeException(throwable);
        }

        return valueReference.get();
    }
}

