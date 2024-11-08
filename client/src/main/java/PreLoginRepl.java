import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreLoginRepl {
    private final ChessClient client;

    public PreLoginRepl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC51 Welcome to the Chess Arena. Enter 'help' for options. \uD83D\uDC51");

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Leaving Chess Arena. Come back soon!")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}