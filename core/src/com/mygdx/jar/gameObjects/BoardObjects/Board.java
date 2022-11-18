package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Color;
import com.mygdx.jar.gameObjects.GamePieces.Piece;
import com.mygdx.jar.gameObjects.GamePieces.PieceType;

public class Board {
    public static final int BoardSize = 8;
    public static final String NonTitle = "התאם כותרת לעמדה";
    public static final String TitleNotFit = "הכותרת לא מתאימה לעמדה";
    public static final String TitleCheck = "בודק התאמת כותרת לעמדה";
    public static final String TitleFit = "הכותרת מתאימה לעמדה";
    public static final String Nothing = "?";

    // 2D array of type Cell
    public Cell[][] cellsGrid;
    public Color startingColor;
    public String title;

    // constructor
    public Board(GroupOfPieces group, GroupOfPieces otherGroup, Color startingColor)
    {
        this.startingColor = startingColor;
        this.title = NonTitle;

        // create a new 2D array of  type Cell
        this.cellsGrid = new Cell[BoardSize][BoardSize];

        initBoard(group, otherGroup);
    }

    public Board(Board originalBoard)
    {
        this.startingColor = originalBoard.startingColor;
        this.title = originalBoard.title;
        this.cellsGrid = new Cell[BoardSize][BoardSize];

        // fill the 2D array with new Cells
        for (int i = 0; i < BoardSize; i++)
        {
            for (int j = 0; j < BoardSize; j++)
            {
                cellsGrid[i][j] = new Cell(originalBoard.cellsGrid[i][j]);
            }
        }
    }

    // called each time a move is played
    public void updateBoard(GroupOfPieces group, GroupOfPieces otherGroup)
    {
        for (int i = 0; i < BoardSize; i++)
        {
            for (int j = 0; j < BoardSize; j++)
            {
                Piece currentPieceGroup1 = group.Piece_location(i, j);
                Piece currentPieceGroup2 = otherGroup.Piece_location(i, j);
                if (currentPieceGroup1 != null && !currentPieceGroup1.isDeleted)
                {
                    cellsGrid[i][j].color = currentPieceGroup1.color;
                    cellsGrid[i][j].type = currentPieceGroup1.type;
                    cellsGrid[i][j].isTherePiece = true;
                }
                else if (currentPieceGroup2 != null && !currentPieceGroup2.isDeleted)
                {
                    cellsGrid[i][j].color = currentPieceGroup2.color;
                    cellsGrid[i][j].type = currentPieceGroup2.type;
                    cellsGrid[i][j].isTherePiece = true;
                }
                else
                {
                    cellsGrid[i][j].color = Color.None;
                    cellsGrid[i][j].type = PieceType.None;
                    cellsGrid[i][j].isTherePiece = false;
                }
            }
        }
    }

    // called when the game starts
    public void initBoard(GroupOfPieces group, GroupOfPieces other_group)
    {
        // fill the 2D array with new Cells
        for (int i = 0; i < BoardSize; i++)
        {
            for (int j = 0; j < BoardSize; j++)
            {
                Piece currentPieceGroup1 = (group != null ? group.Piece_location(i, j) : null);
                Piece currentPieceGroup2 = (other_group != null ? other_group.Piece_location(i, j) : null);
                if (currentPieceGroup1 != null && !currentPieceGroup1.isDeleted)
                {
                    cellsGrid[i][j] = new Cell(i, j, true, currentPieceGroup1.color, currentPieceGroup1.type);
                }
                else if (currentPieceGroup2 != null && !currentPieceGroup2.isDeleted)
                {
                    cellsGrid[i][j] = new Cell(i, j, true, currentPieceGroup2.color, currentPieceGroup2.type);
                }
                else
                {
                    cellsGrid[i][j] = new Cell(i, j, false, Color.None, PieceType.None);
                }
            }
        }
    }

    public boolean hasKing(Color color) {
        for (int i = 0; i < BoardSize; i++){
            for (int j = 0; j < BoardSize; j++){
                if (cellsGrid[i][j].isTherePiece &&
                        cellsGrid[i][j].color.equals(color) &&
                        cellsGrid[i][j].type.equals(PieceType.King)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board)) return false;
        Board board = (Board) o;
        for (int i = 0; i < BoardSize; i++){
            for (int j = 0; j < BoardSize; j++){
                if (!cellsGrid[i][j].equals(board.cellsGrid[i][j])){
                    return false;
                }
            }
        }
        return startingColor == board.startingColor;
    }
}
