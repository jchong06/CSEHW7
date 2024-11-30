import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FollowGraphDriver {
    public static void main(String[] args) throws FileNotFoundException {
        FollowGraph graph = FollowGraph.loadGraph();
        Scanner input = new Scanner(System.in);
        String menu = "(U) Add User\n" +
                "(C) Add Connection\n" +
                "(AU) Load all Users\n" +
                "(AC) Load all Connections\n" +
                "(P) Print all Users\n" +
                "(L) Print all Loops (cycles)\n" +
                "(RU) Remove User\n" +
                "(RC) Remove Connection\n" +
                "(SP) Find Shortest Path\n" +
                "(AP) Find All Paths\n" +
                "(Q) Quit\n";

        System.out.println(menu);
        System.out.print("Enter a selection: ");
        String option = input.nextLine().toUpperCase();
        while (!option.equals("Q")){
            if (option.equals("AU")){
                System.out.print("Enter the file name: ");
                String file = input.nextLine();
                graph.loadAllUsers(file);
            }
            if (option.equals("AC")){
                System.out.print("Enter the file name: ");
                String file = input.nextLine();
                graph.loadAllConnections(file);
            }
            if (option.equals("P")){
                System.out.println("(SA) Sort Users by Name\n" +
                        "(SB) Sort Users by Number of Followers\n" +
                        "(SC) Sort Users by Number of Following\n" +
                        "(Q) Quit // back to main menu");
                System.out.print("Enter a selection: ");
                String comp = input.nextLine().toUpperCase();
                while (!comp.equals("Q")){
                    if (comp.equals("SA")){
                        System.out.println("\nUsers:");
                        graph.printAllUsers(new FollowGraph.NameComparator());
                    }
                    else if (comp.equals("SB")){
                        System.out.println("\nUsers:");
                        graph.printAllUsers(new FollowGraph.FollowersComparator(graph));
                    }
                    else if (comp.equals("SC")){
                        System.out.println("\nUsers:");
                        graph.printAllUsers(new FollowGraph.FollowingComparator(graph));
                    }
                    System.out.println("(SA) Sort Users by Name\n" +
                            "(SB) Sort Users by Number of Followers\n" +
                            "(SC) Sort Users by Number of Following\n" +
                            "(Q) Quit // back to main menu");
                    System.out.print("Enter a selection: ");
                    comp = input.nextLine().toUpperCase();
                }
            }
            if (option.equals("U")){
                System.out.print("Please enter the name of the user: ");
                String u = input.nextLine();
                graph.addUser(u);
            }
            if (option.equals("C")){
                System.out.print("Please enter the source of the connection to add: ");
                String f = input.nextLine();
                System.out.print("Please enter the dest of the connection to add: ");
                String t = input.nextLine();
                graph.addConnections(f, t);
            }
            if (option.equals("SP")){
                System.out.print("Please enter the desired source: ");
                String f = input.nextLine();
                System.out.print("Please enter the desired destination: ");
                String t = input.nextLine();
                String p = graph.shortestPath(f, t);
                System.out.println(p);
                if (!p.equals("No path found.")){
                    int length = p.split("->", -1).length - 1 + 1;
                    System.out.println("The number of users in this path is: " + length);
                }
            }
            if (option.equals("L")){
                List<String> loops = graph.findAllLoops();
                Collections.sort(loops);
                if (loops.isEmpty()){
                    System.out.println("There are no loops.");
                }
                else if (loops.size() == 1){
                    System.out.println("There is 1 loop:");
                }
                else{
                    System.out.print("There are a total of " + loops.size() + " loops: ");
                }
                for (String l : loops){
                    System.out.println(l);
                }
            }
            if (option.equals("AP")){
                System.out.print("\nPlease enter the desired source: ");
                String f = input.nextLine();
                System.out.print("Please enter the desired destination: ");
                String t = input.nextLine();
                List<String> path = graph.allPaths(f, t);
                Collections.sort(path);
                if (!path.isEmpty()) {
                    System.out.println("There are a total of " + path.size() + " paths: ");
                    for (String p : path) {
                        System.out.println(p);
                    }
                }
            }
            if (option.equals("RU")){
                System.out.print("Please enter the user to remove: ");
                String u = input.nextLine();
                while (graph.getUserByName(u) == null) {
                    if (u.isEmpty()) {
                        System.out.println("You can not leave this field empty. ");
                        System.out.println("There is no user with this name, Please choose a valid user! ");
                        System.out.print("Please enter the user to remove: ");
                        u = input.nextLine();
                    }
                    else if (graph.getUserByName(u) == null) {
                        System.out.println("There is no user with this name, Please choose a valid user! ");
                        System.out.print("Please enter the user to remove: ");
                        u = input.nextLine();
                    }
                }
                graph.removeUser(u);
            }
            if (option.equals("RC")){
                System.out.print("Please enter the source of the connection to remove: ");
                String f = input.nextLine();
                while (graph.getUserByName(f) == null) {
                    if (f.isEmpty()) {
                        System.out.println("You can not leave this field empty. ");
                        System.out.println("There is no user with this name, Please choose a valid user! ");
                        System.out.print("Please enter the source of the connection to remove: ");
                        f = input.nextLine();
                    }
                    else if (graph.getUserByName(f) == null) {
                        System.out.println("There is no user with this name, Please choose a valid user! ");
                        System.out.print("Please enter the source of the connection to remove: ");
                        f = input.nextLine();
                    }
                }
                System.out.print("Please enter the dest of the connection to remove: ");
                String t = input.nextLine();
                while (graph.getUserByName(t) == null) {
                    if (t.isEmpty()) {
                        System.out.println("You can not leave this field empty. ");
                        System.out.print("Please enter the dest of the connection to remove: ");
                        t = input.nextLine();
                    }
                    else if (graph.getUserByName(t) == null) {
                        System.out.println("There is no user with this name, Please choose a valid user! ");
                        System.out.print("Please enter the dest of the connection to remove: ");
                        t = input.nextLine();
                    }
                }
                graph.removeConnection(f, t);
            }
            System.out.println(menu);
            System.out.print("Enter a selection: ");
            option = input.nextLine().toUpperCase();
        }
        graph.saveGraph();
    }
}
