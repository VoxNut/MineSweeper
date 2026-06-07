# Minesweeper Project - Entity Relationship Diagram (Mermaid)

Here is the Mermaid code for the ERD of the database schema (Firestore collections: `users`, `scores`, `gameConfig`).

```mermaid
erDiagram
    users {
        string uid PK "Unique User ID"
        string email "Email address"
        string displayName "Name of user"
        string photoURL "URL to avatar"
        string role "player or admin"
        boolean isBlocked "True if banned by admin"
        int eloRating "Base rating starts at 1000"
        timestamp createdAt "Registration time"
        timestamp lastLoginAt "Last successful login time"
    }

    scores {
        string scoreId PK "Unique Score ID"
        string uid FK "Foreign Key to users.uid"
        string displayName "Denormalized user name"
        string difficulty "easy / medium / hard / custom"
        int timeSec "Time taken to complete (seconds)"
        string result "win / lose"
        int boardRows "Number of rows"
        int boardCols "Number of columns"
        int mineCount "Number of mines"
        timestamp playedAt "Time when match ended"
        boolean isFlagged "True if flagged as cheating"
        int eloAfter "Elo rating after match"
    }

    gameConfig {
        string documentId PK "default"
        object easy "Nested DifficultyConfig"
        object medium "Nested DifficultyConfig"
        object hard "Nested DifficultyConfig"
        timestamp updatedAt "Time when config was modified"
        string updatedBy FK "Foreign Key to users.uid (Admin)"
    }

    users ||--o{ scores : "plays"
    users ||--o{ gameConfig : "configures"
```

## How to use

Copy the code block above and paste it into any Mermaid-compatible parser (like GitHub markdown, Notion, or [Mermaid Live Editor](https://mermaid.live)).
