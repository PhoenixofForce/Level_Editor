package data;

import java.util.concurrent.*;

public class Debouncer {
	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private static final ConcurrentHashMap<String, Future<?>> delayedMap = new ConcurrentHashMap<>();

	/**
	 * Debounces {@code callable} by {@code delay}, i.e., schedules it to be executed after {@code delay},
	 * or cancels its execution if the method is called with the same key within the {@code delay} again.
	 */
	public static void debounce(final String key, final Runnable runnable, long delay) {
		final Future<?> prev = delayedMap.put(key, scheduler.schedule(() -> {

			try {
				runnable.run();
			} finally {
				delayedMap.remove(key);
			}

		}, delay, TimeUnit.MILLISECONDS));
		if (prev != null) {
			prev.cancel(true);
		}
	}

	public void shutdown() {
		scheduler.shutdownNow();
	}
}