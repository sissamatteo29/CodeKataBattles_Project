package org.example.testsss;

import org.example.App;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class App2Test {
    /**
     * Unit test for simple App.
     */

        App myApp;

        @Test
        public void testAppFunction() {
            myApp = new App(8);
            myApp.updateScore();
            assertEquals("", myApp.getScore(), 9);

        }


}
