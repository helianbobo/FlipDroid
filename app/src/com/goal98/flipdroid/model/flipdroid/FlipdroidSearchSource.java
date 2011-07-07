package com.goal98.flipdroid.model.flipdroid;

import com.goal98.flipdroid.client.TikaClient;
import com.goal98.flipdroid.client.TikaClientException;
import com.goal98.flipdroid.client.TikaSourceResponse;
import com.goal98.flipdroid.model.GroupedSource;
import com.goal98.flipdroid.model.SearchSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 7/3/11
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlipdroidSearchSource implements SearchSource {
    public GroupedSource searchSource(String queryStr) {
        List<Map<String, String>> sourceList = new ArrayList<Map<String, String>>();
        try {
            List<TikaSourceResponse> responses = new TikaClient().searchSource(queryStr);
            for (int i = 0; i < responses.size(); i++) {
                TikaSourceResponse tikaSourceResponse = responses.get(i);
                Map<String, String> result = new HashMap<String, String>();
                result.put(Source.KEY_SOURCE_NAME, tikaSourceResponse.getName());
                result.put(Source.KEY_SOURCE_ID, tikaSourceResponse.getId());
                result.put(Source.KEY_SOURCE_DESC, tikaSourceResponse.getDesc());
                result.put(Source.KEY_ACCOUNT_TYPE, tikaSourceResponse.getAccountType());
                result.put(Source.KEY_IMAGE_URL, tikaSourceResponse.getImageURL());
                result.put(Source.KEY_CONTENT_URL, tikaSourceResponse.getContentURL());
                result.put(Source.KEY_CAT, tikaSourceResponse.getCat());

                sourceList.add(result);
            }
        } catch (TikaClientException e) {
            return new GroupedSource();
        }
        return SourceRepo.group(sourceList);
    }
}
