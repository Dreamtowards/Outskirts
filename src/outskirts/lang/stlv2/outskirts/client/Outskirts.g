
namespace outskirts::client;


class Outskirts {

    static Outskirts INST;

    bool running;

    RenderEngine renderEngine;
    AudioEngine audioEngine;

    WorldClient world;
    EntityPlayerSP player;

    GuiRoot rootGui;
    Camera camera;
    Timer timer;
    Window window;
    RayPicker rayPicker;
    ThreadWorker scheduler;
    Profiler profiler;


    void run() {

        this.start();

        while (this.running)
        {
            this.runMainLoop();
        }

        this.destroy();
    }

    void start() {

        this.running = true;
        Mods::loadMods();
        ClientSettings::loadSettings();
        this.window = new Window();
        Init::registerAll(Side::CLIENT);

        renderEngine = new RenderEngine();
        audioEngine = new AudioEngine();

        rootGui.add(GuiMainMenu::INST);


    }

    static void runMainLoop() {

        timer.update();
        scheduler.processTasks();
        window.processInput();

        while (timer.pollTick())
        {
            runTick();
        }

        renderEngine.prepare();

        if (world) {
            camera.update();
            rayPicker.update();

            renderEngine.render(world);
        }
        renderGUI();

        window.update();
    }

    static void destroy() {

        delete renderEngine;
        delete audioEngine;

        delete rootGui;
        delete camera;
        delete timer;
        delete window;
        delete rayPicker;
        delete scheduler;
        delete profiler;
    }
}