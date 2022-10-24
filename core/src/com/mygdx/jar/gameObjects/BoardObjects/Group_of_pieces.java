package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Bishop;
import com.mygdx.jar.gameObjects.GamePieces.King;
import com.mygdx.jar.gameObjects.GamePieces.Knight;
import com.mygdx.jar.gameObjects.GamePieces.Piece;
import com.mygdx.jar.gameObjects.GamePieces.Pawn;
import com.mygdx.jar.gameObjects.GamePieces.Queen;
import com.mygdx.jar.gameObjects.GamePieces.Rook;

public class Group_of_pieces {
    public Piece[] Group_Pieces;

    public String Color;
    public final int Sum_Pieces = 16;
    public final int Init_pawns_num = 8;

    public Group_of_pieces(String color, int Up, int[] listPieces)
    // up means if the group of pieces (black or right) is on the top of the board, 0 or 1
    // which means if the board has been turned up-side-down (180 degrees)
    {
        Color = color;
        Group_Pieces = new Piece[Sum_Pieces];

        SortPieces(listPieces, Up);

        for(int i = 0; i < Init_pawns_num; i++)
        {
            Group_Pieces[8 + i] = new Pawn(Color, i, 1 + 5 * Up, 8 + i, 0);
        }
    }

    public Group_of_pieces(Group_of_pieces original_group)
    {
        Color = original_group.Color;
        int len = original_group.Group_Pieces.length;
        Group_Pieces = new Piece[len];
        for (int piece = 0; piece < len; piece++)
        {
            if (original_group.Group_Pieces[piece] != null)// && !original_group.Group_Pieces[piece].IsDeleted)
            {
                switch (original_group.Group_Pieces[piece].Type) {
                    case "Pawn":
                        Group_Pieces[piece] = new Pawn((Pawn) original_group.Group_Pieces[piece]);
                        break;
                    case "Rook":
                        Group_Pieces[piece] = new Rook(original_group.Group_Pieces[piece]);
                        break;
                    case "Bishop":
                        Group_Pieces[piece] = new Bishop(original_group.Group_Pieces[piece]);
                        break;
                    case "Knight":
                        Group_Pieces[piece] = new Knight(original_group.Group_Pieces[piece]);
                        break;
                    case "Queen":
                        Group_Pieces[piece] = new Queen(original_group.Group_Pieces[piece]);
                        break;
                    case "King":
                        Group_Pieces[piece] = new King(original_group.Group_Pieces[piece]);
                        break;
                }
            }
        }
    }

    public Group_of_pieces(Board board, String color) {
        int len = 0;
        for (int i = 0; i < Board.BoardSize; i++){
            for (int j = 0; j < Board.BoardSize; j++) {
                if (board.The_Grid[i][j].Is_there_Piece &&
                        board.The_Grid[i][j].Color_piece.equals(color)) {
                    len++;
                }
            }
        }
        Group_Pieces = new Piece[len];
        int pieceNum = 0;

        for (int i = 0; i < Board.BoardSize; i++){
            for (int j = 0; j < Board.BoardSize; j++){
                if (board.The_Grid[i][j].Is_there_Piece &&
                        board.The_Grid[i][j].Color_piece.equals(color)){
                    switch (board.The_Grid[i][j].Type) {
                        case "Pawn":
                            Group_Pieces[pieceNum] = new Pawn(color, i, j, pieceNum, 0);
                            break;
                        case "Rook":
                            Group_Pieces[pieceNum] = new Rook(color, i, j, pieceNum, 0);
                            break;
                        case "Bishop":
                            Group_Pieces[pieceNum] = new Bishop(color, i, j, pieceNum, 0);
                            break;
                        case "Knight":
                            Group_Pieces[pieceNum] = new Knight(color, i, j, pieceNum, 0);
                            break;
                        case "Queen":
                            Group_Pieces[pieceNum] = new Queen(color, i, j, pieceNum, 0);
                            break;
                        case "King":
                            Group_Pieces[pieceNum] = new King(color, i, j, pieceNum, 0);
                            break;
                    }
                    pieceNum++;
                }
            }
        }
    }

