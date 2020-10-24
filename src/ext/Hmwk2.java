package ext;

import java.util.Scanner;

public class Hmwk2 {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Game().theMainRun();
    }

    public static class Game {

        private Player[] players;

        public void theMainRun() {
            System.out.print("Enter the num that players joins to play: ");
            players = new Player[Integer.parseInt(scanner.nextLine())];
            for (int i = 0;i < players.length;i++) {
                players[i] = new Player();
                System.out.print("Enter the playername("+(i+1)+"): ");
                players[i].name = scanner.nextLine();
            }
            while (true) {
                boolean fgam = false;
                for (int pi = 0;pi < players.length;pi++) {
                    Player currTurnPlayer = players[pi];
                    int score = currTurnPlayer.doDiceAndAddScore();
                    System.out.println(currTurnPlayer.name + " rolled a " + score);

                    if (currTurnPlayer.sumScore > 10)
                        fgam = true;
                }

                if (fgam) {
                    System.out.println("Game Over");
                    for (Player p : players) {
                        System.out.println("Player score: " + p.name + " " + p.sumScore);
                    }
                    break;
                }
            }
        }

    }

    public static class Player {

        private String name;
        private int sumScore;

        public int doDiceAndAddScore() {
            int score = (int)Math.round(Math.random() * 6f);
            sumScore += score;
            return score;
        }
    }

}
