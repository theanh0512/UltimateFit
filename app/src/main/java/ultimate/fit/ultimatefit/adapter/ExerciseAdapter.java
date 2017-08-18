package ultimate.fit.ultimatefit.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Objects;

import ultimate.fit.ultimatefit.R;
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
