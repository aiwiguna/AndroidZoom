package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.YearMonth;
import org.threeten.bp.temporal.WeekFields;

import java.util.Locale;

interface CalendarListener {
    void onSelected(View view);
}

class DayContainer extends ViewContainer implements View.OnClickListener {

    TextView textView;
    CalendarListener listener;

    public DayContainer(View view, CalendarListener listener) {
        super(view);
        this.listener = listener;
        textView = view.findViewById(R.id.calendarDayText);

        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        listener.onSelected(view);
    }
}

public class MainActivity extends AppCompatActivity implements CalendarListener {

    View layout, selectedView;
    CalendarView calendarView;
    boolean isZoomed = false;
    float targetX = 0, targetY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.layout);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setDayBinder(new DayBinder<DayContainer>() {
            @Override
            public DayContainer create(View view) {
                return new DayContainer(view, MainActivity.this);
            }

            @Override
            public void bind(DayContainer viewContainer, CalendarDay calendarDay) {
                viewContainer.textView.setText(calendarDay.getDate().getDayOfMonth() + "");
            }
        });

        YearMonth currentMonth = YearMonth.now();
        YearMonth firstMonth = currentMonth.minusMonths(10);
        YearMonth lastMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);
    }

    @Override
    public void onSelected(View view) {
        if (!isZoomed) {
            selectedView = view;

            int xLocation = getRelativeLeft(view);
            int yLocation = getRelativeTop(view);

            targetX = (float)xLocation + (float)view.getWidth()/2;
            targetY = (float)yLocation + (float)view.getHeight()/2;
            animate();
        }
    }

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == calendarView)
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == calendarView)
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    @Override
    public void onBackPressed() {

        if (isZoomed) {
            animate();
        }else{
            super.onBackPressed();
        }
    }

    void animate() {
        float layoutWidth = layout.getWidth();
        float layoutHeight = layout.getHeight();

        calendarView.setPivotX(targetX);
        calendarView.setPivotY(targetY);

        float difX = layoutWidth/2 - targetX;
        float difY = layoutHeight/2 - targetY;
        if (isZoomed) {
            calendarView.animate().x(0).y(0).scaleY(1).scaleX(1).setDuration(1000);
        } else {
            calendarView.animate().translationX(difX).translationY(difY).scaleY(5).scaleX(5).setDuration(1000);
        }
        isZoomed = !isZoomed;
    }

}