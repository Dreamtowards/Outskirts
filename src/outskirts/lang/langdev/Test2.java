package outskirts.lang.langdev;

import org.lwjgl.Sys;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Test2 {

    public static void main(String[] args) throws IOException {

        byte[] LKUP = "Minecraft 1.11.2".getBytes(StandardCharsets.UTF_8);

        for (File f : new File("/Users/dreamtowards/Library/Application Support/minecraft/versions/1.11.2/1.11.2").listFiles()) {
            if (!f.isFile())
                continue;

            byte[] s = Files.readAllBytes(f.toPath());
            if (CollectionUtils.indexOf(s, LKUP, 0) != -1) {
                System.out.println(s);
            }
        }
    }

}
