package model;

public class Arrow {
    Entity f;
    Entity t;

    public Arrow(Entity from, Entity to) {
        f = from;
        t = to;
    }

    public String getFName() {
        return f.getName();
    }

    public String getTName() {
        return t.getName();
    }

    public double getFPosX() {
        return f.getX();
    }

    public double getFPosY() {
        return f.getY();
    }

    public double getTPosX() {
        return t.getX();
    }

    public double getTPosY() {
        return t.getY();
    }

}
