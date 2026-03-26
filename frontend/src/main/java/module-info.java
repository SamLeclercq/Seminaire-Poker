module com.seminairepoker.frontend {
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires java.net.http;
    requires javafx.controls;

    opens com.seminairepoker.frontend.infrastructure.websocket.transport to com.fasterxml.jackson.databind;

    exports com.seminairepoker.frontend.app;
    exports com.seminairepoker.frontend.infrastructure.assets;
    exports com.seminairepoker.frontend.presentation.state;
    exports com.seminairepoker.frontend.presentation.view;
}

