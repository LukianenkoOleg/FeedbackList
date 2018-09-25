package oleg.lukianenko.feedbackslist;

import android.content.Context;
import android.util.Log;

import oleg.lukianenko.architecture.PresenterInterface;
import oleg.lukianenko.retrofit_util.request_body.feedback.Feedback;
import oleg.lukianenko.retrofit_util.response.feedback.ResponseRetrofit;
import oleg.lukianenko.model.ModelLogout;
import oleg.lukianenko.model.ModelWinner;
import oleg.lukianenko.retrofitutil.ApiInterface;
import oleg.lukianenko.utils.ResponseUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by Oleg Lukianenko.
 */
public class ModelFeedback extends ModelLogout {

    private PresenterFeedback presenter;
    private List<ResponseRetrofit.Data> feedbacks;
    private int currentFeedbackPage;
    private static final int STATUS_UNAUTHORIZED = 401;
    private static final int STATUS_CREATED = 201;


    ModelFeedback(Context context) {
        super(context);
        currentFeedbackPage = 1;
        feedbacks = new ArrayList<>();
    }

    @Override
    public void applyPresenter(PresenterInterface presenter) {
        this.presenter = (PresenterFeedback) presenter;
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setData(Object modelData) {
    }

    public void getFeedbacks() {
        ApiInterface apiService = getRetrofit().create(ApiInterface.class);
        Call<ResponseRetrofit> call = apiService.getfeedback(currentFeedbackPage);

        call.enqueue(new Callback<ResponseRetrofit>() {
            @Override
            public void onResponse(Call<ResponseRetrofit> call, Response<ResponseRetrofit> response) {
                ResponseRetrofit responseFeedback = response.body();
                if (responseFeedback != null) {
                    for (ResponseRetrofit.Data item : responseFeedback.data) {
                        if (item.feedback != null && !item.feedback.isEmpty()) {
                            feedbacks.add(item);
                        }
                    }
                    presenter.showFeedbacks(feedbacks);
                } else {
                    presenter.showMessage(ResponseUtil.getResponse(getRetrofit(), response.errorBody()));
                }
                presenter.hideRefresh();
            }

            @Override
            public void onFailure(Call<ResponseRetrofit> call, Throwable t) {
                t.getMessage();
            }
        });
    }

    public void sendFeedback(final String feedback) {
        ApiInterface apiService = getRetrofit().create(ApiInterface.class);
        Feedback feedbackBody = new Feedback(feedback);
        Call<Object> call;

        if (ModelWinner.id != null) {
            call = apiService.sendFeedback(getToken(), ModelWinner.id, feedbackBody);
        } else {
            call = apiService.sendFeedback(getToken(), "", feedbackBody);
        }

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == STATUS_UNAUTHORIZED) {
                    updateToken(setCallback(feedback));
                }
                if (response.code() == STATUS_CREATED) {
                    currentFeedbackPage = 1;
                    feedbacks = new ArrayList<>();
                    getFeedbacks();
                    presenter.successfullSend();
                }
                if (response.body() == null) {
                    presenter.showMessage(ResponseUtil.getResponse(getRetrofit(), response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Callback setCallback(final String feedback) {

        return new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                sendFeedback(feedback);
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        };
    }

    public void setNextPage() {
        currentFeedbackPage++;
    }
}