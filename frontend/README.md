# Frontend JavaFX

Client JavaFX du projet Seminaire Poker.

## Prerequis

- Java 21+

## Lancer l'application (visuel uniquement)

Depuis `frontend/`:

```bash
./gradlew run
```

Le frontend peut etre lance sans backend pour avancer sur l'UI.

## Lancer depuis la racine du projet

```bash
scripts/dev.sh frontend
```

## Lancer les tests

```bash
./gradlew test
```


## Structure

- `src/main/java/com/seminairepoker/frontend/MainApp.java` : point d'entree JavaFX
- `src/main/resources/com/seminairepoker/frontend/application.css` : style de base
- `src/test/java/com/seminairepoker/frontend/MainAppTest.java` : tests de base
