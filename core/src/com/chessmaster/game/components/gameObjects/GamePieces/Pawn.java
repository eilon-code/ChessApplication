package com.chessmaster.game.components.gameObjects.GamePieces;

import com.chessmaster.game.components.gameObjects.BoardObjects.Board;
import com.chessmaster.game.components.gameObjects.BoardObjects.GroupOfPieces;
import com.chessmaster.game.components.gameObjects.BoardObjects.Move;

import java.util.Stack;

public class Pawn extends Piece
{
    public boolean moved_two_cells;

    public Pawn(Color color, int row, int column, int pieceNum, int numberOfMoves)
    {
        super(PieceType.Pawn, color, row, column, pieceNum, false, numberOfMoves);
        this.moved_two_cells = false;
    }

    public Pawn(Pawn originalPawn)
    {
        super(originalPawn);
        this.moved_two_cells = originalPawn.moved_two_cells;
    }

    public Pawn(Piece originalPawn)
    {
        super(originalPawn);
        this.type = PieceType.Pawn;
        this.moved_two_cells = false;
    }

    @Override
    public int getMaxNumberOfMovesOnBoard()
    {
        return 4;
    }

    @Override
    public void fillPieceMoves(Stack<Move> groupMoves, boolean is_white_turn,
                               boolean Is_white_up, Board board, GroupOfPieces otherGroup, GroupOfPieces group)
    {
        int is_board_turned_up;
        if (is_white_turn ^ Is_white_up)
        {
            is_board_turned_up = 0;
        }
        else
        {
            is_board_turned_up = 1;
        }
        if (column < 7 && column > 0)
        {
            if (!board.cellsGrid[row][column + 1 - 2 * is_board_turned_up].isTherePiece)
            {
                if (column + 1 - 2 * is_board_turned_up == 0 || column + 1 - 2 * is_board_turned_up == 7){
                    for (PieceType crownType : Piece.CrownTypes){
                        groupMoves.push(new Move(row, column, row, column + 1 - 2 * is_board_turned_up, color, PieceType.Pawn, crownType));
                    }
                }
                else{
                    groupMoves.push(new Move(row, column, row,
                            column + 1 - 2 * is_board_turned_up, color, PieceType.Pawn));
                    if (column == 1 + 5 * is_board_turned_up && !board.cellsGrid[row][column + 2 - 4 * is_board_turned_up].isTherePiece)
                    {
                        groupMoves.push(new Move(row, column, row, column + 2 - 4 * is_board_turned_up, color, PieceType.Pawn));
                    }
                }
            }
            if (row < 7)
            {
                if (board.cellsGrid[row + 1][column + 1 - 2 * is_board_turned_up].isTherePiece && !(board.cellsGrid[row + 1][column + 1 - 2 * is_board_turned_up].color.equals(color)))
                {
                    if (column + 1 - 2 * is_board_turned_up == 0 || column + 1 - 2 * is_board_turned_up == 7){
                        for (PieceType crownType : Piece.CrownTypes){
                            groupMoves.push(new Move(row,
                                    column, row + 1, column + 1 - 2 * is_board_turned_up, color, PieceType.Pawn, crownType));
                        }
                    }
                    else {
                        groupMoves.push(new Move(row,
                                column, row + 1, column + 1 - 2 * is_board_turned_up, color, PieceType.Pawn));
                    }
                }
            }
            if (row > 0)
            {
                if (board.cellsGrid[row - 1][column + 1 - 2 * is_board_turned_up].isTherePiece && !(board.cellsGrid[row - 1][column + 1 - 2 * is_board_turned_up].color.equals(color)))
                {
                    if (column + 1 - 2 * is_board_turned_up == 0 || column + 1 - 2 * is_board_turned_up == 7){
                        for (PieceType crownType : Piece.CrownTypes){
                            groupMoves.push(new Move(row,
                                    column, row - 1, column + 1 - 2 * is_board_turned_up, color, PieceType.Pawn, crownType));
                        }
                    }
                    else{
                        groupMoves.push(new Move(row,
                                column, row - 1, column + 1 - 2 * is_board_turned_up, color, PieceType.Pawn));
                    }
                }
            }

            if (column == 4 || column == 3)
            {
                if (row > 0)
                {
                    if (board.cellsGrid[row - 1][column].isTherePiece && !(board.cellsGrid[row - 1][column].color.equals(color)))
                    {
                        if (otherGroup.Piece_location(row - 1, column) != null
                        && !otherGroup.Piece_location(row - 1, column).isDeleted
                        && otherGroup.Piece_location(row - 1, column).type.equals(PieceType.Pawn))
                        {
                            if (((Pawn)otherGroup.Piece_location(row - 1, column)).moved_two_cells)
                            {
                                groupMoves.push(new Move(row,
                                column, row - 1, column + 1 - 2 * is_board_turned_up, color, PieceType.Pawn));
                            }
                        }
                    }
                }
                if (row < 7)
                {
                    if (board.cellsGrid[row + 1][column].isTherePiece && !(board.cellsGrid[row + 1][column].color.equals(color)))
                    {
                        if (otherGroup.Piece_location(row + 1, column) != null
                        && !otherGroup.Piece_location(row + 1, column).isDeleted
                        && otherGroup.Piece_location(row + 1, column).type.equals(PieceType.Pawn))
                        {
                            if (((Pawn)otherGroup.Piece_location(row + 1, column)).moved_two_cells)
                            {
                                groupMoves.push(new Move(row,
                                column, row + 1, column + 1 - 2 * is_board_turned_up, color, PieceType.Pawn));
                            }
                        }
                    }
                }
            }
        }
    }
}
