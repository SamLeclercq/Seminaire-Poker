package com.seminairepoker.frontend.application.port;

public interface PlayerActionPort {
    boolean check(String tableCode);

    boolean call(String tableCode);

    boolean fold(String tableCode);

    boolean bet(String tableCode, int amount);

    boolean raise(String tableCode, int amount);
}

