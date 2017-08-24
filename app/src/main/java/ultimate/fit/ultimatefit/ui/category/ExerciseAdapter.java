package ultimate.fit.ultimatefit.ui.category;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.DataBoundListAdapter;
import ultimate.fit.ultimatefit.databinding.ListItemExerciseBinding;
import ultimate.fit.ultimatefit.entity.Exercise;

/**
 * Created by Pham on 11/8/17.
 */

public class ExerciseAdapter extends DataBoundListAdapter<Exercise, ListItemExerciseBinding> {
    private final ExerciseClickCallback exerciseClickCallback;

    public ExerciseAdapter(ExerciseClickCallback exerciseClickCallback) {
        this.exerciseClickCallback = exerciseClickCallback;
    }

    @Override
    protected ListItemExerciseBinding createBinding(ViewGroup parent) {
        ListItemExerciseBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_exercise, parent, false);
        //Todo: binding.getRoot().setOnClickListener and implement call back interface
        binding.getRoot().setOnClickListener(v -> {
            Exercise exercise = binding.getExercise();
            if (exercise != null && exerciseClickCallback != null) {
                exerciseClickCallback.onClick(exercise);
            }
        });
        return binding;
    }

    @Override
    protected void bind(ListItemExerciseBinding binding, Exercise item) {
        binding.setExercise(item);
        String originalImagePath = item.image;
        CharSequence http = "http://";
        Context context = binding.getRoot().getContext();
        final String imagePath = originalImagePath.contains(http) ? originalImagePath.replace("http://", "https://") : originalImagePath;
        try {
            Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(binding.imageViewExerciseImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(imagePath)
                            .into(binding.imageViewExerciseImage, new Callback() {
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
    protected boolean areItemsTheSame(Exercise oldItem, Exercise newItem) {
        return Objects.equals(oldItem.name, newItem.name) &&
                Objects.equals(oldItem.image, newItem.image);
    }

    @Override
    protected boolean areContentsTheSame(Exercise oldItem, Exercise newItem) {
        return Objects.equals(oldItem.image, newItem.image);
    }

    public interface ExerciseClickCallback {
        void onClick(Exercise exercise);
    }
}
