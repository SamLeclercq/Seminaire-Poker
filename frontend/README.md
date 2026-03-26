# Frontend JavaFX Poker Table

Prototype JavaFX minimal en Java 21, structure modulaire, focalise sur un ecran de table de poker.

## Prerequis

- Java 21+

## Lancer l'application

Depuis `frontend/`:

```bash
./gradlew run
```

## Lancer les tests

```bash
./gradlew test
```

## Architecture logique

- `src/main/java/com/seminairepoker/frontend/app/PokerFrontApplication.java`
- `src/main/java/com/seminairepoker/frontend/presentation/view/PokerTableView.java`
- `src/main/java/com/seminairepoker/frontend/presentation/view/PlayerSeatView.java`
- `src/main/java/com/seminairepoker/frontend/presentation/view/CommunityCardsView.java`
- `src/main/java/com/seminairepoker/frontend/presentation/view/PlayerHandView.java`
- `src/main/java/com/seminairepoker/frontend/presentation/view/ActionBarView.java`
- `src/main/java/com/seminairepoker/frontend/presentation/view/PotView.java`
- `src/main/java/com/seminairepoker/frontend/presentation/state/TableUiState.java`
- `src/main/java/com/seminairepoker/frontend/presentation/state/PlayerSeatUiState.java`
- `src/main/java/com/seminairepoker/frontend/infrastructure/assets/AssetLoader.java`

## Ressources CSS

- `src/main/resources/css/table.css`
- `src/main/resources/css/components.css`

## Notes assets

- Pas de sprite sheet ni decoupage.
- Assets optionnels : si une image carte/table est absente, un fallback visuel propre est rendu automatiquement.
