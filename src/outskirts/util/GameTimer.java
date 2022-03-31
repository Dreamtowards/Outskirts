package outskirts.util;

import outskirts.client.Outskirts;

public class GameTimer {

    public static final int TPS = 20; // ticks pre second, 20 ticks/s
//    private static final long TICK_LENGTH = 1000 / TPS; // 1000/20=50(ms)

    private double prevTime = 0;

    private double delta;

    private double elapsedTicks;

    public void update() {
        double t = Outskirts.getProgramTime();
        if (prevTime == 0)
            prevTime = t;

        delta = t - prevTime;

        elapsedTicks += Math.min(delta * TPS, 10f);

        prevTime = t;
    }

    public float getDelta() {
        return (float)delta;
    }

    public boolean pollFullTick() {
        if (elapsedTicks < 1.0) {
            return false;
        } else {
            elapsedTicks -= 1.0;
            return true;
        }
    }
}
