package app;

public class AppLauncher {
    public static void main(String[] args) {
        var portEnvVariable = System.getenv("MONEY_TRANSFER_SERVER_PORT");
        try {
            int port = 8080;
            if (portEnvVariable != null) {
                port = Integer.parseInt(portEnvVariable);
            }
            new App().start(port);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port specified");
        }
    }
}
