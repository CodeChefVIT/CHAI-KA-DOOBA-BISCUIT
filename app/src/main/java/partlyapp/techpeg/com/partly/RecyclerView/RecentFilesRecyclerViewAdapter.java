package partlyapp.techpeg.com.partly.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import partlyapp.techpeg.com.partly.Models.Member;
import partlyapp.techpeg.com.partly.R;

public class RecentFilesRecyclerViewAdapter extends RecyclerView.Adapter<RecentFilesRecyclerViewAdapter.CustomViewHolder> {

    private Context mContext;
    ArrayList<String> file;

    public RecentFilesRecyclerViewAdapter(Context context, ArrayList<String> files) {
        file = files;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_row, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        String name = file.get(i);
        ColorGenerator generator = ColorGenerator.MATERIAL;


        //Setting text view title
        customViewHolder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return (null != file ? file.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.tv_file_name);

        }
    }
}