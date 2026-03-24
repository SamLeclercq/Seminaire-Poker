package com.seminairepoker.frontend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainAppTest {

    @Test
    void windowTitleIsStable() {
        assertEquals("Seminaire Poker - Frontend", MainApp.WINDOW_TITLE);
    }

    @Test
    void appClassCanBeInstantiated() {
        assertTrue(new MainApp() instanceof MainApp);
    }
}
