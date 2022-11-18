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

public class PositionCheck {
    public static final String[] TitlesList = new String[] {"מט בשני מסעים", "מט בשלושה מסעים", "מט בארבעה מסעים", "מט לדעת בשני מסעים", "מט עזר בשני מסעים", "כפיית פט בשני מסעים"};

    public final Board board;
    public final GroupOfPieces whitePieces;
    public final GroupOfPieces blackPieces;
    public static Color colorTurn;
    public boolean Is_white_up;
    public Point kingAtDanger;
    private final Stack<Move> recordedMoves;

    public PositionCheck(Board chess_board) {
        board = chess_board; // here the magic happens
        whitePieces = new GroupOfPieces(chess_board, Color.White);
        blackPieces = new GroupOfPieces(chess_board, Color.Black);
        colorTurn = chess_board.startingColor;
        Is_white_up = false;
        recordedMoves = new Stack<>();
        setDangerCell(true);
    }

    public boolean king_in_danger(int kingRow, int kingColumn, Stack<Move> moves_options)
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

    private void setDangerCell(boolean hasMoved){
        Point kingLocation = colorTurn.equals(Color.White) ? whitePieces.get_king_point() : blackPieces.get_king_point();
        if (kingLocation != null){
            if (king_in_danger(kingLocation.X, kingLocation.Y, Get_all_Group_moves(!colorTurn.equals(Color.White)))){
                kingAtDanger = hasMoved ? new Point(kingLocation) : null;
                return;
            }
        }
        kingAtDanger = null;
    }

    public Stack<Move> Get_all_Group_moves(boolean isWhiteTurn)
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

    private Stack<Move> Get_all_legal_Group_moves()
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

    public void Play_move(Move move) {
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

    public void Reverse_move() {
        Move move = recordedMoves.pop();
        if (move.deletedPiece != null){
            move.deletedPiece.isDeleted = false;
        }
        GroupOfPieces group = move.color.equals(Color.White) ? whitePieces : blackPieces;
        GroupOfPieces other_group = move.color.equals(Color.White) ? blackPieces : whitePieces;
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
                pawn.moved_two_cells = true;
            }
        }

