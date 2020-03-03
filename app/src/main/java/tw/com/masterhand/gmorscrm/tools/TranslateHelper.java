package tw.com.masterhand.gmorscrm.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.api.ApiHelper;

public class TranslateHelper {

    private final String TARGET_LANGUAGE = "zh-TW";

    public interface TranslateListener {
        void onTranslateFinish(String result);

        void onTranslateError(String errorMsg);
    }

    TranslateListener listener = null;

    public void startTranslate(String input, TranslateListener listener) {
        this.listener = listener;
        detect(input);
    }

    private void showError(String errorMsg) {
        if (listener != null) {
            listener.onTranslateError(errorMsg);
        }
    }

    private void detect(final String input) {
        ApiHelper.getInstance().getTranslateApi().detect(MyApplication.GOOGLE_KEY, input).enqueue
                (new Callback<JSONObject>() {

                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        if (response.body() != null) {
                            try {
                                JSONArray detections = response.body().getJSONObject("data")
                                        .getJSONArray("detections");
                                if (detections.length() > 0) {
                                    JSONObject object = detections.getJSONArray(0).getJSONObject(0);
                                    String language = object.getString("language");
                                    translate(input, language);
                                } else {
                                    showError("can't detect language");
                                }
                            } catch (JSONException e) {
                                showError("can't detect language:" + e.getMessage());
                            }
                        } else {
                            showError("can't detect language:response is null");
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        showError("can't detect language:" + t.getMessage());
                    }
                });
    }

    private void translate(String input, String source) {
        if (source.equals(TARGET_LANGUAGE))
            if (listener != null) {
                listener.onTranslateFinish(input);
            } else {
                showError("translation error:listener is null");
            }
        else
            ApiHelper.getInstance().getTranslateApi().translate(MyApplication.GOOGLE_KEY, input,
                    source, TARGET_LANGUAGE).enqueue
                    (new Callback<JSONObject>() {

                        @Override
                        public void onResponse(Call<JSONObject> call, Response<JSONObject>
                                response) {
                            if (response.body() != null) {
                                try {
                                    JSONArray translations = response.body().getJSONObject
                                            ("data")
                                            .getJSONArray("translations");
                                    if (translations.length() > 0) {
                                        JSONObject object = translations.getJSONObject(0);
                                        if (listener != null) {
                                            listener.onTranslateFinish(object.getString
                                                    ("translatedText"));
                                        } else {
                                            showError("translation error:listener is null");
                                        }
                                    } else {
                                        showError("translation error:result is empty");
                                    }
                                } catch (JSONException e) {
                                    showError("translation error:" + e.getMessage());
                                }
                            } else {
                                showError("translation error:response is null");
                            }
                        }

                        @Override
                        public void onFailure(Call<JSONObject> call, Throwable t) {
                            showError("translation error:" + t.getMessage());
                        }
                    });
    }
}
