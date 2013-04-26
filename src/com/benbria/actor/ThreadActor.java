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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.benbria.actor.listeners.NullActorListener;


public final class ThreadActor<T> implements Runnable, Actor<T> {
	private final BlockingQueue<StopOrT<T>> queue;
	private final Behaviour<T> behaviour;
	private final ActorListener<T> listener;
	
	public static <T> Actor<T> create(Behaviour<T> behaviour, ActorListener<T> listener) {
		return new ThreadActor<T>(behaviour, listener);
	}
	
	public static <T> Actor<T> create(Behaviour<T> behaviour) {
		return create(behaviour, new NullActorListener<T>());
	}
	
	public static <T> Actor<T> spawn(Behaviour<T> behaviour, ActorListener<T> listener){
		Actor<T> a = create(behaviour, listener);
		new Thread(a).start();
		return a;
	}

	public static <T> Actor<T> spawn(Behaviour<T> behaviour) {
		return spawn(behaviour, new NullActorListener<T>());
	}
	
	public static <T> Actor<T> createWithPriorityQueue(Behaviour<T> behaviour, ActorListener<T> listener, final Comparator<T> comparator) {
		return new ThreadActor<T>(behaviour, listener, new PriorityBlockingQueue<StopOrT<T>>(10, new Comparator<StopOrT<T>>() {
			@Override
			public int compare(StopOrT<T> o1, StopOrT<T> o2) {
				if(o1.isStop()) return -1;
				if(o2.isStop()) return 1;
				return comparator.compare(o1.getT(), o2.getT());
			}
		}));
	}
	
	public static <T> Actor<T> createWithPriorityQueue(Behaviour<T> behaviour, Comparator<T> comparator) {
		return createWithPriorityQueue(behaviour, new NullActorListener<T>(), comparator);
	}
	
	public static <T> Actor<T> spawnWithPriorityQueue(Behaviour<T> behaviour, ActorListener<T> listener, Comparator<T> comparator){
		Actor<T> a = createWithPriorityQueue(behaviour, listener, comparator);
		new Thread(a).start();
		return a;
	}

	public static <T> Actor<T> spawnWithPriorityQueue(Behaviour<T> behaviour, Comparator<T> comparator) {
		return spawnWithPriorityQueue(behaviour, new NullActorListener<T>(), comparator);
	}
	
	
	private ThreadActor(Behaviour<T> behaviour, ActorListener<T> listener, BlockingQueue<StopOrT<T>> queue) {
		this.listener = listener;
		this.behaviour = behaviour;
		this.queue = queue;
	}
	
	private ThreadActor(Behaviour<T> behaviour, ActorListener<T> listener) {
		this(behaviour, listener, new LinkedBlockingQueue<StopOrT<T>>());
	}
	
	private ThreadActor(Behaviour<T> behaviour) {
		this(behaviour, new NullActorListener<T>());
	}

	public void run() {
		listener.start(this);
		try {
			while(true) {
				StopOrT<T> stopOrMsg = queue.take();
				if(stopOrMsg.isT()){
					behaviour.receive(this, stopOrMsg.getT());
				}else{
					break;
				}
			}
			listener.stop(this);
		} catch (Exception ex) {
			listener.exception(this, ex);
		}
	}
		
	@Override
	public void send(T msg) throws InterruptedException {
		queue.put(StopOrT.newT(msg));
	}
	
	@Override
	public void stop() throws InterruptedException {
		StopOrT<T> stop = StopOrT.newStop();
		queue.put(stop);
	}	
	
	private static class StopOrT<T> {
		private final T t;
		private StopOrT(T t) {
			this.t = t;
		}
		
		public boolean isStop() {
			return t == null;
		}
		
		public boolean isT() {
			return t != null;
		}
		public T getT() {
		    if (t == null)
		    	throw new RuntimeException("not T");
		    return t;
		}
		public static <T> StopOrT<T> newStop() {
			return new StopOrT<T>(null);
		}
		public static <T> StopOrT<T> newT(T t) {
			return new StopOrT<T>(t);
		}
	}
}
