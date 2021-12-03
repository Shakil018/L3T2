class SearchNode {
    private int moves;
    private SearchNode parent;
    public String[][] board;
    public static int dimension;

    SearchNode(String[][] board, int moves, SearchNode parent){
        this.board = board;
        this.moves = moves;
        this.parent = parent;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public SearchNode getParent() {
        return parent;
    }

    public void setParent(SearchNode parent) {
        this.parent = parent;
    }

    Boolean isSameNode(SearchNode second){

        if(second == null) return false;

        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(!this.board[i][j].equalsIgnoreCase(second.board[i][j]))
                    return false;
            }
        }
        return true;
    }

    Boolean isGoalNode(){

        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(this.board[i][j].equalsIgnoreCase("*")){
                    if(i != (dimension-1) || j != (dimension-1)){
                        return false;
                    }
                    else{
                        continue;
                    }
                }

                int value = Integer.parseInt(this.board[i][j]);
                if(value != (i*dimension + j + 1)){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSolvable(){

        int inversions = getInversions();

        if((dimension*dimension)%2 == 1){
            if(inversions % 2 == 1){
                return false;
            }
            else{
                return true;
            }
        }
        else{

            int blankRow = Integer.parseInt(getBlankPosition().split("#")[0]);
            blankRow = dimension - (blankRow + 1) + 1;

            if((blankRow % 2 == 1) && (inversions % 2 == 0)) {
                return true;
            }
            else if((blankRow % 2 == 0) && (inversions % 2) == 1){
                return true;
            }
            else {
                return false;
            }
        }
    }

    public int getInversions(){
        int inversions = 0;
        String[] linearBoard = new String[dimension*dimension];

        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                linearBoard[i*dimension + j] = this.board[i][j];
            }
        }

        for(int i = 0; i < dimension*dimension; i++){
            if(linearBoard[i].equalsIgnoreCase("*")) continue;

            int value1 = Integer.parseInt(linearBoard[i]);
            for(int j = i + 1; j < dimension*dimension; j++){

                if(linearBoard[j].equalsIgnoreCase("*")) continue;

                int value2 = Integer.parseInt(linearBoard[j]);
                if(value2 < value1) inversions++;
            }
        }

        return inversions;

    }

    public String getBlankPosition(){
        int row = -1, col = -1;
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                if(this.board[i][j].equalsIgnoreCase("*")){
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        return row + "#" + col;
    }


    public int getCost(String heuristicName){

        if(heuristicName.equalsIgnoreCase("hamming")){

            return this.moves + AStarSearch.hamming(this);
        }
        else if(heuristicName.equalsIgnoreCase("manhattan")) {
            return this.moves + AStarSearch.manhattan(this);
        }
        else if(heuristicName.equalsIgnoreCase("linear")){

            return this.moves + AStarSearch.linearConflict(this);
        }
        else{
            return 0;
        }
    }



    public SearchNode getNeighbour(String side){

        SearchNode newNode;
        String[][] newBoard = new String[dimension][dimension];

        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                newBoard[i][j] = this.board[i][j];
            }
        }

        String[] rowCol = getBlankPosition().split("#");
        int row = Integer.parseInt(rowCol[0]);
        int col = Integer.parseInt(rowCol[1]);

        if(side.equalsIgnoreCase("left")){
            //System.out.println("col: " + col);
            if(col == 0){
                //System.out.println("inside if");
                return null;
            }
            newBoard[row][col] = newBoard[row][col - 1];
            newBoard[row][col - 1] = "*";
        }
        else if(side.equalsIgnoreCase("right")){
            if(col == (dimension - 1)){
                return null;
            }
            newBoard[row][col] = newBoard[row][col + 1];
            newBoard[row][col + 1] = "*";

        }
        else if(side.equalsIgnoreCase("up")){
            if(row == 0){
                return null;
            }
            newBoard[row][col] = newBoard[row - 1][col];
            newBoard[row - 1][col] = "*";

        }
        else if(side.equalsIgnoreCase("down")){
            if(row == (dimension - 1)){
                return null;
            }
            newBoard[row][col] = newBoard[row + 1][col];
            newBoard[row + 1][col] = "*";
        }
        else{
            return null;
        }

        newNode = new SearchNode(newBoard, 0, null);
        return newNode;
    }

    public void printNode(){
        for(int i = 0; i < SearchNode.dimension; i++){
            System.out.printf("| ");
            for(int j = 0; j < SearchNode.dimension; j++){
                System.out.printf(this.board[i][j] + " | ");
            }
            System.out.println();
        }
    }
}
