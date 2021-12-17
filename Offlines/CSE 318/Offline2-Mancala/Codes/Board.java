import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;


public class Board{

    private int minStorage, maxStorage;
    private boolean freemove = false;
    List<Integer> maxBins = null, minBins = null;


    public Board() {
        this.minStorage = 0;
        this.maxStorage = 0;

        minBins = new ArrayList<>();
        maxBins = new ArrayList<>();

        for(int i = 0; i < 6; i++){
            minBins.add(4);
            maxBins.add(4);
        }
    }

    public int getMinStorage() {
        return minStorage;
    }

    public int getMaxStorage(){
        return maxStorage;
    }

    public void setMinStorage(int storage) {
        this.minStorage = storage;
    }

    public void setMaxStorage(int storage){
        this.maxStorage = storage;
    }

    public boolean isFreemove() {
        return freemove;
    }

    public void setFreemove(boolean freemove) {
        this.freemove = freemove;
    }

    boolean isMinEmpty(int binNo){

        if (minBins.get(binNo-1) == 0) {
            return true;
        }
        else
            return false;

    }

    boolean isMaxEmpty(int binNo){

        if (maxBins.get(binNo-1) == 0) {
            return true;
        }
        else
            return false;

    }


    int getMinFirstValidMove(){

        for(int i = minBins.size()-1; i >= 0; i--){
            if(!isMinEmpty(i+1)){
                return i+1;
            }
        }
        return -1;
    }

    int getMaxFirstValidMove(){

        for(int i = maxBins.size()-1; i >= 0; i--){
            if(!isMaxEmpty(i+1)){
                return i+1;
            }
        }
        return -1;
    }

    int getMinStones(){
        int stones = 0;
        for(int i = 0; i < minBins.size(); i++){
            stones += minBins.get(i);
        }
        return stones;
    }

    int getMaxStones(){
        int stones = 0;
        for(int i = 0; i < maxBins.size(); i++){
            stones += maxBins.get(i);
        }
        return stones;
    }

    int closeToMinStorage(){
        int close = 0;

        for(int i = 0; i < minBins.size(); i++){
            close += min(i+1, minBins.get(i));
        }

        return close;
    }

    int closeToMaxStorage(){
        int close = 0;

        for(int i = 0; i < maxBins.size(); i++){
            close += min(i+1, maxBins.get(i));
        }

        return close;
    }

    int addToMinBins(int amount){
        int i;
        for(i = minBins.size()-1; (i >= 0) && (amount > 0); i--, amount--){
            minBins.set(i, minBins.get(i)+1);
        }

        return amount;
    }

    int addToMaxBins(int amount){
        int i;
        for(i = maxBins.size()-1; (i >= 0) && (amount > 0); i--, amount--){
            maxBins.set(i, maxBins.get(i)+1);
        }

        return amount;
    }


    public void maxTurn(int binNo){
        int stones = maxBins.get(binNo-1);
        maxBins.set(binNo-1, 0);

        int i;
        while(true){
            for(i = binNo-2; (i >= 0) && stones > 0; i--, stones--){
                maxBins.set(i, maxBins.get(i) + 1);
            }

            if(stones == 0){
                if(maxBins.get(i+1) == 1){
                    if(minBins.get(minBins.size()-i-2) != 0){

                        maxStorage += (minBins.get(minBins.size()-i-2) + 1);
                        minBins.set(minBins.size()-i-2, 0);
                        maxBins.set(i+1, 0);
                    }

                }
            }

            if(stones> 0){
                maxStorage++;
                stones--;
                if(stones == 0){
                    setFreemove(true);   // freemove
                    return;
                }
            }
            for(i = minBins.size()-1; (i >= 0) && stones > 0; i--, stones--){
                minBins.set(i, minBins.get(i) + 1);
            }

           if(stones == 0){
               return;   // no free move
           }
        }
    }

    public void minTurn(int binNo){
        int stones = minBins.get(binNo-1);
        minBins.set(binNo-1, 0);

        int i;
        while(true){
            for(i = binNo-2; (i >= 0) && stones > 0; i--, stones--){
                minBins.set(i, minBins.get(i) + 1);

            }

            if(stones == 0){
                if(minBins.get(i+1) == 1){
                    if(maxBins.get(maxBins.size()-i-2) != 0){

                        minStorage += (maxBins.get(maxBins.size()-i-2) + 1);
                        maxBins.set(maxBins.size()-i-2, 0);
                        minBins.set(i+1, 0);
                    }

                }
            }

            if(stones> 0){
                minStorage++;
                stones--;

                if(stones == 0){
                    setFreemove(true);   // freemove
                    return;
                }
            }
            for(i = maxBins.size()-1; (i >= 0) && stones > 0; i--, stones--){
                maxBins.set(i, maxBins.get(i) + 1);
            }

            if(stones == 0){
                return;
            }
        }
    }


