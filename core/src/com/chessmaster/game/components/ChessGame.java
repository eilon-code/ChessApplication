package com.chessmaster.game.components;

import com.chessmaster.game.components.gameObjects.BoardObjects.*;
import com.chessmaster.game.components.gameObjects.GamePieces.Color;
import com.chessmaster.game.components.gameObjects.GamePieces.PieceType;

import java.util.Random;
import java.util.Stack;

public class ChessGame {
    private static Cell Previous_cell;
    private static Cell Current_cell;
    public static int Click_counted_on_same_cell;
    private static Move[] Previous_legal_moves;
    private static Move[] Current_legal_moves;

    public boolean SomePieceMoved;
    public boolean WinPawn;

    public boolean GameOver;
    private int sum_current_legal_moves;
    private Point lastRedLocation;

    private Stack<Move> all_legal_moves;

    // 2D array of buttons whose values are determined by My_Board
    public static BoardCell[][] BoardCellsGrid;

    public ChessGame(int boardSize, boolean isFisherChess)
    {
        boolean is_white_up = false;
        boolean is_white_start = true;

        int[] listPieces;
        listPieces = FisherChess(isFisherChess, boardSize);

        GroupOfPieces white_pieces = new GroupOfPieces(Color.White, (is_white_up ? 1 : 0), listPieces);
        GroupOfPieces black_pieces = new GroupOfPieces(Color.Black, (is_white_up ? 0 : 1), listPieces);

        Board board = new Board(white_pieces, black_pieces, Color.White);

        new Position(board);

        Current_cell = null;
        Previous_cell = null;

        Previous_legal_moves = null;
        Current_legal_moves = null;
        lastRedLocation = null;

        // Click_counted_on_same_cell = 1;

        BoardCellsGrid = new BoardCell[Board.BoardSize][Board.BoardSize];
        ResetGraphicsCellsArray();

        newMoves();
    }

    public ChessGame(Board board) {
        new Position(board);

        Current_cell = null;
        Previous_cell = null;

        Previous_legal_moves = null;
        Current_legal_moves = null;
        lastRedLocation = null;

        // Click_counted_on_same_cell = 1;

        BoardCellsGrid = new BoardCell[Board.BoardSize][Board.BoardSize];

        ResetGraphicsCellsArray();
        System.out.println("\nCheck 1:");
        Position.compareBoardToPosition(board);

        GroupOfPieces white_pieces = new GroupOfPieces(board, Color.White);
        GroupOfPieces black_pieces = new GroupOfPieces(board, Color.Black);
        Board boardNew = new Board(white_pieces, black_pieces, Color.White);
        System.out.println("\nCheck 2:");
        Position.compareBoardToPosition(boardNew);

        newMoves();
        System.out.println("\nCheck 3:");
        Position.compareBoardToPosition(board);
    }

