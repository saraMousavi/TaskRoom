package ir.android.taskroom.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Attachments;
import ir.android.taskroom.ui.activity.ImagePreviewActivity;
import ir.android.taskroom.viewmodels.AttachmentsViewModel;

public class AttachmentsAdapter extends ListAdapter<Attachments, AttachmentsAdapter.ViewHolderItem> {

    private AttachmentsViewModel attachmentsViewModel;
    private FragmentActivity mFragmentActivity;
    private Runnable progressRunnable;
    private Handler progressHandler = new Handler();

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
        if (attachments.getAttachments_type().equals("3gp")) {
            holder.attachemntImage.setImageResource(R.drawable.ic_play);


            holder.attachemntImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mediaPlayer.isPlaying()) {
                        holder.attachemntImage.setImageResource(R.drawable.ic_play);
                        holder.mediaPlayer.pause();
                        progressHandler.removeCallbacks(progressRunnable);
                        holder.isplaying = true;
                    } else {
                        if (!holder.isplaying) {
                            holder.mediaPlayer = new MediaPlayer();
                            try {
                                holder.mediaPlayer.setDataSource(attachments.getAttachments_path());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                holder.mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        holder.mediaPlayer.start();
                        holder.attachemntImage.setImageResource(R.drawable.ic_pause);
                        holder.playMediaSeekBar.setVisibility(View.VISIBLE);
                        holder.playMediaSeekBar.setMax(holder.mediaPlayer.getDuration());
                        progressRunnable = new Runnable() {
                            @Override
                            public void run() {
                                holder.playMediaSeekBar.setProgress(holder.mediaPlayer.getCurrentPosition());
                                progressHandler.postDelayed(this, 500);
                                if (holder.mediaPlayer.getCurrentPosition() >= holder.mediaPlayer.getDuration()) {
                                    holder.attachemntImage.setImageResource(R.drawable.ic_play);
                                    holder.playMediaSeekBar.setVisibility(View.INVISIBLE);
                                    progressHandler.removeCallbacks(this);
                                    holder.isplaying = false;
                                }
                            }
                        };
                        progressHandler.postDelayed(progressRunnable, 0);
                    }
                }
            });
        } else {
            File imgFile = new File(attachments.getAttachments_path());
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getPath());
            holder.attachemntImage.setImageBitmap(bitmap);
            holder.attachemntImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mFragmentActivity, ImagePreviewActivity.class);
                    intent.putExtra("imagePath", imgFile.getPath());
                    mFragmentActivity.startActivity(intent);
                }
            });
        }
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                File file = new File(tempAttachment.getAttachments_path());
                file.delete();
                if (file.exists()) {
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file.exists()) {
                        mFragmentActivity.deleteFile(file.getName());
                    }
                }
            }
        }, 4000);

    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public ImageView attachemntImage;
        public AppCompatImageButton attachmentDeleteIcon;
        public SeekBar playMediaSeekBar;
        public boolean isplaying = false;
        private MediaPlayer mediaPlayer = new MediaPlayer();

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            attachemntImage = itemView.findViewById(R.id.attachemntImage);
            attachmentDeleteIcon = itemView.findViewById(R.id.attachmentDeleteIcon);
            playMediaSeekBar = itemView.findViewById(R.id.playMediaSeekBar);
        }
    }
}
