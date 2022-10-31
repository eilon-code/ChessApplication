package com.mygdx.jar.gameObjects.BoardObjects;

import static com.mygdx.jar.gameObjects.BoardObjects.PositionCheck.TitlesList;

public class DetectionThread extends Thread{
    public DetectionThread(final String title, final Board board) {
        super(new Runnable() {
            @Override
            public void run() {
                boolean titleExists = false;
                for (String s : TitlesList) {
                    if (s.equals(title)) {
                        titleExists = true;
                        break;
                    }
                }
                if (!titleExists){
                    return;
                }
                System.out.println("Run");
                String previousTitle = board.Title;
                board.Title = "בודק התאמת כותרת לעמדה";

                PositionCheck positionChecker = new PositionCheck(board);
                if (positionChecker.IsTitleFit(title)){
                    System.out.println("Title Match");
                    board.Title = Position.Chess_Board.Title;
                }
                else {
                    System.out.println("Title Doesn't Match");
                    board.Title = Board.TitleNotFit;
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    board.Title = previousTitle;
                }
            }
        });
        this.start();
    }
}
