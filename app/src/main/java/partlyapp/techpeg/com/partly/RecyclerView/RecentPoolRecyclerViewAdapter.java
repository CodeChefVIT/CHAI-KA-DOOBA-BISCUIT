package partlyapp.techpeg.com.partly.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import partlyapp.techpeg.com.partly.Models.Member;
import partlyapp.techpeg.com.partly.Models.Pool;
import partlyapp.techpeg.com.partly.R;

public class RecentPoolRecyclerViewAdapter extends RecyclerView.Adapter<RecentPoolRecyclerViewAdapter.CustomViewHolder> {

    private Context mContext;
    ArrayList<Pool> pools;

    public RecentPoolRecyclerViewAdapter(Context context, ArrayList<Pool> ps) {
        pools = ps;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        String name = pools.get(i).getName();
        ColorGenerator generator = ColorGenerator.MATERIAL;

        int color1 = generator.getColor(name);
        TextDrawable drawable = TextDrawable
                .builder()
                .beginConfig()
                .fontSize(25)
                .endConfig()
                .buildRound(name, color1);

        customViewHolder.iv_user.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return (null != pools ? pools.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView iv_user;

        public CustomViewHolder(View view) {
            super(view);
            this.iv_user = (ImageView) view.findViewById(R.id.iv_user);

        }
    }
}