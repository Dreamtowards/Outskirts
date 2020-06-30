package outskirts.client.main;

import outskirts.client.ClientSettings;
import outskirts.client.Outskirts;
import outskirts.util.Side;

/**
 * A indie class to bootstrap client Outskirts
 * That will more clear. than main() in Outskirts.java or readArgument-impl in main()
 */
public class Main {

    public static void main(String[] args) {

        Side.CURRENT = Side.CLIENT;

        ClientSettings.ProgramArguments.readArguments(args);

        new Outskirts().run();
    }

}
