package com.username.your_application.loggedIn;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.username.your_application.R;
import com.username.your_application.global.pet.Pet;

import java.util.ArrayList;

class PetListAdapter extends ArrayAdapter<Pet> implements View.OnClickListener{

    Context context;
    private int lastPosition = -1;

    // View lookup cache
    private static class ViewHolder {
        TextView firstLine;
        TextView secondLine;
        ImageView img;
    }

    public PetListAdapter(ArrayList<Pet> pets, @NonNull Context context) {
        super(context, R.layout.pet_list_item, pets);
        this.context = context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Pet pet = (Pet) object;

        switch (v.getId())
        {
            /*
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
                */
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Pet pet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.pet_list_item, parent, false);
            viewHolder.firstLine = (TextView) convertView.findViewById(R.id.firstLine);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.pet_list_item_icon);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.firstLine.setText(pet.getName());
        int imageResource = R.drawable.ic_dog;
        switch (pet.getAnimalType()) {
            case Pet.Constants.DOG:
                imageResource = R.drawable.ic_dog;
                break;
            case Pet.Constants.CAT:
                imageResource = R.drawable.ic_cat;
                break;
        }
        viewHolder.img.setImageResource(imageResource);
        viewHolder.img.setOnClickListener(this);
        viewHolder.img.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
