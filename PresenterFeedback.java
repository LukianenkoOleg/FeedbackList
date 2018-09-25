package oleg.lukianenko.feedbackslist;

import oleg.lukianenko.activity.BaseActivity;
import oleg.lukianenko.activity.MenuActivity;
import oleg.lukianenko.fragments.HeaderPresenter;
import oleg.lukianenko.retrofitutil.response.feedback.ResponseRetrofit;

import java.util.List;

/*
 * Created by Oleg Lukianenko.
 */
public class PresenterFeedback extends HeaderPresenter {

    public void onMenu() {
        ((MenuActivity) getActivity()).openMenu();
    }

    public PresenterFeedback(BaseActivity baseActivity) {
        super(baseActivity);
    }

    public void onViewCreated(){
    }

    public void sendFeedback(String feedback){
        ((ModelFeedback) getModel()).sendFeedback(feedback);
    }

    public void successfulSend(){
        ((FragmentFeedback) getView()).successfullSendFeedback();
    }

    public void getFeedback(){
        ((ModelFeedback) getModel()).getFeedbacks();
    }

    public void showFeedbacks(List<ResponseRetrofit.Data> feedbacks){
        ((FragmentFeedback) getView()).showFeedbackList(feedbacks);
    }

    public void hideRefresh(){
        ((FragmentFeedback) getView()).hideSwiyRefreshLayout();
    }

    public void setNextFeedbackPage(){
        ((ModelFeedback) getModel()).setNextPage();
    }
}