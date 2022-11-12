package com.mygdx.jar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.Cell;

import java.util.Stack;

class SQLiteDataBaseHandler extends SQLiteOpenHelper {
    private static final String[] A_TO_H = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
    private static final String[] ONE_TO_EIGHT = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
    private static final String DATABASE_NAME = "ChessMaster.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "Boards";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static String[][] COLUMN_DESCRIPTION;

    public SQLiteDataBaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        String[][] coordinatesNames = new String[A_TO_H.length][ONE_TO_EIGHT.length];
        for (int i = 0; i < A_TO_H.length; i++){
            for (int j = 0; j < ONE_TO_EIGHT.length; j++){
                coordinatesNames[i][j] = A_TO_H[i] + ONE_TO_EIGHT[j];
            }
        }
        COLUMN_DESCRIPTION = coordinatesNames;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < A_TO_H.length; i++){
            for (int j = 0; j < ONE_TO_EIGHT.length; j++){
                description.append(COLUMN_DESCRIPTION[i][j]);
                description.append(" TEXT, ");
            }
        }
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                description + COLUMN_TITLE + " TEXT" + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addBoard(Board board, String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        System.out.println("id board = " + row_id);

        cv.put(COLUMN_ID, row_id);
        cv.put(COLUMN_TITLE, board.Title);
        for (int i = 0; i < A_TO_H.length; i++){
            for (int j = 0; j < ONE_TO_EIGHT.length; j++){
                Cell cell = board.The_Grid[i][j];
                if (cell.Is_there_Piece){
                    cv.put(COLUMN_DESCRIPTION[i][j], cell.Color_piece + " " + cell.Type);
                }
                else{
                    cv.put(COLUMN_DESCRIPTION[i][j], Board.Nothing);
                }
            }
        }
        long result = -1;
        while (result == -1){
            result = db.insert(TABLE_NAME,null, cv);
        }
        db.close();
    }

    public void updateBoard(Board board, String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, board.Title);
        for (int i = 0; i < A_TO_H.length; i++){
            for (int j = 0; j < ONE_TO_EIGHT.length; j++){
                Cell cell = board.The_Grid[i][j];
                if (cell.Is_there_Piece){
                    cv.put(COLUMN_DESCRIPTION[i][j], cell.Color_piece + " " + cell.Type);
                }
                else{
                    cv.put(COLUMN_DESCRIPTION[i][j], Board.Nothing);
                }
            }
        }

        db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        db.close();
    }

    public Stack<Board> readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Stack<Board> allBoards = new Stack<>();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Board board = new Board(A_TO_H.length, null, null, true);
                for (int i = 0; i < A_TO_H.length; i++) {
                    for (int j = 0; j < ONE_TO_EIGHT.length; j++) {
                        String cell = cursor.getString(i * A_TO_H.length + j + 1);
                        if (cell.equals(Board.Nothing)) {
                            board.The_Grid[i][j] = new Cell(i, j, false, "", "");
                        } else {
                            board.The_Grid[i][j] = new Cell(i, j, true, cell.split(" ")[0], cell.split(" ")[1]);
                        }

                    }
                }
                board.Title = cursor.getString(A_TO_H.length * ONE_TO_EIGHT.length + 1);
                allBoards.push(new Board(board));
                System.out.println("Board ID = " + cursor.getPosition() + " in A1: " + board.The_Grid[0][0].Type);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allBoards;
    }

    public void deleteBoard(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        System.out.println("id = " + row_id);
//        db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        db.execSQL(" DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=\"" + row_id + "\";");
        db.close();

        Stack<Board> data = readAllData();
        deleteAllData();
        int i = 0;
        while (!data.empty()){
            addBoard(data.pop(), Integer.toString(i));
            i++;
        }
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }
}