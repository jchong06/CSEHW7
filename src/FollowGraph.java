import java.io.*;
import java.util.*;

public class FollowGraph {
    private ArrayList<User> users;
    public static final int MAX_USERS = 100;
    private boolean[][] connections;

    public FollowGraph() {
        users = new ArrayList<>();
        connections = new boolean[MAX_USERS][MAX_USERS];
    }

    public void addUser(String userName) {
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

    public void addConnections(String userFrom, String userTo) {
        User from = getUserByName(userFrom);
        User to = getUserByName(userTo);

        if ((from != null) && (to != null)) {
            connections[from.getIndexPos()][to.getIndexPos()] = true;
        }
    }

    public void removeUser(String user) {
        User u = getUserByName(user);
        int idx = u.getIndexPos();
        if (u != null){
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

    public void removeConnection(String userFrom, String userTo) {
        User from = getUserByName(userFrom);
        User to = getUserByName(userTo);

        if ((from != null) && (to != null)) {
            connections[from.getIndexPos()][to.getIndexPos()] = false;
        }
    }

    private User getUserByName(String userName) {
        for (User user : users) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

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
        Set<Integer> visited = new HashSet<>();

        findAllPaths(from.getIndexPos(), to.getIndexPos(), visited, currentPath, allPaths);

        Collections.sort(allPaths);
        if (allPaths.isEmpty()) {
            return Collections.singletonList("There is no path from vertex " + userFrom + " to vertex " + userTo + ".");
        }
        return allPaths;
    }

    private void findAllPaths(int current, int target, Set<Integer> visited, List<String> currentPath, List<String> allPaths) {
        visited.add(current);
        currentPath.add(users.get(current).getUserName());

        if (current == target) {
            allPaths.add(String.join(" -> ", currentPath));
        } else {
            for (int i = 0; i < users.size(); i++) {
                if (connections[current][i] && !visited.contains(i)) {
                    findAllPaths(i, target, visited, currentPath, allPaths);
                }
            }
        }

        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }

    public void printAllUsers(Comparator<User> comp) {
        users.sort(comp);

        System.out.printf("%-20s %-25s %-20s\n", "User Name", "Number of Followers", "Number Following");
        for (User user : users) {
            System.out.printf("%-29s %-24d %-20d\n",
                    user.getUserName(),
                    countFollowers(user.getIndexPos()),
                    countFollowing(user.getIndexPos()));
        }
    }

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

    private int countFollowers(int userIndex) {
        int count = 0;
        for (int i = 0; i < users.size(); i++) {
            if (connections[i][userIndex]) {
                count++;
            }
        }
        return count;
    }

    private int countFollowing(int userIndex) {
        int count = 0;
        for (int i = 0; i < users.size(); i++) {
            if (connections[userIndex][i]) {
                count++;
            }
        }
        return count;
    }


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

    public List<String> findAllLoops() {
        List<String> loops = new ArrayList<>();
        Set<String> uniqueLoops = new HashSet<>();

        for (int i = 0; i < users.size(); i++) {
            findLoopsDFS(i, i, new HashSet<>(), new ArrayList<>(), loops, uniqueLoops);
        }
        return loops;
    }

    private void findLoopsDFS(int start, int current, Set<Integer> visited, List<Integer> path, List<String> loops, Set<String> uniqueLoops) {
        visited.add(current);
        path.add(current);

        for (int neighbor = 0; neighbor < users.size(); neighbor++) {
            if (connections[current][neighbor]) {
                if (neighbor == start && path.size() > 2) {

                    String loop = buildLoopString(path);
                    if (uniqueLoops.add(loop)) {
                        loops.add(loop);
                    }
                } else if (!visited.contains(neighbor)) {
                    findLoopsDFS(start, neighbor, visited, path, loops, uniqueLoops);
                }
            }
        }

        visited.remove(current);
        path.remove(path.size() - 1);
    }

    private String buildLoopString(List<Integer> path) {
        List<String> nodeNames = new ArrayList<>();
        for (int index : path) {
            nodeNames.add(users.get(index).getUserName());
        }

        Collections.sort(nodeNames);
        nodeNames.add(nodeNames.get(0)); // Close the loop
        return String.join(" -> ", nodeNames);
    }

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

    public void loadAllConnections(String filename){
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
                    if (i == 1){
                        words[i] = words[i].substring(1);
                        to = words[i];
                    }
                    else{
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

    public static class NameComparator implements Comparator<User> {
        public int compare(User u1, User u2) {
            return u1.getUserName().compareTo(u2.getUserName());
        }
    }

    public static class FollowersComparator implements Comparator<User> {
        private final FollowGraph graph;

        public FollowersComparator(FollowGraph graph) {
            this.graph = graph;
        }

        public int compare(User u1, User u2) {
            return Integer.compare(
                    graph.countFollowers(u2.getIndexPos()),
                    graph.countFollowers(u1.getIndexPos())
            );
        }
    }

    public static class FollowingComparator implements Comparator<User> {
        private final FollowGraph graph;

        public FollowingComparator(FollowGraph graph) {
            this.graph = graph;
        }

        public int compare(User u1, User u2) {
            return Integer.compare(
                    graph.countFollowing(u2.getIndexPos()),
                    graph.countFollowing(u1.getIndexPos())
            );
        }
    }

}

