package com.seminairepoker.frontend.presentation.state;

public record JoinTableFormUiState(
        boolean visible,
        String placeholder,
        String submitLabel,
        String validationMessage
) {
}

