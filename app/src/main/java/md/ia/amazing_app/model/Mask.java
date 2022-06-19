package md.ia.amazing_app.model;

import androidx.annotation.DrawableRes;

public class Mask {
    public int ph;
    @DrawableRes
    public int ph_image;

    public Mask(int ph, int ph_image) {
        this.ph = ph;
        this.ph_image = ph_image;
    }
}
