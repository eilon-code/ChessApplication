package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.Group_of_pieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;

// public class Rook extends Piece
public class Rook  extends Piece
{
    public Rook(String color, int x, int y, int piece_num, int number_of_moves)
    {
        super("Rook", color, x, y, piece_num, false, number_of_moves);
    }

    public Rook(Piece original_rook)
    {
        super(original_rook);
    }

    @Override
    public int Get_Max_Number_Of_Moves_On_Board()
    {
        return 14;
    }

    @Override
    public int Fill_All_In_Board_Moves(Move[] Group_moves, int current_index, boolean is_white_turn,
                                       boolean Is_white_up, Board chess_board, Group_of_pieces other_group, Group_of_pieces group)
    {
        int number_of_valid_moves = 0;

        // right vector
        for (int right = 1; right < 8 - Row_Number; right++)
        {
            if (chess_board.The_Grid[Row_Number + right][Column_Number].Is_there_Piece)
            {
                if (chess_board.The_Grid[Row_Number + right][Column_Number].Color_piece == "white" ^ is_white_turn == false)
                {
                    break;
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + right, Column_Number, is_white_turn, "Rook");
                    number_of_valid_moves++;
                    break;
                }
            }
            else
            {
            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + right, Column_Number, is_white_turn, "Rook");
            number_of_valid_moves++;
            }
        }

        // up vector
        for (int up = 1; up < 8 - Column_Number; up++)
        {
            if (chess_board.The_Grid[Row_Number][Column_Number + up].Is_there_Piece)
            {
                if (chess_board.The_Grid[Row_Number][Column_Number + up].Color_piece == "white" ^ is_white_turn == false)
                {
                    break;
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number, Column_Number + up, is_white_turn, "Rook");
                    number_of_valid_moves++;
                    break;
                }
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number, Column_Number + up, is_white_turn, "Rook");
                number_of_valid_moves++;
            }
        }

        // left vector
        for (int left = 1; left < Row_Number + 1; left++)
        {
            if (chess_board.The_Grid[Row_Number - left][Column_Number].Is_there_Piece)
            {
                if (chess_board.The_Grid[Row_Number - left][Column_Number].Color_piece == "white" ^ is_white_turn == false)
                {
                    break;
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - left, Column_Number, is_white_turn, "Rook");
                    number_of_valid_moves++;
                    break;
                }
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - left, Column_Number, is_white_turn, "Rook");
                number_of_valid_moves++;
            }
        }

        // down vector
        for (int down = 1; down < Column_Number + 1; down++)
        {
            if (chess_board.The_Grid[Row_Number][Column_Number - down].Is_there_Piece)
            {
                if (chess_board.The_Grid[Row_Number][Column_Number - down].Color_piece == "white" ^ is_white_turn == false)
                {
                    break;
                }
                else
                {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number, Column_Number - down, is_white_turn, "Rook");
                    number_of_valid_moves++;
                    break;
                }
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number, Column_Number - down, is_white_turn, "Rook");
                number_of_valid_moves++;
            }
        }
        return number_of_valid_moves;
    }
}
