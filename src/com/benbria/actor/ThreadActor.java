package com.benbria.actor;

/**
 * @author Eric des Courtis
 *
 */

/*
	The MIT License (MIT)
	
	Copyright (c) 2013 Benbria Corporation
	
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
 */

import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public final class ThreadActor<T> implements Actor<T> {
    private final BlockingQueue<T> queue;
    private final Behaviour<T> behaviour;

    public static <T> Actor<T> create(Behaviour<T> behaviour) {
        return new ThreadActor<T>(behaviour);
    }

    public static <T> Actor<T> spawn(Behaviour<T> behaviour){
        Actor<T> a = create(behaviour);
        new Thread(a).start();
        return a;
    }

    public static <T> Actor<T> createWithArrayBlockingQueue(Behaviour<T> behaviour, int capacity) {
        return new ThreadActor<T>(behaviour, new ArrayBlockingQueue<T>(capacity));
    }

    public static <T> Actor<T> spawnWithArrayBlockingQueue(Behaviour<T> behaviour, int capacity){
        Actor<T> a = createWithArrayBlockingQueue(behaviour, capacity);
        new Thread(a).start();
        return a;
    }

    public static <T> Actor<T> createWithPriorityQueue(Behaviour<T> behaviour, int initialCapacity, Comparator<T> comparator) {
        return new ThreadActor<T>(behaviour, new PriorityBlockingQueue<T>(initialCapacity, comparator));
    }

    public static <T> Actor<T> spawnWithPriorityQueue(Behaviour<T> behaviour, int initialCapacity, Comparator<T> comparator){
        Actor<T> a = createWithPriorityQueue(behaviour, initialCapacity, comparator);
        new Thread(a).start();
        return a;
    }

    public static <T> Actor<T> createWithPriorityQueue(Behaviour<T> behaviour, Comparator<T> comparator) {
        return createWithPriorityQueue(behaviour, 10, comparator);
    }

    public static <T> Actor<T> spawnWithPriorityQueue(Behaviour<T> behaviour, Comparator<T> comparator){
        Actor<T> a = createWithPriorityQueue(behaviour, comparator);
        new Thread(a).start();
        return a;
    }

    private ThreadActor(Behaviour<T> behaviour, BlockingQueue<T> queue) {
        this.behaviour = behaviour;
        this.queue = queue;
    }

    private ThreadActor(Behaviour<T> behaviour) {
        this(behaviour, new LinkedBlockingQueue<T>());
    }

    public void run() {
        try {
            while( behaviour.receive(this, queue.take()) ){};
        } catch (Exception ex) {
            behaviour.exception(this, ex);
        }
    }

    @Override
    public void send(T msg) throws InterruptedException {
        queue.put(msg);
    }

}
