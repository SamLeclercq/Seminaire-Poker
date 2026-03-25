package com.seminairepoker.frontend.infrastructure.provider;

import com.seminairepoker.frontend.application.port.TableStateProvider;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;

import java.util.List;

public class InMemoryTableStateProvider implements TableStateProvider {

    @Override
    public TableUiState loadInitialState() {
        return new TableUiState(
                "Flop",
                240,
                List.of("10_of_hearts", "10_of_spades", "2_of_clubs", "3_of_diamonds", "5_of_hearts"),
                List.of("ace_of_spades", "ace_of_hearts"),
                List.of(
                        new PlayerSeatUiState(1, "Nina", 1_540, false, true, false),
                        new PlayerSeatUiState(2, "Leo", 2_240, true, true, false),
                        new PlayerSeatUiState(3, "Yuki", 980, false, true, true),
                        new PlayerSeatUiState(4, "Mara", 1_760, false, true, false),
                        new PlayerSeatUiState(5, "Khan", 1_320, false, true, false),
                        new PlayerSeatUiState(6, "Iris", 2_100, false, true, false)
                )
        );
    }
}

