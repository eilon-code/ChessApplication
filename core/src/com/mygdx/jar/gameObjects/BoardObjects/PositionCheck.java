package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Bishop;
import com.mygdx.jar.gameObjects.GamePieces.Knight;
import com.mygdx.jar.gameObjects.GamePieces.Pawn;
import com.mygdx.jar.gameObjects.GamePieces.Piece;
import com.mygdx.jar.gameObjects.GamePieces.Queen;
import com.mygdx.jar.gameObjects.GamePieces.Rook;

import java.util.Stack;

public class PositionCheck {
    public static final String[] TitlesList = new String[] {"מט בשני מסעים", "מט בשלושה מסעים", "מט בארבעה מסעים", "מט לדעת בשני מסעים", "מט עזר בשני מסעים", "כפיית פט בשני מסעים"};

    public final Board Chess_Board;
    public final Group_of_pieces White_Pieces;
    public final Group_of_pieces Black_Pieces;
    public boolean Is_white_turn;
    public boolean Is_white_up;
    public Point KingAtDanger;
    private final Stack<Move> RecordedMoves;

    public PositionCheck(Board chess_board) {
        Chess_Board = chess_board; // new Board(chess_board); // here the magic happens
        White_Pieces = new Group_of_pieces(chess_board, "white");
        Black_Pieces = new Group_of_pieces(chess_board, "black");
        Is_white_turn = chess_board.IsWhiteTurn;
        Is_white_up = false;
        RecordedMoves = new Stack<>();
        setDangerCell(true);
    }

