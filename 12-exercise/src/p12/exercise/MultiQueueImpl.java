package p12.exercise;

import java.util.*;

public class MultiQueueImpl<T, Q> implements MultiQueue<T, Q> {

    private final Map<Q, Queue<T>> openQueues = new HashMap<>();

    @Override
    public Set<Q> availableQueues() {
        return this.openQueues.keySet();
    }

    @Override
    public void openNewQueue(Q queue) {
        if (this.openQueues.containsKey(queue)) {
            throw new IllegalArgumentException("The queue " + queue + " already exists.");
        }
        this.openQueues.put(queue, new LinkedList<>());
    }

    /* Ensures that a given queue exists */
    private void assertQueueExistance(Q queue) {
        if (!this.openQueues.containsKey(queue)) {
            throw new IllegalArgumentException("The queue " + queue + " does not exist.");
        }
    }

    @Override
    public boolean isQueueEmpty(Q queue) {
        assertQueueExistance(queue);
        return this.openQueues.get(queue).isEmpty();
    }

    @Override
    public void enqueue(T elem, Q queue) {
        assertQueueExistance(queue);
        this.openQueues.get(queue).add(elem);
    }

    @Override
    public T dequeue(Q queue) {
        assertQueueExistance(queue);
        // The selected queue may be empty
        return isQueueEmpty(queue) ? null : this.openQueues.get(queue).remove();
    }

    @Override
    public Map<Q, T> dequeueOneFromAllQueues() {
        final Map<Q, T> mout = new HashMap<>();
        for (final Q queue : this.openQueues.keySet()) {
            mout.put(queue, this.dequeue(queue));
        }
        return mout;
    }

    @Override
    public Set<T> allEnqueuedElements() {
        final Set<T> sout = new HashSet<>();
        for (final Queue<T> queue : this.openQueues.values()) {
            sout.addAll(queue);
        }
        return sout;
    }

    @Override
    public List<T> dequeueAllFromQueue(Q queue) {
        assertQueueExistance(queue);
        final List<T> oldQueue = new ArrayList<>(this.openQueues.get(queue));
        this.openQueues.put(queue, new LinkedList<>());
        return oldQueue;
    }

    @Override
    public void closeQueueAndReallocate(Q queue) {
        assertQueueExistance(queue);
        if (this.openQueues.size() == 1) {
            throw new IllegalStateException("No other queue can replace " + queue);
        }
        final List<T> elements = dequeueAllFromQueue(queue);
        this.openQueues.remove(queue);

        for (final Q alternative : this.openQueues.keySet()) {
            this.openQueues.get(alternative).addAll(elements);
            return;
        }
    }

}
