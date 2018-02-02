package ch.voulgarakis.recruitment.tests.other;

import java.util.concurrent.TimeUnit;

import ch.voulgarakis.icsc2018.commons.FlowablePublisher;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class TestRX2 {

	public static void main(String[] args) throws InterruptedException {
		// Generator<Double> generator = new Generator<>();
		FlowablePublisher<Double> generator = new FlowablePublisher<>();

		Schedulers.newThread();
		Schedulers.newThread();
		Scheduler sub = Schedulers.newThread();
		Scheduler obs = Schedulers.newThread();

		System.out.println("Subscription: " + sub);
		System.out.println("Obsservation: " + obs);

		// Create a stream
		// Flowable.create(generator, BackpressureStrategy.MISSING)
		// Flowable.range(0, 10)
		generator

		// The subscription thread
				.subscribeOn(sub)

		// Backpressure strategy
				.onBackpressureBuffer()

		// Log message to be printed from the emitter
				.doOnNext(i -> System.out.println("Emitting " + i + " on thread " + Thread.currentThread().getName()))

		// Emit an item every 1 second
				.zipWith(Flowable.interval(1, TimeUnit.SECONDS), (w, t) -> w)

		// The observation thread
				.observeOn(obs)

		// Log message to be printed from the subscriber
				.subscribe(w -> System.out.println("Received " + w + " on thread " + Thread.currentThread().getName()),
						e -> System.out
								.println("Received error: " + e + " on thread " + Thread.currentThread().getName()),
						() -> System.out.println("Received Complete on thread " + Thread.currentThread().getName()));

		// Emit a few things...!
		generator.publish(1d);
		generator.publish(2d);
		generator.publish(3d);
		generator.publish(4d);
		generator.publish(5d);

		Thread.sleep(3000);

		generator.dispose();

		Thread.sleep(10000);
	}
}
