package com.mygdx.jar;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.BoardCell;
import com.mygdx.jar.gameObjects.BoardObjects.Cell;
import com.mygdx.jar.gameObjects.BoardObjects.Group_of_pieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;
import com.mygdx.jar.gameObjects.BoardObjects.Point;
import com.mygdx.jar.gameObjects.BoardObjects.Position;

import java.util.Random;

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

    private Move[] all_legal_moves;

    // 2D array of buttons whose values are determined by My_Board
    public static BoardCell[][] BoardCellsGrid;

    public ChessGame(int boardSize, boolean isFisherChess)
    {
        boolean is_white_up = false;
        boolean is_white_start = true;

        int[] listPieces;
        listPieces = FisherChess(isFisherChess, boardSize);

        Group_of_pieces white_pieces = new Group_of_pieces("white", (is_white_up ? 1 : 0), listPieces);
        Group_of_pieces black_pieces = new Group_of_pieces("black", (is_white_up ? 0 : 1), listPieces);

        Board board = new Board(boardSize, white_pieces, black_pieces, true);

        new Position(board, white_pieces, black_pieces, is_white_start, is_white_up);

        Current_cell = null;
        Previous_cell = null;

        Previous_legal_moves = null;
        Current_legal_moves = null;
        lastRedLocation = null;

        // Click_counted_on_same_cell = 1;

        BoardCellsGrid = new BoardCell[Position.boardSize][Position.boardSize];
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

        BoardCellsGrid = new BoardCell[Position.boardSize][Position.boardSize];
        ResetGraphicsCellsArray();

        newMoves();
    }

    public void ClickDuringGame(Point location)
    {
        // this function called when the screen got clicked or touched
        // get the row and the column number of the button clicked
        int x = location.X;
        int y = location.Y;

        WinPawn = false;
        SomePieceMoved = false;
        Current_cell = new Cell(Position.Chess_Board.The_Grid[x][y]);
        Current_legal_moves = null;

        if (Previous_legal_moves != null)
        {
            for (Move legal_move : Previous_legal_moves) {
                if (legal_move != null) {
                    // rules of activate legal move:
                    if (legal_move.Next_row == Current_cell.Row_Number && legal_move.Next_column == Current_cell.Column_Number) {
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

        if (Current_cell.Color_piece.equals("black") ^ Position.Is_white_turn){
            Current_legal_moves = new Move[28];
            int current_move = 0;
            // detect the list of all the available legal moves with the chosen piece from the list of all the legal moves
            for (Move legal_move : all_legal_moves) {
                if (legal_move != null) {
                    if (legal_move.Current_row == Current_cell.Row_Number && legal_move.Current_column == Current_cell.Column_Number) {
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
            if (Previous_cell.Row_Number == Current_cell.Row_Number && Previous_cell.Column_Number == Current_cell.Column_Number) {
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
        if (Click_counted_on_same_cell % 2 == 1 && (Current_cell.Color_piece.equals("black") ^ Position.Is_white_turn)){
            if (Current_cell.Is_there_Piece){
                BoardCellsGrid[Current_cell.Row_Number][Current_cell.Column_Number].BackgroundColor = "brown";
            }
        }

        RemoveUnRelevantLegalMoveOptions();
        PreviousMoveToPurple();
        if (Current_cell.Is_there_Piece && (Current_cell.Color_piece.equals("black") ^ Position.Is_white_turn))
        {
            if (Click_counted_on_same_cell % 2 == 1)
            {
                // update the text on each button
                for (int index = 0; index < sum_current_legal_moves; index++)
                {
                    if (Current_legal_moves[index] != null) {
                        BoardCellsGrid[Current_legal_moves[index].Next_row][Current_legal_moves[index].Next_column].BackgroundColor = "green";
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
    }

    public static int CountClicksOnSameCell_AfterCell(Point cell)
    {
        // cases of clicks (legal moves, previous legal moves, previous_cell...)
        if (Current_cell != null) {
            if (cell.X == Current_cell.Row_Number && cell.Y == Current_cell.Column_Number) {
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

        int sum_legal_moves = 0;
        // detect the number of all the available legal moves from the list of all the legal moves
        for (Move move : all_legal_moves) {
            if (move != null) {
                sum_legal_moves++;
            }
        }
        GameOver = !(sum_legal_moves > 0);
    }

    public void PawnChangedInto(String type)
    {
        WinPawn = false;
        Position.DeclarePawnCrown_in_real(type);
        BoardCellsGrid[Current_cell.Row_Number][Current_cell.Column_Number].PieceType = type;
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
        if (Position.KingAtDanger != null && !WinPawn)
        {
            BoardCellsGrid[Position.KingAtDanger.X][Position.KingAtDanger.Y].BackgroundColor = "red";
            lastRedLocation = new Point(Position.KingAtDanger);
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

        return move.Crowning_pawn;
    }

    private void UpdateMoveToGraphicsArray()
    {
        // King and Rook moved both (castle):
        if (Previous_cell.Type.equals("King") && Current_cell.Type == "Rook" && Previous_cell.Color_piece == Current_cell.Color_piece)
        {
            String colorPiece = Previous_cell.Color_piece;
            if (Previous_cell.Row_Number - Current_cell.Row_Number > 0)
            {
                BoardCellsGrid[Current_cell.Row_Number][Current_cell.Column_Number].PieceColor = "";
                BoardCellsGrid[Current_cell.Row_Number][Current_cell.Column_Number].PieceType = "";

                BoardCellsGrid[3][Current_cell.Column_Number].PieceColor = colorPiece;
                BoardCellsGrid[3][Current_cell.Column_Number].PieceType = "Rook";

                if (Previous_cell.Row_Number != 3){
                    BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].PieceColor = "";
                    BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].PieceType = "";
                }

                BoardCellsGrid[2][Previous_cell.Column_Number].PieceColor = colorPiece;
                BoardCellsGrid[2][Previous_cell.Column_Number].PieceType = "King";
            }
            else
            {
                BoardCellsGrid[Current_cell.Row_Number][Current_cell.Column_Number].PieceColor = "";
                BoardCellsGrid[Current_cell.Row_Number][Current_cell.Column_Number].PieceType = "";

                BoardCellsGrid[5][Current_cell.Column_Number].PieceColor = colorPiece;
                BoardCellsGrid[5][Current_cell.Column_Number].PieceType = "Rook";

                if (Previous_cell.Row_Number != 5){
                    BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].PieceColor = "";
                    BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].PieceType = "";
                }

                BoardCellsGrid[6][Previous_cell.Column_Number].PieceColor = colorPiece;
                BoardCellsGrid[6][Previous_cell.Column_Number].PieceType = "King";
            }
        }
        else {
            // Pawn kills other pawn by go to empty cell:
            if (Previous_cell.Type == "Pawn" && Previous_cell.Row_Number != Current_cell.Row_Number && !Current_cell.Is_there_Piece)
            {
                BoardCellsGrid[Current_cell.Row_Number][Previous_cell.Column_Number].PieceColor = "";
                BoardCellsGrid[Current_cell.Row_Number][Previous_cell.Column_Number].PieceType = "";
            }

            BoardCellsGrid[Current_cell.Row_Number][Current_cell.Column_Number].PieceColor = Previous_cell.Color_piece;
            BoardCellsGrid[Current_cell.Row_Number][Current_cell.Column_Number].PieceType = Previous_cell.Type;

            BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].PieceColor = "";
            BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].PieceType = "";
        }
    }

    private void RenderGraphicsArray(){
        for (int row = 0; row < Position.boardSize; row++){
            for (int column = 0; column < Position.boardSize; column++){
                BoardCellsGrid[row][column].PieceColor = Position.Chess_Board.The_Grid[row][column].Color_piece;
                BoardCellsGrid[row][column].PieceType = Position.Chess_Board.The_Grid[row][column].Type;
            }
        }
    }

    private void RemoveUnRelevantLegalMoveOptions()
    {
        if (Previous_legal_moves != null)
        {
            for (Move previous_legal_move : Previous_legal_moves) {
                if (previous_legal_move != null) {
                    if (previous_legal_move.Next_row % 2 == 0 ^ previous_legal_move.Next_column % 2 == 0) {
                        BoardCellsGrid[previous_legal_move.Next_row][previous_legal_move.Next_column].BackgroundColor = "white";
                    } else {
                        BoardCellsGrid[previous_legal_move.Next_row][previous_legal_move.Next_column].BackgroundColor = "black";
                    }
                }
            }
            Previous_legal_moves = null;
            if (!(new Point(Previous_cell)).equals(Position.KingAtDanger)){
                if (Previous_cell.Row_Number % 2 == 0 ^ Previous_cell.Column_Number % 2 == 0) {
                    BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].BackgroundColor = "white";
                } else {
                    BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].BackgroundColor = "black";
                }
            }
            else {
                BoardCellsGrid[Previous_cell.Row_Number][Previous_cell.Column_Number].BackgroundColor = "red";
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
        for (int i = 0; i < Position.boardSize; i++)
        {
            for (int j = 0; j < Position.boardSize; j++)
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
                BoardCellsGrid[i][j].PieceType = Position.Chess_Board.The_Grid[i][j].Type;
                BoardCellsGrid[i][j].PieceColor = Position.Chess_Board.The_Grid[i][j].Color_piece;
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
