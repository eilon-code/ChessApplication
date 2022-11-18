package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.GroupOfPieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;
import com.mygdx.jar.gameObjects.BoardObjects.Point;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Bishop extends Piece
{
    public Bishop(Color color, int row, int column, int pieceNum, int numberOfMoves)
    {
        super(PieceType.Bishop, color, row, column, pieceNum, false, numberOfMoves);
    }

    public Bishop(Piece originalBishop)
    {
        super(originalBishop);
    }

    @Override
    public int getMaxNumberOfMovesOnBoard()
    {
        return 13;
    }

    @Override
    public void fillPieceMoves(Stack<Move> groupMoves, boolean is_white_turn,
                               boolean Is_white_up, Board board, GroupOfPieces otherGroup, GroupOfPieces group)
    {
        Queue<Point> directions = new LinkedList<>();
        directions.add(new Point(0, 0));
        directions.add(new Point(1, 1));
        directions.add(new Point(1, -1));
        directions.add(new Point(-1, 1));
        directions.add(new Point(-1, -1));

        int radius = 0;
        while (directions.size() > 1){
            if (directions.peek().equals(new Point(0, 0))){
                radius++;
                directions.add(directions.remove());
                continue;
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
    }
}
