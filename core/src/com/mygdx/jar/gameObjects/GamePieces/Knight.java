package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.Group_of_pieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;

// public class Knight extends Piece
public class Knight extends Piece
{
    public Knight(String color, int x, int y, int piece_num, int number_of_moves)
    {
        super("Knight", color, x, y, piece_num, false, number_of_moves);
    }

    public Knight(Piece original_knight)
    {
        super(original_knight);
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
            if (Column_Number < 6)
            {
                if (chess_board.The_Grid[Row_Number - 1][Column_Number + 2].Is_there_Piece)
                {
                    if (chess_board.The_Grid[Row_Number - 1][Column_Number + 2].Color_piece == "black" ^ is_white_turn == false)
                    {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number + 2, is_white_turn, "Knight");
                    number_of_valid_moves++;
                    }
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number + 2, is_white_turn, "Knight");
                    number_of_valid_moves++;
                }
            }
            if (Column_Number > 1)
            {
                if (chess_board.The_Grid[Row_Number - 1][Column_Number - 2].Is_there_Piece)
                {
                    if (chess_board.The_Grid[Row_Number - 1][Column_Number - 2].Color_piece == "black" ^ is_white_turn == false)
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number - 2, is_white_turn, "Knight");
                        number_of_valid_moves++;
                    }
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 1, Column_Number - 2, is_white_turn, "Knight");
                    number_of_valid_moves++;
                }
            }
            if (Row_Number > 1)
            {
                if (Column_Number < 7)
                {
                    if (chess_board.The_Grid[Row_Number - 2][Column_Number + 1].Is_there_Piece)
                    {
                        if (chess_board.The_Grid[Row_Number - 2][Column_Number + 1].Color_piece == "black" ^ is_white_turn == false)
                        {
                            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 2, Column_Number + 1, is_white_turn, "Knight");
                            number_of_valid_moves++;
                        }
                    }
                    else
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 2, Column_Number + 1, is_white_turn, "Knight");
                        number_of_valid_moves++;
                    }
                }
                if (Column_Number > 0)
                {
                    if (chess_board.The_Grid[Row_Number - 2][Column_Number - 1].Is_there_Piece)
                    {
                        if (chess_board.The_Grid[Row_Number - 2][Column_Number - 1].Color_piece == "black" ^ is_white_turn == false)
                        {
                            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 2, Column_Number - 1, is_white_turn, "Knight");
                            number_of_valid_moves++;
                        }
                    }
                    else
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - 2, Column_Number - 1, is_white_turn, "Knight");
                        number_of_valid_moves++;
                    }
                }
            }
        }
        if (Row_Number < 7)
        {
            if (Column_Number < 6)
            {
                if (chess_board.The_Grid[Row_Number + 1][Column_Number + 2].Is_there_Piece)
                {
                    if (chess_board.The_Grid[Row_Number + 1][Column_Number + 2].Color_piece == "black" ^ is_white_turn == false)
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number + 2, is_white_turn, "Knight");
                        number_of_valid_moves++;
                    }
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number + 2, is_white_turn, "Knight");
                    number_of_valid_moves++;
                }
            }
            if (Column_Number > 1)
            {
                if (chess_board.The_Grid[Row_Number + 1][Column_Number - 2].Is_there_Piece)
                {
                    if (chess_board.The_Grid[Row_Number + 1][Column_Number - 2].Color_piece == "black" ^ is_white_turn == false)
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number - 2, is_white_turn, "Knight");
                        number_of_valid_moves++;
                    }
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 1, Column_Number - 2, is_white_turn, "Knight");
                    number_of_valid_moves++;
                }
            }
            if (Row_Number < 6)
            {
                if (Column_Number < 7)
                {
                    if (chess_board.The_Grid[Row_Number + 2][Column_Number + 1].Is_there_Piece)
                    {
                        if (chess_board.The_Grid[Row_Number + 2][Column_Number + 1].Color_piece == "black" ^ is_white_turn == false)
                        {
                            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 2, Column_Number + 1, is_white_turn, "Knight");
                            number_of_valid_moves++;
                        }
                    }
                    else
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 2, Column_Number + 1, is_white_turn, "Knight");
                        number_of_valid_moves++;
                    }
                }
                if (Column_Number > 0)
                {
                    if (chess_board.The_Grid[Row_Number + 2][Column_Number - 1].Is_there_Piece)
                    {
                        if (chess_board.The_Grid[Row_Number + 2][Column_Number - 1].Color_piece == "black" ^ is_white_turn == false)
                        {
                            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 2, Column_Number - 1, is_white_turn, "Knight");
                            number_of_valid_moves++;
                        }
                    }
                    else
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + 2, Column_Number - 1, is_white_turn, "Knight");
                        number_of_valid_moves++;
                    }
                }
            }
        }
        return number_of_valid_moves;
    }
}
