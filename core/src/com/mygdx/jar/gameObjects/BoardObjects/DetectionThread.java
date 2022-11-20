package com.mygdx.jar.gameObjects.BoardObjects;

import static com.mygdx.jar.gameObjects.BoardObjects.PositionCheck.TitlesList;

public class DetectionThread extends Thread{
    private final String mTitle;
    private final String mPreviousTitle;
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
                String previousTitle = board.title;
                board.title = Board.TitleCheck;

                PositionCheck positionChecker = new PositionCheck(board);
                if (positionChecker.IsTitleFit(title)){
                    System.out.println("Title Match");
                    board.title = Board.TitleFit;
                }
                else {
                    System.out.println("Title Doesn't Match");
                    board.title = Board.TitleNotFit;
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    board.title = previousTitle;
                }
            }
        });
        mTitle = title;
        mPreviousTitle = board.title;
        this.start();
    }

    public String getTitle(){
        return mTitle;
    }

    public String getPreviousTitle(){
        return mPreviousTitle;
    }
}
