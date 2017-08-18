package ultimate.fit.ultimatefit.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Objects;

import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.databinding.ListItemCategoryBinding;
import ultimate.fit.ultimatefit.entity.Category;

/**
 * Created by Pham on 11/8/17.
 */

public class CategoryAdapter extends DataBoundListAdapter<Category, ListItemCategoryBinding> {
    private final CategoryClickCallback categoryClickCallback;

    public CategoryAdapter(CategoryClickCallback categoryClickCallback) {
        this.categoryClickCallback = categoryClickCallback;
    }

    @Override
    protected ListItemCategoryBinding createBinding(ViewGroup parent) {
        ListItemCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_category, parent, false);
        //Todo: binding.getRoot().setOnClickListener and implement call back interface
        binding.getRoot().setOnClickListener(v -> {
            Category category = binding.getCategory();
            if (category != null && categoryClickCallback != null) {
                categoryClickCallback.onClick(category);
            }
        });
        return binding;
    }

    @Override
    protected void bind(ListItemCategoryBinding binding, Category item) {
        binding.setCategory(item);
    }

    @Override
    protected boolean areItemsTheSame(Category oldItem, Category newItem) {
        return Objects.equals(oldItem.name, newItem.name) &&
                Objects.equals(oldItem.image, newItem.image);
    }

    @Override
    protected boolean areContentsTheSame(Category oldItem, Category newItem) {
        return Objects.equals(oldItem.image, newItem.image);
    }

    public interface CategoryClickCallback {
        void onClick(Category category);
    }
}
