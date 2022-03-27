
using stl::lang::System;
using stl::lang::String;
using stl::math::vec2;

// using ots::gui::GuiRoot;
// using ots::graphics::RenderEngine;

//using outskirts::client::gui::ex::GuiRoot;
//using outskirts::client::render::renderer::RenderEngine;

// using outskirts.client.gui.ex.GuiRoot;
// using outskirts.client.render.renderer.RenderEngine;

namespace test;

class Entity {

    int x;
    int y;
    int z;

}

class EntitySheep : Entity {

    int color;

}

class _Main {

    static void main() {

        EntitySheep es;
        es.color = 1;
        es.x = 5;
        es.y = 6;
        es.z = 7 + es.y;

        int s = sizeof(EntitySheep);



    }

}


/*

    int running = false;
    
    AudioEngine audioEngine;
    RenderEngine renderEngine;

    WorldClient world;
    EntityPlayerSP player;

    GuiRoot rootGui;
    Camera camera;
    Timer timer;
    Window window;
    RayPicker rayPicker;

    Thread thread;
    ThreadWorker threadWorker;

    Profiler profiler;

    void run() {

        start();

        while (running)
        {
            runMainLoop();
        }

        destroy();

    }

    void start() {

        running = true;

        // load mods



    }

    void runMainLoop() {



    }

    void destroy() {

    }

    static void main() {





        String s1;
        s1.base = " kabcTex234";


        String s2;
        s2.base = "Tex";
        int id = s1.find(s2);



        String v = s2.repeat(3);
        int len = v.length();

        vec2<int> v;
        v.x = 8;
        v.y = 9;
        int vfv = v.sum();

        vec2<byte> v2;
        v2.x = (byte)1;
        v2.y = (byte)2;

        byte b2 = v2.x;

        int sz1 = sizeof(vec2<int>);
        int sz2 = sizeof(vec2<byte>);
        int sz3 = sizeof(vec2i);

        v.x = 8;
        v.y = 9;

        int vl = v.x;

        int sz = sizeof(vec2<byte>); // 2 +4
        int sz2 = sizeof(vec2<int>);  // 8 +4

        string s = string();
        s.hash = 21;
        s.base = "okStr";

    }
}


*/