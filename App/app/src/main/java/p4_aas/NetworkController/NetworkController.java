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
import java.util.LinkedList;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;

import com.fasterxml.jackson.core.type.TypeReference;

import p4_aas.NetworkController.Utils.Utils;

public class NetworkController extends AbstractNetworkController {
    Utils utils = new Utils();

    public SubmodelElement[] getRules(String URL) {
        List<String> resultList = new LinkedList<>();
        List<SubmodelElement> finalRes = new LinkedList<>();
        try {
            CloseableHttpResponse resp = this.apacheClient.execute(new HttpGet(URL));
            resultList = utils.jsonToList(EntityUtils.toString(resp.getEntity()));
            resp.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        resultList.forEach(el -> {
            finalRes.add(utils.createProperty("Rule" , el));
        });

        return finalRes.toArray(new SubmodelElement[finalRes.size()]);
    }

    public void deleteRule(String URL) throws HttpResponseException {
        int statusCode = 0;
        String statusMessage = "";
        try {
            CloseableHttpResponse resp = this.apacheClient.execute(new HttpDelete(URL));
            statusCode = resp.getStatusLine().getStatusCode();
            statusMessage = resp.getStatusLine().getReasonPhrase();
            resp.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        if (statusCode != HTTP_OK) {
            throw new HttpResponseException(statusCode, statusMessage);
        }
    }

    public String getStringInfo(String URL) throws HttpResponseException {
        String result = "";
        int statusCode = 0;
        String statusMessage = "";
        try {
            CloseableHttpResponse resp = this.apacheClient.execute(new HttpGet(URL));
            result = EntityUtils.toString(resp.getEntity());
            statusCode = resp.getStatusLine().getStatusCode();
            statusMessage = resp.getStatusLine().getReasonPhrase();
            resp.close();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        if (statusCode != HTTP_OK) {
            throw new HttpResponseException(statusCode, statusMessage);
        }

        return result;
    }

    public List<SwitchDescribers> getRuleDescribers(String URL) throws HttpResponseException {
        try {
            CloseableHttpResponse resp = this.apacheClient.execute(new HttpGet(URL));
            String res = EntityUtils.toString(resp.getEntity());
            return objMap.readValue(res, new TypeReference<List<SwitchDescribers>>(){});
        } catch (UnsupportedOperationException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean isServerAvailable(String URL) {
        HttpGet getRequest = new HttpGet(URL);
        int statusCode = 0;
        CloseableHttpResponse response = null;

        try {
            response = apacheClient.execute(getRequest);
            statusCode = response.getStatusLine().getStatusCode();
        } catch (IOException e) {} 
        finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {}
            }
        }

        return statusCode == HTTP_OK;
    }
}