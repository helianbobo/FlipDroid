package com.goal98.flipdroid.model.baidu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.util.Log;

import com.goal98.flipdroid.model.GroupedSource;
import com.goal98.flipdroid.model.SearchSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.SourceRepo;
import com.goal98.flipdroid.util.Constants;

public class BaiduSearchSource implements SearchSource {
    private String BAIDUURI="http://news.baidu.com/ns?word=%s&tn=newsrss&sr=0&cl=1&rn=20&ct=0";
     
    public GroupedSource searchSource(String queryStr) {
	 
	try {
	    Log.v("queryStr",queryStr);
	    Log.v("encodequeryStr",URLEncoder.encode(queryStr.trim(), "GB2312"));
	    
	    String uri=String.format(BAIDUURI,URLEncoder.encode(queryStr.trim(), "GB2312"));
	     
	
        List<Map<String, String>> sourceList = new ArrayList<Map<String, String>>();
      
                Map<String, String> result = new HashMap<String, String>();
                result.put(Source.KEY_SOURCE_NAME, queryStr);
                result.put(Source.KEY_SOURCE_ID, "Baidu_"+queryStr);
                result.put(Source.KEY_SOURCE_DESC, queryStr);
                result.put(Source.KEY_SOURCE_TYPE, Constants.TYPE_BAIDUSEARCH);
                result.put(Source.KEY_IMAGE_URL, "");
                result.put(Source.KEY_CONTENT_URL, uri );
                result.put(Source.KEY_CAT, queryStr);

                sourceList.add(result);

                Log.v("uri:",uri);
            
            return SourceRepo.group(sourceList);
	} catch (UnsupportedEncodingException e) {
	     
	    e.printStackTrace();
	    
	    return new GroupedSource();
	}
       
    }
}
