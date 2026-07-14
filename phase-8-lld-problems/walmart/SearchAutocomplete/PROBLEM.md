# Problem: Search Autocomplete / Typeahead
> Domain: Walmart E-commerce | Difficulty: Hard | Est. Time: 60 min

---

## Problem Statement

Design a search autocomplete system that suggests completions as a user types, ranked by relevance.

Requirements:
1. As user types, return top-N suggestions within low latency
2. Ranking strategies: global frequency, personalized (user's history), trending (last 24h spike)
3. Handle typos — "samsng" should suggest "samsung"
4. Suggestions update when the product catalog changes (new products, discontinuations)
5. The ranking algorithm must be swappable without changing the suggestion retrieval logic

---

## Clarifying Questions to Ask

- Is ranking personalized per user or global?
- What is the acceptable latency budget (e.g. < 100ms)?
- How frequently does the catalog change — real-time updates or daily batch?
- Is typo tolerance fuzzy match or phonetic?
- Should suggestions include categories and brands, not just product names?
- What is the maximum prefix length we need to index?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Strategy** — ranking algorithm is pluggable (frequency, personalized, trending)
- **Decorator** — add typo tolerance on top of base suggestion provider
- **Factory** — create appropriate provider/strategy based on query context
- **Observer** — catalog updates invalidate/refresh the index

</details>

---

## Your Task

1. Design `SuggestionProvider`, `RankingStrategy`, `AutocompleteService`
2. Demonstrate: searching "iph" returns iPhone suggestions ranked by frequency
3. Demonstrate: switching from FrequencyRankingStrategy to PersonalizedRankingStrategy with zero change to `AutocompleteService`
4. Implement in `src/main/java/com/lld/phase8/problems/walmart/search/`
5. Discuss (don't implement in full): how Trie fits into this design

---

## Edge Cases

- Empty query — return trending searches
- Query with only special characters — return nothing or error
- Single character query — too many results, how to limit?
- User is not logged in — cannot personalize, fall back to global frequency
