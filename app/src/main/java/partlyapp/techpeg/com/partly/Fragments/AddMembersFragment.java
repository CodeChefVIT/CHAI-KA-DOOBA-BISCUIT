package partlyapp.techpeg.com.partly.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.orm.SugarContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import partlyapp.techpeg.com.partly.Activities.CreatePoolActivity;
import partlyapp.techpeg.com.partly.Activities.PoolActivity;
import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.R;
import partlyapp.techpeg.com.partly.Singleton.PoolSingleton;


public class AddMembersFragment extends Fragment {


    @BindView(R.id.tv_pool_name)
    TextView tv_pool_name;
    @BindView(R.id.iv_pool_qr)
    ImageView iv_pool_qr;

    PoolSingleton poolSingleton;

    public AddMembersFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SugarContext.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_add_members, container, false);
        ButterKnife.bind(this,v);
        poolSingleton=PoolSingleton.getInstance();
        tv_pool_name.setText(poolSingleton.getCurrentPool().getName());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                QRGenerate();
            }
        },2000);

        return v;
    }

    public void QRGenerate() {

        JSONObject object = new JSONObject();
        Log.d("qrgenerate","pool-"+poolSingleton.getCurrentPool().getPool_token());
        try {
            object.put(Constants.KEY_POOL_TOKEN, poolSingleton.getCurrentPool().getPool_token());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String text = object.toString();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            final Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    iv_pool_qr.setImageBitmap(bitmap);
                }
            });

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_members_next)
    public void members_next(){
        poolSingleton.getCurrentPool().save();
        Intent intent = new Intent(getActivity(), PoolActivity.class);
        getActivity().finish();
        startActivity(intent);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SugarContext.terminate();
    }
}
