package com.seminairepoker.frontend.app;

import com.seminairepoker.frontend.application.model.PlayerSeatState;
import com.seminairepoker.frontend.application.model.TableState;
import com.seminairepoker.frontend.presentation.state.PlayerSeatUiState;
import com.seminairepoker.frontend.presentation.state.TableUiState;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

final class TableUiStateMapper {
    private TableUiStateMapper() {
    }

    static TableUiState toUiState(TableState tableState) {
        Objects.requireNonNull(tableState, "tableState must not be null");

        List<PlayerSeatUiState> seatUiStates = tableState.seats().stream()
                .map(TableUiStateMapper::toUiState)
                .toList();

        boolean waitingForReady = isWaitingState(tableState.roundLabel());
        boolean localPlayerReady = tableState.seats().stream()
                .filter(PlayerSeatState::currentPlayer)
                .findFirst()
                .map(PlayerSeatState::ready)
                .orElse(false);

        return new TableUiState(
                tableState.tableCode(),
                tableState.roundLabel(),
                tableState.pot(),
                List.copyOf(tableState.communityCards()),
                List.copyOf(tableState.localPlayerCards()),
                seatUiStates,
                waitingForReady,
                localPlayerReady
        );
    }

    private static PlayerSeatUiState toUiState(PlayerSeatState seatState) {
        return new PlayerSeatUiState(
                seatState.seatIndex(),
                seatState.playerName(),
                seatState.stack(),
                seatState.dealer(),
                seatState.occupied(),
                seatState.acting(),
                seatState.currentPlayer(),
                seatState.ready()
        );
    }

    private static boolean isWaitingState(String roundLabel) {
        if (roundLabel == null) {
            return false;
        }
        return "waiting".equals(roundLabel.trim().toLowerCase(Locale.ROOT));
    }
}

