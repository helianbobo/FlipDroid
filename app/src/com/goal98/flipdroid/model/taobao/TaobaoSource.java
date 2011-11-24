package com.goal98.flipdroid.model.taobao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.goal98.flipdroid.model.GroupedSource;
import com.goal98.flipdroid.model.Source;
import com.goal98.flipdroid.model.RSSSourceRepo;
import com.goal98.flipdroid.util.Constants;

public class TaobaoSource {
    public static String TAOBAOURI="http://gw.api.taobao.com/router/rest";
    public static String TESTTAOBAOURI="http://gw.api.tbsandbox.com/router/rest";
    public static String TAOBAOGETTIEMSURI="http://gw.api.taobao.com/router/rest";
    public static String APPKEY="12307981";
    public static String APPSERECT="7961d6948156f1350d4ba779087b0f1f";
    
     
    public GroupedSource searchSource(String queryStr) {
	 
	try {
	    //Log.v("queryStr",queryStr);
	    //Log.v("encodequeryStr",URLEncoder.encode(queryStr.trim(), "GB2312"));
	    
	    //String uri=String.format(BAIDUURI,URLEncoder.encode(queryStr.trim(), "GB2312"));
	     
	
        List<Map<String, String>> sourceList = new ArrayList<Map<String, String>>();
      
                Map<String, String> result = new HashMap<String, String>();
                result.put(Source.KEY_SOURCE_NAME, queryStr);
                result.put(Source.KEY_SOURCE_ID, Constants.TYPE_TAOBAO+queryStr);
                result.put(Source.KEY_SOURCE_DESC, "淘宝搜索商品");
                result.put(Source.KEY_SOURCE_TYPE, Constants.TYPE_TAOBAO);
                result.put(Source.KEY_IMAGE_URL, "");
                result.put(Source.KEY_CONTENT_URL, "" );
                result.put(Source.KEY_CAT, "淘宝");

                sourceList.add(result);

            
            return RSSSourceRepo.group(sourceList);
	} catch (Exception e) {
	     
	    e.printStackTrace();
	    
	    return new GroupedSource();
	}
       
    }
}
