package com.uname.your_application.loggedIn;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uname.your_application.R;
import com.uname.your_application.global.pet.Pet;

import java.util.ArrayList;

class PetListAdapter extends ArrayAdapter<Pet> implements View.OnClickListener{
    private static final String TAG = "PetListAdapter";
    private static final int VIEW_HOLDER = R.id.PetListAdapterViewHolder;
    public static final int PET_LIST_POSITION = R.id.PetListAdapterPosition;

    private LoggedInActivity activity;
    Context context;
    private int lastPosition = -1;

    // View lookup cache
    private static class ViewHolder {
        TextView firstLine;
        TextView secondLine;
        ImageView img;
    }

    PetListAdapter(ArrayList<Pet> pets, @NonNull Context context, LoggedInActivity activity) {
        super(context, R.layout.pet_list_item, pets);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag(PET_LIST_POSITION);
        Object object = getItem(position);
        Pet pet = (Pet) object;
        Log.d(TAG, "onClick: " + v.getId());
        activity.showPetProfile(pet);
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

            convertView.setTag(VIEW_HOLDER, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(VIEW_HOLDER);
        }

        // Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        // result.startAnimation(animation);
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
        convertView.setOnClickListener(this);
        convertView.setTag(PET_LIST_POSITION, position);
        // Return the completed view to render on screen
        return convertView;
    }
}
