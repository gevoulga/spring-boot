package ch.voulgarakis.icsc2018.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.internal.subscriptions.SubscriptionHelper;

public class FlowablePublisherSubscription<T> implements Subscription {
	private final Subscriber<? super T> a;

	public FlowablePublisherSubscription(Subscriber<? super T> subscriber) {
		this.a = subscriber;
	}

	@Override
	public void request(long n) {
		if (SubscriptionHelper.validate(n)) {
			while (true) {
				List<Event<T>> loop = new ArrayList<>();
				// Drain the queue
				if (n > Integer.MAX_VALUE)
					q.drainTo(loop, Integer.MAX_VALUE);
				else
					q.drainTo(loop, Long.valueOf(n).intValue());
				// Run the loop events
				if (cancelled) {
					return;
				} else {
					for (Event<T> e : loop) {
						if (cancelled) {
							return;
						} else if (e.isComplete()) {
							a.onComplete();
							return;
						} else {
							if (e.getData() != null)
								a.onNext(e.getData());
							else
								a.onError(e.getError());
						}
					}
				}
			}
		}
	}

	private final BlockingQueue<Event<T>> q = new LinkedBlockingQueue<>();
	private boolean cancelled = false;

	public void publish(T t) {
		if (t != null)
			q.offer(new Event<T>(t));
		else
			q.offer(new Event<T>(new NullPointerException("array element is null")));
	}

	@Override
	public final void cancel() {
		cancelled = true;
	}

	public void complete() {
		q.offer(new Event<T>());
	}

	public void throwError(Throwable t) {
		q.offer(new Event<T>(t));
	}
}
