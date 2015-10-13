package br.com.rafaelgn.progressbargradient;

/**
 * Created by rafaelneiva on 5/25/15.
 */
public interface ValueBarSelectionListener {

    /**
     * Called every time the user moves the finger on the ValueBar.
     *
     * @param val
     * @param maxval
     * @param minval
     * @param bar
     */
    public void onSelectionUpdate(float val, float maxval, float minval, CustomProgressBar bar);

    /**
     * Called when the user releases his finger from the ValueBar.
     *
     * @param val
     * @param maxval
     * @param minval
     * @param bar
     */
    public void onValueSelected(float val, float maxval, float minval, CustomProgressBar bar);
}
