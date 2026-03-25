package com.seminairepoker.frontend.presentation.state;

public record HomePageUiState(
        String title,
        String subtitle,
        String createTableLabel,
        String joinTableLabel,
        JoinTableFormUiState joinTableForm
) {
}

