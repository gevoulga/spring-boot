package ch.voulgarakis.icsc2018.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

public class FlowablePublisher<T> extends Flowable<T>implements Disposable {
	private final List<FlowablePublisherSubscription<T>> subscriptions = new ArrayList<>();

	@Override
	protected void subscribeActual(Subscriber<? super T> s) {
		FlowablePublisherSubscription<T> gs = new FlowablePublisherSubscription<>(s);
		subscriptions.add(gs);
		s.onSubscribe(gs);
	}

	public void publish(T t) {
		subscriptions.parallelStream().forEach(s -> s.publish(t));
	}

	public void throwError(Throwable t) {
		subscriptions.parallelStream().forEach(s -> s.throwError(t));
	}

	private final AtomicBoolean disposed = new AtomicBoolean(false);

	@Override
	public void dispose() {
		if (disposed.compareAndSet(false, true))
			subscriptions.parallelStream().forEach(s -> s.complete());
	}

	@Override
	public boolean isDisposed() {
		return disposed.get();
	}
}