    public void ClickDuringGame(Point location)
    {
        // this function called when the screen got clicked or touched
        // get the row and the column number of the button clicked
        int x = location.X;
        int y = location.Y;

        WinPawn = false;
        SomePieceMoved = false;
        Current_cell = new Cell(Position.board.cellsGrid[x][y]);
        Current_legal_moves = null;

        if (Previous_legal_moves != null)
        {
            for (Move legal_move : Previous_legal_moves) {
                if (legal_move != null) {
                    // rules of activate legal move:
                    if (legal_move.endRow == Current_cell.row && legal_move.endColumn == Current_cell.column) {
                        // update move to all calculation classes (like: Board, Group_od_pieces, Position...):
                        SomePieceMoved = true;
                        WinPawn = UpdateMoveToGameClasses(legal_move);

                        // System.out.println("is 3 checkmate = " + Board_Pos.detect_CheckmateIn_3_Moves());
                        // System.out.println("is 2 checkmate = " + Board_Pos.detect_CheckmateIn_2_Moves());
                        // System.out.println("is 1 checkmate = " + Board_Pos.detect_CheckmateIn_1_Moves());
                        break;
                    }
                }
            }
        }

        if (Current_cell.color.equals(Position.colorTurn)){
            Current_legal_moves = new Move[28];
            int current_move = 0;
            // detect the list of all the available legal moves with the chosen piece from the list of all the legal moves
            for (Move legal_move : all_legal_moves) {
                if (legal_move != null) {
                    if (legal_move.startRow == Current_cell.row && legal_move.startColumn == Current_cell.column) {
                        Current_legal_moves[current_move] = legal_move;
                        current_move++;
                    }
                }
            }

            // count how many legal moves are available with the chosen piece
            sum_current_legal_moves = 0;
            for (Move current_legal_move : Current_legal_moves) {
                if (current_legal_move != null) {
                    sum_current_legal_moves++;
                }
            }
        }

        // cases of clicks (legal moves, previous legal moves, previous_cell...)
        if (Previous_cell != null) {
            if (Previous_cell.row == Current_cell.row && Previous_cell.column == Current_cell.column) {
                Click_counted_on_same_cell++;
            }
            else
            {
                Click_counted_on_same_cell = 1;
            }
        }
        else
        {
            Click_counted_on_same_cell = 1;
        }
        if (Click_counted_on_same_cell % 2 == 1 && (Current_cell.color.equals(Position.colorTurn))){
            if (Current_cell.isTherePiece){
                BoardCellsGrid[Current_cell.row][Current_cell.column].BackgroundColor = "brown";
            }
        }

        RemoveUnRelevantLegalMoveOptions();
        PreviousMoveToPurple();
        if (Current_cell.isTherePiece && (Current_cell.color.equals(Position.colorTurn)))
        {
            if (Click_counted_on_same_cell % 2 == 1)
            {
                // update the text on each button
                for (int index = 0; index < sum_current_legal_moves; index++)
                {
                    if (Current_legal_moves[index] != null) {
                        BoardCellsGrid[Current_legal_moves[index].endRow][Current_legal_moves[index].endColumn].BackgroundColor = "green";
                    }
                }
                Previous_legal_moves = Current_legal_moves;
            }
        }

        if (SomePieceMoved)
        {
            newMoves();

            // Sound();
        }
        else
        {
            Previous_cell = Current_cell;
        }
//        System.out.println("At Point: (" + Current_cell.row + ", " + Current_cell.column + ")");
//        System.out.println("There Is" + (Current_cell.isTherePiece ? Current_cell.type : PieceType.None));
    }

    public static int CountClicksOnSameCell_AfterCell(Point cell)
    {
        // cases of clicks (legal moves, previous legal moves, previous_cell...)
        if (Current_cell != null) {
            if (cell.X == Current_cell.row && cell.Y == Current_cell.column) {
                return Click_counted_on_same_cell + 1;
            }
            else
            {
                return 1;
            }
        }
        else
        {
            return 1;
        }
    }

    private void newMoves(){
        // determine legal next moves
        all_legal_moves = Position.Get_all_legal_moves();
        GameOver = all_legal_moves.empty();
    }

    public void PawnChangedInto(PieceType type)
    {
        WinPawn = false;
        Position.DeclarePawnCrown_in_real(type);
        BoardCellsGrid[Current_cell.row][Current_cell.column].type = type;
        newMoves();
    }

    public void KingInDangerToRedBackground()
    {
        if (lastRedLocation != null && !BoardCellsGrid[lastRedLocation.X][lastRedLocation.Y].BackgroundColor.equals("purple"))
        {
            if (lastRedLocation.X % 2 == 0 ^ lastRedLocation.Y % 2 == 0)
            {
                BoardCellsGrid[lastRedLocation.X][lastRedLocation.Y].BackgroundColor = "white";
            }
            else
            {
                BoardCellsGrid[lastRedLocation.X][lastRedLocation.Y].BackgroundColor = "black";
            }
        }
        if (Position.kingAtDanger != null && !WinPawn)
        {
            BoardCellsGrid[Position.kingAtDanger.X][Position.kingAtDanger.Y].BackgroundColor = "red";
            lastRedLocation = new Point(Position.kingAtDanger);
            return;
        }
        lastRedLocation = null;
    }

    private boolean UpdateMoveToGameClasses(Move move)
    {
        RemoveUnRelevantPreviousMove();
        // update position:
        Position.Play_move_in_real(move);

        // update move to btnGrid (the output of graphics):
        UpdateMoveToGraphicsArray();

        return move.crowningPawn;
    }

