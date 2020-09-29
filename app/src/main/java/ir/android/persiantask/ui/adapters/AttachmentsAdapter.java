package ir.android.persiantask.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Attachments;
import ir.android.persiantask.viewmodels.AttachmentsViewModel;

public class AttachmentsAdapter extends ListAdapter<Attachments, AttachmentsAdapter.ViewHolderItem> {

    private AttachmentsViewModel attachmentsViewModel;
    private FragmentActivity mFragmentActivity;

    public static final DiffUtil.ItemCallback DIFF_CALLBACK = new DiffUtil.ItemCallback<Attachments>() {
        @Override
        public boolean areItemsTheSame(@NonNull Attachments oldItem, @NonNull Attachments newItem) {
            return oldItem.getAttachments_id() == newItem.getAttachments_id();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Attachments oldItem, @NonNull Attachments newItem) {
            return oldItem.getAttachments_path().equals(newItem.getAttachments_path());
        }
    };

    public AttachmentsAdapter(AttachmentsViewModel attachmentsViewModel, FragmentActivity fragmentActivity) {
        super(DIFF_CALLBACK);
        this.attachmentsViewModel = attachmentsViewModel;
        this.mFragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public AttachmentsAdapter.ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View attachmentView = inflater.inflate(R.layout.attachments_item_recyclerview, parent, false);
        AttachmentsAdapter.ViewHolderItem viewHolder = new AttachmentsAdapter.ViewHolderItem(attachmentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentsAdapter.ViewHolderItem holder, int position) {
        Attachments attachments = getItem(position);
        File imgFile = new File(attachments.getAttachments_path());
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        holder.attachemntImage.setImageBitmap(bitmap);
        holder.attachmentDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAttachmentEvent(attachments);
            }
        });
    }

    private void deleteAttachmentEvent(Attachments attachments) {
        Attachments tempAttachment = attachments;
        attachmentsViewModel.delete(attachments);
        Snackbar snackbar = Snackbar
                .make(mFragmentActivity.getWindow().getDecorView().findViewById(android.R.id.content), mFragmentActivity.getString(R.string.successDeleteAttachments), Snackbar.LENGTH_LONG);
        snackbar.setAction(mFragmentActivity.getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachmentsViewModel.insert(tempAttachment);
            }
        }).show();
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public ImageView attachemntImage;
        public AppCompatImageButton attachmentDeleteIcon;

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            attachemntImage = itemView.findViewById(R.id.attachemntImage);
            attachmentDeleteIcon = itemView.findViewById(R.id.attachmentDeleteIcon);
        }
    }
}
