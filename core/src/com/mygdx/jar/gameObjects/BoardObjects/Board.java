package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Piece;

public class Board {
    // the size of the board is  8*8 (chess board)
    public static int BoardSize;
    public static final String NonTitle = "התאם כותרת לעמדה";
    public static final String TitleNotFit = "הכותרת לא מתאימה לעמדה";

    // 2D array of type Cell
    public Cell[][] The_Grid;
    public boolean IsWhiteTurn;
    public String Title;

    // constructor
    public Board (int boardSize, Group_of_pieces group, Group_of_pieces other_group, boolean isWhiteTurn)
    {
        BoardSize = boardSize;
        IsWhiteTurn = isWhiteTurn;
        Title = NonTitle;

        // create a new 2D array of  type Cell
        The_Grid = new Cell[BoardSize][BoardSize];

        Get_Started(group, other_group);
    }

    public Board(Board original_board)
    {
        IsWhiteTurn = original_board.IsWhiteTurn;
        Title = original_board.Title;
        The_Grid = new Cell[BoardSize][BoardSize];

        // fill the 2D array with new Cells
        for (int i = 0; i < BoardSize; i++)
        {
            for (int j = 0; j < BoardSize; j++)
            {
                The_Grid[i][j] = new Cell(original_board.The_Grid[i][j]);
            }
        }
    }

    // called each time a move is played
    public void Update_Board(Group_of_pieces group, Group_of_pieces other_group)
    {
        for (int i = 0; i < BoardSize; i++)
        {
            for (int j = 0; j < BoardSize; j++)
            {
                Piece currentPieceGroup1 = group.Piece_location(i, j);
                Piece currentPieceGroup2 = other_group.Piece_location(i, j);
                if (currentPieceGroup1 != null && !currentPieceGroup1.IsDeleted)
                {
                    The_Grid[i][j].Color_piece = currentPieceGroup1.Color;
                    The_Grid[i][j].Type = currentPieceGroup1.Type;
                    The_Grid[i][j].Is_there_Piece = true;
                }
                else if (currentPieceGroup2 != null && !currentPieceGroup2.IsDeleted)
                {
                    The_Grid[i][j].Color_piece = currentPieceGroup2.Color;
                    The_Grid[i][j].Type = currentPieceGroup2.Type;
                    The_Grid[i][j].Is_there_Piece = true;
                }
                else
                {
                    The_Grid[i][j].Color_piece = "";
                    The_Grid[i][j].Type = "";
                    The_Grid[i][j].Is_there_Piece = false;
                }
            }
        }
    }

    // called when the game starts
    public void Get_Started(Group_of_pieces group, Group_of_pieces other_group)
    {
        // fill the 2D array with new Cells
        for (int i = 0; i < BoardSize; i++)
        {
            for (int j = 0; j < BoardSize; j++)
            {
                Piece currentPieceGroup1 = group.Piece_location(i, j);
                Piece currentPieceGroup2 = other_group.Piece_location(i, j);
                if (currentPieceGroup1 != null && !currentPieceGroup1.IsDeleted)
                {
                    The_Grid[i][j] = new Cell(i, j, true, currentPieceGroup1.Color, currentPieceGroup1.Type);
                }
                else if (currentPieceGroup2 != null && !currentPieceGroup2.IsDeleted)
                {
                    The_Grid[i][j] = new Cell(i, j, true, currentPieceGroup2.Color, currentPieceGroup2.Type);
                }
                else
                {
                    The_Grid[i][j] = new Cell(i, j, false, "", "");
                }
            }
        }
    }
}
