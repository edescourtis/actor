package com.benbria.actor.behaviours;

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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

import com.benbria.actor.Actor;
import com.benbria.actor.Behaviour;

public final class LoggingBehaviour<T> implements
Behaviour<T> {
    private final static Logger logger = Logger.getLogger(LoggingBehaviour.class.getName());
    @Override
    public boolean receive(Actor<T> self, T msg) {
        logger.info(self + ": " + msg);
        return true;
    }

    @Override
    public void exception(Actor<T> self, Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        logger.severe(self + ": " + baos.toString());
    }
}
