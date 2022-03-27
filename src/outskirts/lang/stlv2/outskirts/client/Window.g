namespace outskirts::client;

class Window {

    long windowId;
    float x;
    float y;
    float width;
    float height;

    float mouseX;
    float mouseY;
    float mouseDX;
    float mouseDY;   // todo: FFD* FullFrame Deltas. for Frame-wise Sum
    float scrollDX;
    float scrollDY;

    String title;
    bool resizable;

    Window() {

        // listen to the window events
        // MouseMove, MouseScroll, MouseButton
        // KeyboardKey, CharInput
    }

}