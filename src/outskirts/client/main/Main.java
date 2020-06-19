package outskirts.client.main;

import outskirts.client.GameSettings;
import outskirts.client.Outskirts;
import outskirts.mod.Mods;
import outskirts.util.Side;

import java.io.File;

/**
 * A indie class to bootstrap client Outskirts
 * That will more clear. than main() in Outskirts.java or readArgument-impl in main()
 */
public class Main {

    public static void main(String[] args) {

        Side.CURRENT = Side.CLIENT;

        GameSettings.ProgramArguments.readArguments(args);

        new Outskirts().run();
    }

}
