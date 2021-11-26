import com.sun.source.tree.LiteralTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AStarSearch {

    List<SearchNode> openList = new ArrayList<>();
    List<SearchNode> closedList = new ArrayList<>();

    public void Algorithm(SearchNode initial, String heuristicName){

        openList.add(initial);

        int minCost = 100000000, minIdx = -1;
        SearchNode minNode = null;
        int explored = 0;
        while(true){
            explored++;
            for(int i = 0; i < openList.size(); i++){
                if(minCost > openList.get(i).getCost(heuristicName)){
                    minNode = openList.get(i);
                    minCost = minNode.getCost(heuristicName);
                    minIdx = i;
                }
            }

            if(minNode.isGoalNode()){
                System.out.println("Goal node reached, explored: " + explored);
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

}
