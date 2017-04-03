package ultimate.fit.ultimatefit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.model.Exercise;

/**
 * Created by Pham on 18/2/2017.
 */

public class ExerciseArrayListAdapter extends RecyclerView.Adapter<ExerciseArrayListAdapter.ViewHolder> {
    private static final String LOG_TAG = ExerciseArrayListAdapter.class.getSimpleName();
    private final Context context;
    final private ExerciseArrayListAdapterOnClickHandler clickHandler;
    private ArrayList<Exercise> arrayListExercise;

    public ExerciseArrayListAdapter(Context context, ExerciseArrayListAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_array_list_exercise, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("NOT BOUND TO RECYCLER_VIEW");
        }
    }

    @Override
    public void onBindViewHolder(final ExerciseArrayListAdapter.ViewHolder holder, int position) {
        holder.textViewExerciseName.setText(arrayListExercise.get(position).getExerciseName());
    }

    @Override
    public int getItemCount() {
        if (arrayListExercise != null) {
            return arrayListExercise.size();
        }
        return 0;
    }

    public void swapArrayList(ArrayList<Exercise> arrayList) {
        this.arrayListExercise = arrayList;
        notifyDataSetChanged();
    }

    public interface ExerciseArrayListAdapterOnClickHandler {
        void onClick(int setId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_view_array_list_exercise_name)
        TextView textViewExerciseName;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            int exerciseId = arrayListExercise.get(position).getExerciseId();
            clickHandler.onClick(exerciseId);
        }
    }
}
