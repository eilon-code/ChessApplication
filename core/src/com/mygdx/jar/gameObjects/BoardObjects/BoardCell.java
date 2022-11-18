package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Color;
import com.mygdx.jar.gameObjects.GamePieces.PieceType;

public class BoardCell {
    public PieceType type;
    public String BackgroundColor;
    public Color pieceColor;
    public boolean ActivePiece;

    public BoardCell(){
        type = PieceType.None;
        pieceColor = Color.None;
        BackgroundColor = null;
        ActivePiece = false;
    }
}
