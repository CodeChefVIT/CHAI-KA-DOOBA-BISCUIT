package partlyapp.techpeg.com.partly.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestAPIClient {

    private static Retrofit retrofit = null;

    public static RestAPI getClient(String url) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://baseurl.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(RestAPI.class);
    }
}