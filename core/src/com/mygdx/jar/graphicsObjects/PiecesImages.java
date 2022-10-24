package com.mygdx.jar.graphicsObjects;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.jar.gameObjects.BoardObjects.Point;

public class PiecesImages {
    public Texture WhiteKing;
    public Texture WhiteQueen;
    public Texture WhiteRook;
    public Texture WhiteBishop;
    public Texture WhiteKnight;
    public Texture WhitePawn;

    public Texture BlackKing;
    public Texture BlackQueen;
    public Texture BlackRook;
    public Texture BlackBishop;
    public Texture BlackKnight;
    public Texture BlackPawn;

    public Point KingSize;
    public Point QueenSize;
    public Point RookSize;
    public Point BishopSize;
    public Point KnightSize;
    public Point PawnSize;

    public PiecesImages(int CellSize)
    {
        WhiteKing = new Texture("core/images/PiecesImages1/WhiteKing.png");
        WhiteQueen = new Texture("core/images/PiecesImages1/WhiteQueen.png");
        WhiteRook = new Texture("core/images/PiecesImages1/WhiteRook.png");
        WhiteBishop = new Texture("core/images/PiecesImages1/WhiteBishop.png");
        WhiteKnight = new Texture("core/images/PiecesImages1/WhiteKnight.png");
        WhitePawn = new Texture("core/images/PiecesImages1/WhitePawn.png");

        BlackKing = new Texture("core/images/PiecesImages1/BlackKing.png");
        BlackQueen = new Texture("core/images/PiecesImages1/BlackQueen.png");
        BlackRook = new Texture("core/images/PiecesImages1/BlackRook.png");
        BlackBishop = new Texture("core/images/PiecesImages1/BlackBishop.png");
        BlackKnight = new Texture("core/images/PiecesImages1/BlackKnight.png");
        BlackPawn = new Texture("core/images/PiecesImages1/BlackPawn.png");

        KingSize = new Point();
        QueenSize = new Point();
        RookSize = new Point();
        BishopSize = new Point();
        KnightSize = new Point();
        PawnSize = new Point();

        KingSize.Y = (int) (CellSize * 3 / 4);
        KingSize.X =  (int) (KingSize.Y * 150 / 162);

        QueenSize.Y = (int) (CellSize * 3 / 4);
        QueenSize.X = (int) (QueenSize.Y * 145 / 135);

        RookSize.Y = (int) (CellSize * 3 / 4);
        RookSize.X = (int) (RookSize.Y * 1626 / 1884);

        BishopSize.Y = (int) (CellSize * 3 / 4);
        BishopSize.X = (int) (BishopSize.Y * 618 / 640);

        KnightSize.Y = (int) (CellSize * 3 / 4);
        KnightSize.X = (int) (KnightSize.Y * 138 / 138);

        PawnSize.Y = (int) (CellSize * 3 / 4);
        PawnSize.X = (int) (PawnSize.Y * 462 / 595);
    }
}
