package ultimate.fit.ultimatefit.adapter;

/**
 * Created by Pham on 18/2/2017.
 */

//public class CategoryAdapter1 extends RecyclerView.Adapter<CategoryAdapter1.ViewHolder> {
//    private static final String LOG_TAG = CategoryAdapter1.class.getSimpleName();
//    final private CategoryAdapterOnClickHandler clickHandler;
//    List<Category> categories;
//    private Cursor cursor;
//    private Context context;
//
//    public CategoryAdapter1(Context context, CategoryAdapterOnClickHandler clickHandler) {
//        this.context = context;
//        this.clickHandler = clickHandler;
//    }
//
//    @Override
//    public CategoryAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//        ListItemCategoryBinding listItemCategoryBinding = ListItemCategoryBinding.inflate(inflater, parent, false);
//        return new ViewHolder(listItemCategoryBinding);
//    }
//
//    @Override
//    public void onBindViewHolder(final CategoryAdapter1.ViewHolder holder, int position) {
//        Category category = categories.get(position);
//        holder.bind(category);
//        cursor.moveToPosition(position);
//        String categoryName = cursor.getString(cursor.getColumnIndex(CategoryColumns.CATEGORY_NAME));
//        holder.binding.textViewCategoryName.setText(String.format(Locale.ENGLISH, "%s", categoryName));
//        String originalImagePath = cursor.getString(cursor.getColumnIndex(CategoryColumns.IMAGE_PATH));
//        CharSequence http = "http://";
//        final String imagePath = originalImagePath.contains(http) ? originalImagePath.replace("http://", "https://") : originalImagePath;
//        try {
//            Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_place_holder)
//                    .error(R.drawable.ic_error_fallback).into(holder.binding.imageViewCategoryImage, new Callback() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onError() {
//                    Picasso.with(context)
//                            .load(imagePath)
//                            .into(holder.binding.imageViewCategoryImage, new Callback() {
//                                @Override
//                                public void onSuccess() {
//
//                                }
//
//                                @Override
//                                public void onError() {
////                                        Log.v("Picasso","Could not fetch image");
//                                }
//                            });
//                }
//            });
//        } catch (IndexOutOfBoundsException e) {
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        if (cursor != null) {
//            return cursor.getCount();
//        }
//        return 0;
//    }
//
//    public void swapCursor(Cursor newCursor) {
//        cursor = newCursor;
//        notifyDataSetChanged();
//    }
//
//    public interface CategoryAdapterOnClickHandler {
//        void onClick(int categoryPk);
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        private final ListItemCategoryBinding binding;
//
//        ViewHolder(ListItemCategoryBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//            ButterKnife.bind(this, itemView);
//            itemView.setClickable(true);
//            itemView.setOnClickListener(this);
//        }
//
//        public void bind(Category category) {
//            binding.setCategory(category);
//            binding.executePendingBindings();
//        }
//
//        @Override
//        public void onClick(View view) {
//            int position = getAdapterPosition();
//            cursor.moveToPosition(position);
//            int categoryPk = cursor.getInt(0);
//            clickHandler.onClick(categoryPk);
//        }
//
//    }
//}
