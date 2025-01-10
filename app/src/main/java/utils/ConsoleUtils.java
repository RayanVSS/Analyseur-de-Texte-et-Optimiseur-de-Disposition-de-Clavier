package utils;

public class ConsoleUtils {

    /**
     * Efface la console de maniere compatible avec Windows et Unix.
     */
    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}