package com.mygdx.jar.gameObjects.BoardObjects;

public class Cell {
    public int Row_Number;
    public int Column_Number;
    public boolean Is_there_Piece;
    public String Color_piece;
    public String Type;

    public Cell(int x, int y, boolean is_there_piece, String color, String piece_type)
    {
        Row_Number = x;
        Column_Number = y;
        Is_there_Piece = is_there_piece;
        Color_piece = color;
        Type = piece_type;
    }

    public Cell(Cell original_cell)
    {
        Row_Number = original_cell.Row_Number;
        Column_Number = original_cell.Column_Number;
        Is_there_Piece = original_cell.Is_there_Piece;
        Color_piece = original_cell.Color_piece;
        Type = original_cell.Type;
    }
}
