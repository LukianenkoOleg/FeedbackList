package oleg.lukianenko.feedbackslist;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import oleg.lukianenko.R;
import oleg.lukianenko.activity.BaseActivity;
import oleg.lukianenko.fragments.HeaderFragment;
import oleg.lukianenko.main.feedback.adapter.FeedbackAdapter;
import oleg.lukianenko.model.ErrorMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Oleg Lukianenko.
 */
public class FragmentFeedback extends HeaderFragment<PresenterFeedback>
        implements ViewFeedbacks, SwipyRefreshLayout.OnRefreshListener {

    @BindView(R.id.list_feedback)
    protected RecyclerView listFeedback;

    @BindView(R.id.srl_feedback)
    protected SwipyRefreshLayout swipyRefreshLayout;

    @BindView(R.id.container_send_feedback)
    protected RelativeLayout containerFeedback;

    @BindView(R.id.et_feedback)
    protected EditText etFeedback;

    private FeedbackAdapter feedbackAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_feedback;
    }

    @Override
    protected void initPresenter() {
        applyPresenter(new PresenterFeedback((BaseActivity) getActivity()));
        getPresenter().initComponents(new ModelFeedback(getActivity()),this);
    }

    @Override
    public View createHeader() {
        return new HeaderBuilder(this).addMenuButton().addLogOutButton().addTittle(getString(R.string.menu_feedback)).build();
    }

    @Override
    public void onNavigationEvent() {
        getPresenter().onMenu();
    }

    @Override
    public void onActionEvent() {
        super.onActionEvent();
    }

    @Override
    public void showErrorMessage(ErrorMessage message) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewSettings();
        createListFeedback();
        getFeedbackList();
        ifUserNotWinner();
        feedbackAdapter = new FeedbackAdapter(getResources());
        listFeedback.setAdapter(feedbackAdapter);
    }

    private void getFeedbackList(){
        if(checkInternetConnection()) {
            getPresenter().getFeedback();
        } else {
            showConnectDialog(getString(R.string.error_internet_connection));
        }
    }

    private void showConnectDialog(String message) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_message, null);
        ((TextView) dialogView.findViewById(R.id.tv_error)).setText(message);
        ad.setView(dialogView);
        final AlertDialog dialogEdit = ad.create();
        dialogEdit.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnComplaintOk = (Button) dialogView.findViewById(R.id.btn_ok);
        btnComplaintOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEdit.dismiss();
                getFeedbackList();
            }
        });

        dialogEdit.setCancelable(false);
        dialogEdit.show();
        dialogEdit.getWindow().setLayout((int) getResources().getDimension(R.dimen.dialog_edit_profile_error),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setViewSettings(){
        swipyRefreshLayout.setOnRefreshListener(this);
        etFeedback.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    hideKeyboard(view);
                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        onNavigationEvent();
        return true;
    }

    private void ifUserNotWinner(){
        if(!isWinner())
            containerFeedback.setVisibility(View.GONE);
    }

    private void createListFeedback(){
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        listFeedback.setLayoutManager(manager);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        swipyRefreshLayout.setRefreshing(true);
        getPresenter().setNextFeedbackPage();
        getPresenter().getFeedback();
    }

    public void hideSwipeRefreshLayout(){
        swipeRefreshLayout.setRefreshing(false);
    }

    @OnClick(R.id.btn_send_feedback)
    public void sendFeedback(){
        getPresenter().sendFeedback(etFeedback.getText().toString());
    }

    @Override
    public void showFeedbackList(List items) {
        try {
            feedbackAdapter.setlist(items);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void successfulSendFeedback(){
        feedbackWasSent();
        containerFeedback.setVisibility(View.GONE);
    }
}