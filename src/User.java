import java.io.Serializable;

/**
 * @author Justin Chong
 * Email: justin.chong@stonybrook.edu
 * Student ID: 116143020
 * Recitation Number: CSE 214 R03
 * TA: Kevin Zheng
 * Represents a user in the follow graph system.
 * Each user has a unique username and an index position within the graph.
 * Implements Serializable to allow saving and loading user data.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName; // The username of the user.
    private int indexPos;    // The index position of the user in the graph.
    private static int userCount; // A static counter to assign unique index positions to users.

    /**
     * Constructs a new User with the specified username.
     * The user's index position is automatically assigned based on the current user count.
     *
     * @param u The username of the user.
     */
    public User(String u) {
        userName = u;
        indexPos = userCount;
        userCount++;
    }

    /**
     * Gets the username of this user.
     *
     * @return The username of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of this user.
     *
     * @param userName The new username to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the index position of this user in the graph.
     *
     * @return The index position of the user.
     */
    public int getIndexPos() {
        return indexPos;
    }

    /**
     * Sets the index position of this user in the graph.
     *
     * @param indexPos The new index position to set.
     */
    public void setIndexPos(int indexPos) {
        this.indexPos = indexPos;
    }

    /**
     * Gets the total number of users created so far.
     *
     * @return The total user count.
     */
    public static int getUserCount() {
        return userCount;
    }

    /**
     * Sets the total user count. This can be used for resetting or loading state.
     *
     * @param c The new user count to set.
     */
    public static void setUserCount(int c) {
        userCount = c;
    }
}
