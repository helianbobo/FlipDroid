package com.goal98.flipdroid2.model.flipdroid;

import com.goal98.flipdroid2.client.TikaClient;
import com.goal98.flipdroid2.client.TikaClientException;
import com.goal98.flipdroid2.client.TikaSourceResponse;
import com.goal98.flipdroid2.model.GroupedSource;
import com.goal98.flipdroid2.model.SearchSource;
import com.goal98.flipdroid2.model.Source;
import com.goal98.flipdroid2.model.SourceRepo;
import com.goal98.flipdroid2.util.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    private String BAIDUURI="http://news.baidu.com/ns?word=%s&tn=newsrss&sr=0&cl=1&rn=20&ct=0";
    
    public GroupedSource searchSource(String queryStr) {
        List<Map<String, String>> sourceList = new ArrayList<Map<String, String>>();
        try {
            List<TikaSourceResponse> responses = new TikaClient(Constants.TIKA_HOST).searchSource(queryStr);
            for (int i = 0; i < responses.size(); i++) {
                TikaSourceResponse tikaSourceResponse = responses.get(i);
                Map<String, String> result = new HashMap<String, String>();
                result.put(Source.KEY_SOURCE_NAME, tikaSourceResponse.getName());
//                result.put(Source.KEY_SOURCE_ID, tikaSourceResponse.getId());
                result.put(Source.KEY_SOURCE_DESC, tikaSourceResponse.getDesc());
                result.put(Source.KEY_SOURCE_TYPE, tikaSourceResponse.getAccountType());
                result.put(Source.KEY_IMAGE_URL, tikaSourceResponse.getImageURL());
                result.put(Source.KEY_CONTENT_URL, tikaSourceResponse.getContentURL());
                result.put(Source.KEY_CAT, tikaSourceResponse.getCat());
                sourceList.add(result);
      
                
            }
            
            
            
            
        } catch (TikaClientException e) {
            e.printStackTrace();
            //return new GroupedSource();
            sourceList.clear();
        }
        
        String uri;
	    try {
		
		uri = String.format(BAIDUURI,URLEncoder.encode(queryStr.trim(), "GB2312"));
		Map<String, String> result = new HashMap<String, String>();
	            result.put(Source.KEY_SOURCE_NAME, queryStr);
//	            result.put(Source.KEY_SOURCE_ID, Constants.TYPE_BAIDUSEARCH+queryStr);
	            result.put(Source.KEY_SOURCE_DESC, "百度RSS新闻订阅");
	            result.put(Source.KEY_SOURCE_TYPE, Constants.TYPE_BAIDUSEARCH);
	            result.put(Source.KEY_IMAGE_URL, "");
	            result.put(Source.KEY_CONTENT_URL, uri );
	            result.put(Source.KEY_CAT, "百度");
	            sourceList.add(result);
	    } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    
	    
	    Map<String, String> result = new HashMap<String, String>();
            result.put(Source.KEY_SOURCE_NAME, queryStr);
//            result.put(Source.KEY_SOURCE_ID, Constants.TYPE_TAOBAO+queryStr);
            result.put(Source.KEY_SOURCE_DESC, "淘宝搜索商品");
            result.put(Source.KEY_SOURCE_TYPE, Constants.TYPE_TAOBAO);
            result.put(Source.KEY_IMAGE_URL, "");
            result.put(Source.KEY_CONTENT_URL, "" );
            result.put(Source.KEY_CAT, "淘宝");

            sourceList.add(result);
        System.out.println("wawawa");
        return SourceRepo.group(sourceList);
    }
}
