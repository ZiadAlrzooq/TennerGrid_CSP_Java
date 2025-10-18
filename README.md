# TennerGrid CSP Solver

A Java implementation of a TennerGrid puzzle generator and solver using Constraint Satisfaction Problem (CSP) algorithms.

## Overview

TennerGrid is a logic puzzle where:
- The grid contains digits 0-9
- Each row must contain unique digits (no duplicates)
- No two adjacent cells (including diagonals) can have the same digit
- Each column must sum to a specific target value

This implementation generates random TennerGrid puzzles and solves them using multiple CSP algorithms.

## Features

### Solving Algorithms

The solver implements four different CSP algorithms:

1. **Backtracking**: Basic recursive backtracking without heuristics
2. **Backtracking with MRV**: Uses Minimum Remaining Values heuristic to choose variables
3. **Forward Checking**: Eliminates values from domains proactively
4. **Forward Checking with MRV**: Combines forward checking with intelligent variable ordering

### Performance Metrics

The solver tracks and displays:
- Execution time for each algorithm
- Number of consistency checks performed
- Comparison between different approaches

## Project Structure

```
├── TennerGridSolver.java      # Main solver class with grid generation
├── TennerGridConfig.java      # Configuration for grid dimensions
├── SolverAlgorithm.java       # Enum for available algorithms
├── CSP.java                   # Core CSP solver implementation
├── Variable.java              # Represents a grid cell
├── Constraint.java            # Abstract constraint base class
├── NotEqualConstraint.java    # Constraint for different values
├── ColumnConstraint.java      # Constraint for column sums
└── README.md                  # This file
```

## How to Run

### Compile
```bash
javac *.java
```

### Run
```bash
java TennerGridSolver
```

### Expected Output

Example output:
```
╔═══════════════════════════════════════════════╗
║       TennerGrid CSP Solver                   ║
╚═══════════════════════════════════════════════╝

Grid Configuration:
  Width:  10 columns
  Height: 3 rows
  Total cells: 30

Initial state:
   0   0   7   0   0   0   0   0   0   0
   0   0   0   0   0   0   0   0   0   0
   0   0   0   0   0   0   0   0   0   0
  12  15  18  10  14  16  11  13  17  19

Algorithm: Backtracking
   4   5   7   2   6   8   1   3   9   0
   3   8   6   1   5   7   9   4   2   0
   5   2   5   7   3   1   1   6   6  19

Consistency checks: 542
Time elapsed: 15ms
...
```

## Customization

### Change Grid Size

Modify the grid dimensions in `TennerGridConfig.java`:

```java
public static final int DEFAULT_WIDTH = 10;
public static final int DEFAULT_HEIGHT = 3;
```

Or create a custom configuration:

```java
TennerGridConfig config = new TennerGridConfig(12, 4); // 12 columns, 4 rows
```

### Run Specific Algorithm

To run only a specific algorithm:

```java
TennerGridSolver solver = new TennerGridSolver(config);
solver.generateAllConstraints(csp);
solver.solveAndPrint(csp, SolverAlgorithm.FORWARD_CHECKING_MRV);
```

## Algorithm Comparison

The different algorithms trade off between simplicity and efficiency:

| Algorithm | Description | Performance | Best For |
|-----------|-------------|-------------|----------|
| Backtracking | Simple depth-first search | Slowest | Small problems, educational |
| Backtracking + MRV | Smart variable selection | Faster | Medium problems |
| Forward Checking | Proactive domain pruning | Fast | Most problems |
| Forward Checking + MRV | Combined optimizations | Fastest | Large, complex problems |

## Technical Details

### Constraint Satisfaction Problem (CSP)

A CSP consists of:
- **Variables**: Grid cells that need values
- **Domains**: Possible values (0-9) for each variable
- **Constraints**: Rules that must be satisfied

### Minimum Remaining Values (MRV) Heuristic

MRV chooses the variable with the fewest legal values remaining. This:
- Fails faster on wrong paths
- Reduces the search space
- Often dramatically improves performance

### Forward Checking

Forward checking removes inconsistent values from neighboring variables' domains after each assignment. This:
- Detects failures earlier
- Reduces backtracking
- Maintains arc consistency