        // update board:
        board.updateBoard(whitePieces, blackPieces);
    }

    public void DeclarePawnCrown(PieceType type) {
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
        if (pawn == null){
            return;
        }

        Stack<Piece> temp = new Stack<>();
        if (!type.equals(PieceType.None)){
            while (!group.pieces.empty() && group.pieces.peek().pieceNum > piece_num){
                temp.push(group.pieces.pop());
            }
            if (!group.pieces.empty() && pawn.pieceNum == group.pieces.peek().pieceNum){
                // set piece as eaten
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
            }
            group.pieces.peek().type = type;
        }
        setDangerCell(true);

        // update board:
        board.cellsGrid[pawn.row][pawn.column].type = type;

        while (!temp.empty()){
            group.pieces.push(temp.pop());
        }
    }

    public boolean IsTitleFit(String title){
        int i = 0;
        while (i < TitlesList.length && !title.equals(TitlesList[i])){
            i++;
        }
        switch (i){
            case 0:
                return detect_CheckmateIn_2_Moves();
            case 1:
                return detect_CheckmateIn_3_Moves();
            case 2:
                return detect_CheckmateIn_4_Moves();
            case 3:
                return detect_SuicideCheckmateIn_2_Moves();
            case 4:
                return detect_CheckmateHelperIn_2_Moves();
            case 5:
                return detect_PatIn_2_Moves();
            default:
                System.out.println("Not Official Title");
                return false;
        }
    }

    public boolean detect_CheckmateIn_2_Moves(){
        Move[] moves = new Move[8];
        return detect_CheckmateIn_X_Moves(2, Position.colorTurn, moves);
    }

    public boolean detect_CheckmateIn_3_Moves(){
        Move[] moves = new Move[8];
        return detect_CheckmateIn_X_Moves(3, Position.colorTurn, moves);
    }

    public boolean detect_CheckmateIn_4_Moves(){
        Move[] moves = new Move[8];
        return detect_CheckmateIn_X_Moves(4, Position.colorTurn, moves);
    }

    public boolean detect_SuicideCheckmateIn_2_Moves(){
        Move[] moves = new Move[8];
        return detect_SuicideCheckmateIn_X_Moves(2, Position.colorTurn, moves);
    }

    public boolean detect_CheckmateHelperIn_2_Moves(){
        Move[] moves = new Move[8];
        return detect_CheckmateHelp_In_X_Moves(2, Position.colorTurn, moves);
    }

    public boolean detect_PatIn_2_Moves(){
        Move[] moves = new Move[8];
        return detect_PatIn_X_Moves(2, Position.colorTurn, moves);
    }

    private boolean detect_CheckmateIn_X_Moves(int x, Color color, Move[] moves){
        Stack<Move> currentLegalMoves = Get_all_legal_Group_moves();
        GroupOfPieces group = colorTurn.equals(Color.White) ? whitePieces : blackPieces;
        if (x == 0){
            Point king = group.get_king_point();
            boolean king_in_danger = (king != null && king.equals(kingAtDanger));
            return currentLegalMoves.empty() && king_in_danger;
        }
        if (currentLegalMoves.empty()){
            return false;
        }

        boolean hasBeenTrue = false;
        for (Move move : currentLegalMoves){
            if (move == null){
                continue;
            }
            moves[8 - 2*x - ((!colorTurn.equals(color)) ? 1 : 0)] = move;

            setDangerCell(false);
            Play_move(move);
            setDangerCell(true);
            boolean isPass = detect_CheckmateIn_X_Moves(x - ((!colorTurn.equals(color)) ? 1 : 0), color, moves);
            setDangerCell(false);
            Reverse_move();
            setDangerCell(true);

            if ((!colorTurn.equals(color)) && !isPass){
                return false;
            }
            if (isPass){
                hasBeenTrue = true;
                if (colorTurn.equals(color)){
                    System.out.println("//////");
                    System.out.println("Win:");
                    for (Move move_solution : moves){
                        if (move_solution != null){
                            System.out.println(move_solution.type + ": (" +
                                    move_solution.startRow + ", " + move_solution.startColumn + ") to (" +
                                    move_solution.endRow + ", " + move_solution.endColumn + ")");
                        }
                    }
                    System.out.println("//////");
                }
            }
        }
        return hasBeenTrue;
    }

    private boolean detect_PatIn_X_Moves(int x, Color color, Move[] moves){
        if (x < 0){
            return false;
        }
        Stack<Move> currentLegalMoves = Get_all_legal_Group_moves();
        GroupOfPieces group = colorTurn.equals(Color.White) ? whitePieces : blackPieces;
        System.out.println("Is color white?" + colorTurn.equals(color));

        if (x == 0){
            Point king = group.get_king_point();
            boolean king_in_danger = (king != null && king.equals(kingAtDanger));
            if (currentLegalMoves.empty() && !king_in_danger){
                return true;
            }
            else{
                if (!colorTurn.equals(color)){
                    return false;
                }
            }
        }
        else if (currentLegalMoves.empty()){
            return false;
        }

        boolean hasBeenTrue = false;
        for (Move move : currentLegalMoves){
            if (move == null){
                continue;
            }
            moves[8 - 2*x - ((colorTurn.equals(color)) ? 1 : 0)] = move;

            setDangerCell(false);
            Play_move(move);
            setDangerCell(true);
            boolean isPass = detect_PatIn_X_Moves(x - ((colorTurn.equals(color)) ? 1 : 0), color, moves);
            setDangerCell(false);
            Reverse_move();
            setDangerCell(true);

            if ((!colorTurn.equals(color)) && !isPass){
                return false;
            }
            if (isPass){
                hasBeenTrue = true;
                if (colorTurn.equals(color)){
                    System.out.println("//////");
                    System.out.println("Draw:");
                    for (Move move_solution : moves){
                        if (move_solution != null){
                            System.out.println(move_solution.type + ": (" +
                                    move_solution.startRow + ", " + move_solution.startColumn + ") to (" +
                                    move_solution.endRow + ", " + move_solution.endColumn + ")");
                        }
                    }
                    System.out.println("//////");
                    return true;
                }
            }
        }
        return hasBeenTrue;
    }

    private boolean detect_SuicideCheckmateIn_X_Moves(int x, Color color, Move[] moves){
        Stack<Move> currentLegalMoves = Get_all_legal_Group_moves();
        GroupOfPieces group = colorTurn.equals(Color.White) ? whitePieces : blackPieces;

        if (x == 0){
            Point king = group.get_king_point();
            boolean king_in_danger = (king != null && king.equals(kingAtDanger));
            return currentLegalMoves.empty() && king_in_danger;
        }
        if (currentLegalMoves.empty()){
            return false;
        }

        boolean hasBeenTrue = false;
        for (Move move : currentLegalMoves){
            if (move == null){
                continue;
            }
            moves[8 - 2*x - ((!colorTurn.equals(color)) ? 1 : 0)] = move;

            setDangerCell(false);
            Play_move(move);
            setDangerCell(true);
            boolean isPass = detect_SuicideCheckmateIn_X_Moves(x - ((colorTurn.equals(color)) ? 1 : 0), color, moves);
            setDangerCell(false);
            Reverse_move();
            setDangerCell(true);

            if ((!colorTurn.equals(color)) && !isPass){
                return false;
            }
            if (isPass){
                hasBeenTrue = true;
                if (colorTurn.equals(color)){
                    System.out.println("//////");
                    System.out.println("Win:");
                    for (Move move_solution : moves){
                        if (move_solution != null){
                            System.out.println(move_solution.type + ": (" +
                                    move_solution.startRow + ", " + move_solution.startColumn + ") to (" +
                                    move_solution.endRow + ", " + move_solution.endColumn + ")");
                        }
                    }
                    System.out.println("//////");
                    return true;
                }
            }
        }
        return hasBeenTrue;
    }

    private boolean detect_CheckmateHelp_In_X_Moves(int x, Color color, Move[] moves){
        Stack<Move> currentLegalMoves = Get_all_legal_Group_moves();
        GroupOfPieces group = colorTurn.equals(Color.White) ? whitePieces : blackPieces;

        if (x == 0){
            Point king = group.get_king_point();
            boolean king_in_danger = (king != null && king.equals(kingAtDanger));
            return currentLegalMoves.empty() && king_in_danger;
        }
        if (currentLegalMoves.empty()){
            return false;
        }

        for (Move move : currentLegalMoves){
            if (move == null){
                continue;
            }
            moves[8 - 2*x - ((!colorTurn.equals(color)) ? 1 : 0)] = move;

            setDangerCell(false);
            Play_move(move);
            setDangerCell(true);
            boolean isPass = detect_CheckmateHelp_In_X_Moves(x - ((!colorTurn.equals(color)) ? 1 : 0), color, moves);
            setDangerCell(false);
            Reverse_move();
            setDangerCell(true);

            if (isPass){
                System.out.println("//////");
                System.out.println("Win:");
                for (Move move_solution : moves){
                    if (move_solution != null){
                        System.out.println(move_solution.type + ": (" +
                                move_solution.startRow + ", " + move_solution.startColumn + ") to (" +
                                move_solution.endRow + ", " + move_solution.endColumn + ")");
                    }
                }
                System.out.println("//////");
                return true;
            }
        }
        return false;
    }
}
