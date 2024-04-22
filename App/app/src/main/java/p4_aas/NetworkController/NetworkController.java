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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import p4_aas.NetworkController.Serialization.SwitchDescribers;

public class NetworkController extends AbstractNetworkController {
    int ruleParamsCounter = 0;

    /**
     * 
     * @param URL
     * @return current rules into Switch.
     */
    public SubmodelElement[] getRules(String URL) {
        List<String> resultList = new LinkedList<>();
        List<SubmodelElement> finalRes = new LinkedList<>();

        resultList = this.jsonToList(this.getRequest(URL));
        resultList.forEach(el -> {
            finalRes.add(this.createProperty("Rule" , el));
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

    /**
     * 
     * @param URL
     * @return rule describers for creating new Firewall rules.
     * @throws HttpResponseException
     */
    public List<SwitchDescribers> getRuleDescribers(String URL) {
        String res = this.getRequest(URL);
        List<SwitchDescribers> results = new LinkedList<>();
        try {
            results = objMap.readValue(res, new TypeReference<List<SwitchDescribers>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return results;
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

    /**
     * 
     * @param URL
     * @param inputs: (Key, Value) -> (input field name, input field value)
     * ex. (hdr.ipv4.dstAddr, 10.0.0.1/24) -> if match is LPM (without /24 otherwise)
     * @throws HttpResponseException 
     */
    public void postRule(String URL, Map<String, String> inputs) {
        this.ruleParamsCounter = 0;
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();

        inputs.forEach((k,v) -> {
            if (k.startsWith("hdr")) {
                nvps.add(new BasicNameValuePair("key0", v));
            } else {
                nvps.add(new BasicNameValuePair("par" + this.ruleParamsCounter, v));
                this.ruleParamsCounter++;
            }
        });

        this.postRequest(URL, nvps);
    }
}