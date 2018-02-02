package ch.voulgarakis.icsc2018.commons;

public class Event<T> {
	private final T data;
	private final Throwable t;
	private final boolean complete;

	public Event(T data) {
		super();
		this.data = data;
		this.t = null;
		this.complete = false;
	}

	public Event(Throwable t) {
		super();
		this.data = null;
		this.t = t;
		this.complete = false;
	}

	public Event() {
		super();
		this.data = null;
		this.t = null;
		this.complete = true;
	}

	public T getData() {
		return data;
	}

	public Throwable getError() {
		return t;
	}

	public boolean isComplete() {
		return complete;
	}
}