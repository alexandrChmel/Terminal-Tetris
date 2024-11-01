package tetris;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int width = scanner.nextInt();
        int height = scanner.nextInt();
        TetrisGame tetris = new TetrisGame(height, width);
        tetris.printGrid();
        String command = scanner.next();
        label:
        //gameLoop
        while (true) {
            switch (command) {
                case "rotate" -> tetris.rotate();
                case "piece" -> tetris.addLetter(scanner.next());
                case "break" -> tetris.breakLayer();
                case "exit" -> {
                    return;
                }
                default -> tetris.move(command);
            }
            tetris.printGrid();
            if (tetris.badCollision() || !tetris.getIsCurrentLetterMoving() && tetris.getCurrentLetterMovesCount() == 0) {
                break;
            }
            command = scanner.next();
        }
        System.out.println("Game Over!");
    }
}