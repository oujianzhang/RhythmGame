package at.kaindorf.myRhythmGame;

public class Calculator {
    public int divide(int a, int b) {
        if( b == 0 ) throw new IllegalArgumentException("Division by Zero not allowed");
        return a/b;
    }
}
