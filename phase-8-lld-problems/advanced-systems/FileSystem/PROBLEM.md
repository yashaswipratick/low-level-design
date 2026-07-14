# Problem: In-Memory File System
> Domain: Systems | Difficulty: Medium | Est. Time: 45 min | Interview Frequency: 📌 Occasional

---

## Problem Statement

Design an in-memory file system supporting files and directories.

Requirements:
1. Create, delete, and rename files and directories
2. Read and write content to files
3. List directory contents (non-recursive and recursive)
4. Search files by name pattern or content
5. File permissions: owner, group, world (read/write/execute)
6. Directory tree traversal: find, list, size calculation
7. Move/copy files and directories (deep copy for directories)

---

## Clarifying Questions to Ask

- Is this a virtual (in-memory) file system or wrapping a real OS file system?
- Is the path separator `/` only or also support Windows `\`?
- Are symbolic links in scope?
- Is concurrent access (two threads writing same file) in scope?
- What is the max file size constraint?
- Should directory size be the sum of all children recursively?

---

## Key Patterns (Hints)

<details>
<summary>Click to reveal</summary>

- **Composite** — `FileSystemNode` is the component; `File` is a leaf; `Directory` is a composite that holds children
- **Iterator** — custom tree iterator for recursive traversal (DFS/BFS)
- **Visitor** — operations like `SizeCalculator`, `SearchVisitor`, `PrintTree` implemented as visitors without modifying node classes

</details>

---

## Class Design Starting Point

```
FileSystemNode (abstract / interface)
  ├── String getName()
  ├── FileSystemNode getParent()
  ├── long getSize()
  └── void accept(FileSystemVisitor visitor)

File extends FileSystemNode
  ├── byte[] content
  ├── Permission permission
  └── long getSize() → content.length

Directory extends FileSystemNode
  ├── Map<String, FileSystemNode> children
  ├── long getSize() → sum of children sizes
  ├── void add(FileSystemNode node)
  └── void remove(String name)

FileSystemVisitor (interface)
  ├── void visitFile(File file)
  └── void visitDirectory(Directory dir)

FileSystem (facade)
  ├── void createFile(String path, byte[] content)
  ├── void createDirectory(String path)
  ├── List<String> list(String path)
  ├── List<String> search(String path, String pattern)
  └── long getSize(String path)
```

---

## Your Task

1. Implement `File` and `Directory` using Composite pattern
2. `FileSystem` facade with path parsing and navigation
3. `SizeCalculatorVisitor` — calculates total size of a directory tree
4. `SearchVisitor` — finds all files matching a name pattern
5. `PrintTreeVisitor` — prints tree structure (like `ls -R`)
6. Iterator for DFS traversal of directory tree
7. Implement in `src/main/java/com/lld/phase8/problems/advanced/filesystem/`

---

## Edge Cases

- Create a file at `/a/b/c.txt` when `/a/b/` doesn't exist yet — auto-create or error?
- Delete a non-empty directory — cascade or reject?
- Move a directory into its own subdirectory — circular reference
- Two files with same name in same directory — reject
- `..` and `.` in paths (e.g., `/a/b/../c/./d.txt`) — normalize before resolving

---

## Extension Points

- Permissions enforcement → check permission in `FileSystemNode` before read/write
- Symbolic links → new `SymLink` leaf node that redirects to target
- Watch service → Observer pattern on directory changes
- Disk quotas per user → Decorator around `FileSystem`
