import java.util.Scanner;

public class Demo {

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert initial board, * for blank");
        System.out.println("input size of the board: ");
        int dimension = Integer.parseInt(scanner.nextLine());
        SearchNode.dimension = dimension;

        System.out.println("Input initial board: ");
        String[][] myBoard = new String[dimension][dimension];

        for(int i = 0; i < dimension; i++){
            myBoard[i] = scanner.nextLine().split(" ");
        }

        SearchNode initial = new SearchNode(myBoard, 0, null);


        if(initial.isSolvable()){
            System.out.println("solvability: SOLVABLE");

//            String heuristicName = scanner.nextLine();

            System.out.println("##########################################");
            System.out.println("HAMMING: ");
            System.out.println("##########################################");
            AStarSearch aStarSearch = new AStarSearch();
            aStarSearch.Algorithm(initial, "hamming");

            System.out.println("##########################################");
            System.out.println("Manhattan: ");
            System.out.println("##########################################");
            aStarSearch = new AStarSearch();
            aStarSearch.Algorithm(initial, "manhattan");

            System.out.println("##########################################");
            System.out.println("Linear conflict: ");
            System.out.println("##########################################");
            aStarSearch = new AStarSearch();
            aStarSearch.Algorithm(initial, "linear");


        }
        else{
            System.out.println("solvability: NOT SOLVABLE");
        }

    }
}
