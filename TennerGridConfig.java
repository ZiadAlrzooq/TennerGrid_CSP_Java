/**
 * Configuration class for TennerGrid puzzle parameters.
 * Provides customizable grid dimensions and solver settings.
 */
public class TennerGridConfig {
    public static final int DEFAULT_WIDTH = 10;
    public static final int DEFAULT_HEIGHT = 3;
    public static final int MIN_DIGIT = 0;
    public static final int MAX_DIGIT = 9;
    
    private final int width;
    private final int height;
    
    /**
     * Creates a configuration with default dimensions.
     */
    public TennerGridConfig() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    /**
     * Creates a configuration with custom dimensions.
     * 
     * @param width  The width of the grid (number of columns)
     * @param height The height of the grid (number of rows)
     * @throws IllegalArgumentException if dimensions are invalid
     */
    public TennerGridConfig(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getTotalCells() {
        return width * height;
    }
    
    /**
     * Converts a 2D position to a 1D index.
     */
    public int getIndex(int row, int col) {
        return row * width + col;
    }
    
    /**
     * Checks if position is within grid bounds.
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }
}
