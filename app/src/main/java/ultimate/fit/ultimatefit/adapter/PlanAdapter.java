package ultimate.fit.ultimatefit.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.activity.MainActivity;
import ultimate.fit.ultimatefit.data.PlanColumns;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.fragment.TabWorkoutFragment;
import ultimate.fit.ultimatefit.utils.SharedPreferenceHelper;

/**
 * Created by Pham on 18/2/2017.
 */

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
    private static final String LOG_TAG = PlanAdapter.class.getSimpleName();
    public static int currentAppliedPlanID = 0;
    private final Context context;
    final private PlanAdapterOnClickHandler clickHandler;
    private Cursor cursor;

    public PlanAdapter(Context context, PlanAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_plan, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("NOT BOUND TO RECYCLER_VIEW");
        }
    }

    @Override
    public void onBindViewHolder(final PlanAdapter.ViewHolder holder, int position) {
        final int pos = position;
        cursor.moveToPosition(position);
        holder.textViewPlanName.setText(String.format(Locale.ENGLISH, "%s", cursor.getString(cursor.getColumnIndex(PlanColumns.NAME))));
        int numOfWeeks = cursor.getInt(cursor.getColumnIndex(PlanColumns.NUM_OF_WEEK));
        holder.textViewPlanNumOfWeeks.setText(String.format(Locale.ENGLISH, "%s", numOfWeeks) + " week" + (numOfWeeks == 1 ? "" : "s"));
        //ToDo: if cannot click button, add button to the plan detail instead
        if (cursor.getInt(cursor.getColumnIndex(PlanColumns.ID)) == currentAppliedPlanID) {
            holder.buttonApplyPlan.setVisibility(View.INVISIBLE);
            holder.imageViewOnGoingCheck.setVisibility(View.VISIBLE);
        } else {
            holder.buttonApplyPlan.setVisibility(View.VISIBLE);
            holder.imageViewOnGoingCheck.setVisibility(View.INVISIBLE);
        }
        holder.buttonApplyPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(pos);
                holder.buttonApplyPlan.setVisibility(View.INVISIBLE);
                currentAppliedPlanID = cursor.getInt(cursor.getColumnIndex(PlanColumns.ID));
                SharedPreferenceHelper.getInstance().put(SharedPreferenceHelper.Key.CURRENT_APPLIED_PLANID_INT, currentAppliedPlanID);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues cv = new ContentValues();
                        cv.put(PlanColumns.APPLIED_DATE, new DateTime().getMillis());
                        context.getContentResolver().update(UltimateFitProvider.Plans.withId(currentAppliedPlanID), cv, null, null);
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TabWorkoutFragment tabWorkoutFragment = (TabWorkoutFragment) MainActivity.adapter.getRegisteredFragment(0);
                if (tabWorkoutFragment != null) {
                    tabWorkoutFragment.getLoaderManager().restartLoader(2000, null, tabWorkoutFragment);
                }
                holder.imageViewOnGoingCheck.setVisibility(View.VISIBLE);
            }
        });
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

    public interface PlanAdapterOnClickHandler {
        void onClick(int planId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageViewOnGoingCheck)
        ImageView imageViewOnGoingCheck;
        @BindView(R.id.textViewPlanName)
        TextView textViewPlanName;
        @BindView(R.id.textViewPlanNumOfWeeks)
        TextView textViewPlanNumOfWeeks;
        @BindView(R.id.buttonApplyPlan)
        Button buttonApplyPlan;

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
            int planId = cursor.getInt(cursor.getColumnIndex(PlanColumns.ID));
            clickHandler.onClick(planId);
        }
    }
}
