package partlyapp.techpeg.com.partly.Fragments;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import partlyapp.techpeg.com.partly.Constants.Constants;
import partlyapp.techpeg.com.partly.R;
import partlyapp.techpeg.com.partly.ViewModels.PoolViewModel;

public class AddMembersFragment extends Fragment {


    @BindView(R.id.iv_pool_qr)
    ImageView iv_pool_qr;

    PoolViewModel mViewModel;

    public AddMembersFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(PoolViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_members, container, false);
    }

    public void QRGenerate() {


        JSONObject object = new JSONObject();

        try {
            object.put(Constants.KEY_POOL_TOKEN, mViewModel.getCurrentPool().getPool_token());

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
