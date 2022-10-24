package com.mygdx.jar.gameObjects.BoardObjects;

import java.util.Objects;

public class Point {
    public int X;
    public int Y;
    public Point(int x, int y){
        X = x;
        Y = y;
    }

    public Point() {
    }

    public Point(Point original_point) {
        X = original_point.X;
        Y = original_point.Y;
    }

    public Point(Cell original_cell) {
        X = original_cell.Row_Number;
        Y = original_cell.Column_Number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;
        Point point = (Point) o;
        return X == point.X &&
                Y == point.Y;
    }
}
