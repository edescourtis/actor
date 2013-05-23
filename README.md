actor
=====

A java actor library with linked and priority queue support.

To produce a jar file simply run ant.

    package com.benbria.actor;
    
    public class ActorExample {
        public static void main(String[] args) throws InterruptedException {
            Actor<String> actor = ThreadActor.spawn(new Behaviour<String>() {
                @Override
                public void receive(Actor<String> self, String msg) {
                    System.out.println("Got: " + msg);
                }
            });
 
            actor.send("hello");
            actor.send("world");
            Thread.sleep(1000);
            actor.stop();
        }
    }

Output:
<pre>
Got: hello
Got: world
</pre>

License
-

MIT
