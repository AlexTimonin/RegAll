package com.regall.fragments;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.regall.R;
import com.regall.adapters.ListAdapter;
import com.regall.controllers.AutowashController;
import com.regall.old.fragments.BaseFragment;
import com.regall.old.model.AutowashFilter;
import com.regall.old.network.Callback;
import com.regall.old.network.response.ResponseGetOrganizations;

/**
 * Created by Alex on 14.02.2015.
 */
public class ListFragment extends BaseFragment implements TextWatcher, View.OnClickListener, AdapterView.OnItemClickListener {

    private ListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_fragment_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText searchEditText = (EditText) view.findViewById(R.id.searchEditText);
        searchEditText.removeTextChangedListener(this);
        searchEditText.setText(getCurrentAutowashFilter().getSearchKey());
        searchEditText.addTextChangedListener(this);
        view.findViewById(R.id.searchButton).setOnClickListener(this);
        ((ListView) view.findViewById(R.id.listView)).setAdapter(listAdapter = new ListAdapter(getActivity(), getAutowashController().getOrganisations()));
        ((ListView) view.findViewById(R.id.listView)).setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getAutowashController().loadOrganizations(0, getCurrentAutowashFilter(), callback);
    }

    private AutowashController getAutowashController() {
        return getMainActivity().getAutowashController();
    }

    private Location getCurrentLocation() {
        return  getMainActivity().getCurrentLocation();
    }

    private Location getDefaultLocation() {
        return  getMainActivity().getDefaultLocation();
    }

    private AutowashFilter getCurrentAutowashFilter() {
        return getMainActivity().getCurrentAutowashFilter();
    }

    private Callback<ResponseGetOrganizations> callback = new Callback<ResponseGetOrganizations>() {
        @Override
        public void success(Object object) {
            listAdapter.notifyDataSetChanged();
            //TODO use getAutowashController().getOrganisations();
        }

        @Override
        public void failure(Exception e) {}
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        getCurrentAutowashFilter().setSearchKey(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton:
                getAutowashController().loadOrganizations(0, getCurrentAutowashFilter(), callback);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getMainActivity().startDetailsActivity(listAdapter.getItem(position));
    }

}
