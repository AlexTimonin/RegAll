package com.regall;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.regall.controllers.DetailsExpandableListController;
import com.regall.old.network.response.ResponseGetOrganizations;

/**
 * Created by Alex on 12.03.2015.
 */
public class DetailsActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_POINT = "point";
    private static final int STATE_VIEW = 0;
    private static final int STATE_SUBMITTED = 1;
    private static final int STATE_OFFER = 2;

    @InjectView(R.id.detailsApplyButton) View applyButton;
    @InjectView(R.id.detailsExpandableListView) ExpandableListView expandableListView;
    @InjectView(R.id.detailsImage) ImageView imageView;
    @InjectView(R.id.detailsNavigateButton) View navigateButton;

    private ResponseGetOrganizations.Point point;
    private DetailsExpandableListController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_details);
        ButterKnife.inject(this);
        point = (ResponseGetOrganizations.Point) getIntent().getSerializableExtra(EXTRA_POINT);
        initUI();
        setState(STATE_VIEW);
    }

    private void initUI() {
        applyButton.setOnClickListener(this);
        navigateButton.setOnClickListener(this);
        controller = new DetailsExpandableListController(this, expandableListView);
        controller.init();
    }

    private void setState(int state) {
        switch (state) {
            case STATE_VIEW:
                navigateButton.setVisibility(View.GONE);
                break;
            case STATE_SUBMITTED:
                navigateButton.setVisibility(View.VISIBLE);
                break;
            case STATE_OFFER:
                navigateButton.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detailsApplyButton:
                break;
            case R.id.detailsNavigateButton:
                break;
        }
    }

}
