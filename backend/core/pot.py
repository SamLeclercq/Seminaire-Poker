from core.score_type import HandRank
from core.score import get_winners
import random

def calculate_pots(contributions: dict[str, int]) -> list[tuple[int, list[str]]]:
    """
    Calculate main pot and side pots from player contributions.
    Each unique contribution level creates a new pot.

    :param contributions: A dict mapping player_id to total amount contributed this hand.
    :return: List of (amount, eligible_player_ids) tuples, main pot first then side pots.
    :rtype: list[tuple[int, list[str]]]
    """
    if not contributions:
        return []
    levels = sorted(set(contributions.values()))
    pots: list[tuple[int, list[str]]] = []
    prev_level = 0

    for level in levels:
        eligible = [pid for pid, amount in contributions.items() if amount >= level]
        pot_amount = (level - prev_level) * len(eligible)

        if pot_amount > 0:
            pots.append((pot_amount, eligible))

        prev_level = level
    return pots


def distribute_pots(
        pots: list[tuple[int, list[str]]],
        players_scores: dict[str, tuple[HandRank, list[int]] | None]
) -> dict[str, int]:
    """
    Distribute each pot to its winner(s) based on hand scores.
    In case of a tie, the pot is split equally. Any remainder goes to the first winner.
    :param pots: Output of calculate_pots.
    :param players_scores: A dict mapping player_id to their score (None if folded).
    :return: A dict mapping player_id to total amount won this hand.
    :rtype: dict[str, int]
    """
    winnings: dict[str, int] = {}


    for pot_amount, eligible_ids in pots:
        eligible_scores = {
            pid: score
            for pid, score in players_scores.items()
            if pid in eligible_ids and score is not None
        }

        #if nobody is eligible for this pot -> next turn
        if not eligible_scores:
            continue

        winners = get_winners(eligible_scores)

        if not winners:
            continue

        share = pot_amount // len(winners)
        remainder = pot_amount % len(winners)

        for winner_id in winners:
            winnings[winner_id] = winnings.get(winner_id, 0) + share

        if remainder > 0:
            the_lucky_one = random.choice(winners)
            winnings[the_lucky_one] = winnings.get(the_lucky_one, 0) + remainder

    return winnings