package com.mygdx.jar.gameObjects.GamePieces;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.Group_of_pieces;
import com.mygdx.jar.gameObjects.BoardObjects.Move;

public class Pawn extends Piece
{
    public boolean moved_two_cells;

    public Pawn(String color, int x, int y, int piece_num, int number_of_moves)
    {
        super("Pawn", color, x, y, piece_num, false, number_of_moves);
        moved_two_cells = false;
    }

    public Pawn(Pawn original_pawn)
    {
        super(original_pawn);
        moved_two_cells = original_pawn.moved_two_cells;
    }

    public Pawn(Piece original_pawn)
    {
        super(original_pawn);
        moved_two_cells = false;
    }

    @Override
    public int Get_Max_Number_Of_Moves_On_Board()
    {
        return 4;
    }

    @Override
    public int Fill_All_In_Board_Moves(Move[] Group_moves, int current_index, boolean is_white_turn,
                                       boolean Is_white_up, Board chess_board, Group_of_pieces other_group, Group_of_pieces group)
    {
        int number_of_valid_moves = 0;

        int is_board_turned_up;
        if (is_white_turn ^ Is_white_up)
        {
            is_board_turned_up = 0;
        }
        else
        {
            is_board_turned_up = 1;
        }
        if (Column_Number != 7 && Column_Number != 0)
        {
            if (!chess_board.The_Grid[Row_Number][Column_Number + 1 - 2 * is_board_turned_up].Is_there_Piece)
            {
                if (Column_Number + 1 - 2 * is_board_turned_up == 0 || Column_Number + 1 - 2 * is_board_turned_up == 7){
                    for (String crownType : Piece.CrownTypes){
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number,
                                Column_Number + 1 - 2 * is_board_turned_up, is_white_turn, "Pawn", crownType);
                        number_of_valid_moves++;
                    }
                }
                else{
                    Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number, Column_Number, Row_Number,
                            Column_Number + 1 - 2 * is_board_turned_up, is_white_turn, "Pawn");
                    number_of_valid_moves++;
                    if (Column_Number == 1 + 5 * is_board_turned_up && !chess_board.The_Grid[Row_Number][Column_Number + 2 - 4 * is_board_turned_up].Is_there_Piece)
                    {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number,
                                Column_Number, Row_Number, Column_Number + 2 - 4 * is_board_turned_up, is_white_turn, "Pawn");
                        number_of_valid_moves++;
                    }
                }
            }
            if (Row_Number < 7)
            {
                if (chess_board.The_Grid[Row_Number + 1][Column_Number + 1 - 2 * is_board_turned_up].Is_there_Piece && !(chess_board.The_Grid[Row_Number + 1][Column_Number + 1 - 2 * is_board_turned_up].Color_piece.equals(Color)))
                {
                    if (Column_Number + 1 - 2 * is_board_turned_up == 0 || Column_Number + 1 - 2 * is_board_turned_up == 7){
                        for (String crownType : Piece.CrownTypes){
                            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number,
                                    Column_Number, Row_Number + 1, Column_Number + 1 - 2 * is_board_turned_up, is_white_turn, "Pawn", crownType);
                            number_of_valid_moves++;
                        }
                    }
                    else {
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number,
                                Column_Number, Row_Number + 1, Column_Number + 1 - 2 * is_board_turned_up, is_white_turn, "Pawn");
                        number_of_valid_moves++;
                    }
                }
            }
            if (Row_Number > 0)
            {
                if (chess_board.The_Grid[Row_Number - 1][Column_Number + 1 - 2 * is_board_turned_up].Is_there_Piece && !(chess_board.The_Grid[Row_Number - 1][Column_Number + 1 - 2 * is_board_turned_up].Color_piece.equals(Color)))
                {
                    if (Column_Number + 1 - 2 * is_board_turned_up == 0 || Column_Number + 1 - 2 * is_board_turned_up == 7){
                        for (String crownType : Piece.CrownTypes){
                            Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number,
                                    Column_Number, Row_Number - 1, Column_Number + 1 - 2 * is_board_turned_up, is_white_turn, "Pawn", crownType);
                            number_of_valid_moves++;
                        }
                    }
                    else{
                        Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number,
                                Column_Number, Row_Number - 1, Column_Number + 1 - 2 * is_board_turned_up, is_white_turn, "Pawn");
                        number_of_valid_moves++;
                    }
                }
            }

            if (Column_Number == 4 || Column_Number == 3)
            {
                if (Row_Number > 0)
                {
                    if (chess_board.The_Grid[Row_Number - 1][Column_Number].Is_there_Piece && !(chess_board.The_Grid[Row_Number - 1][Column_Number].Color_piece.equals(Color)))
                    {
                        if (other_group.Piece_location(Row_Number - 1, Column_Number) != null
                        && !other_group.Piece_location(Row_Number - 1, Column_Number).IsDeleted
                        && other_group.Piece_location(Row_Number - 1, Column_Number).Type.equals("Pawn"))
                        {
                            if (((Pawn)other_group.Piece_location(Row_Number - 1, Column_Number)).moved_two_cells)
                            {
                                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number,
                                Column_Number, Row_Number - 1, Column_Number + 1 - 2 * is_board_turned_up, is_white_turn, "Pawn");
                                number_of_valid_moves++;
                            }
                        }
                    }
                }
                if (Row_Number < 7)
                {
                    if (chess_board.The_Grid[Row_Number + 1][Column_Number].Is_there_Piece && !(chess_board.The_Grid[Row_Number + 1][Column_Number].Color_piece.equals(Color)))
                    {
                        if (other_group.Piece_location(Row_Number + 1, Column_Number) != null
                        && !other_group.Piece_location(Row_Number + 1, Column_Number).IsDeleted
                        && other_group.Piece_location(Row_Number + 1, Column_Number).Type.equals("Pawn"))
                        {
                            if (((Pawn)other_group.Piece_location(Row_Number + 1, Column_Number)).moved_two_cells)
                            {
                                Group_moves[current_index + number_of_valid_moves] = new Move(Row_Number,
                                Column_Number, Row_Number + 1, Column_Number + 1 - 2 * is_board_turned_up, is_white_turn, "Pawn");
                                number_of_valid_moves++;
                            }
                        }
                    }
                }
            }
        }
        return number_of_valid_moves;
    }
}
