package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.application.port.TableStateProvider;

import java.util.List;
import java.util.function.Consumer;

public class InMemoryTableStateProvider implements TableStateProvider {

    @Override
    public TableState loadInitialState() {
        return new TableState(
                "LOCAL",
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(
                        new PlayerSeatState(1, "Nina", 1_540, false, true, false),
                        new PlayerSeatState(2, "Leo", 2_240, true, true, false),
                        new PlayerSeatState(3, "Yuki", 980, false, true, true),
                        new PlayerSeatState(4, "Mara", 1_760, false, true, false),
                        new PlayerSeatState(5, "Khan", 1_320, false, true, false),
                        new PlayerSeatState(6, "Iris", 2_100, false, true, false)
                )
        );
    }

    @Override
    public Runnable subscribe(Consumer<TableState> onTableStateUpdated) {
        return () -> { };
    }
}