    public Board getClone(){
        Board newboard = new Board();

        newboard.setMaxStorage(this.getMaxStorage());
        newboard.setMinStorage(this.getMinStorage());

        for(int i = 0; i < this.minBins.size(); i++){
            newboard.minBins.set(i, this.minBins.get(i));
            newboard.maxBins.set(i, this.maxBins.get(i));
        }
        return newboard;

    }

    public ArrayList<Board> allPossibleMoves(boolean turn){

        ArrayList<Board>possibleMoves = new ArrayList<>();

//        Board board1, board2, board3, board4, board5, board6;
//        board1 = this.getClone();
//        board2 = this.getClone();
//        board3 = this.getClone();
//        board4 = this.getClone();
//        board5 = this.getClone();
//        board6 = this.getClone();

        Board[] boards = new Board[6];

        for(int i = 0; i < boards.length; i++){
            boards[i] = this.getClone();
        }

        if(turn){

            for(int i = 0; i < boards.length; i++){
                if(this.maxBins.get(i) == 0){
                    boards[i] = null;
                }
                else{
                    boards[i].maxTurn(i+1);
                }
            }

//            if(this.maxBins.get(1) == 0) board1 = null;
//            if(this.maxBins.get(2) == 0) board2 = null;
//            if(this.maxBins.get(3) == 0) board3 = null;
//            if(this.maxBins.get(4) == 0) board4 = null;
//            if(this.maxBins.get(5) == 0) board5 = null;
//            if(this.maxBins.get(6) == 0) board6 = null;
//
//            if(board1 != null){
//                board1.maxTurn(1);
//
//            }
//            if(board2 != null){
//                board2.maxTurn(2);
//
//            }
//            if(board3 != null){
//                board3.maxTurn(3);
//
//            }
//            if(board4 != null){
//                board4.maxTurn(4);
//            }
//            if(board5 != null){
//                board5.maxTurn(5);
//            }
//            if(board6 != null){
//                board6.maxTurn(6);
//            }

        }
        else{

            for(int i = 0; i < boards.length; i++){
                if(this.minBins.get(i) == 0){
                    boards[i] = null;
                }
                else{
                    boards[i].minTurn(i+1);
                }
            }

//            if(this.minBins.get(0) == 0) board1 = null;
//            if(this.minBins.get(1) == 0) board2 = null;
//            if(this.minBins.get(2) == 0) board3 = null;
//            if(this.minBins.get(3) == 0) board4 = null;
//            if(this.minBins.get(4) == 0) board5 = null;
//            if(this.minBins.get(5) == 0) board6 = null;
//
//
//            if(board1 != null){
//                board1.minTurn(1);
//            }
//            if(board2 != null){
//                board2.minTurn(2);
//
//            }
//            if(board3 != null){
//                board3.minTurn(3);
//
//            }
//            if(board4 != null){
//                board4.minTurn(4);
//
//            }
//            if(board5 != null){
//                board5.minTurn(5);
//
//            }
//            if(board6 != null){
//                board6.minTurn(6);
//
//            }

        }

        for(int i = 0; i < boards.length; i++){
            possibleMoves.add(boards[i]);
        }

//        possibleMoves.add(board1);
//        possibleMoves.add(board2);
//        possibleMoves.add(board3);
//        possibleMoves.add(board4);
//        possibleMoves.add(board5);
//        possibleMoves.add(board6);

        return possibleMoves;

    }

    public int isMatchWon()
    {
        int totalMax = 0, totalMin = 0;
        for(int i = 0; i < maxBins.size(); i++){
            totalMax += maxBins.get(i);
            totalMin += minBins.get(i);
        }

        if(totalMax == 0){
            int minCount = getMinStones() + getMinStorage();
            if(getMaxStorage() > minCount){
                return 1; // max won
            }
            else if(getMaxStorage() == minCount){
                return 0; // match tie
            }
            else{
                return 2; // min won
            }
        }
        else if(totalMin == 0){
            int maxCount = getMaxStones() + getMaxStorage();

            if(getMinStorage() > maxCount){
                return 2; // min won
            }
            else if(getMinStorage() == maxCount){
                return 0; // match tie
            }
            else{
                return 1; // max won
            }

        }
        else{
            return -1; // match not finished
        }

    }

    public void print(){


        System.out.println("Min Store |=========Min Player=========| Max Store");
        System.out.println("==============================================");


        System.out.printf("|    |");
        for(int i = 0; i < minBins.size(); i++){
            System.out.printf(minBins.get(i) + "  |  ");
        }
        System.out.printf("   |");

        System.out.println();

        System.out.println("| "+ getMinStorage() + "  |---------------------------------|  "+ getMaxStorage() + "  |");

        System.out.printf("|    |");
        for(int i = maxBins.size()-1; i >= 0; i--){
            System.out.printf(maxBins.get(i) + "  |  ");
        }
        System.out.printf("   |");

        System.out.println();

        System.out.println("==============================================");
        System.out.println("=================Max Player===================");

    }


}
