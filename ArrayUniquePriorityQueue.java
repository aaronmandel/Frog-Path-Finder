public class ArrayUniquePriorityQueue<T> implements UniquePriorityQueueADT<T> {
    private static final int INITIAL_CAPACITY = 10;
    private Object[] queue;
    private double[] priority;
    private int count;

    public ArrayUniquePriorityQueue() {
        // Initialize arrays with initial capacity
        this.queue = new Object[INITIAL_CAPACITY];
        this.priority = new double[INITIAL_CAPACITY];
        this.count = 0;
    }

    @Override
    public void add(T data, double prio) {
        if (contains(data)) {// Checks if the given data item is already contained in the queue
            return;
        }

        //Expands the capacity
        if (count == queue.length) {
            expandCapacity();
        }

        // Finds the correct index for ordering and ensures priority is kept
        int index = 0;
        while (index < count && prio >= priority[index]) {
            index++;
        }

        // Shift elements to make room for the new item
        for (int i = count - 1; i >= index; i--) {
            queue[i + 1] = queue[i];
            priority[i + 1] = priority[i];
        }

        // Adds new item at the correct position
        queue[index] = data;
        priority[index] = prio;
        count++;
    }


    @Override
    public boolean contains(T data) {
        for (int i = 0; i < count; i++) {
            if (data.equals(queue[i])) {
                return true;
            }
        }
        return false;
    }


    // Helper that expands the capacity of the arrays
    private void expandCapacity() {
        int newCapacity = queue.length + 5;
        Object[] newQueue = new Object[newCapacity];
        double[] newPriority = new double[newCapacity];

        // Copies the elements to the new arrays
        for (int i = 0; i < count; i++) {
            newQueue[i] = queue[i];
            newPriority[i] = priority[i];
        }

        this.queue = newQueue;
        this.priority = newPriority;
    }
    
    
    @Override
    public boolean isEmpty() {
        return count == 0;
    }

   
    

	public T peek() throws CollectionException { 
        if (isEmpty()) { 
            throw new CollectionException("PQ is empty");
        }

        return (T) queue[0];
    }

	@Override
    public T removeMin() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }

        // Get and remove the item with the smallest priority
        T minItem = (T) queue[0];

        // Shift subsequent values in both arrays
        for (int i = 0; i < count - 1; i++) {
            queue[i] = queue[i + 1];
            priority[i] = priority[i + 1];
        }

        // Clear the last element and decrement count
        queue[count - 1] = null;
        priority[count - 1] = 0;
        count--;

        return minItem;
    }
		
		
		
	public void updatePriority(T data, double newPrio) throws CollectionException {
	    int index = -1;
	    for (int i = 0; i < count; i++) {
	        if (data.equals(queue[i])) {
	            index = i;
	            break;
	        }
	    }

	    if (index == -1) {
	        throw new CollectionException("Item not found in PQ"); //  exception message
	    }

	    // Temporarily store the item to re-add it
	    T tempData = (T) queue[index];

	    // Shift elements to "remove" the item at the found index
	    for (int i = index; i < count - 1; i++) {
	        queue[i] = queue[i + 1];
	        priority[i] = priority[i + 1];
	    }
	    count--; // Decrement count to reflect the removal

	    // Re-add the item with the new priority, ensuring correct ordering
	    add(tempData, newPrio);
	}

    // Helper method to remove an item and its priority at a specific index
    private void removeAtIndex(int index) {
        for (int i = index; i < count - 1; i++) {
            queue[i] = queue[i + 1];
            priority[i] = priority[i + 1];
        }

        // Clear the last element and decrement count
        queue[count - 1] = null;
        priority[count - 1] = 0;
        count--;
    }

    // Helper method to find the index of an item in the queue, -1 if not found
    private int indexOf(T item) {
        for (int i = 0; i < count; i++) {
            if (item.equals(queue[i])) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public int size() {
        return count;
    }

    public int getLength() {
        return queue.length; // Assuming this method is meant to return the capacity.
    }

    @Override
    public String toString() {
        // Check if the priority queue is empty
        if (isEmpty()) {
            return "The PQ is empty";
        }

        // Use StringBuilder for efficient string concatenation
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) { // Iterate only through the filled elements
            if (i > 0) {
                sb.append(", "); // Add a separator between elements
            }
            sb.append(queue[i]).append(" [").append(priority[i]).append("]");
        }

        return sb.toString();
    }


	
	
	
}
