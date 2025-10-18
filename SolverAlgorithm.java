/**
 * Enumeration of available CSP solving algorithms.
 * Each algorithm represents a different approach to solving constraint satisfaction problems.
 */
public enum SolverAlgorithm {
    /**
     * Basic recursive backtracking without heuristics.
     */
    BACKTRACKING("Backtracking", 1),
    
    /**
     * Backtracking with Minimum Remaining Values (MRV) heuristic.
     * Chooses the variable with the fewest legal values first.
     */
    BACKTRACKING_MRV("Backtracking with MRV", 2),
    
    /**
     * Forward checking algorithm.
     * Eliminates values from domains before recursive calls.
     */
    FORWARD_CHECKING("Forward Checking", 3),
    
    /**
     * Forward checking with MRV heuristic.
     * Combines forward checking with intelligent variable ordering.
     */
    FORWARD_CHECKING_MRV("Forward Checking with MRV", 4);
    
    private final String displayName;
    private final int algorithmId;
    
    SolverAlgorithm(String displayName, int algorithmId) {
        this.displayName = displayName;
        this.algorithmId = algorithmId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getAlgorithmId() {
        return algorithmId;
    }
    
    /**
     * Gets algorithm by its ID.
     * 
     * @param id The algorithm identifier
     * @return The corresponding algorithm, or null if not found
     */
    public static SolverAlgorithm fromId(int id) {
        for (SolverAlgorithm algorithm : values()) {
            if (algorithm.algorithmId == id) {
                return algorithm;
            }
        }
        return null;
    }
}
