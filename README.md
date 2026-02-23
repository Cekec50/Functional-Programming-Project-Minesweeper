# Scala Minesweeper

A classic implementation of the Minesweeper game built with Scala and the Swing UI toolkit. This project showcases the use of functional programming principles in a desktop application, featuring dynamic board generation, game state management, and custom level creation.

## Tech Stack

- **Language:** [Scala](https://www.scala-lang.org/) (v2.13.16)
- **Build Tool:** [sbt](https://www.scala-sbt.org/) (v1.11.6)
- **UI Framework:** [Scala Swing](http://www.scala-lang.org/api/2.12.1/scala/swing/index.html)
- **Testing:** [ScalaTest](https://www.scalatest.org/)

## Features

- **Classic Minesweeper Gameplay:** Navigate a minefield, flagging mines and clearing safe cells.
- **Multiple Difficulty Levels:** Choose from predefined levels like Beginner, Intermediate, and Expert.
- **Save & Load Games:** Save your progress at any time and load it back later.
- **High Score Tracking:** Compete against yourself with a local high score board.
- **Custom Level Creator:** Design and build your own challenging Minesweeper levels.
- **Board Isometries:** Apply geometric transformations like rotation and reflection to the fields in the custom leavel creator

## Getting Started

Follow these instructions to get the project running on your local machine for development and testing purposes.

### Prerequisites

You will need the following software installed on your system:

- **Java Development Kit (JDK):** Version 11 or newer.
- **sbt (Simple Build Tool):** The standard build tool for Scala projects. You can find installation instructions [here](https://www.scala-sbt.org/download.html).

### Installation & Running

1.  **Clone the repository:**
    ```sh
    git clone <repository-url>
    cd Functional-Programming-Project-Minesweeper
    ```

2.  **Run the application:**
    `sbt` handles dependency installation automatically. To compile and run the project, execute the following command in the project's root directory:
    ```sh
    sbt run
    ```
    The command will download the necessary dependencies, compile the source code, and launch the main application window.

## Project Structure

The repository is organized as follows:

```
├── build.sbt               # sbt build configuration and dependencies
├── levels/                 # Pre-defined level files
├── saved/                  # Directory for saved game states
├── src/
│   ├── main/
│   │   ├── scala/
│   │   │   ├── controller/ # Game logic and state management
│   │   │   ├── isometries/ # Geometric board transformation logic
│   │   │   ├── model/      # Core data models (Board, Field, etc.)
│   │   │   └── ui/         # Swing-based UI components
│   │   └── resources/      # Image assets
│   └── test/
│       └── scala/          # Unit and integration tests
└── ...
```

## Usage

Once the application is running, you will be greeted by the main menu. From here you can:

- **Start a New Game:** Select a difficulty and begin playing.
- **Load a Game:** Open a previously saved game file.
- **View Highscores:** See the best times for completed levels.
- **Create a Level:** Open the level editor to design your own board.

In the game, **left-click** to reveal a cell and **right-click** to place or remove a flag. Good luck!
