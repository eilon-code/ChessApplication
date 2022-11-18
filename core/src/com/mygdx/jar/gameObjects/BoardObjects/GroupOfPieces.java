package com.mygdx.jar.gameObjects.BoardObjects;

import com.mygdx.jar.gameObjects.GamePieces.Bishop;
import com.mygdx.jar.gameObjects.GamePieces.Color;
import com.mygdx.jar.gameObjects.GamePieces.King;
import com.mygdx.jar.gameObjects.GamePieces.Knight;
import com.mygdx.jar.gameObjects.GamePieces.Piece;
import com.mygdx.jar.gameObjects.GamePieces.Pawn;
import com.mygdx.jar.gameObjects.GamePieces.PieceType;
import com.mygdx.jar.gameObjects.GamePieces.Queen;
import com.mygdx.jar.gameObjects.GamePieces.Rook;

import java.util.Stack;

public class GroupOfPieces {
    public Stack<Piece> pieces;

    public Color color;

    public GroupOfPieces(Color color, int Up, int[] listPieces)
    {
        this.color = color;
        this.pieces = new Stack<>();

        SortPieces(listPieces, Up);

        for(int i = 0; i < Board.BoardSize; i++)
        {
            pieces.push(new Pawn(this.color, i, 1 + 5 * Up, Board.BoardSize + i, 0));
        }
    }

    public GroupOfPieces(GroupOfPieces originalGroup)
    {
        this.color = originalGroup.color;
        this.pieces = new Stack<>();
        Stack<Piece> temp = new Stack<>();
        while (!originalGroup.pieces.empty()){
            temp.push(originalGroup.pieces.pop());
            if (temp.peek() != null)// && !original_group.Group_Pieces[piece].IsDeleted)
            {
                switch (temp.peek().type) {
                    case Pawn:
                        this.pieces.push(new Pawn((Pawn) temp.peek()));
                        break;

                    case Rook:
                        this.pieces.push(new Rook(temp.peek()));
                        break;

                    case Bishop:
                        this.pieces.push(new Bishop(temp.peek()));
                        break;

                    case Knight:
                        this.pieces.push(new Knight(temp.peek()));
                        break;

                    case Queen:
                        this.pieces.push(new Queen(temp.peek()));
                        break;

                    case King:
                        this.pieces.push(new King(temp.peek()));
                        break;
                }
            }
        }
        while (!temp.empty()){
            originalGroup.pieces.push(temp.pop());
        }
    }

    public GroupOfPieces(Board board, Color color) {
        this.pieces = new Stack<>();
        int pieceNum = 0;

        for (int i = 0; i < Board.BoardSize; i++){
            for (int j = 0; j < Board.BoardSize; j++){
                if (board.cellsGrid[i][j].isTherePiece &&
                        board.cellsGrid[i][j].color.equals(color)){
                    switch (board.cellsGrid[i][j].type) {
                        case Pawn:
                            this.pieces.push(new Pawn(color, i, j, pieceNum, 0));
                            break;

                        case Rook:
                            this.pieces.push(new Rook(color, i, j, pieceNum, 0));
                            break;

                        case Bishop:
                            this.pieces.push(new Bishop(color, i, j, pieceNum, 0));
                            break;

                        case Knight:
                            this.pieces.push(new Knight(color, i, j, pieceNum, 0));
                            break;

                        case Queen:
                            this.pieces.push(new Queen(color, i, j, pieceNum, 0));
                            break;

                        case King:
                            this.pieces.push(new King(color, i, j, pieceNum, 0));
                            break;

                        default:
                            pieceNum--;// encounter adding 1 to pieceNum
                    }
                    pieceNum++;
                }
            }
        }
    }

