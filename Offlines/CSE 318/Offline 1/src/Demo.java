import java.util.Scanner;

public class Demo {

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

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
            System.out.println("It is solvable");
            System.out.println("Enter heuristic name: ");

            String heuristicName = scanner.nextLine();

            AStarSearch aStarSearch = new AStarSearch();
            aStarSearch.Algorithm(initial, heuristicName);
        }
        else{
            System.out.println("Not solvable");
        }

    }
}
