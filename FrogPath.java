public class FrogPath {
    private Pond pond;

    public FrogPath(String filename) {
        try {
            pond = new Pond(filename);
        } catch (Exception e) {
            System.err.println("Error initializing the pond: " + e.getMessage()); //Exception message
        }
    }

    public Hexagon findBest(Hexagon currCell) { //method to find the best path
        ArrayUniquePriorityQueue<Hexagon> potentialMoves = new ArrayUniquePriorityQueue<>(); //creating the priority queue
        
        addSafeNeighbors(currCell, potentialMoves); 
        
        if (currCell.isLilyPadCell() || currCell.equals(pond.getStart())) {//logic for if the current is a lilypad
            addExtendedLilyPadJumps(currCell, potentialMoves);
        }
        
        return potentialMoves.isEmpty() ? null : potentialMoves.removeMin();
    }

    private void addSafeNeighbors(Hexagon cell, ArrayUniquePriorityQueue<Hexagon> potentialMoves) { //Adds only safe neighbours to the queue
        for (int i = 0; i < 6; i++) {
            try {
                Hexagon neighbor = cell.getNeighbour(i);
                if (isValidMove(neighbor)) {
                    double priority = calculatePriority(neighbor);
                    potentialMoves.add(neighbor, priority);
                }
            } catch (InvalidNeighbourIndexException e) {
                System.err.println("Invalid neighbor index: " + e.getMessage());
            }
        }
    }

    private void addExtendedLilyPadJumps(Hexagon currCell, ArrayUniquePriorityQueue<Hexagon> potentialMoves) {
        Hexagon[] neighbors = new Hexagon[6];
        // Populate the immediate neighbors first
        for (int i = 0; i < 6; i++) {
            try {
                neighbors[i] = currCell.getNeighbour(i);
            } catch (InvalidNeighbourIndexException e) {
                // If an invalid neighbor index occurs, log it and continue with the next neighbor
                System.err.println("Invalid neighbor index: " + e.getMessage());
                neighbors[i] = null; // Explicitly set to null to handle the exception scenario
            }
        }

        // Process each neighbor for possible jumps
        for (int i = 0; i < 6; i++) {
            if (neighbors[i] != null) { // Check that the neighbor is not null
                try {
                    Hexagon straightJumpCell = neighbors[i].getNeighbour(i);
                    if (isValidMove(straightJumpCell) && !potentialMoves.contains(straightJumpCell)) {
                        double priority = calculatePriority(straightJumpCell) + 0.5; // Priority for straight line
                        potentialMoves.add(straightJumpCell, priority);
                    }

                    // Check for non-straight line neighbors
                    int leftIndex = (i == 0) ? 5 : i - 1; // Wrap-around index calculation
                    int rightIndex = (i + 1) % 6;

                    Hexagon leftJumpCell = neighbors[i].getNeighbour(leftIndex);
                    if (isValidMove(leftJumpCell) && !potentialMoves.contains(leftJumpCell)) {
                        double priority = calculatePriority(leftJumpCell) + 1.0; // Priority for non-straight line
                        potentialMoves.add(leftJumpCell, priority);
                    }

                    Hexagon rightJumpCell = neighbors[i].getNeighbour(rightIndex);
                    if (isValidMove(rightJumpCell) && !potentialMoves.contains(rightJumpCell)) {
                        double priority = calculatePriority(rightJumpCell) + 1.0; // Priority for non-straight line
                        potentialMoves.add(rightJumpCell, priority);
                    }
                } catch (InvalidNeighbourIndexException e) {
                    // Log and ignore invalid extended jump cells
                    System.err.println("Invalid neighbor index for extended jump: " + e.getMessage());
                }
            }
        }
    }



    private boolean isValidMove(Hexagon currCell) {
        return currCell != null && !currCell.isMarked() && !currCell.isAlligator() && 
               isSafeFromAlligators(currCell) && !currCell.isMudCell();
    }

    private double calculatePriority(Hexagon cell) {
        double priority = 10; // Default priority for an invalid cell

        if (cell.isEnd()) priority = 3.0;
        else if (cell instanceof FoodHexagon) {
            FoodHexagon foodCell = (FoodHexagon) cell;
            switch (foodCell.getNumFlies()) {
                case 1: priority = 2.0; break;
                case 2: priority = 1.0; break;
                case 3: priority = 0.0; break;
            }
        } else if (cell.isLilyPadCell()) priority = 4.0;
        else if (cell.isReedsCell()) priority = 5.0;
        else if (cell.isWaterCell()) priority = 6.0;

        return priority; //returns the priority
    }

    private boolean isSafeFromAlligators(Hexagon cell) {
        for (int i = 0; i < 6; i++) {
            try {
                Hexagon neighbor = cell.getNeighbour(i);
                if (neighbor != null && neighbor.isAlligator()) {
                    if (!cell.isReedsCell()) {
                        return false;
                    }
                }
            } catch (InvalidNeighbourIndexException e) {
                System.err.println("Invalid neighbor index when checking for alligators: " + e.getMessage());
            }
        }
        return true;
    }

    public String findPath() { //Method that allows Freddy to find the best 
        ArrayStack<Hexagon> stack = new ArrayStack<>();
        StringBuilder path = new StringBuilder();
        int fliesEaten = 0;
 
        Hexagon currentCell = pond.getStart();
        stack.push(currentCell);
        currentCell.markInStack(); // Mark the start cell as in-stack
 
        while (!stack.isEmpty()) {
            currentCell = stack.peek();
            path.append(currentCell.getID()).append(" "); // Add the ID of the current cell to the string
 
            if (currentCell.isEnd()) {
                break; // End the loop immediately if the current cell is the end cell
            }
 
            if (currentCell instanceof FoodHexagon) {
                FoodHexagon foodCell = (FoodHexagon) currentCell;
                fliesEaten += foodCell.getNumFlies();
                foodCell.removeFlies(); // Remove flies from the cell
            }
 
            Hexagon nextCell = findBest(currentCell);
 
            if (nextCell == null) {
                stack.pop(); // Pop from the stack if no next cell is found
                currentCell.markOutStack(); // Mark the current cell as out-of-stack
            } else {
                stack.push(nextCell); // Push the next cell onto the stack
                nextCell.markInStack(); // Mark the next cell as in-stack
            }
        }
 
        if (stack.isEmpty()) {
            return "No solution"; // Change the string to “No solution” if the stack is empty
        } else {
            path.append("ate ").append(fliesEaten).append(" flies"); // Add the number of flies eaten to the string
            return path.toString().trim(); // Return the path string
        }
    }
    
   
        
        public static void main(String[] args) {
            // Check if a filename has been provided as a command line argument
            if (args.length < 1) {
                System.out.println("Usage: java FrogPathMain <pond_filename>");
                System.exit(1); // Exit the program if no filename is provided
            }
 
            String filename = args[0]; // Get the filename from command line arguments
 
            // Create an instance of FrogPath with the given filename
            FrogPath frogPath = new FrogPath(filename);
 
            // Call the findPath() method and print out the resulting String
            String pathResult = frogPath.findPath();
            System.out.println(pathResult);
        }
    }
 
    
    

