package org.example;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest{

    App myApp;

    @Test
    public void testAppFunction() {
        myApp = new App(8);
        myApp.updateScore();
        assertEquals("", myApp.getScore(), 9);

    }

}
