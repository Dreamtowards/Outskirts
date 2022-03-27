
namespace outskirts::client;


class Outskirts {

    static Outskirts INST;

    static bool running;

    static RenderEngine renderEngine;
    static AudioEngine audioEngine;

    static WorldClient world;
    static EntityPlayerSP player;

    static GuiRoot rootGui;
    static Camera camera;
    static Timer timer;
    static Window window;
    static RayPicker rayPicker;
    static ThreadWorker scheduler;
    static Profiler profiler;


    static void run() {

        start();

        while (running)
        {
            runMainLoop();
        }

        destroy();
    }

    static void start() {

        running = true;
        Mods::loadMods();
        ClientSettings::loadSettings();
        window = new Window();
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