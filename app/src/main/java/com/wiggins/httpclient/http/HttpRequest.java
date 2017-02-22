package com.wiggins.httpclient.http;

import android.os.NetworkOnMainThreadException;

import com.wiggins.httpclient.R;
import com.wiggins.httpclient.bean.ResultDesc;
import com.wiggins.httpclient.utils.Constant;
import com.wiggins.httpclient.utils.LogUtil;
import com.wiggins.httpclient.utils.StringUtil;
import com.wiggins.httpclient.utils.UIUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 网络请求封装
 * @Author 一花一世界
 * @Time 2017/2/20 9:48
 */

public class HttpRequest {

    public static final int CONNECTION_TIME_OUT = 1000 * 20;//连接超时
    public static final int SO_TIMEOUT = 1000 * 20;//Socket 超时
    public static final String ENCODING = "UTF-8";//编码格式

    /**
     * HttpClient Post 请求
     *
     * @param apiUrl    接口地址
     * @param paramsMap 接口参数
     * @return
     */
    public static ResultDesc postRequest(String apiUrl, Map<String, String> paramsMap) {
        // 创建表单
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (paramsMap != null && paramsMap.size() != 0) {
            for (String key : paramsMap.keySet()) {
                params.add(new BasicNameValuePair(key, paramsMap.get(key)));
            }
        }

        //创建HttpClient对象
        HttpClient httpClient = getHttpClient();
        //创建请求对象,参数是访问的服务器地址
        HttpPost httpPost = new HttpPost(apiUrl);

        HttpResponse httpResponse;
        ResultDesc resultDesc = null;
        try {
            UrlEncodedFormEntity encode = new UrlEncodedFormEntity(params, ENCODING);
            httpPost.setEntity(encode);

            //执行请求，获取服务器返回的相应对象
            httpResponse = httpClient.execute(httpPost);
            //检查相应的状态是否正常，状态码返回值为200表示正常
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //从相应对象当中取出数据放到entity当中
                HttpEntity entity = httpResponse.getEntity();
                //将entity当中的数据转换为字符串
                String response = EntityUtils.toString(entity, ENCODING);

                LogUtil.e(Constant.LOG_TAG, "接口地址:" + apiUrl + "\n请求返回:" + response);

                resultDesc = getReturnData(response);
            } else {
                resultDesc = dataRestructuring(-1, UIUtils.getString(R.string.back_abnormal_results), "");
            }
        } catch (Exception e) {
            resultDesc = dataRestructuring(-1, ExceptionCode(e), "");
        }

