# Iterator — Practice Problems
> Goal: Traverse a collection WITHOUT knowing its internal structure.

---

## Key Intuition
**Iterator = reading a book.** The book has chapters. You use a bookmark to track where you are. You call `next()` to go to the next chapter. You don't care if chapters are stored as an array, linked list, or database rows — the ITERATOR hides that.

Java already has `Iterator<T>` and `Iterable<T>`. Your custom collections should implement `Iterable<T>` so they work with `for-each` loops.

---

## Problem 1: Binary Search Tree In-Order Iterator

### Scenario
You have a Binary Search Tree (BST). You want to iterate over all values in **sorted order** (in-order: left → root → right).

The caller shouldn't need to understand BST traversal — they just call `next()` like any other collection.

```java
for (int value : bst) {
    System.out.println(value);  // prints in sorted order
}
```

### Your Task
1. `BSTNode` with `left`, `right`, `value`
2. `BinarySearchTree` implements `Iterable<Integer>`
3. `BSTInOrderIterator` — uses a stack to do iterative in-order traversal

<details>
<summary>🔍 Hint</summary>

For iterative in-order traversal, use a `Stack<BSTNode>`:
1. Push all left children of root to stack
2. `next()`: pop a node, push all left children of its right subtree
3. `hasNext()`: stack is not empty

</details>

<details>
<summary>✅ Pattern Reveal</summary>

**Pattern: Iterator**

Why: The BST has a specific traversal algorithm (in-order). Clients shouldn't know about it. Iterator encapsulates the traversal and exposes a simple `hasNext()/next()` interface.

</details>

### Starter Code

```java
import java.util.*;

public class BSTNode {
    int value;
    BSTNode left, right;

    BSTNode(int value) { this.value = value; }
}

public class BinarySearchTree implements Iterable<Integer> {
    private BSTNode root;

    public void insert(int value) {
        root = insertRec(root, value);
    }

    private BSTNode insertRec(BSTNode node, int value) {
        if (node == null) return new BSTNode(value);
        if (value < node.value) node.left  = insertRec(node.left, value);
        else if (value > node.value) node.right = insertRec(node.right, value);
        return node;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new BSTInOrderIterator(root);
    }

    // In-Order Iterator using a stack
    private static class BSTInOrderIterator implements Iterator<Integer> {
        private final Deque<BSTNode> stack = new ArrayDeque<>();

        public BSTInOrderIterator(BSTNode root) {
            pushAllLeft(root);  // push leftmost path
        }

        private void pushAllLeft(BSTNode node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            BSTNode node = stack.pop();
            pushAllLeft(node.right);  // push left path of right subtree
            return node.value;
        }
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        bst.insert(5);
        bst.insert(3);
        bst.insert(7);
        bst.insert(1);
        bst.insert(4);
        bst.insert(6);
        bst.insert(9);

        // Should print: 1 3 4 5 6 7 9 (sorted order)
        for (int value : bst) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
```

---

## Problem 2: Social Media Feed Iterator

### Scenario
A user's social media feed loads posts. Posts can come from:
- **Friends' posts** (stored in memory)
- **Sponsored posts** (fetched from external API — paginated)

The UI component just wants to iterate: `for (Post post : feed)`. It doesn't care where posts come from.

Create two iterators: `InMemoryFeedIterator` and `PaginatedFeedIterator`.

### Starter Code

