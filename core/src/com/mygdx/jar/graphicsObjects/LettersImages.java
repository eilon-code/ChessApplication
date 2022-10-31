package com.mygdx.jar.graphicsObjects;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.jar.gameObjects.BoardObjects.Point;

public class LettersImages {
    public Texture[] BlackLetters = new Texture[27];
    public Point[] Sizes = new Point[27];

    public LettersImages()
    {
        for (int i = 0; i < BlackLetters.length; i++){
            String num = String.valueOf(i + 1);
            BlackLetters[i] = new Texture("core/images/letters/black_" + num + ".png");
        }
        Sizes[0] = new Point(40, 48);
        Sizes[1] = new Point(41, 48);
        Sizes[2] = new Point(29, 47);
        Sizes[3] = new Point(36, 48);
        Sizes[4] = new Point(41, 48);
        Sizes[5] = new Point(8, 48);
        Sizes[6] = new Point(22, 48);
        Sizes[7] = new Point(41, 48);
        Sizes[8] = new Point(45, 50);
        Sizes[9] = new Point(8, 48);
        Sizes[10] = new Point(36, 50);
        Sizes[11] = new Point(37, 66);
        Sizes[12] = new Point(43, 50);
        Sizes[13] = new Point(22, 49);
        Sizes[14] = new Point(44, 50);
        Sizes[15] = new Point(38, 52);
        Sizes[16] = new Point(41, 50);
        Sizes[17] = new Point(39, 48);
        Sizes[18] = new Point(46, 69);
        Sizes[19] = new Point(37, 49);
        Sizes[20] = new Point(55, 48);
        Sizes[21] = new Point(50, 50);
        Sizes[22] = new Point(42, 49);
        Sizes[23] = new Point(8, 68);
        Sizes[24] = new Point(37, 69);
        Sizes[25] = new Point(39, 69);
        Sizes[26] = new Point(40, 69);
    }
}
