import java.util.Scanner;

public class Main {

    public static void main(String[] args){

        Mancala mancala = new Mancala();
        System.out.println("1. AI vs Player");
        System.out.println("2. AI vs AI");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        String heuristic;

        if(choice == 1){
            System.out.println("Choose AI Heuristic");
            System.out.println("1. Heuristic 1");
            System.out.println("2. Heuristic 2");
            System.out.println("3. Heuristic 3");
            System.out.println("4. H3 with Maximize Capture");
            System.out.println("5. H4 with stones close to My Storage");
            System.out.println("6. H5 with farthest valid move");

            choice = scanner.nextInt();

            heuristic = "heuristic" + choice;

            Mancala.minHeuristic = heuristic;

            mancala.MancalaGame(true, 20);

        }
        else if(choice == 2){

            System.out.println("Choose AI Heuristic");
            System.out.println("1. Heuristic 1");
            System.out.println("2. Heuristic 2");
            System.out.println("3. Heuristic 3");
            System.out.println("4. H3 with Maximize Capture");
            System.out.println("5. H4 with stones close to My Storage");
            System.out.println("6. H5 with farthest valid move");

            System.out.println("Enter Max players Heuristic: ");
            choice = scanner.nextInt();
            heuristic = "heuristic" + choice;
            Mancala.maxHeuristic = heuristic;

            System.out.println("Enter Min players Heuristic: ");
            choice = scanner.nextInt();

            heuristic = "heuristic" + choice;
            Mancala.minHeuristic = heuristic;

            mancala.MancalaGame(false, 10);

        }
        else {
            System.out.println("Invalid choice");
        }
    }
}
