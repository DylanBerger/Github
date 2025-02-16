import java.util.*;
import java.text.SimpleDateFormat;
// Dylan Berger
// 02/12/2025
// CSE 123
// TA: Eeshani Shilamkar
// This class implements a repository that contains some of the 
// methods seen in real git
public class Repository {
    private Commit head;
    private String name; 

//Behavior: Constructs a new repository with the specified name
//String name: The name of the repository
//If the string name is empty or null, a IllegalArgumentException is thrown
//No returns
    public Repository(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }
// Behavior: 
//   - Returns the head of the repository
//   - If the head does not exist, it returns nothing 
// Parameters:
//   - No paramaters 
// Returns:
//   - Returns the address/ID of the repository head or null
//     if there is no head 
// Exceptions:
//   - No exceptions
    public String getRepoHead() {
        if (head == null) {
            return null;
        }
        return this.head.id;
    }

// Behavior: 
//   - Returns size of the repository (the number of commits) 
// Parameters:
//   - No paramaters 
// Returns:
//   - Returns the size of the repository
// Exceptions:
//   - No exceptions
    public int getRepoSize() {
        int size  = 0;
        Commit curr = head;
        while (curr != null) {
            curr = curr.past;
            size++;
        }

        return size;
    }

// Behavior: 
//   - Returns a string representation of the repository in the form of:
//     <name> - Current head: <head> 
// Parameters:
//   - No paramaters 
// Returns:
//   - Returns a string in the format <name> - Current head: <head>
//     or <name> - No commits, if there are no commits in the repo
// Exceptions:
//   - No exceptions
    public String toString() {
        if (head == null) {
            return name + " - No commits";
        } else {
            return name + " - Current head: " + head.toString();
        }
    }

// Behavior: 
//   - This method determines whether a commit with a specified ID is in the 
//     repository
// Parameters:
//   - String targetId: The target ID the user is searching for 
// Returns:
//   - Returns a true if the repository contains the specified Id, and false
//     if not
// Exceptions:
//   - Throws an IllegalArgumentException if the target Id is null
    public boolean contains(String targetId) {
        Commit curr = head;
        if (targetId == null) {
            throw new IllegalArgumentException();
        } 

        while (curr != null) {
            if (curr.id.equals(targetId)) {
                return true;
            }
            curr = curr.past;
        }

        return false;
    }

// Behavior: 
//   - This method returns the n last commits in the repository, effectively
//     returning its history
// Parameters:
//   - Int n: The number of most recent commits to retrieve, must be a positive
//            number
// Returns:
//   - Returns a string of the n most recent commits to the repository 
// Exceptions:
//   - Throws an IllegalArgumentException if the inputted number is negative or zero
    public String getHistory(int n) {
        Commit curr = head;
        String repoHistory = "";
        if (n <= 0) {
            throw new IllegalArgumentException();

        } else if (head == null) {
            return repoHistory;

        } else if (n > this.getRepoSize()){
            while (curr != null) {
                repoHistory += (curr + "\n");
                curr = curr.past;
            }
            return repoHistory;
        } else {
            while (curr != null && n > 0) {
                repoHistory += (curr + "\n");
                curr = curr.past;
                n--;
            } 
            return repoHistory;
        }
        
    }

// Behavior: 
//   - This method creates a new commit with the specified message 
// Parameters:
//   - String message: The message that the commit will contain. Must not be 
//                     null
// Returns:
//   - Returns the id of the new commit 
// Exceptions:
//   - Throws an IllegalArgumentException if the message is null
    public String commit(String message) {
        if (message == null) {
            throw new IllegalArgumentException();
        }

        Commit newHead = new Commit(message, head);
        head = newHead;
        return head.id;
    }

// Behavior: 
//   - This method removes a commit with the specified Id from the repository 
//     preserving the rest of the repo's history
//   - Stops searching once the commit with targetId is found.
// Parameters:
//   - String targetId: The Id of the commit that is to be removed
// Returns:
//   - Returns True if the commit was successfully removed
//   - Returns false if the program was unable to find a commit with the target id
//     or if the repository is empty
// Exceptions:
//   - Throws an IllegalArgumentException if the targetId is null
    public boolean drop(String targetId) {
        if (targetId == null) {
            throw new IllegalArgumentException();
        } else if (this.head == null) {
            return false;
        }
        Commit curr = head;
        if (curr.id.equals(targetId)) {
            curr = curr.past;
            head = curr;
            return true;
        }
        while (curr.past != null) {
            if (curr.past.id.equals(targetId)) {
                curr.past = curr.past.past;
                return true;
            }
            curr = curr.past;
        }

        return false; 
    }

// Behavior: 
//   - This method gathers the commits from one repository and transfers them
//     into another repository, preserving the order from most recent to least
//     recent commit
//   - If other is empty, this repository remains unchanged
//   - If this is empty, all commits in other are moved here
//   - At the end of the method, other is emptied in all cases
// Parameters:
//   - Repository other: The repository whose contents will be transferred 
// Returns:
//   - No returns
// Exceptions:
//   - Throws an IllegalArgumentException if the other repo is null
    public void synchronize(Repository other) {

        if (other == null) {
            throw new IllegalArgumentException();
        }

        if (other.head != null) {
            if (this.head == null) {
                this.head = other.head;
            } else {

                Commit curr = this.head;
                Commit otherCurr = other.head;
                Commit newHead = null;
                Commit previous = null;

                while (curr != null && otherCurr != null) {
                    if (curr.timeStamp >= otherCurr.timeStamp) {
                        if (newHead == null) {
                            newHead = curr;
                            previous = curr;
                        } else {
                            previous.past = curr;
                            previous = curr;
                        }
                        curr = curr.past;
                    } else {
                        if (newHead == null) {
                            newHead = otherCurr;
                            previous = otherCurr;
                        } else {
                            previous.past = otherCurr;
                            previous = otherCurr;
                        }
                        otherCurr = otherCurr.past;
                    }
                }

                if (curr != null) {
                    previous.past = curr;
                } else {
                    previous.past = otherCurr;
                }

                this.head = newHead; 
            }
        }

        other.head = null; 
    }
    
    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public static class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}