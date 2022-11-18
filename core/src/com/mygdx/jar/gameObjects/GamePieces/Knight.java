package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.GroupOfPieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;
import com.mygdx.jar.gameObjects.BoardObjects.Point;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Knight extends Piece
{
    public Knight(Color color, int row, int column, int pieceNum, int numberOfMoves)
    {
        super(PieceType.Knight, color, row, column, pieceNum, false, numberOfMoves);
    }

    public Knight(Piece originalKnight)
    {
        super(originalKnight);
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
        directions.add(new Point(2, 1));
        directions.add(new Point(2, -1));
        directions.add(new Point(-2, 1));
        directions.add(new Point(-2, -1));
        directions.add(new Point(1, 2));
        directions.add(new Point(1, -2));
        directions.add(new Point(-1, 2));
        directions.add(new Point(-1, -2));
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
    }
}