    private void UpdateMoveToGraphicsArray()
    {
        // King and Rook moved both (castle):
        if (Previous_cell.type.equals(PieceType.King) && Current_cell.type.equals(PieceType.Rook) && Previous_cell.color.equals(Current_cell.color))
        {
            Color colorPiece = Previous_cell.color;
            if (Previous_cell.row - Current_cell.row > 0)
            {
                BoardCellsGrid[Current_cell.row][Current_cell.column].pieceColor = Color.None;
                BoardCellsGrid[Current_cell.row][Current_cell.column].type = PieceType.None;

                BoardCellsGrid[3][Current_cell.column].pieceColor = colorPiece;
                BoardCellsGrid[3][Current_cell.column].type = PieceType.Rook;

                if (Previous_cell.row != 3){
                    BoardCellsGrid[Previous_cell.row][Previous_cell.column].pieceColor = Color.None;
                    BoardCellsGrid[Previous_cell.row][Previous_cell.column].type = PieceType.None;
                }

                BoardCellsGrid[2][Previous_cell.column].pieceColor = colorPiece;
                BoardCellsGrid[2][Previous_cell.column].type = PieceType.King;
            }
            else
            {
                BoardCellsGrid[Current_cell.row][Current_cell.column].pieceColor = Color.None;
                BoardCellsGrid[Current_cell.row][Current_cell.column].type = PieceType.None;

                BoardCellsGrid[5][Current_cell.column].pieceColor = colorPiece;
                BoardCellsGrid[5][Current_cell.column].type = PieceType.Rook;

                if (Previous_cell.row != 5){
                    BoardCellsGrid[Previous_cell.row][Previous_cell.column].pieceColor = Color.None;
                    BoardCellsGrid[Previous_cell.row][Previous_cell.column].type = PieceType.None;
                }

                BoardCellsGrid[6][Previous_cell.column].pieceColor = colorPiece;
                BoardCellsGrid[6][Previous_cell.column].type = PieceType.King;
            }
        }
        else {
            // Pawn kills other pawn by go to empty cell:
            if (Previous_cell.type.equals(PieceType.Pawn) && Previous_cell.row != Current_cell.row && !Current_cell.isTherePiece)
            {
                BoardCellsGrid[Current_cell.row][Previous_cell.column].pieceColor = Color.None;
                BoardCellsGrid[Current_cell.row][Previous_cell.column].type = PieceType.None;
            }

            BoardCellsGrid[Current_cell.row][Current_cell.column].pieceColor = Previous_cell.color;
            BoardCellsGrid[Current_cell.row][Current_cell.column].type = Previous_cell.type;

            BoardCellsGrid[Previous_cell.row][Previous_cell.column].pieceColor = Color.None;
            BoardCellsGrid[Previous_cell.row][Previous_cell.column].type = PieceType.None;
        }
    }

    private void RenderGraphicsArray(){
        for (int row = 0; row < Board.BoardSize; row++){
            for (int column = 0; column < Board.BoardSize; column++){
                BoardCellsGrid[row][column].pieceColor = Position.board.cellsGrid[row][column].color;
                BoardCellsGrid[row][column].type = Position.board.cellsGrid[row][column].type;
            }
        }
    }

    private void RemoveUnRelevantLegalMoveOptions()
    {
        if (Previous_legal_moves != null)
        {
            for (Move previous_legal_move : Previous_legal_moves) {
                if (previous_legal_move != null) {
                    if (previous_legal_move.endRow % 2 == 0 ^ previous_legal_move.endColumn % 2 == 0) {
                        BoardCellsGrid[previous_legal_move.endRow][previous_legal_move.endColumn].BackgroundColor = "white";
                    } else {
                        BoardCellsGrid[previous_legal_move.endRow][previous_legal_move.endColumn].BackgroundColor = "black";
                    }
                }
            }
            Previous_legal_moves = null;
            if (!(new Point(Previous_cell)).equals(Position.kingAtDanger)){
                if (Previous_cell.row % 2 == 0 ^ Previous_cell.column % 2 == 0) {
                    BoardCellsGrid[Previous_cell.row][Previous_cell.column].BackgroundColor = "white";
                } else {
                    BoardCellsGrid[Previous_cell.row][Previous_cell.column].BackgroundColor = "black";
                }
            }
            else {
                BoardCellsGrid[Previous_cell.row][Previous_cell.column].BackgroundColor = "red";
            }
        }
    }

    private void RemoveUnRelevantPreviousMove()
    {
        if (Position.reverseMoveAvailable()){
            Point start = Position.getMoveStartCell();
            Point end = Position.getMoveEndCell();
            if (start.X == 0 ^ start.Y == 0) {
                BoardCellsGrid[start.X][start.Y].BackgroundColor = "white";
            }
            else {
                BoardCellsGrid[start.X][start.Y].BackgroundColor = "black";
            }
            if (start.X == 0 ^ start.Y == 0) {
                BoardCellsGrid[end.X][end.Y].BackgroundColor = "white";
            }
            else{
                BoardCellsGrid[end.X][end.Y].BackgroundColor = "black";
            }
        }
    }

