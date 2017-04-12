package ultimate.fit.ultimatefit.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.SetColumns;
import ultimate.fit.ultimatefit.data.UltimateFitDatabase;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.generated.values.SetsValuesBuilder;

/**
 * Created by Pham on 18/2/2017.
 */

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {
    private static final String LOG_TAG = SetAdapter.class.getSimpleName();
    final private SetAdapterOnClickHandler clickHandler;
    private Cursor cursor;
    private Context context;

    public SetAdapter(Context context, SetAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public SetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_set, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SetAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String exerciseName = cursor.getString(cursor.getColumnIndex(SetColumns.SET_NAME));
        holder.textViewSetName.setText(String.format(Locale.ENGLISH, "%s", exerciseName));
        int weight = cursor.getInt(cursor.getColumnIndex(SetColumns.WEIGHT));
        int rep = cursor.getInt(cursor.getColumnIndex(SetColumns.REP));
        holder.editTextRep.setText(String.valueOf(rep));
        holder.editTextWeight.setText(String.valueOf(weight));
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

    public interface SetAdapterOnClickHandler {
        void onClick(int exerciseId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_view_set_name)
        TextView textViewSetName;
        @BindView(R.id.edit_text_set_rep)
        EditText editTextRep;
        @BindView(R.id.edit_text_set_weight)
        EditText editTextWeight;

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
            int exerciseId = cursor.getInt(cursor.getColumnIndex(SetColumns.EXERCISE_ID));
            clickHandler.onClick(exerciseId);
        }

        @OnEditorAction(R.id.edit_text_set_rep)
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            final int setId = cursor.getInt(0);
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (!event.isShiftPressed()) {
                    // the user is done typing.

                    //final Context context = context;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContentValues contentValues = new SetsValuesBuilder().rep(Integer.valueOf(editTextRep.getText().toString())).values();
                            context.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI,
                                    contentValues, UltimateFitDatabase.Tables.SETS + "." + SetColumns.ID + "=" + setId, null);
                        }
                    }).start();
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.

        }

        @OnEditorAction(R.id.edit_text_set_weight)
        public boolean onEditorActionSet(TextView v, int actionId, KeyEvent event) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            final int setId = cursor.getInt(0);
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (!event.isShiftPressed()) {
                    // the user is done typing.

                    //final Context context = context;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContentValues contentValues = new SetsValuesBuilder().weight(Integer.valueOf(editTextWeight.getText().toString())).values();
                            context.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI,
                                    contentValues, UltimateFitDatabase.Tables.SETS + "." + SetColumns.ID + "=" + setId, null);
                        }
                    }).start();
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.

        }
    }
}
