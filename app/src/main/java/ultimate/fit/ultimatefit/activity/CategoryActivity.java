package ultimate.fit.ultimatefit.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.CategoryAdapter;
import ultimate.fit.ultimatefit.adapter.PlanAdapter;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.fragment.TabPlanFragment;

public class CategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = CategoryActivity.class.getSimpleName();
    private static final int CATEGORY_LOADER = 3;
    ItemsListClickHandler handler;
    @BindView(R.id.fab_add_category)
    FloatingActionButton fabAddCategory;
    @BindView(R.id.recyclerview_category)
    RecyclerView recyclerViewCategory;
    @BindView(R.id.toolbarCategory)
    Toolbar toolbarCategory;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarCategory);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryAdapter = new CategoryAdapter(this, new CategoryAdapter.CategoryAdapterOnClickHandler() {
            @Override
            public void onClick(int categoryId) {
                Log.i(LOG_TAG, "category ID: " + categoryId);
                handler.onHandleItemClick(categoryId);
            }
        });
        recyclerViewCategory.setAdapter(categoryAdapter);
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this));
        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
    }

    @OnClick(R.id.fab_add_category)
    public void onClickAddExercise(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, UltimateFitProvider.Categories.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        categoryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        categoryAdapter.swapCursor(null);
    }

    public interface ItemsListClickHandler {
        public void onHandleItemClick(int categoryId);
    }
}
