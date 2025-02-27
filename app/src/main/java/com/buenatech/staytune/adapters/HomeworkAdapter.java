package com.buenatech.staytune.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;

import com.buenatech.staytune.R;
import com.buenatech.staytune.model.Homework;
import com.buenatech.staytune.utils.AlertDialogsHelper;
import com.buenatech.staytune.utils.ColorPalette;
import com.buenatech.staytune.utils.DbHelper;
import com.buenatech.staytune.utils.PreferenceUtil;
import com.buenatech.staytune.utils.WeekUtils;

import java.util.ArrayList;
import java.util.Objects;


public class HomeworkAdapter extends ArrayAdapter<Homework> {

    @NonNull
    private final AppCompatActivity mActivity;
    private final DbHelper dbHelper;
    @NonNull
    private final ArrayList<Homework> homeworklist;
    private Homework homework;
    private final ListView mListView;

    private static class ViewHolder {
        TextView subject;
        TextView description;
        TextView date;
        CardView cardView;
        ImageView popup;
    }

    public HomeworkAdapter(DbHelper dbHelper, @NonNull AppCompatActivity activity, ListView listView, int resource, @NonNull ArrayList<Homework> objects) {
        super(activity, resource, objects);
        this.dbHelper = dbHelper;
        mActivity = activity;
        mListView = listView;
        homeworklist = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String subject = Objects.requireNonNull(getItem(position)).getSubject();
        String description = Objects.requireNonNull(getItem(position)).getDescription();
        String date = Objects.requireNonNull(getItem(position)).getDate();
        int color = Objects.requireNonNull(getItem(position)).getColor();
        homework = new Homework(subject, description, date, color);
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            convertView = inflater.inflate(R.layout.listview_homeworks_adapter, parent, false);
            holder = new ViewHolder();
            holder.subject = convertView.findViewById(R.id.subjecthomework);
            holder.description = convertView.findViewById(R.id.descriptionhomework);
            holder.date = convertView.findViewById(R.id.datehomework);
            holder.cardView = convertView.findViewById(R.id.homeworks_cardview);
            holder.popup = convertView.findViewById(R.id.popupbtn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Setup colors based on Background
        int textColor = ColorPalette.pickTextColorBasedOnBgColorSimple(color, Color.WHITE, Color.BLACK);
        holder.subject.setTextColor(textColor);
        holder.description.setTextColor(textColor);
        holder.date.setTextColor(textColor);
        ImageViewCompat.setImageTintList(convertView.findViewById(R.id.timeimage), ColorStateList.valueOf(textColor));
        ImageViewCompat.setImageTintList(convertView.findViewById(R.id.popupbtn), ColorStateList.valueOf(textColor));
        convertView.findViewById(R.id.line).setBackgroundColor(textColor);


        holder.subject.setText(homework.getSubject());
        holder.description.setText(homework.getDescription());
        holder.date.setText(WeekUtils.localizeDate(getContext(), homework.getDate()));
        holder.cardView.setCardBackgroundColor(homework.getColor());
        holder.popup.setOnClickListener(v -> {
            ContextThemeWrapper theme = new ContextThemeWrapper(mActivity, PreferenceUtil.isDark(getContext()) ? R.style.Widget_AppCompat_PopupMenu : R.style.Widget_AppCompat_Light_PopupMenu);
            final PopupMenu popup = new PopupMenu(theme, holder.popup);
            popup.inflate(R.menu.popup_menu);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.delete_popup) {
                        dbHelper.deleteHomeworkById(Objects.requireNonNull(getItem(position)));
                        dbHelper.updateHomework(Objects.requireNonNull(getItem(position)));
                        homeworklist.remove(position);

                        notifyDataSetChanged();
                        return true;
                    } else if (itemId == R.id.edit_popup) {
                        final View alertLayout = mActivity.getLayoutInflater().inflate(R.layout.dialog_add_homework, null);
                        AlertDialogsHelper.getEditHomeworkDialog(dbHelper, mActivity, alertLayout, homeworklist, mListView, position);
                        notifyDataSetChanged();
                        return true;
                    }
                    return onMenuItemClick(item);
                }
            });
            popup.show();
        });

        hidePopUpMenu(holder);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    public ArrayList<Homework> getHomeworkList() {
        return homeworklist;
    }

    public Homework getHomework() {
        return homework;
    }

    private void hidePopUpMenu(@NonNull ViewHolder holder) {
        SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
        if (checkedItems.size() > 0) {
            for (int i = 0; i < checkedItems.size(); i++) {
                int key = checkedItems.keyAt(i);
                if (checkedItems.get(key)) {
                    holder.popup.setVisibility(View.INVISIBLE);
                }
            }
        } else {
            holder.popup.setVisibility(View.VISIBLE);
        }
    }
}

