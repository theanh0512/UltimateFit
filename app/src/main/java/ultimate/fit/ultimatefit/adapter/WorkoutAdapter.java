package ultimate.fit.ultimatefit.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.PlanColumns;
import ultimate.fit.ultimatefit.data.WorkoutColumns;

/**
 * Created by Pham on 18/2/2017.
 */

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {
    private static final String LOG_TAG = WorkoutAdapter.class.getSimpleName();
    final private WorkoutAdapter.WorkoutAdapterOnClickHandler clickHandler;
    private Cursor cursor;
    private Context context;

    public WorkoutAdapter(Context context, WorkoutAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public WorkoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_workout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkoutAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        int dayNumber = cursor.getInt(cursor.getColumnIndex(WorkoutColumns.DAY_NUMBER));
        holder.textViewWorkoutDate.setText(String.format(Locale.ENGLISH, "%s", "Day: " + dayNumber));
        holder.textViewWorkoutBodyPart.setText(String.format(Locale.ENGLISH, "%s", cursor.getString(cursor.getColumnIndex(WorkoutColumns.BODY_PART))));
        DateTime today = new DateTime();
        DateTime relativeDate = new DateTime(cursor.getLong(cursor.getColumnIndex(PlanColumns.APPLIED_DATE))).plusDays(dayNumber - 1);
        int isToday = DateTimeComparator.getDateOnlyInstance().compare(today, relativeDate);
        if (isToday == 0) {
            holder.imageViewOnGoingCheck.setVisibility(View.VISIBLE);
        }
        else holder.imageViewOnGoingCheck.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public interface WorkoutAdapterOnClickHandler {
        void onClick(int workoutId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageViewOnGoingCheck)
        ImageView imageViewOnGoingCheck;
        @BindView(R.id.textViewWorkoutDate)
        TextView textViewWorkoutDate;
        @BindView(R.id.textViewWorkoutBodyPart)
        TextView textViewWorkoutBodyPart;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            int workoutId = cursor.getInt(cursor.getColumnIndex(WorkoutColumns.ID));
            clickHandler.onClick(workoutId);
        }

    }
}
