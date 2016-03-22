package com.benbria.actor;

/**
 * This is a simplified version of <https://github.com/edescourtis/actor>
 */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Actor<Msg> implements Runnable {
    enum STATE {
        alive, dead
    }

    @SuppressWarnings("serial")
    public static class DeadException extends Exception{};

    interface Behavior<Msg> {
        /**
         * @param self
         * @param msg
         * @return - `false` - stop the actor; `true` - continue
         */
        public abstract boolean onReceive(Actor<Msg> self, Msg msg);

        /**
         * DeadException thrown by the actor `self`. The thread is dead.
         * 
         * @param self
         * @param e
         */
        public abstract void onException(Actor<Msg> self, Exception e);
    }

    private final BlockingQueue<Msg> queue;
    private final Behavior<Msg> behavior;
    private STATE state;

    public static <M> Actor<M> create(Behavior<M> behavior) {
        return new Actor<M>(behavior);
    }

    public static <M> Actor<M> createAndStart(Behavior<M> behavior) {
        Actor<M> a = create(behavior);
        new Thread(a).start();
        return a;
    }

    private Actor(Behavior<Msg> behavior, BlockingQueue<Msg> queue) {
        this.state = STATE.alive;
        this.behavior = behavior;
        this.queue = queue;
    }

    private Actor(Behavior<Msg> behavior) {
        this(behavior, new LinkedBlockingQueue<Msg>());
    }

    public void run() {
        try {
            while (behavior.onReceive(this, queue.take())) {}
        }
        catch (InterruptedException ex) {
            behavior.onException(this, ex);
        }
        this.state = STATE.dead;
        this.queue.clear();
    }

    /**
     * Try to send "msg" to the actor
     * 
     * @param msg
     * @return true if successfully sent, false - if not
     * @throws DeadException - if the actor is already dead
     */
    public boolean send(Msg msg) throws DeadException {
        if (state == STATE.dead) {
            // System.out.println("-- cleaning the queue ["+queue.size()+"]");
            throw new DeadException();
        }
        return queue.offer(msg);
    }
}
