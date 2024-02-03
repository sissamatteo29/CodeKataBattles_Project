package org.example;

/**
 * Hello world!
 */
public class App {
    int score;

    public App(int score) {
        this.score = score;
    }

    public int updateScore() {
        score += 1;
        return score;
    }

    public int getScore(){
        return this.score;
    }
}
