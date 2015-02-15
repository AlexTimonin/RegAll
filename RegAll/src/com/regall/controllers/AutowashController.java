package com.regall.controllers;

import android.widget.Toast;
import com.regall.R;
import com.regall.old.MainActivity;
import com.regall.old.model.AutowashFilter;
import com.regall.old.network.API;
import com.regall.old.network.Callback;
import com.regall.old.network.request.RequestGetOrganizations;
import com.regall.old.network.response.ResponseGetOrganizations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 11.02.2015.
 */
public class AutowashController {

    private MainActivity mainActivity;
    private Callback<ResponseGetOrganizations> fragmentCallback;
    private List<ResponseGetOrganizations.Organisation> organisations = new ArrayList<ResponseGetOrganizations.Organisation>();
    private int lastLoadedPage = -1;

    public AutowashController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public List<ResponseGetOrganizations.Organisation> getOrganisations() {
        return organisations;
    }

    public void loadOrganizations(int page, AutowashFilter filter, Callback<ResponseGetOrganizations> fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
        API api = new API(mainActivity.getString(R.string.server_url));
        mainActivity.showProgressDialog(R.string.message_loading);
        api.getOrganisations(RequestGetOrganizations.byFilter(filter, 1, page), callback);
    }

    private Callback<ResponseGetOrganizations> callback = new Callback<ResponseGetOrganizations>() {
        @Override
        public void success(Object object) {
            ResponseGetOrganizations response = (ResponseGetOrganizations) object;
            if (response.isSuccess() && fragmentCallback != null) {
                if (response.getPage().getPageNumber() > lastLoadedPage) {
                    organisations.addAll(response.getOrganisations());
                } else if (lastLoadedPage != 0 && response.getPage().getPageNumber() == lastLoadedPage) {
                    //TODO refresh page?
                } else {
                    organisations.clear();
                    if (response.getOrganisations() != null) {
                        organisations.addAll(response.getOrganisations());
                    }
                }
                lastLoadedPage = response.getPage().getPageNumber();
                fragmentCallback.success(object);
            } else {
                Toast.makeText(mainActivity, mainActivity.getString(R.string.error_loading_data), Toast.LENGTH_LONG).show();
            }
            mainActivity.hideProgressDialog();
        }

        @Override
        public void failure(Exception e) {
            Toast.makeText(mainActivity, mainActivity.getString(R.string.error_loading_data), Toast.LENGTH_LONG).show();
            mainActivity.hideProgressDialog();
        }
    };

}
