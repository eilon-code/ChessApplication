package com.chessmaster.game.components.gameObjects.GamePieces;

public enum PieceType {
    None,
    Pawn,
    Knight,
    Bishop,
    Rook,
    Queen,
    King;

    public static PieceType getPieceType(String name){
        switch (name){
            case "pawn":
                return Pawn;

            case "knight":
                return Knight;

            case "bishop":
                return Bishop;

            case "rook":
                return Rook;

            case "queen":
                return Queen;

            case "king":
                return King;

            default:
                return None;
        }
    }
}

