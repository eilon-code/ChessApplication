package com.chessmaster.game.components.gameObjects.BoardObjects;

import com.chessmaster.game.components.gameObjects.GamePieces.Color;
import com.chessmaster.game.components.gameObjects.GamePieces.PieceType;

public class Cell {
    public final int row;
    public final int column;
    public boolean isTherePiece;
    public Color color;
    public PieceType type;

    public Cell(int row, int column, boolean isTherePiece, Color color, PieceType pieceType)
    {
        this.row = row;
        this.column = column;
        this.isTherePiece = isTherePiece;
        this.color = color;
        this.type = pieceType;
    }

    public Cell(Cell original_cell)
    {
        this.row = original_cell.row;
        this.column = original_cell.column;
        this.isTherePiece = original_cell.isTherePiece;
        this.color = original_cell.color;
        this.type = original_cell.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return row == cell.row && column == cell.column && isTherePiece == cell.isTherePiece && color.equals(cell.color) && type.equals(cell.type);
    }
}
