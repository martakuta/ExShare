package pl.edu.mimuw.exshare;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

public class BooleanVolleyRequest extends Request {

    private String url;

    public BooleanVolleyRequest(String url) {
        super(url, null);
        this.url = url;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(Object response) {

    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
