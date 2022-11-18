package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Color;
import com.mygdx.jar.gameObjects.GamePieces.Piece;
import com.mygdx.jar.gameObjects.GamePieces.PieceType;

import java.util.Objects;

public class Move {
    public int startRow;
    public int startColumn;
    public int endRow;
    public int endColumn;
    public Color color;
    public PieceType type;
    public boolean castle;
    public boolean crowningPawn;
    public PieceType crowningPawnType;
    public Piece deletedPiece;

    public Move(int startRow, int startColumn, int endRow, int endColumn, Color color, PieceType type)
    {
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.endRow = endRow;
        this.endColumn = endColumn;
        this.color = color;
        this.type = type;

        this.castle = false;
        this.crowningPawn = false;
        this.crowningPawnType = PieceType.None;
        this.deletedPiece = null;
    }

    public Move(int startRow, int startColumn, int endRow, int endColumn, Color color, PieceType type, PieceType crowningPawnType)
    {
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.endRow = endRow;
        this.endColumn = endColumn;
        this.color = color;
        this.type = type;

        this.castle = false;
        this.crowningPawn = true;
        this.crowningPawnType = crowningPawnType;
        this.deletedPiece = null;
    }

    public void setCastle(){
        this.castle = true;
    }

    public void setDeletedPiece(Piece deletedPiece){
        this.deletedPiece = deletedPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        if (deletedPiece == null){
            if (move.deletedPiece != null){
                return false;
            }
        }
        else{
            if (!deletedPiece.equals(move.deletedPiece)){
                return false;
            }
        }
        return startRow == move.startRow && startColumn == move.startColumn && endRow == move.endRow && endColumn == move.endColumn && castle == move.castle && crowningPawn == move.crowningPawn && color == move.color && type == move.type && crowningPawnType == move.crowningPawnType;
    }
}