    private void PreviousMoveToPurple()
    {
        if (Position.reverseMoveAvailable()){
            Point start = Position.getMoveStartCell();
            Point end = Position.getMoveEndCell();
            BoardCellsGrid[start.X][start.Y].BackgroundColor = "purple";
            BoardCellsGrid[end.X][end.Y].BackgroundColor = "purple";
        }
    }

    private void ResetGraphicsCellsArray()
    {
        // nested loops. create buttons and print them to the scren
        for (int i = 0; i < Board.BoardSize; i++)
        {
            for (int j = 0; j < Board.BoardSize; j++)
            {
                BoardCellsGrid[i][j] = new BoardCell();
                if (i % 2 == 0 ^ j % 2 == 0)
                {
                    BoardCellsGrid[i][j].BackgroundColor = "white";
                }
                else
                {
                    BoardCellsGrid[i][j].BackgroundColor = "black";
                }

                // set the text for the new button
                BoardCellsGrid[i][j].type = Position.board.cellsGrid[i][j].type;
                BoardCellsGrid[i][j].pieceColor = Position.board.cellsGrid[i][j].color;
            }
        }
    }

    public int[] FisherChess(boolean isFisher, int boardSize){
        if (!isFisher){
            // not fisher-chess
            return new int[] {4, 0, 7, 2, 5, 1, 6, 3};
        }
        else {
            // fisher-chess
            Random rnd = new Random();
            int[] emptyPlaces = new int[boardSize];
            int[] RowList = new int[boardSize];

            int newPlace;
            for (int j = 0; j < boardSize; j++) {

                if (j == 0){
                    newPlace = Math.abs(rnd.nextInt()) % 6 + 1;
                }
                else{
                    if (j == 1 || j == 2){
                        newPlace = Math.abs(rnd.nextInt()) % (7 * (j - 1) - (j * 2 - 3) * emptyPlaces[0]) + (j - 1) * (emptyPlaces[0] + 1);
                    }
                    else{
                        if (j == 4){
                            newPlace = (Math.abs(rnd.nextInt()) % 4 * 2 + emptyPlaces[3] + 1) % 8;
                            while (RowList[newPlace] != 0) {
                                newPlace += 2;
                                newPlace %= 8;
                            }
                        }
                        else{
                            newPlace = Math.abs(rnd.nextInt()) % 8;
                            while (RowList[newPlace] != 0) {
                                newPlace++;
                                newPlace %= 8;
                            }
                        }
                    }
                }
                emptyPlaces[j] = newPlace;
                RowList[newPlace] = 1;
            }
            return emptyPlaces;
        }
    }

    public void ReversePawnWinning() {
        WinPawn = false;
        RemoveUnRelevantPreviousMove();
        Position.Reverse_move();
        PreviousMoveToPurple();

        // update move to btnGrid (the output of graphics):
        RenderGraphicsArray();
        newMoves();
    }

    public void ReverseMove() {
        RemoveUnRelevantPreviousMove();
        Position.Reverse_move_in_real();
        PreviousMoveToPurple();

        // update move to btnGrid (the output of graphics):
        RenderGraphicsArray();
        newMoves();
    }

    public void Replay_reversed_move() {
        if (!WinPawn){
            RemoveUnRelevantPreviousMove();
            Position.Replay_reversed_move_in_real();
            PreviousMoveToPurple();

            // update move to btnGrid (the output of graphics):
            RenderGraphicsArray();
            newMoves();
        }
    }

    public void ReverseAllMoves() {
        RemoveUnRelevantPreviousMove();
        while (Position.reverseMoveAvailable()){
            Position.Reverse_move_in_real();
        }
        PreviousMoveToPurple();

        // update move to btnGrid (the output of graphics):
        RenderGraphicsArray();
        newMoves();
    }

    public void ReplayAllReversedMoves() {
        if (!WinPawn){
            RemoveUnRelevantPreviousMove();
            while (Position.replayReversedMoveAvailable()){
                Position.Replay_reversed_move_in_real();
            }
            PreviousMoveToPurple();

            // update move to btnGrid (the output of graphics):
            RenderGraphicsArray();
            newMoves();
        }
    }

    public void ClearHistory() {
        Position.ClearHistory();
        Click_counted_on_same_cell = 0;
        newMoves();
    }
}
