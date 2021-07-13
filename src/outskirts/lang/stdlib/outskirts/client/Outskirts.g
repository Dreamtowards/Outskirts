package outskirts.client;


class Outskirts {

    @private
    Window window;

    @private
    WorldClient world;

    @private
    boolean running;

    @private
    void start() {

        window = Window.create(1200, 580, "Display");

        window.addOnCloseListener(this._wOnClose);



    }

    @private
    void run() {

        start();

        while (this.running) {

            // TICKING WORLD.
            while (timer.polltick())
            {
                world.onTick();
            }

            // RENDERING.
            renderEngine.prepare();

            renderEngine.render(world);

            renderGUI();

            // SYSTEM
            updateWindow();
        }

        destroy();

    }

    void renderGUI () {
        ...
    }

    void updateWindow() {


        window.swapBuffer();
        window.dispatchEvents();

    }

    void destroy() {

        window.destroy();
    }

}