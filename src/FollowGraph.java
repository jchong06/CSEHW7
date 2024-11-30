import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class FollowGraph implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<User> users;
    public static final int MAX_USERS = 100;
    private boolean[][] connections;

    public FollowGraph() {
        users = new ArrayList<>();
        connections = new boolean[MAX_USERS][MAX_USERS];
    }

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

    public User getUserByName(String userName) {
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

    public void printAllUsers(Comparator<User> comp) {
        for (int i = 0; i < users.size(); i++) {
            for (int j = 0; j < users.size(); j++) {
                if (connections[i][j]) {
                    System.out.println(users.get(i).getUserName() + " -> " + users.get(j).getUserName());
                }
            }
        }
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

    public List<String> findAllLoops() {
        List<String> loops = new ArrayList<>();
        Set<String> uniqueLoops = new HashSet<>();

        for (int i = 0; i < users.size(); i++) {
            findLoopsDFS(i, i, new ArrayList<>(), loops, uniqueLoops);
        }

        return loops;
    }



    private String buildLoopString(List<Integer> path) {
        List<String> nodeNames = new ArrayList<>();
        for (int index : path) {
            nodeNames.add(users.get(index).getUserName());
        }
        return String.join(" -> ", nodeNames);
    }

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
            int followersComparison = Integer.compare(
                    graph.countFollowers(u2.getIndexPos()),
                    graph.countFollowers(u1.getIndexPos())
            );

            return (followersComparison != 0)
                    ? followersComparison
                    : Integer.compare(
                    graph.countFollowing(u1.getIndexPos()),
                    graph.countFollowing(u2.getIndexPos())
            );
        }
    }

    public static class FollowingComparator implements Comparator<User> {
        private final FollowGraph graph;

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


    public void saveGraph() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("FollowGraph.obj"))) {
            oos.writeObject(this);
            System.out.println("FollowGraph object saved into file FollowGraph.obj.");
        } catch (IOException e) {
            System.err.println("Error saving the graph: " + e.getMessage());
        }
    }

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

}

