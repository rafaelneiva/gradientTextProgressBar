package br.com.rafaelgn.progressbargradient;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final int TOTAL_TIME = 10000;
    private int progressCount = 0;

    public MainActivityFragment() {
    }

    TimerTask totalTimerTask;
    Timer totalTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        final CustomProgressBar progressBar = (CustomProgressBar) view.findViewById(R.id.progressBar);
        final GradientTextView gradientTextView = (GradientTextView) view.findViewById(R.id.gradientTextView);

        totalTimerTask = new TimerTask() {
            @Override
            public void run() {
                progressCount++;
                if (progressCount <= 100) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setValue(progressCount);
                                progressBar.invalidate();
                            }
                        });
                } else {
                    totalTimer.cancel();
                }
            }
        };
        totalTimer = new Timer();
        totalTimer.schedule(totalTimerTask, 0, TOTAL_TIME / 100);

//        gradientTextView.setDirection(0);
//        ObjectAnimator.ofFloat(gradientTextView, "offset", 0, 1).setDuration(TOTAL_TIME)
//                .start();

        ((MainActivity) getActivity()).setOnFabClickListener(new MainActivity.OnFabClickListener() {
            @Override
            public void onFabClick() {
                progressCount = 0;
                totalTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        progressCount++;
                        if (progressCount <= 100) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setValue(progressCount);
                                    progressBar.invalidate();
                                }
                            });
                        } else {
                            totalTimer.cancel();
                        }
                    }
                };
                totalTimer = new Timer();
                totalTimer.schedule(totalTimerTask, 0, TOTAL_TIME / 100);
            }
        });

        return view;
    }
}
