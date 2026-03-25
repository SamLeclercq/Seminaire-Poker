module com.seminairepoker.frontend {
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires javafx.controls;

    exports com.seminairepoker.frontend.app;
    exports com.seminairepoker.frontend.infrastructure.assets;
    exports com.seminairepoker.frontend.presentation.state;
    exports com.seminairepoker.frontend.presentation.view;
}

