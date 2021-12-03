import com.sun.source.tree.LiteralTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class AStarSearch {

    List<SearchNode> openList = new ArrayList<>();
    List<SearchNode> closedList = new ArrayList<>();

    public static int hamming(SearchNode searchNode){

        int heuristic = 0;
        for(int i = 0; i < SearchNode.dimension; i++){
            for(int j = 0; j < SearchNode.dimension; j++){
                if(searchNode.board[i][j].equalsIgnoreCase("*")) continue;

                int value = Integer.parseInt(searchNode.board[i][j]);
                if( (i*SearchNode.dimension+j+1) != value){
                    heuristic++;
                }
            }
        }

        return heuristic;

    }

    public static int manhattan(SearchNode searchNode){

        int heuristic = 0;
        for(int i = 0; i < SearchNode.dimension; i++){
            for(int j = 0; j < SearchNode.dimension; j++){

                if(searchNode.board[i][j].equalsIgnoreCase("*")) continue;

                int value = Integer.parseInt(searchNode.board[i][j]);
                int row, col;
                if(value % SearchNode.dimension == 0){
                    row = (int) Math.floor((value-1)/SearchNode.dimension) + 1;
                    col = (value-1) % SearchNode.dimension + 1;
                }
                else{
                    row = (int) Math.floor(value/SearchNode.dimension) + 1;
                    col = value % SearchNode.dimension;
                }

                heuristic += Math.abs(row - i - 1);
                heuristic += Math.abs(col - j - 1);

            }
        }

        return heuristic;
    }

    public static int linearConflict(SearchNode searchNode){

        int manhattanDistance = manhattan(searchNode);
        int linear = 0;

        for(int i = 0; i < SearchNode.dimension; i++){
            for(int j = 0; j < SearchNode.dimension; j++){
                if(searchNode.board[i][j].equalsIgnoreCase("*")) continue;

                int value1 = Integer.parseInt(searchNode.board[i][j]);
                for(int k = j + 1; k < SearchNode.dimension; k++){
                    if(searchNode.board[i][k].equalsIgnoreCase("*")) continue;

                    int value2 = Integer.parseInt(searchNode.board[i][k]);

                    int row1, row2;
                    if(value1 % SearchNode.dimension == 0){
                        row1 = (int) Math.floor((value1-1)/SearchNode.dimension) + 1;
                    }
                    else{
                        row1 = (int) Math.floor(value1/SearchNode.dimension) + 1;
                    }
                    if(value2 % SearchNode.dimension == 0){
                        row2 = (int) Math.floor((value2-1)/SearchNode.dimension) + 1;
                    }
                    else{
                        row2 = (int) Math.floor(value2/SearchNode.dimension) + 1;
                    }

                    if(row1 == (i+1) && row2 == (i+1)){
                        if(value2 < value1){
                            linear++;
                        }
                    }
                }
            }
        }

        return (manhattanDistance + linear*2);

    }





    public void Algorithm(SearchNode initial, String heuristicName){

        openList.add(initial);

        while(true){

            int minCost = 100000000, minIdx = -1;
            SearchNode minNode = null;
            for(int i = 0; i < openList.size(); i++){
                if(minCost > openList.get(i).getCost(heuristicName)){
                    minNode = openList.get(i);
                    minCost = minNode.getCost(heuristicName);
                    minIdx = i;
                }
            }

            if(minNode.isGoalNode()){
                System.out.println("****************************************************************");
                System.out.println("Goal node reached");
                System.out.println("****************************************************************");


                System.out.println("Moves required : " + minNode.getCost(heuristicName));
                System.out.println("explored: " + closedList.size() + ", expanded: " + (openList.size()+closedList.size()));
                System.out.println();

                printPath(minNode, heuristicName);

                break;
            }

            openList.remove(minIdx);
            closedList.add(minNode);


            boolean left = true, right = true, up = true, down = true;

            SearchNode leftNeighbour = minNode.getNeighbour("left");
            SearchNode rightNeighbour = minNode.getNeighbour("right");
            SearchNode upNeighbour = minNode.getNeighbour("up");
            SearchNode downNeighbour = minNode.getNeighbour("down");

            if(leftNeighbour == null) left = false;
            if(rightNeighbour == null) right = false;
            if(upNeighbour == null) up = false;
            if(downNeighbour == null) down = false;

            for(int i = 0; i < closedList.size(); i++)
            {
                if(closedList.get(i).isSameNode(leftNeighbour)){
                    left = false;
                }
                if(closedList.get(i).isSameNode(rightNeighbour)){
                    right = false;
                }
                if(closedList.get(i).isSameNode(upNeighbour)){
                    up = false;
                }
                if(closedList.get(i).isSameNode(downNeighbour)){
                    down = false;
                }

            }
            if(left){
                leftNeighbour.setMoves(minNode.getMoves() + 1);
                leftNeighbour.setParent(minNode);
                openList.add(leftNeighbour);
            }
            if(right){
                rightNeighbour.setMoves(minNode.getMoves() + 1);
                rightNeighbour.setParent(minNode);
                openList.add(rightNeighbour);

            }
            if(up){
                upNeighbour.setMoves(minNode.getMoves() + 1);
                upNeighbour.setParent(minNode);
                openList.add(upNeighbour);

            }
            if(down){
                downNeighbour.setMoves(minNode.getMoves() + 1);
                downNeighbour.setParent(minNode);
                openList.add(downNeighbour);

            }
        }
    }

    public void printPath(SearchNode goalNode, String heuristicName){
        Stack<SearchNode> path = new Stack<>();

        SearchNode tempNode = goalNode;
        while(tempNode != null){
            path.push(tempNode);
            tempNode = tempNode.getParent();
        }

        int step = 0;
        while(!path.empty()){
            tempNode = path.pop();

            System.out.println("Step: " + step++);
            tempNode.printNode();
            System.out.println("g(n): " + tempNode.getMoves());
            System.out.println("h(n): " + (tempNode.getCost(heuristicName) - tempNode.getMoves()) +"\n");

            if(tempNode != goalNode){
                printArrow();
            }

        }
    }

    public void printArrow(){
        System.out.println("   |  |");
        System.out.println("  \\|__|/");
        System.out.println("   \\  /");
        System.out.println("    \\/");
        System.out.println();
    }

}
