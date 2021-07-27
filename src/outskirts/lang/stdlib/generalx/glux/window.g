

class window {

    long id;

    float x;
    float y;
    float width;
    float height;

    string title;
    boolean decoration;
    map<int, image> icons;

    boolean active;
    boolean fullscreen;

    // window event listeners.
    eventbus eventbus;

    function<void> init = s_internal_func("nstdlib/gnlx/glux/window::init");

    function<boolean> is_close_requested = s_internal_func("nstdlib/gnlx/glux/window::is_close_requested");

}