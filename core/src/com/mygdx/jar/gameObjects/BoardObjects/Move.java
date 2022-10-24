package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Piece;

public class Move {
    public int Current_row;
    public int Current_column;
    public int Next_row;
    public int Next_column;
    public boolean Is_white;
    public String Type_of_piece;
    public boolean Castle;
    public boolean Crowning_pawn;
    public String Crowning_pawn_type;
    public Piece Deleted_piece;

    public Move(int x1, int y1, int x2, int y2, boolean is_white_piece, String type)
    {
        Current_row = x1;
        Current_column = y1;
        Next_row = x2;
        Next_column = y2;
        Is_white = is_white_piece;
        Type_of_piece = type;
        Castle = false;
        Crowning_pawn = false;
        Crowning_pawn_type = null;
        Deleted_piece = null;
    }

    public Move(int x1, int y1, int x2, int y2, boolean is_white_piece, String type, String crownType)
    {
        Current_row = x1;
        Current_column = y1;
        Next_row = x2;
        Next_column = y2;
        Is_white = is_white_piece;
        Type_of_piece = type;
        Castle = false;
        Deleted_piece = null;
        Crowning_pawn = true;
        Crowning_pawn_type = crownType;
    }

    public void setCastle(){
        Castle = true;
    }

    public void setDeleted_piece(Piece deleted_piece){
        Deleted_piece = deleted_piece;
    }
}
