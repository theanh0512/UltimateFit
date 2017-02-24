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

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.WorkoutColumns;
import ultimate.fit.ultimatefit.utils.ViewHolderUtil;

/**
 * Created by Pham on 18/2/2017.
 */

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {
    private static final String LOG_TAG = WorkoutAdapter.class.getSimpleName();
    private final Cursor cursor;
    private Context context;
    private ViewHolderUtil.SetOnClickListener listener;

    public WorkoutAdapter(Cursor cursor) {
        this.cursor = cursor;
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
        holder.textViewWorkoutDate.setText(String.format(Locale.ENGLISH, "%s", cursor.getString(cursor.getColumnIndex(WorkoutColumns.DAY_NUMBER))
                + " - " + cursor.getString(cursor.getColumnIndex(WorkoutColumns.DAY_NUMBER))));
        holder.textViewWorkoutBodyPart.setText(String.format(Locale.ENGLISH, "%s", cursor.getString(cursor.getColumnIndex(WorkoutColumns.DAY_NUMBER))));
        DateTime today = new DateTime();
        //DateTime appliedDate = new DateTime(cursor.getLong(cursor.getColumnIndex(WorkoutColumns.APPLIED_DATE)));
//        int isToday = DateTimeComparator.getDateOnlyInstance().compare(today, appliedDate);
//        if (isToday == 0) holder.imageViewOnGoingCheck.setVisibility(View.VISIBLE);
//        else holder.imageViewOnGoingCheck.setVisibility(View.INVISIBLE);
        holder.setItemClickListener(listener);
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setOnClickListener(ViewHolderUtil.SetOnClickListener clickListener) {
        this.listener = clickListener;
    }

    public interface SetOnClickListener extends ViewHolderUtil.SetOnClickListener {
        void onItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageViewOnGoingCheck)
        ImageView imageViewOnGoingCheck;
        @BindView(R.id.textViewWorkoutDate)
        TextView textViewWorkoutDate;
        @BindView(R.id.textViewWorkoutBodyPart)
        TextView textViewWorkoutBodyPart;
        private ViewHolderUtil.SetOnClickListener listener;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

        void setItemClickListener(ViewHolderUtil.SetOnClickListener itemClickListener) {
            this.listener = itemClickListener;
        }

    }
}
