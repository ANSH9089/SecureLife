package com.example.securelife.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.securelife.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ImageView map;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Create image list
        List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years.", ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct.",ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel("https://bit.ly/3fLJf72", "And people do that.",ScaleTypes.CENTER_CROP));

        // Find the ImageSlider in the inflated view
        ImageSlider imageSlider = view.findViewById(R.id.slider);


        imageSlider.setImageList(imageList);
        map=view.findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap(); 
            }
        });

        return view;
    }

    private void openMap() {
        double latitude = 21.1501;
        double longitude = 79.0119;

        String label = "IIIT Nagpur";
        Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(label));
        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
}
