// Copyright 2023 riccardo.bacca@studio.unibo.it
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package p4_aas.NetworkController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
public abstract class AbstractNetworkController {
    protected static final int HTTP_OK = 200;
    CloseableHttpClient apacheClient;
    ObjectMapper objMap;

    public AbstractNetworkController() {
        this.apacheClient = HttpClients.createDefault();
        this.objMap = new ObjectMapper();
    }

    /**
     * @param URL
     * @param nvps
     * @return Post request Status Code
     * @throws HttpResponseException 
     */
    public void postRequest(String URL, List<NameValuePair> nvps) {
        int statusCode = 0;
        String statusMessage = "";
        HttpPost httpPost = new HttpPost(URL);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            CloseableHttpResponse response = apacheClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            statusMessage = response.getStatusLine().getReasonPhrase();
            response.close();
            httpPost.abort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (statusCode != HTTP_OK)
            try {
                throw new HttpResponseException(statusCode, statusMessage);
            } catch (HttpResponseException e) {
                e.printStackTrace();
            }
    }

    public String getRequest(String URL) {
        int statusCode = 0;
        String statusMessage = "";
        String result = "";
        
        CloseableHttpResponse resp;
        try {
            resp = this.apacheClient.execute(new HttpGet(URL));
            result = EntityUtils.toString(resp.getEntity());
            statusCode = resp.getStatusLine().getStatusCode();
            statusMessage = resp.getStatusLine().getReasonPhrase();
            resp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (statusCode != HTTP_OK)
            try {
                throw new HttpResponseException(statusCode, statusMessage);
            } catch (HttpResponseException e) {
                e.printStackTrace();
            }

        return result;
    }



    public List<String> jsonToList(String json) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
        List<String> resultList = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            resultList.add(jsonArray.get(i).toString());
        }

        return resultList;
    }
}