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
                if(this.board[i][j] == "*") continue;

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

    public int hamming(){

        int heuristic = 0;
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                if(this.board[i][j].equalsIgnoreCase("*")) continue;

                int value = Integer.parseInt(this.board[i][j]);
                if( (i*dimension+j+1) != value){
                    heuristic++;
                }
            }
        }

        return heuristic;

    }

    public int manhattan(){

        int heuristic = 0;
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){

                if(this.board[i][j] == "*") continue;

                int value = Integer.parseInt(this.board[i][j]);
                int row, col;
                if(value % dimension == 0){
                    row = (int) Math.floor((value-1)/dimension) + 1;
                    col = (value-1) % dimension + 1;
                }
                else{
                    row = (int) Math.floor(value/dimension) + 1;
                    col = value % dimension;
                }

                heuristic += Math.abs(row - i);
                heuristic += Math.abs(col - j);

            }
        }

        return heuristic;
    }

    public int linearConflict(){

        int manhattanDistance = manhattan();
        int linear = 0;

        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                if(this.board[i][j] == "*") continue;

                int value1 = Integer.parseInt(this.board[i][j]);
                for(int k = j + 1; k < dimension; k++){
                    if(this.board[i][k] == "*") continue;

                    int value2 = Integer.parseInt(this.board[i][k]);

                    int row1, row2;
                    if(value1 % dimension == 0){
                        row1 = (int) Math.floor((value1-1)/dimension) + 1;
                    }
                    else{
                        row1 = (int) Math.floor(value1/dimension) + 1;
                    }
                    if(value2 % dimension == 0){
                        row2 = (int) Math.floor((value2-1)/dimension) + 1;
                    }
                    else{
                        row2 = (int) Math.floor(value2/dimension) + 1;
                    }

                    if(row1 == i && row2 == i){
                        if(value2 < value1){
                            linear++;
                        }
                    }
                }
            }
        }

        return (manhattanDistance + linear*2);

    }


    public int getCost(String heuristicName){

        if(heuristicName.equalsIgnoreCase("hamming")){

            return this.moves + hamming();
        }
        else if(heuristicName.equalsIgnoreCase("manhattan")) {
            return this.moves + manhattan();
        }
        else if(heuristicName.equalsIgnoreCase("linear")){

            return this.moves + linearConflict();
        }
        else{
            return 0;
        }
    }

    public String getBlankPosition(){
        int row = -1, col = -1;
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++){
                if(this.board[i][j] == "*"){
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        return row + "#" + col;
    }

    public SearchNode getNeighbour(String side){

        SearchNode newNode;
        String[][] newBoard = this.board;

        String[] rowCol = getBlankPosition().split("#");
        int row = Integer.parseInt(rowCol[0]);
        int col = Integer.parseInt(rowCol[1]);

        if(side.equalsIgnoreCase("left")){
            if(col == 0){
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
}
