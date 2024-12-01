import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author Justin Chong
 * Email: justin.chong@stonybrook.edu
 * Student ID: 116143020
 * Recitation Number: CSE 214 R03
 * TA: Kevin Zheng
 * The FollowGraph class represents a graph-based structure for managing a social network where users can follow each other.
 * It supports functionalities like adding users, creating/removing connections, finding paths between users, and saving/loading the graph.
 */
public class FollowGraph implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * List of all users in the graph
     */
    private ArrayList<User> users;

    /**
     * Maximum number of users allowed in the graph
     */
    public static final int MAX_USERS = 100;

    /**
     * Adjacency matrix to store connections between users
     */
    private boolean[][] connections;

    /**
     * Constructs a new FollowGraph object.
     */
    public FollowGraph() {
        users = new ArrayList<>();
        connections = new boolean[MAX_USERS][MAX_USERS];
    }

    /**
     * Adds a new user to the graph if the user does not already exist.
     *
     * @param userName the name of the user to be added
     * @throws IllegalStateException if the maximum number of users is reached
     */
    public void addUser(String userName) {
        User.setUserCount(users.size());
        for (User user : users) {
            if (user.getUserName().equals(userName)) {
                return;
            }
        }
        if (users.size() >= MAX_USERS) {
            throw new IllegalStateException("Maximum number of users reached.");
        }
        User u = new User(userName);
        users.add(u);
    }

    /**
     * Adds a connection from one user to another.
     *
     * @param userFrom the username initiating the connection
     * @param userTo   the username receiving the connection
     */
    public void addConnections(String userFrom, String userTo) {
        User from = getUserByName(userFrom);
        User to = getUserByName(userTo);

        if ((from != null) && (to != null)) {
            connections[from.getIndexPos()][to.getIndexPos()] = true;
        }
    }

    /**
     * Removes a user and all their connections from the graph.
     *
     * @param user the username to be removed
     */
    public void removeUser(String user) {
        User u = getUserByName(user);
        int idx = u.getIndexPos();
        if (u != null) {
            users.remove(u);
            for (int i = idx; i < users.size(); i++) {
                for (int j = 0; j < connections.length; j++) {
                    connections[i][j] = connections[i + 1][j];
                }
            }
            for (int j = idx; j < users.size(); j++) {
                for (int i = 0; i < connections.length; i++) {
                    connections[i][j] = connections[i][j + 1];
                }
            }
            for (int i = 0; i < connections.length; i++) {
                connections[users.size()][i] = false;
                connections[i][users.size()] = false;
            }
            for (int i = 0; i < users.size(); i++) {
                users.get(i).setIndexPos(i);
            }
        }
    }

    /**
     * Removes a connection between two users.
     *
     * @param userFrom the username initiating the connection
     * @param userTo   the username receiving the connection
     */
    public void removeConnection(String userFrom, String userTo) {
        User from = getUserByName(userFrom);
        User to = getUserByName(userTo);

        if ((from != null) && (to != null)) {
            connections[from.getIndexPos()][to.getIndexPos()] = false;
        }
    }

    /**
     * Finds and returns a user object by their username.
     *
     * @param userName the name of the user to search for
     * @return the User object if found, otherwise null
     */
    public User getUserByName(String userName) {
        for (User user : users) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Finds the shortest path between two users in the graph.
     *
     * @param userFrom the starting user
     * @param userTo   the target user
     * @return a string representation of the shortest path, or a message if no path exists
     */
    public String shortestPath(String userFrom, String userTo) {
        User from = getUserByName(userFrom);
        User to = getUserByName(userTo);

        if (from == null) {
            return "The vertex " + userFrom + " does not exist.";
        }
        if (to == null) {
            return "The vertex " + userTo + " does not exist.";
        }

        List<String> shortestPath = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        dfs(from.getIndexPos(), to.getIndexPos(), visited, currentPath, shortestPath);

        if (shortestPath.isEmpty()) {
            return "No path found.";
        }
        return String.join(" -> ", shortestPath);
    }

    /**
     * Prints all users in the graph with their follower and following counts, sorted by a given comparator.
     *
     * @param comp the comparator to use for sorting users
     */
    public void printAllUsers(Comparator<User> comp) {
        ArrayList<User> temp = new ArrayList<>(users);
        temp.sort(comp);
        String header = "User Name              " + "Number of Followers     " + "Number of Following";
        System.out.println(header);
        for (User user : temp) {
            String userName = user.getUserName();
            int followersCount = countFollowers(user.getIndexPos());
            int followingCount = countFollowing(user.getIndexPos());
            System.out.println(userName + " ".repeat(30 - userName.length()) + followersCount + "                      " + followingCount);
        }
    }

    /**
     * Prints all the followers of the specified user.
     *
     * @param userName the username of the user whose followers are to be printed
     */
    public void printAllFollowers(String userName) {
        User user = getUserByName(userName);
        if (user == null) {
            System.out.println("Invalid user.");
            return;
        }

        System.out.println("Followers of " + userName + ":");
        for (int i = 0; i < users.size(); i++) {
            if (connections[i][user.getIndexPos()]) {
                System.out.println(users.get(i).getUserName());
            }
        }
    }

    /**
     * Prints all the users followed by the specified user.
     *
     * @param userName the username of the user whose followings are to be printed
     */
    public void printAllFollowing(String userName) {
        User user = getUserByName(userName);
        if (user == null) {
            System.out.println("Invalid user.");
            return;
        }

        System.out.println("Following by " + userName + ":");
        for (int i = 0; i < users.size(); i++) {
            if (connections[user.getIndexPos()][i]) {
                System.out.println(users.get(i).getUserName());
            }
        }
    }

    /**
     * Counts the number of followers for a user by index.
     *
     * @param userIndex the index of the user
     * @return the number of followers the user has
     */
    private int countFollowers(int userIndex) {
        int count = 0;
        for (int i = 0; i < users.size(); i++) {
            if (connections[i][userIndex]) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of users being followed by a user by index.
     *
     * @param userIndex the index of the user
     * @return the number of users the user is following
     */
    private int countFollowing(int userIndex) {
        int count = 0;
        for (int i = 0; i < users.size(); i++) {
            if (connections[userIndex][i]) {
                count++;
            }
        }
        return count;
    }

    /**
     * Performs a depth-first search to find the shortest path between two users.
     *
     * @param current      the index of the current user being explored
     * @param target       the index of the target user
     * @param visited      the set of visited nodes
     * @param currentPath  the path being currently explored
     * @param shortestPath the shortest path found so far
     */
    private void dfs(int current, int target, Set<Integer> visited, List<String> currentPath, List<String> shortestPath) {
        visited.add(current);
        currentPath.add(users.get(current).getUserName());
        if (current == target) {
            if (shortestPath.isEmpty() || currentPath.size() < shortestPath.size()) {
                shortestPath.clear();
                shortestPath.addAll(currentPath);
            }
        } else {
            for (int i = 0; i < users.size(); i++) {
                if (connections[current][i] && !visited.contains(i)) {
                    dfs(i, target, visited, currentPath, shortestPath);
                }
            }
        }

        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }

    /**
     * Finds all paths between two users.
     *
     * @param userFrom the username of the starting user
     * @param userTo   the username of the target user
     * @return a list of all paths from the starting user to the target user
     */
    public List<String> allPaths(String userFrom, String userTo) {
        User from = getUserByName(userFrom);
        User to = getUserByName(userTo);

        if (from == null) {
            return Collections.singletonList("The vertex " + userFrom + " does not exist.");
        }
        if (to == null) {
            return Collections.singletonList("The vertex " + userTo + " does not exist.");
        }

        List<String> allPaths = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();

        findAllPaths(from.getIndexPos(), to.getIndexPos(), new HashSet<>(), currentPath, allPaths);

        if (allPaths.isEmpty()) {
            return Collections.singletonList("No path exists between " + userFrom + " and " + userTo + ".");
        }

        Collections.sort(allPaths);
        return allPaths;
    }

    /**
     * Finds all possible paths using depth-first search.
     *
     * @param current     the index of the current user
     * @param target      the index of the target user
     * @param visited     the set of visited nodes
     * @param currentPath the path being currently explored
     * @param allPaths    the list of all paths found so far
     */
    private void findAllPaths(int current, int target, Set<Integer> visited, List<String> currentPath, List<String> allPaths) {
        visited.add(current);
        currentPath.add(users.get(current).getUserName());

        if (current == target) {
            allPaths.add(String.join(" -> ", currentPath));
        } else {
            for (int neighbor = 0; neighbor < users.size(); neighbor++) {
                if (connections[current][neighbor] && !visited.contains(neighbor)) {
                    findAllPaths(neighbor, target, visited, currentPath, allPaths);
                }
            }
        }

        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }

    /**
     * Finds all loops in the graph.
     *
     * @return a list of all loops in the graph
     */
    public List<String> findAllLoops() {
        List<String> loops = new ArrayList<>();
        Set<String> uniqueLoops = new HashSet<>();

        for (int i = 0; i < users.size(); i++) {
            findLoopsDFS(i, i, new ArrayList<>(), loops, uniqueLoops);
        }

        return loops;
    }

    /**
     * Helper method to perform depth-first search and find loops in the graph.
     *
     * @param start       the starting index of the loop
     * @param current     the current index being explored
     * @param path        the current path being explored
     * @param loops       the list of discovered loops
     * @param uniqueLoops the set of unique normalized loops
     */
    private void findLoopsDFS(int start, int current, List<Integer> path, List<String> loops, Set<String> uniqueLoops) {
        path.add(current);
        for (int neighbor = 0; neighbor < users.size(); neighbor++) {
            if (connections[current][neighbor]) {
                if (neighbor == start && path.size() > 1) {
                    List<Integer> loopPath = new ArrayList<>(path);
                    loopPath.add(start);
                    String loop = buildLoopString(loopPath);
                    String normalizedLoop = normalizeLoop(loop);

                    if (!uniqueLoops.contains(normalizedLoop)) {
                        loops.add(loop);
                        uniqueLoops.add(normalizedLoop);
                    }
                } else if (!path.contains(neighbor) || neighbor == start) {
                    findLoopsDFS(start, neighbor, path, loops, uniqueLoops);
                }
            }
        }
        path.remove(path.size() - 1);
    }

    /**
     * Builds a string representation of a loop from a list of indices.
     *
     * @param path the list of indices representing the loop
     * @return a string representation of the loop
     */
    private String buildLoopString(List<Integer> path) {
        List<String> nodeNames = new ArrayList<>();
        for (int index : path) {
            nodeNames.add(users.get(index).getUserName());
        }
        return String.join(" -> ", nodeNames);
    }

    /**
     * Normalizes a loop string to a canonical form for comparison.
     *
     * @param loop the loop string to be normalized
     * @return the normalized loop string
     */
    private String normalizeLoop(String loop) {
        String[] parts = loop.split(" -> ");
        int n = parts.length - 1;
        int minIndex = 0;

        for (int i = 1; i < n; i++) {
            if (parts[i].compareTo(parts[minIndex]) < 0) {
                minIndex = i;
            }
        }

        List<String> normalized = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            normalized.add(parts[(minIndex + i) % n]);
        }
        normalized.add(parts[minIndex]);

        return String.join(" -> ", normalized);
    }

    /**
     * Loads all users from a specified file and adds them to the graph.
     *
     * @param filename the name of the file containing user data
     */
    public void loadAllUsers(String filename) {
        File file = new File(filename);

        if (!file.exists() || !file.isFile()) {
            System.out.println("Error: File " + filename + " does not exist or is not a valid file.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addUser(line);
                System.out.println(line + " has been added.");
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    /**
     * Loads all connections from a specified file and adds them to the graph.
     *
     * @param filename the name of the file containing connection data
     */
    public void loadAllConnections(String filename) {
        File file = new File(filename);

        if (!file.exists() || !file.isFile()) {
            System.out.println("Error: File " + filename + " does not exist or is not a valid file.");
            return;
        }

        String to = null;
        String from = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(",");
                for (int i = 0; i < words.length; i++) {
                    if (i == 1) {
                        words[i] = words[i].substring(1);
                        to = words[i];
                    } else {
                        from = words[i];
                    }
                }
                System.out.println(from + ", " + to + " added");
                addConnections(from, to);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the current state of the graph to a file.
     */
    public void saveGraph() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("FollowGraph.obj"))) {
            oos.writeObject(this);
            System.out.println("FollowGraph object saved into file FollowGraph.obj.");
        } catch (IOException e) {
            System.err.println("Error saving the graph: " + e.getMessage());
        }
    }

    /**
     * Loads a FollowGraph object from a file. If the file does not exist, a new graph is created.
     *
     * @return the loaded FollowGraph object or a new one if the file does not exist
     */
    public static FollowGraph loadGraph() {
        File file = new File("FollowGraph.obj");
        if (!file.exists()) {
            System.out.println("follow_graph.obj is not found. New FollowGraph object will be created.");
            return new FollowGraph();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            System.out.println("FollowGraph object loaded from file FollowGraph.obj.");
            return (FollowGraph) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new FollowGraph();
        }
    }

    /**
     * Comparator for sorting users alphabetically by username.
     */
    public static class NameComparator implements Comparator<User> {
        public int compare(User u1, User u2) {
            return u1.getUserName().compareTo(u2.getUserName());
        }
    }

    /**
     * Comparator for sorting users by follower count in descending order.
     */
    public static class FollowersComparator implements Comparator<User> {
        private final FollowGraph graph;

        /**
         * Constructs a FollowersComparator with the given graph.
         *
         * @param graph the FollowGraph instance
         */
        public FollowersComparator(FollowGraph graph) {
            this.graph = graph;
        }

        public int compare(User u1, User u2) {
            int followerCount1 = graph.countFollowers(u1.getIndexPos());
            int followerCount2 = graph.countFollowers(u2.getIndexPos());

            if (followerCount1 != followerCount2) {
                return Integer.compare(followerCount2, followerCount1);
            }

            int followingCount1 = graph.countFollowing(u1.getIndexPos());
            int followingCount2 = graph.countFollowing(u2.getIndexPos());
            return Integer.compare(followingCount1, followingCount2);
        }
    }

    /**
     * Comparator for sorting users by the number of people they follow, in descending order.
     */
    public static class FollowingComparator implements Comparator<User> {
        private final FollowGraph graph;

        /**
         * Constructs a FollowingComparator with the given graph.
         *
         * @param graph the FollowGraph instance
         */
        public FollowingComparator(FollowGraph graph) {
            this.graph = graph;
        }

        public int compare(User u1, User u2) {
            int followingComparison = Integer.compare(
                    graph.countFollowing(u2.getIndexPos()),
                    graph.countFollowing(u1.getIndexPos())
            );

            if (followingComparison != 0) {
                return followingComparison;
            }

            return Integer.compare(
                    graph.countFollowers(u2.getIndexPos()),
                    graph.countFollowers(u1.getIndexPos())
            );
        }
    }
}