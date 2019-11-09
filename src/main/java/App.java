import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class App {
    public static void main(String[] args) {
        var app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.logIfServerNotStarted = true;
            config.defaultContentType = "application/json";
            config.enableDevLogging();
            config.registerPlugin(new RouteOverviewPlugin("/info"));
        });

        app.routes(() -> {
            path("/accounts", () -> {
                post(ctx -> {});
                path(":id", () -> {
                    get(ctx -> {});
                    delete(ctx -> {});
                    post("deposit", ctx -> {});
                    post("withdrawal", ctx -> {
                    });
                });
            });
            post("/transfers", ctx -> {});
        });

        app.start(8080);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }
}
