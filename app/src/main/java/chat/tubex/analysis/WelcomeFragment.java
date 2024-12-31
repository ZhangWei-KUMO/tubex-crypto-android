package chat.tubex.analysis;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import chat.tubex.analysis.R;


public class WelcomeFragment extends Fragment {
    private static final String ARG_IMAGE_RES = "image_res";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";


    public static WelcomeFragment newInstance(int imageRes,String title,String description) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES, imageRes);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_page, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if(args!=null){
            int imageRes = args.getInt(ARG_IMAGE_RES);
            String title = args.getString(ARG_TITLE);
            String description = args.getString(ARG_DESCRIPTION);
            ImageView imageView = view.findViewById(R.id.imageView);
            TextView titleView = view.findViewById(R.id.titleView);
            TextView descriptionView = view.findViewById(R.id.descriptionView);
            imageView.setImageResource(imageRes);
            titleView.setText(title);
            descriptionView.setText(description);


        }
    }
}
