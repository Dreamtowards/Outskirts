package generalx.wiut;


class Window {

    float getWidth();
    void setWidth(float width);

    float getHeight();
    void setHeight(float height);

    float getX();
    void setX(float x);

    float getY();
    void setY(float y);

    String getTitle();
    void setTitle(String title);

    Map<int, Image> getIcon();

    void setDecoration();

    boolean isActive();

    boolean isFullscreen();


}