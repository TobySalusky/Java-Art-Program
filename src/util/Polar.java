package util;

public class Polar extends Vector {

    public Polar(float mag, float angle) {

        super((float) Math.cos(angle) * mag, (float) Math.sin(angle) * mag);

    }

}
