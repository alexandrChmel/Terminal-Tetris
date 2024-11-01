package tetris;

public class TetrisGame {
    static int HEIGHT;
    static int WIDTH;
    Letter currentLetter;
    String[][] grid;
    String[][] lettersGrid;
    public TetrisGame(int height, int width) {
        HEIGHT = height;
        WIDTH = width;
        // creating grid
        lettersGrid = new String[HEIGHT][WIDTH];
        fillGrid(lettersGrid);
    }

    public void breakLayer() {
        currentLetter.makeStatic();
        currentLetter.toPrint = false;
        currentLetter = null;
        grid = new String[HEIGHT][WIDTH];

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                System.arraycopy(lettersGrid[j], 0, grid[j], 0, WIDTH);
            }
            int count = 0;
            for (int j = 0; j < WIDTH; j++) {
                if (lettersGrid[i][j].equals("0")) {
                    count++;
                }
            }
            if (count == WIDTH) {
                for (int j = 0; j < WIDTH; j++) {
                    lettersGrid[i][j] = "-";
                }
                for (int j = 0; j < HEIGHT; j++) {
                    for (int k = 0; k < WIDTH; k++) {
                        if (isWithinBounds(j - 1, k)) {
                            lettersGrid[j][k] = grid[j - 1][k];
                        }
                    }
                }
            }
        }
    }

    public void rotate() {
        if (currentLetter == null) {
            return;
        }
        currentLetter.rotate();
    }

    public void move(String direction) {
        if (currentLetter == null) {
            return;
        }
        currentLetter.move(direction);
    }

    public void addLetter(String inputLetter) {
        if (currentLetter != null) {
            currentLetter.makeStatic();
        }
        currentLetter = new Letter(inputLetter);
    }

    public void printGrid() {
        editGrid();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void fillGrid(String[][] grid) {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                grid[i][j] = "-";
            }
        }
    }

    public boolean getIsCurrentLetterMoving() {
        if (currentLetter == null) {
            return false;
        }
        return currentLetter.isMoving;
    }

    public int getCurrentLetterMovesCount() {
        if (currentLetter == null) {
            return 1;
        }
        return currentLetter.movesCount;
    }

    public void editGrid() {
        grid = new String[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            System.arraycopy(lettersGrid[i], 0, grid[i], 0, WIDTH);
        }
        if (currentLetter == null || !currentLetter.toPrint) {
            return;
        }
        int rotation = currentLetter.rotation;
        int moveCol = currentLetter.moveCol;
        int moveRow = currentLetter.moveRow;
        int[][] piece = currentLetter.piece;
        for (int j = 0; j < piece[rotation].length; j++) {
            int col = piece[rotation][j] % WIDTH + moveCol;
            int row = piece[rotation][j] / WIDTH + moveRow;
            grid[row][col] = "0";
        }
    }

    public boolean badCollision() {
        if (currentLetter == null) {
            return false;
        }
        int rotation = currentLetter.rotation;
        int moveCol = currentLetter.moveCol;
        int moveRow = currentLetter.moveRow;
        int[][] piece = currentLetter.piece;

        for (int i = 0; i < piece[rotation].length; i++) {
            int col = piece[rotation][i] % WIDTH + moveCol;
            int row = piece[rotation][i] / WIDTH + moveRow;
            // check
            if (lettersGrid[row][col].equals("0")) {
                return true;
            }
        }
        return false;
    }

    public boolean isWithinBounds(int row, int col) {
        if (row > HEIGHT - 1 || row < 0) {
            return false;
        }
        return col <= WIDTH - 1 && col >= 0;

    }

    private class Letter {

        static int[][] O = {{4, 14, 15, 5}};
        static int[][] I = {{4, 14, 24, 34}, {3, 4, 5, 6}};
        static int[][] S = {{5, 4, 14, 13}, {4, 14, 15, 25}};
        static int[][] Z = {{4, 5, 15, 16}, {5, 15, 14, 24}};
        static int[][] L = {{4, 14, 24, 25}, {5, 15, 14, 13}, {4, 5, 15, 25}, {6, 5, 4, 14}};
        static int[][] J = {{5, 15, 25, 24}, {15, 5, 4, 3}, {5, 4, 14, 24}, {4, 14, 15, 16}};
        static int[][] T = {{4, 14, 24, 15}, {4, 13, 14, 15}, {5, 15, 25, 14}, {4, 5, 6, 15}};
        int[][] piece;
        int rotation = 0;
        int moveRow = 0;
        int moveCol = 0;
        boolean isMoving = true;
        int movesCount = 0;
        boolean toPrint = true;

        public Letter(String inputLetter) {
            switch (inputLetter) {
                case "O" -> piece = O;
                case "I" -> piece = I;
                case "S" -> piece = S;
                case "Z" -> piece = Z;
                case "L" -> piece = L;
                case "J" -> piece = J;
                case "T" -> piece = T;
            }
        }

        public boolean collision() {
            for (int i = 0; i < piece[rotation].length; i++) {
                int col = piece[rotation][i] % WIDTH + moveCol;
                int row = piece[rotation][i] / WIDTH + moveRow;
                // check
                if (isWithinBounds(row + 1, col) && lettersGrid[row + 1][col].equals("0")) {
                    isMoving = false;
                    return true;
                }
            }
            return false;
        }

        public void makeStatic() {
            isMoving = false;
            for (int j = 0; j < piece[rotation].length; j++) {
                int col = piece[rotation][j] % WIDTH + moveCol;
                int row = piece[rotation][j] / WIDTH + moveRow;
                lettersGrid[row][col] = "0";
            }
        }

        public void rotate() {
            if (!isMoving) {
                return;
            }
            if (badCollision()) {
                return;
            }
            if (collision()) {
                return;
            }
            rotation = (rotation + 1) % piece.length;
            if (isBorder() || badCollision()) {
                rotation = (rotation - 1) % piece.length;
            }
            moveRow++;
            if (isBottom()) {
                isMoving = false;
            }
            movesCount++;
        }

        public void move(String direction) {
            if (badCollision()) {
                return;
            }
            if (!isMoving) {
                return;
            }
            if (collision()) {
                return;
            }
            moveRow++;
            if (direction.equals("left")) {
                moveCol--;
                if (isBorder() || badCollision()) {
                    moveCol++;
                }
            } else if (direction.equals("right")) {
                moveCol++;
                if (isBorder() || badCollision()) {
                    moveCol--;
                }
            }
            if (isBottom()) {
                isMoving = false;
            }
            collision();
            movesCount++;
        }

        public boolean isBorder() {
            for (int i = 0; i < piece[rotation].length; i++) {
                int col = piece[rotation][i] % WIDTH + moveCol;
                if (col > WIDTH - 1 || col < 0) {
                    return true;
                }
            }
            return false;
        }

        public boolean isBottom() {
            for (int i = 0; i < piece[rotation].length; i++) {
                int row = piece[rotation][i] / WIDTH + moveRow;
                if (row >= HEIGHT - 1) {
                    return true;
                }
            }
            return false;
        }
    }
}