    public void Update_move(Move move)
    {
        for (Piece groupPiece : Group_Pieces) {
            if (groupPiece != null && !groupPiece.IsDeleted && groupPiece.Type.equals("Pawn")){
                ((Pawn) groupPiece).moved_two_cells = false;
            }
        }
        for (Piece groupPiece : Group_Pieces) {
            if (groupPiece != null && !groupPiece.IsDeleted && move.Type_of_piece.equals(groupPiece.Type) &&
                    groupPiece.Row_Number == move.Current_row && groupPiece.Column_Number == move.Current_column) {
                groupPiece.Number_of_moves++;
                switch (groupPiece.Type) {
                    case "Pawn":
                        groupPiece.Row_Number = move.Next_row;
                        groupPiece.Column_Number = move.Next_column;
                        if (Math.abs(move.Current_column - move.Next_column) == 2) {
                            ((Pawn) groupPiece).moved_two_cells = true;
                        }
                        break;
                    case "King":
                        if (!move.Castle) {
                            groupPiece.Row_Number = move.Next_row;
                            groupPiece.Column_Number = move.Next_column;
                        } else {
                            for (Piece group_piece : Group_Pieces) {
                                if (group_piece != null && !group_piece.IsDeleted && group_piece.Type.equals("Rook") &&
                                        group_piece.Row_Number == move.Next_row && group_piece.Column_Number == move.Next_column) {
                                    groupPiece.Row_Number = (((move.Next_row > move.Current_row) ? 1 : 0) + 2) * 2;
                                    groupPiece.Column_Number = move.Next_column;

                                    group_piece.Row_Number = groupPiece.Row_Number - ((move.Next_row > move.Current_row) ? 1 : -1);
                                    group_piece.Column_Number = move.Next_column;
                                    group_piece.Number_of_moves++;
                                }
                            }
                        }
                        break;
                    default:
                        groupPiece.Row_Number = move.Next_row;
                        groupPiece.Column_Number = move.Next_column;
                        break;
                }
                return;
            }
        }
    }

    public Piece Delete_piece(int endMove_x, int endMove_y, int startMove_x, int startMove_y, Group_of_pieces other_group)
    {
        Piece deletedPiece = null;
        Piece checkedPiece = Piece_location(endMove_x, endMove_y);
        if (checkedPiece == null) // no opponent piece
        {
            if (other_group.Piece_location(startMove_x, startMove_y) != null && other_group.Piece_location(startMove_x, startMove_y).Type.equals("Pawn") && startMove_x != endMove_x) {
                deletedPiece = Piece_location(endMove_x, startMove_y);
                if (deletedPiece != null){
                    Group_Pieces[deletedPiece.Piece_num].IsDeleted = true;
                }
            }
            return deletedPiece;
        }
        else{
            if (Piece_location(startMove_x, startMove_y) != null){
                // castle
                if (Piece_location(startMove_x, startMove_y).Color.equals(checkedPiece.Color)){
                    return null;
                }
            }
        }

        // normal delete:
        deletedPiece = checkedPiece;
        Group_Pieces[deletedPiece.Piece_num].IsDeleted = true;
        return deletedPiece;
    }

    public Piece Piece_location(int x_cell, int y_cell)
    {
        for (Piece group_piece : Group_Pieces) {
            if (group_piece != null && !group_piece.IsDeleted) {
                if (group_piece.Row_Number == x_cell && group_piece.Column_Number == y_cell) {
                    return group_piece;
                }
            }
        }
        return null;
    }

    public Point get_king_point()
    {
        Point king_location = null;
        for (Piece group_piece : Group_Pieces) {
            if (group_piece != null && !group_piece.IsDeleted && group_piece.Type.equals("King")) {
                king_location = new Point(group_piece.Row_Number, group_piece.Column_Number);
            }
        }
        return king_location;
    }

    private void SortPieces(int[] listPieces, int Up){
        // int[] listPieces = new int[] {4, 0, 7, 2, 5, 1, 6, 3}; --> regular chess start-position

        Group_Pieces[0] = new King(Color, listPieces[0], 7 * Up, 0, 0);

        Group_Pieces[1] = new Rook(Color, listPieces[1], 7 * Up, 1, 0);
        Group_Pieces[2] = new Rook(Color, listPieces[2], 7 * Up, 2, 0);

        Group_Pieces[3] = new Bishop(Color, listPieces[3], 7 * Up, 3, 0);
        Group_Pieces[4] = new Bishop(Color, listPieces[4], 7 * Up, 4, 0);

        Group_Pieces[5] = new Knight(Color, listPieces[5], 7 * Up, 5, 0);
        Group_Pieces[6] = new Knight(Color, listPieces[6], 7 * Up, 6, 0);

        Group_Pieces[7] = new Queen(Color, listPieces[7], 7 * Up, 7, 0);
    }
}