    public void Update_move(Move move)
    {
        for (Piece groupPiece : pieces) {
            if (groupPiece != null && !groupPiece.isDeleted && groupPiece.type.equals(PieceType.Pawn)){
                ((Pawn) groupPiece).moved_two_cells = false;
            }
        }
        for (Piece groupPiece : pieces) {
            if (groupPiece != null && !groupPiece.isDeleted && move.type.equals(groupPiece.type) &&
                    groupPiece.row == move.startRow && groupPiece.column == move.startColumn) {
                groupPiece.numberOfMoves++;
                switch (groupPiece.type) {
                    case Pawn:
                        groupPiece.row = move.endRow;
                        groupPiece.column = move.endColumn;
                        if (Math.abs(move.startColumn - move.endColumn) == 2) {
                            ((Pawn) groupPiece).moved_two_cells = true;
                        }
                        break;

                    case King:
                        if (!move.castle) {
                            groupPiece.row = move.endRow;
                            groupPiece.column = move.endColumn;
                        } else {
                            for (Piece rook : pieces) {
                                if (rook != null && !rook.isDeleted && rook.type.equals(PieceType.Rook) &&
                                        rook.row == move.endRow && rook.column == move.endColumn) {
                                    groupPiece.row = (((move.endRow > move.startRow) ? 1 : -1) + 2) * 2;
                                    groupPiece.column = move.endColumn;

                                    rook.row = groupPiece.row - ((move.endRow > move.startRow) ? 1 : -1);
                                    rook.numberOfMoves++;
                                    break;
                                }
                            }
                        }
                        break;
                    default:
                        groupPiece.row = move.endRow;
                        groupPiece.column = move.endColumn;
                        break;
                }
            }
        }
    }

    public Piece Delete_piece(int endRow, int endColumn, int startRow, int startColumn, GroupOfPieces otherGroup)
    {
        // this function called by the eaten color-group
        Piece deletedPiece = null;
        Piece checkedPiece = Piece_location(endRow, endColumn);
        if (checkedPiece == null) // no piece at the "normal eating" location
        {
            Piece pawnTheEater = otherGroup.Piece_location(startRow, startColumn);
            if (pawnTheEater != null && pawnTheEater.type.equals(PieceType.Pawn) && startRow != endRow) {
                deletedPiece = Piece_location(endRow, startColumn);
            }
        }
        else{
            // normal delete:
            deletedPiece = checkedPiece;
        }

        if (deletedPiece != null){
            deletedPiece.isDeleted = true;
        }
        return deletedPiece;
    }

    public Piece Piece_location(int cellRow, int cellColumn)
    {
        for (Piece groupPiece : pieces) {
            if (groupPiece != null && !groupPiece.isDeleted) {
                if (groupPiece.row == cellRow && groupPiece.column == cellColumn) {
                    return groupPiece;
                }
            }
        }
        return null;
    }

    public Point get_king_point()
    {
        Point king_location = null;
        for (Piece groupPiece : pieces) {
            if (groupPiece != null && !groupPiece.isDeleted && groupPiece.type.equals(PieceType.King)) {
                king_location = new Point(groupPiece.row, groupPiece.column);
            }
        }
        return king_location;
    }

    private void SortPieces(int[] listPieces, int Up){
        // int[] listPieces = new int[] {4, 0, 7, 2, 5, 1, 6, 3}; --> regular chess start-position
        pieces.push(new King(color, listPieces[0], 7 * Up, 0, 0));

        pieces.push(new Rook(color, listPieces[1], 7 * Up, 1, 0));
        pieces.push(new Rook(color, listPieces[2], 7 * Up, 2, 0));

        pieces.push(new Bishop(color, listPieces[3], 7 * Up, 3, 0));
        pieces.push(new Bishop(color, listPieces[4], 7 * Up, 4, 0));

        pieces.push(new Knight(color, listPieces[5], 7 * Up, 5, 0));
        pieces.push(new Knight(color, listPieces[6], 7 * Up, 6, 0));

        pieces.push(new Queen(color, listPieces[7], 7 * Up, 7, 0));
    }
}
