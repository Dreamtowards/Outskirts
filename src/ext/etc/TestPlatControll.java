package ext.etc;

import outskirts.util.DigestUtils;
import outskirts.util.StringUtils;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static outskirts.util.logging.Log.LOGGER;

public class TestPlatControll {

    public void robo() throws AWTException {

        Robot robot = new Robot();

        robot.mouseMove(10, 10);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        robot.delay(1000);

        robot.keyPress(KeyEvent.VK_META);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);

//        for (int i = 0;i < 10;i++) {
//            System.out.println("A:"+i);
//            robot.keyPress(KeyEvent.VK_A);
//            robot.keyRelease(KeyEvent.VK_A);
//            Thread.sleep(1000);
//        }
        LOGGER.info(StringUtils.toHexString(DigestUtils.sha256("".getBytes())));


    }

}
