package ir.android.persiantask.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Category;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.ViewHolder> {

    public static final DiffUtil.ItemCallback DIFF_CALLBACK = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getCategory_id() == newItem.getCategory_id();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getCategory_title().equals(newItem.getCategory_title());
        }
    };

    public CategoryAdapter(){
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.category_item_recyclerview, parent, false);
        CategoryAdapter.ViewHolder viewHolder = new CategoryAdapter.ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = getItem(position);
        holder.categoryTitle.setText(category.getCategory_title());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView categoryTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_title);
        }
    }
}
