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
import ultimate.fit.ultimatefit.data.CategoryColumns;

/**
 * Created by Pham on 18/2/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static final String LOG_TAG = CategoryAdapter.class.getSimpleName();
    final private CategoryAdapterOnClickHandler clickHandler;
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

        View view = inflater.inflate(R.layout.list_item_category, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String categoryName = cursor.getString(cursor.getColumnIndex(CategoryColumns.CATEGORY_NAME));
        holder.textViewCategoryName.setText(String.format(Locale.ENGLISH, "%s", categoryName));
        final String imagePath = cursor.getString(cursor.getColumnIndex(CategoryColumns.IMAGE_PATH));
        try {
            Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(holder.imageViewCategoryImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(imagePath)
                            .into(holder.imageViewCategoryImage, new Callback() {
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

        @BindView(R.id.imageViewCategoryImage)
        ImageView imageViewCategoryImage;
        @BindView(R.id.textViewCategoryName)
        TextView textViewCategoryName;

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
            int categoryId = cursor.getInt(0);
            clickHandler.onClick(categoryId);
        }

    }
}
