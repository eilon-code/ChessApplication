package com.chessmaster.game.components.gameObjects.GamePieces;

import com.chessmaster.game.components.gameObjects.BoardObjects.Board;
import com.chessmaster.game.components.gameObjects.BoardObjects.GroupOfPieces;
import com.chessmaster.game.components.gameObjects.BoardObjects.Move;

import java.util.Stack;

public abstract class Piece {
    public static PieceType[] CrownTypes =
            new PieceType[] {PieceType.Queen, PieceType.Bishop, PieceType.Knight, PieceType.Rook};
    public int row;
    public int column;

    public PieceType type;
    public Color color;
    public boolean isDeleted;
    public int pieceNum;
    public int numberOfMoves;

    abstract public int getMaxNumberOfMovesOnBoard();

    abstract public void fillPieceMoves(Stack<Move> groupMoves, boolean is_white_turn,
                                        boolean Is_white_up, Board board, GroupOfPieces otherGroup, GroupOfPieces group);

    public boolean HasBeenMoved(){
        return this.numberOfMoves > 0;
    }

    public Piece(PieceType type, Color color, int x, int y, int pieceNum, boolean isDeleted, int numberOfMoves)
    {
        this.type = type;
        this.color = color;
        this.row = x;
        this.column = y;
        this.pieceNum = pieceNum;
        this.isDeleted = isDeleted;
        this.numberOfMoves = numberOfMoves;
    }

    public Piece(Piece originalPiece)
    {
        this.type = originalPiece.type;
        this.color = originalPiece.color;
        this.row = originalPiece.row;
        this.column = originalPiece.column;
        this.pieceNum = originalPiece.pieceNum;
        this.isDeleted = originalPiece.isDeleted;
        this.numberOfMoves = originalPiece.numberOfMoves;
    }
}
