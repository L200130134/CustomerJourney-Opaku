package com.rikyahmadfathoni.test.opaku.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.rikyahmadfathoni.test.opaku.R;
import com.rikyahmadfathoni.test.opaku.model.AddressModel;
import com.rikyahmadfathoni.test.opaku.utils.UtilsList;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<AddressModel> addressModels = new ArrayList<>();
    private int lastPositionSelected = -1;

    public void setAddressModels(List<AddressModel> addressModels) {
        this.addressModels = UtilsList.nonNull(addressModels);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(getItem(position));
    }

    @Nullable
    public AddressModel getItem(int position) {
        try {
            return addressModels.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return addressModels.size();
    }

    public int getIndex(String bankId) {
        if (bankId != null && addressModels != null) {
            for (int i = 0; i< addressModels.size(); i++) {
                final AddressModel bankModel = getItem(i);
                if (bankModel != null) {
                    if (bankId.equals(bankModel.getId())) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    public int getLastPositionSelected() {
        return lastPositionSelected;
    }

    public void setLastPositionBy(String bankId) {
        if (bankId == null) {
            return;
        }
        final int index = getIndex(bankId);
        System.err.println("Position : " + index);
        if (index >= 0) {
            lastPositionSelected = index;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name;
        private final TextView address;
        private final ImageView edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            edit = itemView.findViewById(R.id.edit);

            edit.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void onBindData(AddressModel item) {
            if (item != null) {
                name.setText(item.getName());
                address.setText(item.getFullAddress());
            }
        }

        @Override
        public void onClick(View view) {
            final int position = getAdapterPosition();
            final AddressModel addressModel = getItem(position);
            if (view == edit) {
                if (addressModel != null) {
                    if (eventListener != null) {
                        eventListener.onClick(addressModel, position);
                    }
                    lastPositionSelected = position;
                }
            } else {
                if (eventListener != null) {
                    eventListener.onSelect(addressModel);
                }
            }
        }
    }

    public interface EventListener {
        void onClick(AddressModel addressModel, int position);
        void onSelect(AddressModel addressModel);
    }

    private EventListener eventListener;

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }
}
