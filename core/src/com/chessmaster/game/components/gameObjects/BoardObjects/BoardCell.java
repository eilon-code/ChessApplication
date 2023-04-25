package com.chessmaster.game.components.gameObjects.BoardObjects;

import com.chessmaster.game.components.gameObjects.GamePieces.Color;
import com.chessmaster.game.components.gameObjects.GamePieces.PieceType;

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
