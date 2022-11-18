package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Bishop;
import com.mygdx.jar.gameObjects.GamePieces.Color;
import com.mygdx.jar.gameObjects.GamePieces.Knight;
import com.mygdx.jar.gameObjects.GamePieces.Pawn;
import com.mygdx.jar.gameObjects.GamePieces.Piece;
import com.mygdx.jar.gameObjects.GamePieces.PieceType;
import com.mygdx.jar.gameObjects.GamePieces.Queen;
import com.mygdx.jar.gameObjects.GamePieces.Rook;

import java.util.Stack;

public class Position {
    public static Board board;
    public static GroupOfPieces whitePieces;
    public static GroupOfPieces blackPieces;
    public static Color colorTurn;
    public static boolean Is_white_up;
    public static Point kingAtDanger;
    private static Stack<Move> recordedMoves;
    private static Stack<Move> reversedMoves;

    public Position(Board chess_board) {
        board = new Board(chess_board);
        whitePieces = new GroupOfPieces(chess_board, Color.White);
        blackPieces = new GroupOfPieces(chess_board, Color.Black);
        colorTurn = chess_board.startingColor;
        Is_white_up = false;
        recordedMoves = new Stack<Move>();
        reversedMoves = new Stack<Move>();
        setDangerCell(true);
    }

