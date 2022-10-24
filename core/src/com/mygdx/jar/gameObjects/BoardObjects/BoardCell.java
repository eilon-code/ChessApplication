package com.mygdx.jar.gameObjects.BoardObjects;

public class BoardCell {
    public String PieceType;
    public String BackgroundColor;
    public String PieceColor;
    public boolean ActivePiece;

    public BoardCell(){
        PieceType = null;
        PieceColor = null;
        BackgroundColor = null;
        ActivePiece = false;
    }
}
