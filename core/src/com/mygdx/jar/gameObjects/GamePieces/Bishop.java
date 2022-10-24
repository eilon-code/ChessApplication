package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.Group_of_pieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;

public class Bishop extends Piece
{
    public Bishop(String color, int x, int y, int piece_num, int number_of_moves)
    {
        super("Bishop", color, x, y, piece_num, false, number_of_moves);
    }

    public Bishop(Piece original_bishop)
    {
        super(original_bishop);
    }

    @Override
    public int Get_Max_Number_Of_Moves_On_Board()
    {
        return 13;
    }

    @Override
    public int Fill_All_In_Board_Moves(Move[] Group_moves, int current_index, boolean is_white_turn,
                                       boolean Is_white_up, Board chess_board, Group_of_pieces other_group, Group_of_pieces group)
    {
        int number_of_valid_moves = 0;
        // slant right and up
        for (int slant = 1; slant < 8 - Row_Number; slant++)
        {
            if (Column_Number + slant > 7)
            {
                break;
            }
            if (chess_board.The_Grid[Row_Number + slant][Column_Number + slant].Is_there_Piece)
            {
                if (!chess_board.The_Grid[Row_Number + slant][Column_Number + slant].Color_piece.equals(Color)) {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + slant, Column_Number + slant, is_white_turn, "Bishop");
                    number_of_valid_moves++;
                }
                break;
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + slant, Column_Number + slant, is_white_turn, "Bishop");
                number_of_valid_moves++;
            }
        }

        // slant right and down
        for (int slant = 1; slant < 8 - Row_Number; slant++)
        {
            if (Column_Number - slant < 0)
            {
                break;
            }
            if (chess_board.The_Grid[Row_Number + slant][Column_Number - slant].Is_there_Piece)
            {
                if (!chess_board.The_Grid[Row_Number + slant][Column_Number - slant].Color_piece.equals(Color)) {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + slant, Column_Number - slant, is_white_turn, "Bishop");
                    number_of_valid_moves++;
                }
                break;
            }
            else
            {
            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number + slant, Column_Number - slant, is_white_turn, "Bishop");
            number_of_valid_moves++;
            }
        }

        // slant left and up
        for (int slant = 1; slant < Row_Number + 1; slant++)
        {
            if (Column_Number + slant > 7)
            {
                break;
            }
            if (chess_board.The_Grid[Row_Number - slant][Column_Number + slant].Is_there_Piece)
            {
                if (!chess_board.The_Grid[Row_Number - slant][Column_Number + slant].Color_piece.equals(Color)) {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - slant, Column_Number + slant, is_white_turn, "Bishop");
                    number_of_valid_moves++;
                }
                break;
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - slant, Column_Number + slant, is_white_turn, "Bishop");
                number_of_valid_moves++;
            }
        }

        // slant left and down
        for (int slant = 1; slant < Row_Number + 1; slant++)
        {
            if (Column_Number - slant < 0)
            {
                break;
            }
            if (chess_board.The_Grid[Row_Number - slant][Column_Number - slant].Is_there_Piece)
            {
                if (!chess_board.The_Grid[Row_Number - slant][Column_Number - slant].Color_piece.equals(Color)) {
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - slant, Column_Number - slant, is_white_turn, "Bishop");
                    number_of_valid_moves++;
                }
                break;
            }
            else
            {
                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number - slant, Column_Number - slant, is_white_turn, "Bishop");
                number_of_valid_moves++;
            }
        }

        return number_of_valid_moves;
    }
}
