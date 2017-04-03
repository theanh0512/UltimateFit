package ultimate.fit.ultimatefit.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.ExerciseColumns;

/**
 * Created by Pham on 18/2/2017.
 */

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {
    private static final String LOG_TAG = ExerciseAdapter.class.getSimpleName();
    final private ExerciseAdapterOnClickHandler clickHandler;
    private Cursor cursor;
    private Context context;

    public ExerciseAdapter(Context context, ExerciseAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public ExerciseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_exercise, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ExerciseAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String exerciseName = cursor.getString(cursor.getColumnIndex(ExerciseColumns.EXERCISE_NAME));
        holder.textViewExerciseName.setText(String.format(Locale.ENGLISH, "%s", exerciseName));
        String originalImagePath = cursor.getString(cursor.getColumnIndex(ExerciseColumns.IMAGE_PATH));
        CharSequence http = "http://";
        final String imagePath = originalImagePath.contains(http) ? originalImagePath.replace("http://", "https://") : originalImagePath;
        try {
            Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(holder.imageViewExerciseImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(imagePath)
                            .into(holder.imageViewExerciseImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
//                                        Log.v("Picasso","Could not fetch image");
                                }
                            });
                }
            });
        } catch (IndexOutOfBoundsException e) {
        }
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

    public interface ExerciseAdapterOnClickHandler {
        void onClick(int exerciseId, String exerciseImagePath, String exerciseName);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageViewExerciseImage)
        ImageView imageViewExerciseImage;
        @BindView(R.id.textViewExerciseName)
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
            cursor.moveToPosition(position);
            int exerciseId = cursor.getInt(0);
            String exerciseImagePath = cursor.getString(cursor.getColumnIndex(ExerciseColumns.IMAGE_PATH));
            String exerciseName = cursor.getString(cursor.getColumnIndex(ExerciseColumns.EXERCISE_NAME));
            clickHandler.onClick(exerciseId, exerciseImagePath, exerciseName);
        }

    }
}