    public static void compareBoardToPosition(Board board){
        String[] A_TO_H = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] ONE_TO_EIGHT = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        StringBuilder txt = new StringBuilder("Board Fuck-Ups: ");
        for (int i = 0; i < A_TO_H.length; i++) {
            for (int j = 0; j < ONE_TO_EIGHT.length; j++) {
                Cell cell1 = new Cell(board.cellsGrid[i][j]);
                Cell cell2 = new Cell(Position.board.cellsGrid[i][j]);
                if (!cell1.equals(cell2)){
                    txt.append(A_TO_H[i]);
                    txt.append(ONE_TO_EIGHT[j]);
                    txt.append(": ");
                    txt.append("{Before load: ");
                    txt.append(cell1.isTherePiece ? cell1.type : Board.Nothing);
                    txt.append(", After load: ");
                    txt.append(cell2.isTherePiece ? cell2.type : Board.Nothing);
                    txt.append("}");
                    if (i < A_TO_H.length - 1 || j < ONE_TO_EIGHT.length - 1){
                        txt.append(", ");
                    }
                }
            }
        }
        if (txt.toString().equals("Board Fuck-Ups: ")){
            System.out.println(txt.toString() + "NONE :)");
        }
        else{
            String text = txt.toString();
            text = text.substring(0, text.length() - 2);
            System.out.println(text);
        }
    }

    public static Stack<Move> Get_all_legal_moves()
    {
        return Get_all_legal_Group_moves();
    }

    public static boolean king_in_danger(int kingRow, int kingColumn, Stack<Move> moves_options)
    {
        for (Move moves_option : moves_options) {
            if (moves_option != null) {
                if (moves_option.endRow == kingRow && moves_option.endColumn == kingColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void setDangerCell(boolean hasMoved){
        Point kingLocation = colorTurn.equals(Color.White) ? whitePieces.get_king_point() : blackPieces.get_king_point();
        if (kingLocation != null){
            if (king_in_danger(kingLocation.X, kingLocation.Y, Get_all_Group_moves(!colorTurn.equals(Color.White)))){
                kingAtDanger = hasMoved ? new Point(kingLocation) : null;
                return;
            }
        }
        kingAtDanger = null;
    }

    public static Point getMoveStartCell(){
        Move move = recordedMoves.peek();
        return new Point(move.startRow, move.startColumn);
    }

    public static Point getMoveEndCell(){
        Move move = recordedMoves.peek();
        if (!move.castle){
            return new Point(move.endRow, move.endColumn);
        }
        else{
            return (colorTurn.equals(Color.Black) ? whitePieces : blackPieces).get_king_point();
        }
    }

    public static Point getNextEndMoveCell(){
        if (reversedMoves.empty()){
            return null;
        }
        Move move = reversedMoves.peek();
        if (!move.castle){
            return new Point(move.endRow, move.endColumn);
        }
        else{
            return (colorTurn.equals(Color.Black) ? whitePieces : blackPieces).get_king_point();
        }
    }

    public static Stack<Move> Get_all_Group_moves(boolean isWhiteTurn)
    {
        GroupOfPieces group = isWhiteTurn ? whitePieces : blackPieces;
        GroupOfPieces other_group = isWhiteTurn ? blackPieces : whitePieces;
        Stack<Move> groupMoves = new Stack<>();
        for (Piece groupPiece : group.pieces){
            if (groupPiece != null && !groupPiece.isDeleted)
            {
                groupPiece.fillPieceMoves(groupMoves, isWhiteTurn, Is_white_up, board, other_group, group);
            }
        }
        return groupMoves;
    }

    private static Stack<Move> Get_all_legal_Group_moves()
    {
        GroupOfPieces group = colorTurn.equals(Color.White) ? whitePieces : blackPieces;
        Stack<Move> groupMoves = Get_all_Group_moves(colorTurn.equals(Color.White));
        Stack<Move> groupLegalMoves = new Stack<>();

        Point kingPoint;

        while (!groupMoves.empty()){
            Move groupMove = groupMoves.pop();
            if (groupMove != null &&
                    group.Piece_location(groupMove.startRow, groupMove.startColumn) != null &&
                    !group.Piece_location(groupMove.startRow, groupMove.startColumn).isDeleted)
            {
                boolean moveLegal = true;
                if (groupMove.type.equals(PieceType.King) && board.cellsGrid[groupMove.endRow][groupMove.endColumn].type.equals(PieceType.Rook) &&
                        groupMove.color.equals(board.cellsGrid[groupMove.endRow][groupMove.endColumn].color)){
                    int wantedRow;
                    if (groupMove.startRow < groupMove.endRow)
                    {
                        wantedRow = 6;
                    }
                    else
                    {
                        wantedRow = 2;
                    }
                    int startRowMovement = Math.min(groupMove.startRow, wantedRow);
                    int endRowMovement = Math.max(groupMove.startRow, wantedRow);

                    Stack<Move> optionalOpponentResponse = Get_all_Group_moves(!colorTurn.equals(Color.White));
                    for (int row = startRowMovement; row <= endRowMovement; row++)
                    {
                        if (king_in_danger(row, groupMove.endColumn, optionalOpponentResponse))
                        {
                            moveLegal = false;
                            break;
                        }
                    }
                    if (!moveLegal){
                        continue;
                    }
                }

                Play_move(groupMove);

                Stack<Move> optionalOpponentResponse = Get_all_Group_moves(colorTurn.equals(Color.White));

                kingPoint = group.get_king_point();

                if (kingPoint == null || king_in_danger(kingPoint.X, kingPoint.Y, optionalOpponentResponse))
                {
                    moveLegal = false;
                }
                Reverse_move();

                if (moveLegal){
                    groupLegalMoves.push(groupMove);
                }
            }
        }
        return groupLegalMoves;
    }

    public static void ClearHistory() {
        reversedMoves = new Stack<Move>();
        recordedMoves = new Stack<Move>();
    }

    public static void Play_move(Move move) {
        // update groups of pieces:
        GroupOfPieces group = colorTurn.equals(Color.White) ? whitePieces : blackPieces;
        GroupOfPieces other_group = colorTurn.equals(Color.White) ? blackPieces : whitePieces;
        Piece deleted_piece = other_group.Delete_piece(move.endRow, move.endColumn, move.startRow, move.startColumn, group);
        group.Update_move(move);

        move.setDeletedPiece(deleted_piece);
        recordedMoves.push(move);
        if (move.crowningPawn){
            DeclarePawnCrown(move.crowningPawnType);
        }

        // update board:
        board.updateBoard(whitePieces, blackPieces);
        colorTurn = colorTurn.equals(Color.White) ? Color.Black : Color.White;
    }

    public static Move Reverse_move() {
        Move move = recordedMoves.pop();
        GroupOfPieces group = move.color.equals(Color.White) ? whitePieces : blackPieces;
        GroupOfPieces other_group = move.color.equals(Color.White) ? blackPieces : whitePieces;

        if (move.deletedPiece != null){
            move.deletedPiece.isDeleted = false;
        }
        if (move.castle){
            Point king_point = group.get_king_point();
            Piece king = group.Piece_location(king_point.X, king_point.Y);
            Piece rook = group.Piece_location(king_point.X + (move.endRow > move.startRow ? -1 : 1), king_point.Y);
            king.row = move.startRow;
            king.column = move.startColumn;
            king.numberOfMoves--;

            rook.row = move.endRow;
            rook.column = move.endColumn;
            rook.numberOfMoves--;
        }
        else {
            Piece piece = group.Piece_location(move.endRow, move.endColumn);
            if (piece != null){
                piece.row = move.startRow;
                piece.column = move.startColumn;
                piece.numberOfMoves--;
                if (move.crowningPawn){
                    Stack<Piece> temp = new Stack<>();
                    while (!group.pieces.empty() && group.pieces.peek().pieceNum > piece.pieceNum){
                        temp.push(group.pieces.pop());
                    }
                    if (!group.pieces.empty() && piece.pieceNum == group.pieces.peek().pieceNum){
                        // set piece as eaten
                        group.pieces.pop();
                        piece.isDeleted = false;
                        group.pieces.push(new Pawn(piece));
                    }
                    while (!temp.empty()){
                        group.pieces.push(temp.pop());
                    }
                }
            }
        }
        colorTurn = colorTurn.equals(Color.White) ? Color.Black : Color.White;

        if (!recordedMoves.empty()){
            Move previous_move = recordedMoves.peek();
            if (previous_move.type.equals(PieceType.Pawn) && Math.abs(previous_move.startColumn - previous_move.endColumn) > 1){
                Pawn pawn = (Pawn) other_group.Piece_location(previous_move.endRow, previous_move.endColumn);
                if (pawn != null){
                    pawn.moved_two_cells = true;
                }
            }
        }

        // update board:
        board.updateBoard(whitePieces, blackPieces);
        return move;
    }

    public static void DeclarePawnCrown(PieceType type) {
        if (recordedMoves.empty()){
            return;
        }
        Move crowningMove = recordedMoves.peek();
        if (!crowningMove.type.equals(PieceType.Pawn)){
            return;
        }
        setDangerCell(false);
        GroupOfPieces group = crowningMove.color.equals(Color.White) ? whitePieces : blackPieces;

        Piece pawn = group.Piece_location(crowningMove.endRow, crowningMove.endColumn);
        int piece_num = (pawn != null ? pawn.pieceNum : group.pieces.size());
        Stack<Piece> temp = new Stack<>();

        if (!type.equals(PieceType.None)){
            while (!group.pieces.empty() && group.pieces.peek().pieceNum > piece_num){
                temp.push(group.pieces.pop());
            }
            if (!group.pieces.empty() && piece_num == group.pieces.peek().pieceNum){
                group.pieces.pop();
                switch (type) {
                    case Rook:
                        group.pieces.push(new Rook(pawn));
                        break;

                    case Bishop:
                        group.pieces.push(new Bishop(pawn));
                        break;

                    case Knight:
                        group.pieces.push(new Knight(pawn));
                        break;

                    case Queen:
                        group.pieces.push(new Queen(pawn));
                        break;

                    default:
                        return;
                }
                group.pieces.peek().type = type;
            }
        }
        setDangerCell(true);

        // update board:
        board.cellsGrid[pawn.row][pawn.column].type = type;

        while (!temp.empty()){
            group.pieces.push(temp.pop());
        }
    }

    public static void Play_move_in_real(Move move){
        setDangerCell(false);
        Play_move(move);
        setDangerCell(true);
        if (!reversedMoves.empty() && !move.crowningPawn && !reversedMoves.pop().equals(move)) {
            reversedMoves = new Stack<Move>();
        }
    }

    public static void DeclarePawnCrown_in_real(PieceType type) {
        DeclarePawnCrown(type);
        recordedMoves.peek().crowningPawnType = type;
        if (!reversedMoves.empty() && !reversedMoves.pop().crowningPawnType.equals(type)){
            reversedMoves = new Stack<Move>();
        }
    }

    public static void Reverse_move_in_real(){
        if (!recordedMoves.empty()){
            setDangerCell(false);
            reversedMoves.push(Reverse_move());
            setDangerCell(true);
        }
    }

    public static void Replay_reversed_move_in_real(){
        if (!reversedMoves.empty()){
            setDangerCell(false);
            Play_move(reversedMoves.pop());
            setDangerCell(true);
        }
    }

    public static boolean reverseMoveAvailable() {
        return !recordedMoves.empty();
    }

    public static boolean replayReversedMoveAvailable() {
        return !reversedMoves.empty();
    }
}
