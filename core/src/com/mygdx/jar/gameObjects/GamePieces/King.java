package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.Group_of_pieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;

public class King extends Piece
{
    public King(String color, int x, int y, int piece_num, int number_of_moves)
    {
        super("King", color, x, y, piece_num, false, number_of_moves);
    }

    public King(Piece original_king)
    {
        super(original_king);
    }

    @Override
    public int Get_Max_Number_Of_Moves_On_Board()
    {
        return 8;
    }

    @Override
    public int Fill_All_In_Board_Moves(Move[] Group_moves, int current_index, boolean is_white_turn,
                                       boolean Is_white_up, Board chess_board, Group_of_pieces other_group, Group_of_pieces group)
    {
        int number_of_valid_moves = 0;

        if (Row_Number > 0)
        {
            if (chess_board.The_Grid[Row_Number - 1][Column_Number].Is_there_Piece)
            {
                if (!chess_board.The_Grid[Row_Number - 1][Column_Number].Color_piece.equals(Color))
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number, is_white_turn, "King");
                    number_of_valid_moves++;
                }
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number, is_white_turn, "King");
                number_of_valid_moves++;
            }
            if (Column_Number > 0)
            {
                if (chess_board.The_Grid[Row_Number - 1][Column_Number - 1].Is_there_Piece)
                {
                    if (!chess_board.The_Grid[Row_Number - 1][Column_Number - 1].Color_piece.equals(Color))
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number - 1, is_white_turn, "King");
                        number_of_valid_moves++;
                    }
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number - 1, is_white_turn, "King");
                    number_of_valid_moves++;
                }
            }
            if (Column_Number < 7)
            {
                if (chess_board.The_Grid[Row_Number - 1][Column_Number + 1].Is_there_Piece)
                {
                    if (!chess_board.The_Grid[Row_Number - 1][Column_Number + 1].Color_piece.equals(Color))
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number + 1, is_white_turn, "King");
                        number_of_valid_moves++;
                    }
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number + 1, is_white_turn, "King");
                    number_of_valid_moves++;
                }
            }
        }
        if (Row_Number < 7)
        {
            if (chess_board.The_Grid[Row_Number + 1][Column_Number].Is_there_Piece)
            {
                if (!chess_board.The_Grid[Row_Number + 1][Column_Number].Color_piece.equals(Color))
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number, is_white_turn, "King");
                    number_of_valid_moves++;
                }
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number, is_white_turn, "King");
                number_of_valid_moves++;
            }
            if (Column_Number > 0)
            {
                if (chess_board.The_Grid[Row_Number + 1][Column_Number - 1].Is_there_Piece)
                {
                    if (!chess_board.The_Grid[Row_Number + 1][Column_Number - 1].Color_piece.equals(Color))
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number - 1, is_white_turn, "King");
                        number_of_valid_moves++;
                    }
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number - 1, is_white_turn, "King");
                    number_of_valid_moves++;
                }
            }
            if (Column_Number < 7)
            {
                if (chess_board.The_Grid[Row_Number + 1][Column_Number + 1].Is_there_Piece)
                {
                    if (!chess_board.The_Grid[Row_Number + 1][Column_Number + 1].Color_piece.equals(Color))
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number + 1, is_white_turn, "King");
                        number_of_valid_moves++;
                    }
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number + 1, is_white_turn, "King");
                    number_of_valid_moves++;
                }
            }
        }
        if (Column_Number > 0)
        {
            if (chess_board.The_Grid[Row_Number][Column_Number - 1].Is_there_Piece)
            {
                if (!chess_board.The_Grid[Row_Number][Column_Number - 1].Color_piece.equals(Color))
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number, Column_Number - 1, is_white_turn, "King");
                    number_of_valid_moves++;
                }
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number, Column_Number - 1, is_white_turn, "King");
                number_of_valid_moves++;
            }
        }
        if (Column_Number < 7)
        {
            if (chess_board.The_Grid[Row_Number][Column_Number + 1].Is_there_Piece)
            {
                if (!chess_board.The_Grid[Row_Number][Column_Number + 1].Color_piece.equals(Color))
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number, Column_Number + 1, is_white_turn, "King");
                    number_of_valid_moves++;
                }
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number, Column_Number + 1, is_white_turn, "King");
                number_of_valid_moves++;
            }
        }

        // castle:
        if (!HasBeenMoved())
        {
            for (int piece_num = 0; piece_num < group.Group_Pieces.length; piece_num++)
            {
                if (group.Group_Pieces[piece_num] != null && !group.Group_Pieces[piece_num].IsDeleted)
                {
                    if (group.Group_Pieces[piece_num].Type.equals("Rook") && !group.Group_Pieces[piece_num].HasBeenMoved())
                    {
                        boolean castle_with_current_rook_possible = true;
                        int startRow = Math.min(Row_Number, group.Group_Pieces[piece_num].Row_Number);
                        int endRow = Math.max(Row_Number, group.Group_Pieces[piece_num].Row_Number);
                        for (int row = startRow + 1; row < endRow; row++){
                            if (chess_board.The_Grid[row][Column_Number].Is_there_Piece) {
                                castle_with_current_rook_possible = false;
                                break;
                            }
                        }
                        if (castle_with_current_rook_possible){
                            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, group.Group_Pieces[piece_num].Row_Number, Column_Number, is_white_turn, "King");
                            Group_moves[current_index + number_of_valid_moves].Castle = true;
                            number_of_valid_moves++;
                        }
                    }
                }
            }
        }
        return number_of_valid_moves;
    }
}
