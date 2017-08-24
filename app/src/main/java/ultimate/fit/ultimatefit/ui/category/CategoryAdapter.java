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
import ultimate.fit.ultimatefit.databinding.ListItemCategoryBinding;
import ultimate.fit.ultimatefit.entity.CategoryApiResponse;

/**
 * Created by Pham on 11/8/17.
 */

public class CategoryAdapter extends DataBoundListAdapter<CategoryApiResponse, ListItemCategoryBinding> {
    private final CategoryClickCallback categoryClickCallback;

    public CategoryAdapter(CategoryClickCallback categoryClickCallback) {
        this.categoryClickCallback = categoryClickCallback;
    }

    @Override
    protected ListItemCategoryBinding createBinding(ViewGroup parent) {
        ListItemCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_category, parent, false);
        //Todo: binding.getRoot().setOnClickListener and implement call back interface
        binding.getRoot().setOnClickListener(v -> {
            CategoryApiResponse category = binding.getCategory();
            if (category != null && categoryClickCallback != null) {
                categoryClickCallback.onClick(category);
            }
        });
        return binding;
    }

    @Override
    protected void bind(ListItemCategoryBinding binding, CategoryApiResponse item) {
        binding.setCategory(item);
        String originalImagePath = item.category.image;
        CharSequence http = "http://";
        Context context = binding.getRoot().getContext();
        final String imagePath = originalImagePath.contains(http) ? originalImagePath.replace("http://", "https://") : originalImagePath;
        try {
            Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(binding.imageViewCategoryImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(imagePath)
                            .into(binding.imageViewCategoryImage, new Callback() {
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
    protected boolean areItemsTheSame(CategoryApiResponse oldItem, CategoryApiResponse newItem) {
        return Objects.equals(oldItem.category.name, newItem.category.name) &&
                Objects.equals(oldItem.category.image, newItem.category.image);
    }

    @Override
    protected boolean areContentsTheSame(CategoryApiResponse oldItem, CategoryApiResponse newItem) {
        return Objects.equals(oldItem.category.image, newItem.category.image);
    }

    public interface CategoryClickCallback {
        void onClick(CategoryApiResponse category);
    }
}
