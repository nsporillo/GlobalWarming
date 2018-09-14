package net.porillo.database.queue;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentHashQueue<T> implements Iterable<T> {

	private Map<Integer, T> hashcodeMap;
	private Queue<Integer> hashcodeQueue;

	public ConcurrentHashQueue() {
		this.hashcodeMap = new ConcurrentHashMap<>();
		this.hashcodeQueue = new ConcurrentLinkedQueue<>();
	}

	public void offer(T element) {
		if (!hashcodeMap.containsKey(element.hashCode())) {
			hashcodeQueue.offer(element.hashCode());
			hashcodeMap.put(element.hashCode(), element);
		}
	}

	public T poll() {
		if (isEmpty()) return null;

		Integer hashcode = hashcodeQueue.poll();
		return hashcodeMap.remove(hashcode);
	}

	public T peek() {
		if (isEmpty()) return null;

		Integer hashcode = hashcodeQueue.peek();
		return hashcodeMap.get(hashcode);
	}

	public boolean isEmpty() {
		return hashcodeQueue.isEmpty();
	}

	public int size() {
		return hashcodeQueue.size();
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return hashcodeMap.values().iterator();
	}
}
