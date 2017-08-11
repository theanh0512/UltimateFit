package ultimate.fit.ultimatefit.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.CategoryColumns;
import ultimate.fit.ultimatefit.databinding.ListItemCategoryBinding;
import ultimate.fit.ultimatefit.entity.Category;

/**
 * Created by Pham on 18/2/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static final String LOG_TAG = CategoryAdapter.class.getSimpleName();
    final private CategoryAdapterOnClickHandler clickHandler;
    List<Category> categories;
    private Cursor cursor;
    private Context context;

    public CategoryAdapter(Context context, CategoryAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ListItemCategoryBinding listItemCategoryBinding = ListItemCategoryBinding.inflate(inflater, parent, false);
        return new ViewHolder(listItemCategoryBinding);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
        cursor.moveToPosition(position);
        String categoryName = cursor.getString(cursor.getColumnIndex(CategoryColumns.CATEGORY_NAME));
        holder.binding.textViewCategoryName.setText(String.format(Locale.ENGLISH, "%s", categoryName));
        String originalImagePath = cursor.getString(cursor.getColumnIndex(CategoryColumns.IMAGE_PATH));
        CharSequence http = "http://";
        final String imagePath = originalImagePath.contains(http) ? originalImagePath.replace("http://", "https://") : originalImagePath;
        try {
            Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(holder.binding.imageViewCategoryImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(imagePath)
                            .into(holder.binding.imageViewCategoryImage, new Callback() {
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

    public interface CategoryAdapterOnClickHandler {
        void onClick(int categoryId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ListItemCategoryBinding binding;

        ViewHolder(ListItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        public void bind(Category category) {
            binding.setCategory(category);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            int categoryId = cursor.getInt(0);
            clickHandler.onClick(categoryId);
        }

    }
}
