/**
 * 
 */
package com.benbria.actor.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.benbria.actor.Actor;
import com.benbria.actor.Behaviour;
import com.benbria.actor.ThreadActor;

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
    class ExceptionGeneratingBehaviour implements Behaviour<String> {
        private boolean exceptionOccurred = false;
        public boolean hasExceptionOccurred() {
            return exceptionOccurred;
        }

        @Override
        public boolean receive(Actor<String> self, String msg) {
            throw new RuntimeException("evil");
        }

        @Override
        public void exception(Actor<String> self, Exception e) {
            exceptionOccurred = true;
        }
    }

    @Test
    public void testException() {
        ExceptionGeneratingBehaviour behaviour = new ExceptionGeneratingBehaviour();
        Actor<String> a = ThreadActor.spawn(behaviour);
        try {
            a.send("hello");
            Thread.sleep(1000);
        } catch (InterruptedException e) { }
        assertTrue(behaviour.hasExceptionOccurred());
    }

    class ReceivedMessageCheckingBehaviour implements Behaviour<String> {
        private String msg;

        @Override
        public boolean receive(Actor<String> self, String msg) {
            this.msg = msg;
            return false;
        }

        public String getMsg() {
            return msg;
        }

        @Override
        public void exception(Actor<String> self, Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void testSend() {
        ReceivedMessageCheckingBehaviour behaviour = new ReceivedMessageCheckingBehaviour();
        Actor<String> a = ThreadActor.spawn(behaviour);
        try {
            a.send("testing");
            Thread.sleep(1000);
        } catch (InterruptedException e) { }
        assertEquals("testing", behaviour.getMsg());
    }
}
