actor
=====

A minimal java actor library.

    package com.benbria.actor;
    
    public class ActorExample {
        public static void main(String[] args) throws InterruptedException {
            Actor<String> actor = Actor.createAndStart(new Actor.Behavior<String>()
                @Override
                public boolean onReceive(Actor<String> self, String msg) {
                    System.out.println("Got: " + msg);
                    return !msg.equals("stop");
                }

                @Override
                public void onException(Actor<String> self, Exception e) {}
            });
 
            actor.send("hello");
            actor.send("world");
            Thread.sleep(1000);
            actor.send("stop");
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
