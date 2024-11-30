import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        FollowerGraph graph = new FollowerGraph();
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
                System.out.println();
                graph.loadAllUsers(file);
                System.out.println();
            }
            if (option.equals("AC")){
                System.out.print("Enter the file name: ");
                String file = input.nextLine();
                System.out.println();
                graph.loadAllConnections(file);
                System.out.println();
            }
            if (option.equals("P")){
                System.out.println("\n(SA) Sort Users by Name\n" +
                        "(SB) Sort Users by Number of Followers\n" +
                        "(SC) Sort Users by Number of Following\n" +
                        "(Q) Quit // back to main menu\n");
                System.out.print("Enter a selection: ");
                String comp = input.nextLine().toUpperCase();
                if (comp.equals("SA")){
                    System.out.println();
                    graph.printAllUsers(new FollowerGraph.NameComparator());
                    System.out.println();
                }
                else if (comp.equals("SB")){
                    System.out.println();
                    graph.printAllUsers(new FollowerGraph.FollowersComparator(graph));
                    System.out.println();
                }
                else if (comp.equals("SC")){
                    System.out.println();
                    graph.printAllUsers(new FollowerGraph.FollowingComparator(graph));
                    System.out.println();
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
                System.out.println();
                System.out.println(graph.shortestPath(f, t));
                System.out.println();
            }
            if (option.equals("L")){
                List<String> loops = graph.findAllLoops();
                Collections.sort(loops);
                System.out.println();
                System.out.print("There is " + loops.size() + " loop: ");
                for (String l : loops){
                    System.out.println(l);
                }
                System.out.println();
            }
            if (option.equals("AP")){
                System.out.print("\nPlease enter the desired source: ");
                String f = input.nextLine();
                System.out.print("Please enter the desired destination: ");
                String t = input.nextLine();
                List<String> path = graph.allPaths(f, t);
                Collections.sort(path);
                System.out.println();
                System.out.println("There are a total of " + path.size() + " paths: ");
                for (String p : path){
                    System.out.println(p);
                }
                System.out.println();
            }
            if (option.equals("RU")){
                System.out.print("\nPlease enter the user to remove: ");
                String u = input.nextLine();
                graph.removeUser(u);
            }
            if (option.equals("RC")){
                System.out.print("\nPlease enter the source of the connection to remove: ");
                String f = input.nextLine();
                System.out.print("Please enter the dest of the connection to remove: ");
                String t = input.nextLine();
                graph.removeConnection(f, t);
            }
            System.out.println(menu);
            System.out.print("Enter a selection: ");
            option = input.nextLine().toUpperCase();
        }

        graph.loadAllUsers("users.txt");
        graph.loadAllConnections("connections.txt");
        graph.printAllUsers(new FollowerGraph.NameComparator());
    }
}
