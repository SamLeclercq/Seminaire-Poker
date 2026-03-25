package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;

import java.util.List;
import java.util.Objects;

final class TableUiStateMapper {
    private TableUiStateMapper() {
    }

    static TableUiState toUiState(TableState tableState) {
        Objects.requireNonNull(tableState, "tableState must not be null");

        List<PlayerSeatUiState> seatUiStates = tableState.seats().stream()
                .map(TableUiStateMapper::toUiState)
                .toList();

        return new TableUiState(
                tableState.tableCode(),
                tableState.roundLabel(),
                tableState.pot(),
                List.copyOf(tableState.communityCards()),
                List.copyOf(tableState.localPlayerCards()),
                seatUiStates
        );
    }

    private static PlayerSeatUiState toUiState(PlayerSeatState seatState) {
        return new PlayerSeatUiState(
                seatState.seatIndex(),
                seatState.playerName(),
                seatState.stack(),
                seatState.dealer(),
                seatState.occupied(),
                seatState.acting()
        );
    }
}

