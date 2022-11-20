package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.GroupOfPieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;
import com.mygdx.jar.gameObjects.BoardObjects.Point;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class King extends Piece
{
    public King(Color color, int row, int column, int pieceNum, int numberOfMoves)
    {
        super(PieceType.King, color, row, column, pieceNum, false, numberOfMoves);
    }

    public King(Piece originalKing)
    {
        super(originalKing);
    }

    @Override
    public int getMaxNumberOfMovesOnBoard()
    {
        return 8;
    }

    @Override
    public void fillPieceMoves(Stack<Move> groupMoves, boolean is_white_turn,
                               boolean Is_white_up, Board board, GroupOfPieces otherGroup, GroupOfPieces group)
    {
        Queue<Point> directions = new LinkedList<>();
        directions.add(new Point(1, 1));
        directions.add(new Point(1, 0));
        directions.add(new Point(1, -1));
        directions.add(new Point(-1, 1));
        directions.add(new Point(-1, 0));
        directions.add(new Point(-1, -1));
        directions.add(new Point(0, 1));
        directions.add(new Point(0, -1));
        directions.add(new Point(0, 0));

        int radius = 1;
        while (directions.size() > 1){
            if (directions.peek().equals(new Point(0, 0))){
                break;
            }
            Point cellInCheck = new Point(directions.peek());
            cellInCheck.X *= radius;
            cellInCheck.Y *= radius;
            cellInCheck.X += row;
            cellInCheck.Y += column;
            if (cellInCheck.X < 0 || cellInCheck.X >= Board.BoardSize ||
                    cellInCheck.Y < 0 || cellInCheck.Y >= Board.BoardSize){
                directions.remove();
                continue;
            }
            if (board.cellsGrid[cellInCheck.X][cellInCheck.Y].isTherePiece)
            {
                if (!board.cellsGrid[cellInCheck.X][cellInCheck.Y].color.equals(color)) {
                    groupMoves.push(new Move(row, column, cellInCheck.X, cellInCheck.Y, color, type));
                }
                directions.remove();
                continue;
            }
            else
            {
                groupMoves.push(new Move(row, column, cellInCheck.X, cellInCheck.Y, color, type));
            }
            directions.add(directions.remove());
        }
        while (!directions.isEmpty()){
            directions.remove();
        }

        // castle:
        if (!HasBeenMoved())
        {
            for (Piece groupPiece : group.pieces)
            {
                if (groupPiece != null && !groupPiece.isDeleted)
                {
                    if (groupPiece.type.equals(PieceType.Rook) &&
                            !groupPiece.HasBeenMoved() &&
                            groupPiece.column == column)
                    {
                        boolean castle_with_current_rook_possible = true;

                        int startRow;
                        int endRow;
                        if (row < groupPiece.row)
                        {
                            int wantedRow = 6;
                            startRow = Math.min(row, wantedRow - 1);
                            endRow = Math.max(groupPiece.row, wantedRow);
                        }
                        else
                        {
                            int wantedRow = 2;
                            startRow = Math.min(groupPiece.row, wantedRow);
                            endRow = Math.max(row, wantedRow + 1);
                        }

                        for (int currentRow = startRow; currentRow <= endRow; currentRow++){
                            if (board.cellsGrid[currentRow][column].isTherePiece &&
                                    currentRow != row && currentRow != groupPiece.row){
                                castle_with_current_rook_possible = false;
                                break;
                            }
                        }
                        if (castle_with_current_rook_possible){
                            Move move = new Move(row, column, groupPiece.row, groupPiece.column, color, PieceType.King);
                            move.setCastle();
                            groupMoves.push(move);
                        }
                    }
                }
            }
        }
    }
}
