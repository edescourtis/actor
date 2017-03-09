package com.benbria.actor;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ActorTest {
    int numberOfMessagesToAccept = 10;
    @Test
    public void testCreateAndStart() {
        Actor<String> myActor = Actor.createAndStart(new Actor.Behavior<String>() {
            int counter = 0;
            public boolean onReceive(Actor < String > self, String msg){
                counter++;
                System.out.println(counter+". "+msg);
                return (counter < numberOfMessagesToAccept);
            }

            @Override
            public void onException(Actor<String> self, Exception e) {
                System.out.println("Ehhh");
            }
        });

        int count = 0;
        while (true) {
            try {
                myActor.send("toto");
                count++;
            } catch (Actor.DeadException e) {
                assertTrue(count > numberOfMessagesToAccept);
                return;
            }
        }
    }

}