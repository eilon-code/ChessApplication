package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.Group_of_pieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;

public abstract class Piece {
    protected static String[] CrownTypes = new String[] {"Knight", "Bishop", "Rook", "Queen"};
    // piece might be Pawn, Knight, Bishop,  Rook, Queen, or King
    public String Type;
    public String Color;
    public int Row_Number;
    public int Column_Number;
    public boolean IsDeleted;
    public int Piece_num;
    public int Number_of_moves;

    abstract public int Get_Max_Number_Of_Moves_On_Board();

    abstract public int Fill_All_In_Board_Moves(Move[] Group_moves, int current_index, boolean is_white_turn,
                                                boolean Is_white_up, Board chess_board, Group_of_pieces other_group, Group_of_pieces group);

    public boolean HasBeenMoved(){
        return Number_of_moves > 0;
    }

    // function new Regular_Piece
    public Piece(String type, String color, int x, int y, int piece_num, boolean is_deleted, int number_of_moves)
    {
        Type = type;
        Color = color;
        Row_Number = x;
        Column_Number = y;
        Piece_num = piece_num;
        IsDeleted = is_deleted;
        Number_of_moves = number_of_moves;
    }

    public Piece(Piece original_piece)
    {
        Type = original_piece.Type;
        Color = original_piece.Color;
        Row_Number = original_piece.Row_Number;
        Column_Number = original_piece.Column_Number;
        Piece_num = original_piece.Piece_num;
        IsDeleted = original_piece.IsDeleted;
        Number_of_moves = original_piece.Number_of_moves;
    }
}