    public boolean king_in_danger(int king_x, int king_y, Move[] moves_options)
    {
        for (Move moves_option : moves_options) {
            if (moves_option != null) {
                if (moves_option.Next_row == king_x && moves_option.Next_column == king_y) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setDangerCell(boolean hasMoved){
        Point kingLocation = Is_white_turn ? White_Pieces.get_king_point() : Black_Pieces.get_king_point();
        if (kingLocation != null){
            if (king_in_danger(kingLocation.X, kingLocation.Y, Get_all_Group_moves(!Is_white_turn))){
                KingAtDanger = hasMoved ? new Point(kingLocation) : null;
                return;
            }
        }
        KingAtDanger = null;
    }

    public Move[] Get_all_Group_moves(boolean is_white_turn)
    {
        Group_of_pieces group = is_white_turn ? White_Pieces : Black_Pieces;
        Group_of_pieces other_group = is_white_turn ? Black_Pieces : White_Pieces;
        int group_size = group.Group_Pieces.length;
        int max_number_of_group_moves = 0;
        for (int i = 0; i < group_size; i++)
        {
            if (group.Group_Pieces[i] != null && !group.Group_Pieces[i].IsDeleted)
            {
                max_number_of_group_moves += group.Group_Pieces[i].Get_Max_Number_Of_Moves_On_Board();
            }
        }

        Move[] Group_moves = new Move[max_number_of_group_moves];

        int current_index = 0;
        for (int i = 0; i < group_size; i++)
        {
            if (group.Group_Pieces[i] != null && !group.Group_Pieces[i].IsDeleted)
            {
                current_index += group.Group_Pieces[i].Fill_All_In_Board_Moves(Group_moves, current_index,
                        is_white_turn, Is_white_up, Chess_Board, other_group, group);
            }
        }
        return Group_moves;
    }

    private Move[] Get_all_legal_Group_moves()
    {
        Group_of_pieces group = Is_white_turn ? White_Pieces : Black_Pieces;
        Move[] legal_moves = Get_all_Group_moves(Is_white_turn);

        Point king_point;

        int move_index = 0;
        for (Move optional_move : legal_moves)
        {
            if (optional_move != null &&
                    group.Piece_location(optional_move.Current_row, optional_move.Current_column) != null &&
                    !group.Piece_location(optional_move.Current_row, optional_move.Current_column).IsDeleted)
            {
                if (optional_move.Type_of_piece.equals("King") && Chess_Board.The_Grid[optional_move.Next_row][optional_move.Next_column].Type.equals("Rook") &&
                        optional_move.Is_white == Chess_Board.The_Grid[optional_move.Next_row][optional_move.Next_column].Color_piece.equals("white")){
                    int wantedRow;
                    if (optional_move.Current_row < optional_move.Next_row)
                    {
                        wantedRow = 6;
                    }
                    else
                    {
                        wantedRow = 2;
                    }
                    int startRowMovement = Math.min(optional_move.Current_row, wantedRow);
                    int endRowMovement = Math.max(optional_move.Current_row, wantedRow);
                    Move[] optional_opponent_moves = Get_all_Group_moves(!Is_white_turn);
                    for (int row = startRowMovement; row <= endRowMovement; row++)
                    {
                        if (king_in_danger(row, optional_move.Current_column, optional_opponent_moves))
                        {
                            legal_moves[move_index] = null;
                            break;
                        }
                    }
                }
                Play_move(optional_move);

                Move[] Next_options_of_optional_moves = Get_all_Group_moves(Is_white_turn);

                king_point = group.get_king_point();

                if (king_point == null || king_in_danger(king_point.X, king_point.Y, Next_options_of_optional_moves))
                {
                    legal_moves[move_index] = null;
                }
                Reverse_move();
            }
            move_index++;
        }
        return legal_moves;
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
        return detect_CheckmateIn_X_Moves(2, Is_white_turn, moves);
    }

    public boolean detect_CheckmateIn_3_Moves(){
        Move[] moves = new Move[8];
        return detect_CheckmateIn_X_Moves(3, Is_white_turn, moves);
    }

    public boolean detect_CheckmateIn_4_Moves(){
        Move[] moves = new Move[8];
        return detect_CheckmateIn_X_Moves(4, Is_white_turn, moves);
    }

    public boolean detect_SuicideCheckmateIn_2_Moves(){
        Move[] moves = new Move[6];
        return detect_SuicideCheckmateIn_X_Moves(2, Is_white_turn, moves);
    }

    public boolean detect_CheckmateHelperIn_2_Moves(){
        Move[] moves = new Move[6];
        return detect_CheckmateHelp_In_X_Moves(2, Is_white_turn, moves);
    }

    public boolean detect_PatIn_2_Moves(){
        Move[] moves = new Move[6];
        return detect_PatIn_X_Moves(2, Is_white_turn, moves);
    }

    private boolean detect_CheckmateIn_X_Moves(int x, boolean is_white_turn, Move[] moves){
        Move[] current_legal_moves = Get_all_legal_Group_moves();
        Group_of_pieces group = Is_white_turn ? White_Pieces : Black_Pieces;
        int legal_moves = 0;
        for (Move current_legal_move : current_legal_moves) {
            if (current_legal_move != null) {
                legal_moves++;
            }
        }

        if (x == 0){
            Point king = group.get_king_point();
            boolean king_in_danger = (king != null && king.equals(KingAtDanger));
            return legal_moves == 0 && king_in_danger;
        }
        if (legal_moves == 0){
            return false;
        }

        boolean hasBeenTrue = false;
        for (Move move : current_legal_moves){
            if (move == null){
                continue;
            }
            moves[8 - 2*x - ((is_white_turn != Is_white_turn) ? 1 : 0)] = move;

            Play_move(move);
            boolean isPass = detect_CheckmateIn_X_Moves(x - ((is_white_turn != Is_white_turn) ? 1 : 0), is_white_turn, moves);
            Reverse_move();

            if ((is_white_turn != Is_white_turn) && !isPass){
                return false;
            }
            if (isPass){
                hasBeenTrue = true;
                if (is_white_turn == Is_white_turn){
                    System.out.println("//////");
                    System.out.println("Win:");
                    for (Move move_solution : moves){
                        if (move_solution != null){
                            System.out.println(move_solution.Type_of_piece + ": (" +
                                    move_solution.Current_row + ", " + move_solution.Current_column + ") to (" +
                                    move_solution.Next_row + ", " + move_solution.Next_column + ")");
                        }
                    }
                    System.out.println("//////");
                    return true;
                }
            }
        }
        return hasBeenTrue;
    }

    private boolean detect_PatIn_X_Moves(int x, boolean is_white_turn, Move[] moves){
        Move[] current_legal_moves = Get_all_legal_Group_moves();
        Group_of_pieces group = Is_white_turn ? White_Pieces : Black_Pieces;
        int legal_moves = 0;
        for (Move current_legal_move : current_legal_moves) {
            if (current_legal_move != null) {
                legal_moves++;
            }
        }

        if (x == 0){
            Point king = group.get_king_point();
            boolean king_in_danger = (king != null && king.equals(KingAtDanger));
            return legal_moves == 0 && !king_in_danger;
        }
        if (legal_moves == 0){
            return false;
        }

        boolean hasBeenTrue = false;
        for (Move move : current_legal_moves){
            if (move == null){
                continue;
            }
            moves[6 - 2*x - ((is_white_turn != Is_white_turn) ? 1 : 0)] = move;

            Play_move(move);
            boolean isPass = detect_PatIn_X_Moves(x - ((is_white_turn != Is_white_turn) ? 1 : 0), is_white_turn, moves);
            Reverse_move();

            if ((is_white_turn != Is_white_turn) && !isPass){
                return false;
            }
            if (isPass){
                hasBeenTrue = true;
                if (is_white_turn == Is_white_turn){
                    System.out.println("//////");
                    System.out.println("Pat:");
                    for (Move move_solution : moves){
                        if (move_solution != null){
                            System.out.println(move_solution.Type_of_piece + ": (" +
                                    move_solution.Current_row + ", " + move_solution.Current_column + ") to (" +
                                    move_solution.Next_row + ", " + move_solution.Next_column + ")");
                        }
                    }
                    System.out.println("//////");
                    return true;
                }
            }
        }
        return hasBeenTrue;
    }

    private boolean detect_SuicideCheckmateIn_X_Moves(int x, boolean is_white_turn, Move[] moves){
        Move[] current_legal_moves = Get_all_legal_Group_moves();
        Group_of_pieces group = Is_white_turn ? White_Pieces : Black_Pieces;
        Group_of_pieces other_group = Is_white_turn ? Black_Pieces : White_Pieces;
        int legal_moves = 0;
        for (Move current_legal_move : current_legal_moves) {
            if (current_legal_move != null) {
                legal_moves++;
            }
        }

        if (x == 0 && is_white_turn != Is_white_turn){
            Point king = group.get_king_point();
            boolean king_in_danger = (king != null && king.equals(KingAtDanger));
            if (legal_moves == 0 && king_in_danger){
                System.out.println("//////");
                System.out.println("Win:");
                for (Move move : moves){
                    if (move != null){
                        System.out.println(move.Type_of_piece + ": (" + move.Current_row + ", " + move.Current_column + ") to (" + move.Next_row + ", " + move.Next_column + ")");
                    }
                }
                System.out.println("//////");
            }
            return legal_moves == 0 && king_in_danger;
        }

        boolean hasBeenTrue = false;
        for (Move move : current_legal_moves){
            if (move == null){
                continue;
            }
            moves[5 - 2*x - ((is_white_turn != Is_white_turn) ? 1 : 0)] = move;

            Play_move(move);
            boolean isPass = detect_SuicideCheckmateIn_X_Moves(x - ((is_white_turn == Is_white_turn) ? 1 : 0), is_white_turn, moves);
            Reverse_move();

            if ((is_white_turn != Is_white_turn) && !isPass){
                return false;
            }
            if (isPass){
                hasBeenTrue = true;
            }
        }
        return hasBeenTrue;
    }

    private boolean detect_CheckmateHelp_In_X_Moves(int x, boolean is_white_turn, Move[] moves){
        Move[] current_legal_moves = Get_all_legal_Group_moves();
        Group_of_pieces group = Is_white_turn ? White_Pieces : Black_Pieces;
        Group_of_pieces other_group = Is_white_turn ? Black_Pieces : White_Pieces;
        int legal_moves = 0;
        for (Move current_legal_move : current_legal_moves) {
            if (current_legal_move != null) {
                legal_moves++;
            }
        }

        if (x == 0){
            Point king = group.get_king_point();
            boolean king_in_danger = (king != null && king.equals(KingAtDanger));
            if (legal_moves == 0 && king_in_danger){
                System.out.println("//////");
                System.out.println("Win:");
                for (Move move : moves){
                    if (move != null){
                        System.out.println(move.Type_of_piece + ": (" + move.Current_row + ", " + move.Current_column + ") to (" + move.Next_row + ", " + move.Next_column + ")");
                    }
                }
                System.out.println("//////");
            }
            return legal_moves == 0 && king_in_danger;
        }

        boolean hasBeenTrue = false;
        for (Move move : current_legal_moves){
            if (move == null){
                continue;
            }
            moves[4 - 2*x - ((is_white_turn != Is_white_turn) ? 1 : 0)] = move;

            Play_move(move);
            boolean isPass = detect_CheckmateHelp_In_X_Moves(x - ((is_white_turn != Is_white_turn) ? 1 : 0), is_white_turn, moves);
            Reverse_move();

            if (isPass){
                hasBeenTrue = true;
            }
        }
        return hasBeenTrue;
    }

    public void Play_move(Move move) {
        setDangerCell(false);

        // update groups of pieces:
        Group_of_pieces group = Is_white_turn ? White_Pieces : Black_Pieces;
        Group_of_pieces other_group = Is_white_turn ? Black_Pieces : White_Pieces;
        Piece deleted_piece = other_group.Delete_piece(move.Next_row, move.Next_column, move.Current_row, move.Current_column, group);
        group.Update_move(move);

        move.setDeleted_piece(deleted_piece);
        RecordedMoves.push(move);
        if (move.Crowning_pawn){
            DeclarePawnCrown(move.Crowning_pawn_type);
        }
        setDangerCell(true);

        // update board:
        Chess_Board.Update_Board(White_Pieces, Black_Pieces);
        Is_white_turn = !Is_white_turn;
    }

    public void Reverse_move() {
        Move move = RecordedMoves.pop();
        if (move.Deleted_piece != null){
            move.Deleted_piece.IsDeleted = false;
        }
        setDangerCell(false);

        Group_of_pieces group = move.Is_white ? White_Pieces : Black_Pieces;
        Group_of_pieces other_group = move.Is_white ? Black_Pieces : White_Pieces;
        if (move.Castle){
            Point king_point = group.get_king_point();
            Piece king = group.Piece_location(king_point.X, king_point.Y);
            Piece rook = group.Piece_location(king_point.X + (move.Next_row > move.Current_row ? -1 : 1), king_point.Y);
            king.Row_Number = move.Current_row;
            king.Column_Number = move.Current_column;
            king.Number_of_moves--;

            rook.Row_Number = move.Next_row;
            rook.Column_Number = move.Next_column;
            rook.Number_of_moves--;
        }
        else {
            Piece piece = group.Piece_location(move.Next_row, move.Next_column);
            if (piece != null){
                piece.Row_Number = move.Current_row;
                piece.Column_Number = move.Current_column;
                piece.Number_of_moves--;
                if (move.Crowning_pawn){
                    group.Group_Pieces[piece.Piece_num] = new Pawn(piece);
                    group.Group_Pieces[piece.Piece_num].Type = "Pawn";
                }
            }
        }
        Is_white_turn = !Is_white_turn;

        if (!RecordedMoves.empty()){
            Move previous_move = RecordedMoves.peek();
            if (previous_move.Type_of_piece.equals("Pawn") && Math.abs(previous_move.Current_column - previous_move.Next_column) > 1){
                Pawn pawn = (Pawn) other_group.Piece_location(previous_move.Next_row, previous_move.Next_column);
                pawn.moved_two_cells = true;
            }
        }
        setDangerCell(true);

        // update board:
        Chess_Board.Update_Board(White_Pieces, Black_Pieces);
    }

    public void DeclarePawnCrown(String type) {
        if (RecordedMoves.empty()){
            return;
        }
        Move crowningMove = RecordedMoves.peek();
        if (!crowningMove.Type_of_piece.equals("Pawn")){
            return;
        }
        setDangerCell(false);
        Group_of_pieces group;
        if (crowningMove.Is_white) {
            group = White_Pieces;
        }
        else {
            group = Black_Pieces;
        }
        Piece pawn = group.Piece_location(crowningMove.Next_row, crowningMove.Next_column);
        int piece_num = (pawn != null ? pawn.Piece_num : -1);
        switch (type) {
            case "Rook":
                group.Group_Pieces[piece_num] = new Rook(pawn);
                break;
            case "Bishop":
                group.Group_Pieces[piece_num] = new Bishop(pawn);
                break;
            case "Knight":
                group.Group_Pieces[piece_num] = new Knight(pawn);
                break;
            case "Queen":
                group.Group_Pieces[piece_num] = new Queen(pawn);
                break;
            default:
                return;
        }
        group.Group_Pieces[piece_num].Type = type;
        setDangerCell(true);

        // update board:
        Chess_Board.The_Grid[pawn.Row_Number][pawn.Column_Number].Type = type;
    }
}
