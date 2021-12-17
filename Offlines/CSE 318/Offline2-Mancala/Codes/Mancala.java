import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Mancala {
    public static int INFINITY = 100000000;
    public static int initialDepth = 10;
    public static Board chosenBoard = null;

    public static String maxHeuristic = null, minHeuristic = null;

    //Board board = null;
    int w1 = 10, w2 = 20, w3 = 10;

    public int Heuristic1(Board newBoard){
        return newBoard.getMaxStorage() - newBoard.getMinStorage();
    }

    public int Heuristic2(Board newBoard){
        return w1*(newBoard.getMaxStorage() - newBoard.getMinStorage()) + w2*(newBoard.getMaxStones() - newBoard.getMinStones());
    }

    public int Heuristic3(Board newBoard){
        int addition_move_earned = 0;
        if(newBoard.isFreemove()){
            addition_move_earned = 1;
        }

        return w1*(newBoard.getMaxStorage() - newBoard.getMinStorage()) + w2*(newBoard.getMaxStones() - newBoard.getMinStones()) + w3*addition_move_earned;

    }

    public int Heuristic4(Board newBoard){
        return 0;
    }

    public int Heuristic5(Board newBoard){
        return 0;
    }

    public int Heuristic6(Board newBoard){
        return 0;
    }

    public int evaluate(String heuristicName, Board newBoard){
        if(heuristicName.equalsIgnoreCase("heuristic1")){
            return Heuristic1(newBoard);
        }
        else if(heuristicName.equalsIgnoreCase("heuristic2")){
            return Heuristic2(newBoard);
        }
        else if(heuristicName.equalsIgnoreCase("heuristic3")){
            return Heuristic3(newBoard);
        }
        else if(heuristicName.equalsIgnoreCase("heuristic4")){
            return Heuristic4(newBoard);
        }
        else if(heuristicName.equalsIgnoreCase("heuristic5")){
            return Heuristic5(newBoard);
        }
        else if(heuristicName.equalsIgnoreCase("heuristic6")){
            return Heuristic6(newBoard);
        }
        else{
            return -1;
        }

    }

    public int MiniMax(Board currentBoard, boolean turn, int alpha, int beta, int depth){

        if(depth == 0 || currentBoard.isMatchWon() >= 0){
            if(turn){
                return evaluate(maxHeuristic, currentBoard);
            }
            else{
                return evaluate(minHeuristic, currentBoard);
            }
        }




        //System.out.println("Inside minimax");
        ArrayList<Board> possibleMoves = currentBoard.allPossibleMoves(turn);
        Board tempBoard = null, bestBoard = null;
        int maxVal, minVal, eval, idx = -1;

        //System.out.println("depth: " + depth) ;


        //max's turn
        if(turn){

            maxVal = -INFINITY;
            for(int i = 0; i < possibleMoves.size(); i++){
                tempBoard = possibleMoves.get(i);
                //System.out.println("tempboard size: " + tempBoard.maxBins.size());



                if(tempBoard != null){
                    if(tempBoard.isFreemove()){
                        turn = !turn;
                    }
                    eval = MiniMax(tempBoard, !turn, alpha, beta, depth-1);

                    if(eval > maxVal){
                        maxVal = eval;
                        bestBoard = tempBoard;
                        idx = i+1;
                    }
                    alpha = max(alpha, eval);
                    if(beta <= alpha){
                        break;
                    }
                }
            }

            if(depth == initialDepth){
                chosenBoard = bestBoard;
                return idx;
            }

            return maxVal;
        }
        else{
            minVal = INFINITY;

            for(int i = 0; i < possibleMoves.size(); i++){
                tempBoard = possibleMoves.get(i);

                if(tempBoard != null){
                    if(tempBoard.isFreemove()){
                        turn = !turn;
                    }
                    eval = MiniMax(tempBoard, !turn, alpha, beta, depth-1);


                    if(eval < minVal){

                        minVal = eval;
                        bestBoard = tempBoard;
                        idx = i+1;

                    }
                    beta = min(beta, eval);
                    if(beta <= alpha){
                        break;
                    }

                }
            }

            if(depth == initialDepth){
                chosenBoard = bestBoard;
                return idx;
            }

            return minVal;

        }
    }


    public void MancalaGame(boolean manualMode, int depth){


        Scanner scanner = new Scanner(System.in);
        Board board = new Board();
        boolean turn;

        int random = (int)Math.floor(Math.random() * (10));
        if(random >= 5){
            turn = true;
        }

        else{
            turn = false;
        }

        Mancala.initialDepth = depth;
        int alpha, beta, move, returnVal;
        while(true){
            alpha = (int) -INFINITY;
            beta = (int) INFINITY;

            if(turn){
                System.out.println("max's turn: ");
            }

            if(turn && manualMode){
                System.out.println("Enter your move: ");
                move = scanner.nextInt();
                board.maxTurn(move);

                turn = !turn;
                board.print();
            }

            if(!turn){
                System.out.println("min's turn");
            }

            move = MiniMax(board, turn, alpha, beta, depth);
            board = chosenBoard;

            System.out.println("player chose Bin: " + move);
            board.print();

            turn = !turn;

            returnVal = board.isMatchWon();

            if(returnVal == 0){
                System.out.println("Match Tie");
                break;
            }
            else if(returnVal == 1){
                System.out.println("Max Won");
                break;
            }
            else if(returnVal == 2){
                System.out.println("Min won");
                break;
            }
            else if(returnVal == -1){
                System.out.println("Still running");
                continue;
            }

        }



    }
}