        return resultDesc;
    }

    /**
     * HttpClient Get 请求
     *
     * @param apiUrl    接口地址
     * @param paramsMap 接口参数
     * @return
     */
    public static ResultDesc getRequest(String apiUrl, Map<String, String> paramsMap) {
        //创建表单
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (paramsMap != null && paramsMap.size() != 0) {
            for (String key : paramsMap.keySet()) {
                params.add(new BasicNameValuePair(key, paramsMap.get(key)));
            }
            //对参数编码
            String param = URLEncodedUtils.format(params, ENCODING);
            apiUrl += "?" + param;
        }

        //创建HttpClient对象
        HttpClient httpClient = getHttpClient();
        //创建请求对象,参数是访问的服务器地址
        HttpGet httpGet = new HttpGet(apiUrl);

        HttpResponse httpResponse;
        ResultDesc resultDesc = null;
        try {
            //执行请求，获取服务器返回的相应对象
            httpResponse = httpClient.execute(httpGet);
            //检查相应的状态是否正常，状态码返回值为200表示正常
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //从相应对象当中取出数据放到entity当中
                HttpEntity entity = httpResponse.getEntity();
                //将entity当中的数据转换为字符串
                String response = EntityUtils.toString(entity, ENCODING);

                LogUtil.e(Constant.LOG_TAG, "接口地址:" + apiUrl + "\n请求返回:" + response);

                resultDesc = getReturnData(response);
            } else {
                resultDesc = dataRestructuring(-1, UIUtils.getString(R.string.back_abnormal_results), "");
            }
        } catch (Exception e) {
            resultDesc = dataRestructuring(-1, ExceptionCode(e), "");
        }

        return resultDesc;
    }

    /**
     * HttpClient 配置
     *
     * @return
     */
    public static HttpClient getHttpClient() {
        //创建HttpParams以用来设置HTTP参数（这一部分不是必需的）
        BasicHttpParams httpParams = new BasicHttpParams();
        //设置连接超时
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIME_OUT);
        //设置Socket超时
        HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
        //设置Socket缓存大小
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        //设置userAgent
        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
        HttpProtocolParams.setUserAgent(httpParams, userAgent);

        //设置重定向，缺省为true
        //HttpClientParams.setRedirecting(httpParams, true);
        //设置HttpClient支持HTTP和HTTPS两种模式
        //SchemeRegistry schReg = new SchemeRegistry();
        //schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        //schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        //使用线程安全的连接管理来创建HttpClient
        //ClientConnectionManager conMgr = new ThreadSafeClientConnManager(httpParams, schReg);
        //创建HttpClient对象
        //HttpClient httpClient = new DefaultHttpClient(conMgr, httpParams);

        //创建HttpClient对象
        HttpClient httpClient = new DefaultHttpClient(httpParams);
        return httpClient;
    }

    /**
     * 返回数据解析
     *
     * @param result 请求返回字符串
     * @return
     */
    public static ResultDesc getReturnData(String result) {
        ResultDesc resultDesc = null;

        if (StringUtil.isEmpty(result)) {
            //返回数据为空
            resultDesc = dataRestructuring(-1, UIUtils.getString(R.string.back_abnormal_results), "");
            return resultDesc;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);
            //返回码
            int error_code = jsonObject.getInt("error_code");
            //返回说明
            String reason = jsonObject.getString("reason");
            //返回数据
            String resultData = jsonObject.getString("result");

            resultDesc = dataRestructuring(error_code, reason, resultData);
        } catch (JSONException e) {
            resultDesc = dataRestructuring(-1, ExceptionCode(e), "");
        }

        return resultDesc;
    }

    /**
     * 数据重组
     *
     * @param error_code 返回码
     * @param reason     返回说明
     * @param resultData 返回数据
     * @return
     */
    public static ResultDesc dataRestructuring(int error_code, String reason, String resultData) {
        ResultDesc resultDesc = new ResultDesc();
        resultDesc.setError_code(error_code);
        resultDesc.setReason(reason);
        resultDesc.setResult(resultData);
        return resultDesc;
    }

    /**
     * 异常处理
     *
     * @param e 各类异常
     * @return
     */
    public static String ExceptionCode(Exception e) {
        if (e instanceof NetworkOnMainThreadException) {
            // 主线程中访问网络时异常
            // Android在4.0之前的版本支持在主线程中访问网络，但是在4.0以后对这部分程序进行了优化，也就是说访问网络的代码不能写在主线程中了。
            // 解决方法：采用多线程、异步加载的方式加载数据
            return UIUtils.getString(R.string.main_thread_access_network_exception);
        } else if (e instanceof ClientProtocolException) {
            // 有些网站在发出http请求时需要辨别你浏览器的类别、版本等信息，如果没有则出现异常ClientProtocolException或者抛出503不响应你的请求。
            return UIUtils.getString(R.string.site_access_exception);
        } else if (e instanceof SocketTimeoutException) {
            // 服务器响应超时
            return UIUtils.getString(R.string.server_response_timeout);
        } else if (e instanceof ConnectTimeoutException) {
            // 服务器请求超时
            return UIUtils.getString(R.string.server_request_timeout);
        } else if (e instanceof HttpException) {
            // HTTP异常
            return UIUtils.getString(R.string.http_exception);
        } else if (e instanceof IOException) {
            // I/O异常
            return UIUtils.getString(R.string.io_exception);
        } else if (e instanceof JSONException) {
            // JSON格式转换异常
            return UIUtils.getString(R.string.json_format_conversion_exception);
        } else {
            // 其他异常
            return UIUtils.getString(R.string.back_abnormal_results);
        }
    }
}