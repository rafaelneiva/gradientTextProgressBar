package br.com.rafaelgn.progressbargradient;

/**
 * Created by rafaelneiva on 5/25/15.
 */
public interface BarColorFormatter {

    /**
     * Use this method to return whatever color you like the ValueBar to have.
     * You can also make use of the current value the bar has.
     *
     * @param value
     * @param maxVal the maximum value the bar can display
     * @param minVal the minimum value the bar can display
     * @return
     */
    public int getColor(float value, float maxVal, float minVal);
}
