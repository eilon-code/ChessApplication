package com.mygdx.jar.gameObjects.BoardObjects;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return Row_Number == cell.Row_Number && Column_Number == cell.Column_Number && Is_there_Piece == cell.Is_there_Piece && Color_piece.equals(cell.Color_piece) && Type.equals(cell.Type);
    }
}
