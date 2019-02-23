package partlyapp.techpeg.com.partly.Retrofit;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface RestAPI {

    @Headers({
            "User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Accept-Language: en-GB,en;q=0.5",
            "DNT: 1",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1"})
    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl, @Header("Authorization") String auth, @Header("Range") String content_length);

    @Headers({
            "User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Accept-Language: en-GB,en;q=0.5",
            "DNT: 1",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1"})
    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithAuth(@Url String fileUrl, @Header("Range") String content_length, @Header("Authorization") String auth);


    @HEAD
    @Headers({
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:61.0) Gecko/20100101 Firefox/61.0",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Accept-Language: en-US,en;q=0.5",
            "Accept-Encoding: gzip, deflate, br",
            "DNT: 1",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1"})
    Call<Void> getHeaders(@Url String fileUrl);


    @HEAD
    @Headers({
            "User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Accept-Language: en-GB,en;q=0.5",
            "DNT: 1",
            "Connection: keep-alive",
            "Upgrade-Insecure-Requests: 1"})
    Call<Void> getHeadersWithAuth(@Url String fileUrl, @Header("Authorization") String auth);
}