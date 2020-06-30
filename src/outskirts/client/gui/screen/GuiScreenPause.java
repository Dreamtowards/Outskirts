package outskirts.client.gui.screen;

import outskirts.client.Outskirts;
import outskirts.client.gui.GuiButton;
import outskirts.util.vector.Vector4f;

public class GuiScreenPause extends GuiScreen {

    public static final GuiScreenPause INSTANCE = new GuiScreenPause();

    private GuiScreenPause() {

//        Vector3f[] circle_s = new Vector3f[10];
//        for (int i = 0;i < 10;i++) {
//            Vector3f vec = Matrix3f.transform(Matrix3f.rotate(i/10f * 2* Maths.PI, Vector3f.UNIT_Z, null), new Vector3f(1, 0, 0), null);
//            circle_s[i] = vec.add(2,0.8f,0);
//        }
//
//        Vector3f[] box_s = new Vector3f[] {
//                new Vector3f(-1, 1, 0),
//                new Vector3f( 1, 1, 0),
//                new Vector3f( 1,-1, 0),
//                new Vector3f(-1,-1, 0)
//        };
//        for (Vector3f v : box_s)
//            v.add(new Vector3f(1,-1,0));
//
//        addBeforeDrawListener(e -> {
//
//            for (Vector3f vbox : box_s)
//                for (Vector3f vcir : circle_s)
//                    drawPoint(Colors.RED, vbox.x-vcir.x, vbox.y-vcir.y);
//
//            for (Vector3f v : box_s)
//                drawPoint(Colors.GREEN, v.x, v.y);
//
//            for (Vector3f v : circle_s)
//                drawPoint(Colors.DARK_GREEN, v.x, v.y);
//
//        });

    }

    private static void drawPoint(Vector4f color, float x, float y) {
        drawRect(color, (int)(Outskirts.getWidth()/2 + x*50f), (int)(Outskirts.getHeight()/2 - y*50f), 2, 2);
    }

    private GuiButton btnBack = addGui(new GuiButton("Back")).addOnClickListener(e -> {
        Outskirts.closeScreen();
    }).addOnLayoutListener(e -> {
        e.gui().setX(Outskirts.getWidth() - e.gui().getWidth() - 20);
        e.gui().setY(100);
    });

    private GuiButton btnOptions = addGui(new GuiButton("Options")).addOnClickListener(e -> {
        Outskirts.startScreen(GuiScreenOptions.INSTANCE);
    }).addOnLayoutListener(e -> {
        e.gui().setX(Outskirts.getWidth() - e.gui().getWidth() - 20);
        e.gui().setY(160);
    });

    private GuiButton btnDisconne = addGui(new GuiButton("Disconne")).addOnClickListener(e -> {
        Outskirts.setWorld(null);
//        Outskirts.getPlayer().connection.closeChannel("Dinsconne");  //client ext.test
        Outskirts.closeScreen();
        Outskirts.startScreen(GuiScreenMainMenu.INSTANCE);
    }).addOnLayoutListener(e -> {
        e.gui().setX(Outskirts.getWidth() - e.gui().getWidth() - 20);
        e.gui().setY(220);
    });

}
