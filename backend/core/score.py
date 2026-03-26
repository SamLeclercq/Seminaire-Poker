from itertools import combinations
from core.card import Card
from core.card_types import Rank
from core.score_type import HandRank


def calculate_score(pocket: list[Card], community_cards: list[Card]) -> tuple[HandRank, list[int]] | None :
    """
    Calculate the best possible score from the player's pocket cards and the community cards.
    :param pocket: The player's 2 pocket cards.
    :param community_cards: The 5 community cards on the table.
    :return: The best hand achievable as a (HandRank, tiebreakers) tuple.
    :rtype: tuple[HandRank, list[int]]
    """
    if len(pocket) != 2 :
        print("Hand size !2")

    if len(community_cards) != 5:
        print("Community side !5")

    all_cards: list[Card] = pocket + community_cards
    best: tuple[HandRank, list[int]] | None = None

    for combo in combinations(all_cards, 5):
        score = _score_five(list(combo))
        if best is None or _score_key(score) > _score_key(best):
            best = score

    return best


def get_winners(players_scores: dict[str, tuple[HandRank, list[int]]]) -> list[str]:
    """
    Determine the winner(s) from a dict of player scores.
    Returns a single player_id if there is no tie, or multiple player_ids if truly tied.
    :param players_scores: A dict mapping player_id to their score.
    :return: A list with one player_id if no tie, or multiple if truly tied.
    :rtype: list[str]
    """
    if not players_scores:
        return []

    best = max(players_scores.values(), key=_score_key)
    return [pid for pid, score in players_scores.items() if _score_key(score) == _score_key(best)]


def _score_key(score: tuple[HandRank, list[int]]) -> tuple[int, list[int]]:
    """
    Convert a score tuple into a comparable key.
    :param score: A (HandRank, tiebreakers) tuple.
    :return: A (HandRank.value, tiebreakers) tuple that is natively comparable.
    :rtype: tuple[int, list[int]]
    """
    return (score[0].value, score[1])


def _score_five(cards: list[Card]) -> tuple[HandRank, list[int]]:
    """
    Evaluate a 5-card hand and return its score.
    :param cards: Exactly 5 cards to evaluate.
    :return: A (HandRank, tiebreakers) tuple.
    :rtype: tuple[HandRank, list[int]]
    """
    if len(cards) != 5:
        print("Expected exactly 5 cards.")

    ranks = sorted([c.rank.value for c in cards], reverse=True)
    suits = [c.suit for c in cards]
    is_flush    = len(set(suits)) == 1
    is_straight = _is_straight(ranks)

    rank_counts: dict[int, int] = {}
    for r in ranks:
        rank_counts[r] = rank_counts.get(r, 0) + 1

    groups = sorted(rank_counts.items(), key=lambda x: (x[1], x[0]), reverse=True)
    counts = [g[1] for g in groups]
    values = [g[0] for g in groups]

    if is_straight and is_flush:
        if ranks == [Rank.ACE.value, Rank.KING.value, Rank.QUEEN.value, Rank.JACK.value, 10]:
            return (HandRank.ROYAL_FLUSH, _straight_tiebreak(ranks))
        return (HandRank.STRAIGHT_FLUSH, _straight_tiebreak(ranks))

    match (counts[0], counts[1] if len(counts) > 1 else 0, is_flush, is_straight):
        case (4, _, _, _):
            return (HandRank.FOUR_OF_A_KIND, values)
        case (3, 2, _, _):
            return (HandRank.FULL_HOUSE, values)
        case (_, _, True, _):
            return (HandRank.FLUSH, ranks)
        case (_, _, _, True):
            return (HandRank.STRAIGHT, _straight_tiebreak(ranks))
        case (3, _, _, _):
            return (HandRank.THREE_OF_A_KIND, values)
        case (2, 2, _, _):
            return (HandRank.TWO_PAIR, values)
        case (2, _, _, _):
            return (HandRank.ONE_PAIR, values)
        case _:
            return (HandRank.HIGH_CARD, ranks)

def _is_straight(ranks: list[int]) -> bool:
    """
    Check if sorted ranks form a straight, including the A-2-3-4-5 low straight.
    :param ranks: Sorted list of rank values (descending).
    :return: True if the ranks form a straight.
    :rtype: bool
    """
    return (len(set(ranks)) == 5 and ranks[0] - ranks[4] == 4) or (ranks == [Rank.ACE.value, 5, 4, 3, 2])

def _straight_tiebreak(ranks: list[int]) -> list[int]:
    """
    Return the correct tiebreaker for a straight.
    For A-2-3-4-5 (la roue), the straight is 5-high, not Ace-high.

    :param ranks: Sorted list of rank values (descending).
    :return: A single-element list with the highest card of the straight.
    :rtype: list[int]
    """
    return [5] if ranks == [Rank.ACE.value, 5, 4, 3, 2] else [ranks[0]]