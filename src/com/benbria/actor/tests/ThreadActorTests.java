/**
 * 
 */
package com.benbria.actor.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.benbria.actor.Actor;
import com.benbria.actor.ActorListener;
import com.benbria.actor.Behaviour;
import com.benbria.actor.ThreadActor;
import com.benbria.actor.behaviours.NullBehaviour;

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


public class ThreadActorTests {
	class StartStopExceptionCheckingActorListener implements ActorListener<String> {
		private boolean started = false;
		private boolean stopped = false;
		private boolean exception = false;
		@Override
		public void start(Actor<String> actor) {
			setStarted();
		}
		
		@Override
		public void stop(Actor<String> actor) {
			setStopped();
		}
		
		@Override
		public void exception(Actor<String> actor, Exception ex) {
			setException();
		}
		
		public boolean isStarted() {
			return started;
		}
		
		public void setStarted() {
			this.started = true;
		}
		
		public boolean isStopped() {
			return stopped;
		}
		
		public void setStopped() {
			this.stopped = true;
		}
		
		public boolean isException() {
			return exception;
		}
		
		public void setException() {
			this.exception = true;
		}
	};
	
	@Test
	public void testStartStopException(){
		StartStopExceptionCheckingActorListener checkListener = new StartStopExceptionCheckingActorListener();
		Actor<String> a = ThreadActor.spawn(new NullBehaviour<String>(), checkListener);
		
		try {
			a.stop();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		
		assertTrue(checkListener.isStarted());
		assertTrue(checkListener.isStopped());
		assertFalse(checkListener.isException());
		
	}
	
	class ExceptionGeneratingBehaviour implements Behaviour<String> {
		@Override
		public void receive(Actor<String> self, String msg) {
			throw new RuntimeException("evil");
		}	
	}
	
	@Test
	public void testException() {
		StartStopExceptionCheckingActorListener checkListener = new StartStopExceptionCheckingActorListener();
		Actor<String> a = ThreadActor.spawn(new ExceptionGeneratingBehaviour(), checkListener);
		try {
			a.send("hello");
			Thread.sleep(1000);
			a.stop();
		} catch (InterruptedException e) {

		}
		
		assertTrue(checkListener.isException());
	}
	
	class ReceivedMessageCheckingBehaviour implements Behaviour<String> {
		private String msg;
		
		@Override
		public void receive(Actor<String> self, String msg) {
			this.msg = msg;
		}
		
		public String getMsg() {
			return msg;
		}
	}
	
	@Test
	public void testSend() {
		ReceivedMessageCheckingBehaviour behaviour = new ReceivedMessageCheckingBehaviour();
		Actor<String> a = ThreadActor.spawn(behaviour);
		try {
			a.send("testing");
			Thread.sleep(1000);
			a.stop();
		} catch (InterruptedException e) {
		}
		
		assertEquals("testing", behaviour.getMsg());
		
	}

}