```java
import java.util.*;

public record Post(String author, String content, long timestamp) {}

// In-memory iterator — feeds from a pre-loaded list
public class InMemoryFeedIterator implements Iterator<Post> {
    private final List<Post> posts;
    private int currentIndex = 0;

    public InMemoryFeedIterator(List<Post> posts) {
        // Sort by most recent first
        this.posts = posts.stream()
            .sorted(Comparator.comparingLong(Post::timestamp).reversed())
            .toList();
    }

    @Override
    public boolean hasNext() { return currentIndex < posts.size(); }

    @Override
    public Post next() {
        if (!hasNext()) throw new NoSuchElementException();
        return posts.get(currentIndex++);
    }
}

// Paginated iterator — fetches page-by-page from "API"
public class PaginatedFeedIterator implements Iterator<Post> {
    private static final int PAGE_SIZE = 3;
    private final List<Post> allPosts;  // simulate API data
    private int pageNumber = 0;
    private List<Post> currentPage = new ArrayList<>();
    private int pageIndex = 0;

    public PaginatedFeedIterator(List<Post> allPosts) {
        this.allPosts = allPosts;
        fetchNextPage();
    }

    private void fetchNextPage() {
        int from = pageNumber * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allPosts.size());
        currentPage = from < allPosts.size() ? allPosts.subList(from, to) : List.of();
        pageNumber++;
        pageIndex = 0;
        if (!currentPage.isEmpty()) {
            System.out.println("[API] Fetched page " + pageNumber + " (" + currentPage.size() + " posts)");
        }
    }

    @Override
    public boolean hasNext() {
        if (pageIndex < currentPage.size()) return true;
        if (currentPage.size() < PAGE_SIZE) return false;  // last page was partial
        fetchNextPage();
        return !currentPage.isEmpty();
    }

    @Override
    public Post next() {
        if (!hasNext()) throw new NoSuchElementException();
        return currentPage.get(pageIndex++);
    }
}

// Unified feed that combines both iterators
public class SocialFeed implements Iterable<Post> {
    private final List<Post> inMemoryPosts;
    private final List<Post> sponsoredPosts;
    private final boolean usePagination;

    public SocialFeed(List<Post> inMemory, List<Post> sponsored, boolean paginate) {
        this.inMemoryPosts = inMemory;
        this.sponsoredPosts = sponsored;
        this.usePagination = paginate;
    }

    @Override
    public Iterator<Post> iterator() {
        return usePagination
            ? new PaginatedFeedIterator(sponsoredPosts)
            : new InMemoryFeedIterator(inMemoryPosts);
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        List<Post> friends = List.of(
            new Post("Alice", "Hello World!", 1000L),
            new Post("Bob", "Nice day!", 2000L),
            new Post("Carol", "Just coding...", 3000L)
        );

        System.out.println("=== In-Memory Feed (most recent first) ===");
        SocialFeed feed = new SocialFeed(friends, List.of(), false);
        for (Post p : feed) {
            System.out.println(p.author() + ": " + p.content());
        }

        System.out.println("\n=== Paginated Sponsored Feed ===");
        List<Post> sponsored = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            sponsored.add(new Post("Sponsor" + i, "Ad #" + i, (long)i));
        }
        SocialFeed sponsoredFeed = new SocialFeed(List.of(), sponsored, true);
        for (Post p : sponsoredFeed) {
            System.out.println(p.author() + ": " + p.content());
        }
    }
}
```

---

## Problem 3: Playlist Iterator with Skip and Repeat

### Scenario
A music playlist supports:
- `next()` — play next song
- `hasNext()` — any songs left?
- `repeat(boolean)` — when true, after last song, cycle back to first
- `shuffle()` — play in random order

### Starter Code

```java
import java.util.*;

public record Song(String title, String artist) {
    @Override public String toString() { return title + " by " + artist; }
}

public class PlaylistIterator implements Iterator<Song> {
    private final List<Song> songs;
    private int currentIndex;
    private boolean repeat;

    public PlaylistIterator(List<Song> songs, boolean repeat) {
        this.songs = songs;
        this.currentIndex = 0;
        this.repeat = repeat;
    }

    public void setRepeat(boolean repeat) { this.repeat = repeat; }

    @Override
    public boolean hasNext() {
        return currentIndex < songs.size() || repeat;
    }

    @Override
    public Song next() {
        if (!hasNext()) throw new NoSuchElementException("Playlist ended");
        if (currentIndex >= songs.size()) {
            currentIndex = 0;  // repeat: cycle back
        }
        return songs.get(currentIndex++);
    }
}

public class ShufflePlaylistIterator implements Iterator<Song> {
    private final List<Song> shuffled;
    private int currentIndex = 0;

    public ShufflePlaylistIterator(List<Song> songs) {
        shuffled = new ArrayList<>(songs);
        Collections.shuffle(shuffled);
    }

    @Override
    public boolean hasNext() { return currentIndex < shuffled.size(); }

    @Override
    public Song next() {
        if (!hasNext()) throw new NoSuchElementException();
        return shuffled.get(currentIndex++);
    }
}

// Test:
class Main {
    public static void main(String[] args) {
        List<Song> playlist = List.of(
            new Song("Bohemian Rhapsody", "Queen"),
            new Song("Hotel California", "Eagles"),
            new Song("Stairway to Heaven", "Led Zeppelin")
        );

        System.out.println("=== Normal Playthrough ===");
        PlaylistIterator iter = new PlaylistIterator(playlist, false);
        while (iter.hasNext()) {
            System.out.println("Playing: " + iter.next());
        }

        System.out.println("\n=== Shuffle Mode ===");
        ShufflePlaylistIterator shuffle = new ShufflePlaylistIterator(playlist);
        while (shuffle.hasNext()) {
            System.out.println("Playing: " + shuffle.next());
        }

        System.out.println("\n=== Repeat mode (first 6 songs) ===");
        PlaylistIterator repeat = new PlaylistIterator(playlist, true);
        for (int i = 0; i < 6; i++) {
            System.out.println("Playing: " + repeat.next());
        }
    }
}
```
