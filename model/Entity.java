package model;

public class Entity {
    public String n;
    public double x;
    public double y;
    public int original_x;
    public int original_y;
    public boolean selected;

    public Entity(String name, double posX, double posY) {
        n = name;
        x = posX;
        y = posY;
        original_x = (int) posX;
        original_y = (int) posY;
        selected = false;
    }

    public String getName() {
        return n;
    }

    public void setName(String new_name) {
        n = new_name;
    }

    public double getX() {
        return x;
    }

    public void setX(double newX) {
        x = newX;
    }

    public double getY() {
        return y;
    }

    public void setY(double newY) {
        y = newY;
    }

    public int get_originalX() { return original_x; }

    public void setOriginal_x(double newX) { original_x = (int)newX; }

    public int get_originalY() { return original_y; }

    public void setOriginal_y(double newY) { original_y = (int)newY; }
}
