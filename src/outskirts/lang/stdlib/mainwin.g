package outskirts.client;

import generalx.opengl.GL31;
import generalx.glfw.GLFW;

class Main {

    Window window;

    boolean running;

    void start() {

        // Window Creation.
        window = Window.create();


    }

    void run() {

        start();


        window.eventbus().register(OnCloseEvent.class, (e) {
            running = false;
        });

        while (running) {


        }

    }

}