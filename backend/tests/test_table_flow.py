import unittest

from core.player import Player
from core.table import Table


class TableFlowTest(unittest.TestCase):
    def test_should_return_player_ids_in_showdown_winnings(self):
        # Arrange
        table = Table("ABCDE")
        player_one = Player("player-1", "Nina")
        player_two = Player("player-2", "Leo")
        table.add_player(player_one)
        table.add_player(player_two)
        player_one.toggle_ready()
        player_two.toggle_ready()
        table.start()

        # Act
        response = table.fold(table.current_player.player_id)

        # Assert
        self.assertIsInstance(response, dict)
        self.assertTrue(all(isinstance(key, str) for key in response.keys()))

    def test_should_advance_to_next_hand_after_resetting_table_state(self):
        # Arrange
        table = Table("ABCDE")
        player_one = Player("player-1", "Nina")
        player_two = Player("player-2", "Leo")
        table.add_player(player_one)
        table.add_player(player_two)
        player_one.toggle_ready()
        player_two.toggle_ready()
        table.start()

        # Act
        table.next_hand()

        # Assert
        self.assertEqual("preflop", table.current_state)
        self.assertEqual(2, table.current_hand)
        self.assertEqual(150, table.pot)
        self.assertIsNotNone(table.current_player)

    def test_should_go_back_to_waiting_when_only_one_active_player_remains(self):
        # Arrange
        table = Table("ABCDE")
        player_one = Player("player-1", "Nina")
        player_two = Player("player-2", "Leo")
        table.add_player(player_one)
        table.add_player(player_two)
        player_one.toggle_ready()
        player_two.toggle_ready()
        table.start()
        player_two.bet(player_two.balance)

        # Act
        table.next_hand()

        # Assert
        self.assertEqual("waiting", table.current_state)
        self.assertIsNone(table.current_player)


if __name__ == "__main__":
    unittest.main()

