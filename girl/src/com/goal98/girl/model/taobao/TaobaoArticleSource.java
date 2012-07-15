package com.goal98.girl.model.taobao;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.goal98.girl.model.Article;
import com.goal98.girl.model.ArticleSource;
import com.goal98.girl.util.Constants;
import com.mars.api.core.ItemDomainApi;
import com.mars.api.core.OpenServiceClient;
import com.mars.api.domain.TwoTuples;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


import com.taobao.api.ApiException;
import com.taobao.api.domain.Item;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 6/6/11 Time: 1:12 PM To
 * change this template use File | Settings | File Templates.
 */
public class TaobaoArticleSource implements ArticleSource {
    private static String  fields ="detail_url,num_iid,title,nick,type,cid,pic_url," +
    		"num,valid_thru,list_time,delist_time,stuff_status,location,price,post_fee," +
    		"express_fee,ems_fee,has_discount,freight_payer,has_invoice,has_warranty,modified," +
    		"approve_status,auction_point,outer_id,wap_desc,desc";
    private String query;
    private volatile boolean   loaded;
    private String sourceImage;
    private List<Article>  list = new LinkedList<Article>();
     
     
    TwoTuples<Item[], Long> taobaolist;
    public TaobaoArticleSource(String query, Context context) {
	TelephonyManager telephonyManager = (TelephonyManager) context
		.getSystemService(Context.TELEPHONY_SERVICE);
	String imei = telephonyManager.getDeviceId();
	String imsi = telephonyManager.getSubscriberId();
	OpenServiceClient.init(Taobao.TAOBAOURI, Taobao.APPKEY,
		Taobao.APPSERECT, context, Taobao.TTID, imei, imsi);
	this.query = query;
	
	/*
	if(this.isloadedlist==false) {
	    Log.v("hahahahaha", String.valueOf(this.isloadedlist));
	 try { 
		taobaolist = ItemDomainApi.getItems(this.query);	
		 
		this.isloadedlist=true;
		Log.v("uuuuuu", String.valueOf(this.isloadedlist));
		//this.loaded=true;
	    } catch (ApiException e) {
		// TODO Auto-generated catch block
		  Log.v("xxxxxxxxx", "xxxxxxxxxxxxxxxx");
		e.printStackTrace();
		this.isloadedlist=false;
		//return false;
	    }	   
	}
	 */
	

    }
    private void initTaobao() {
	if(this.isloadedlist==false) {
	    Log.v("hahahahaha", String.valueOf(this.isloadedlist));
	 try { 
		taobaolist = ItemDomainApi.getItems(this.query);	
		 
		this.isloadedlist=true;
		Log.v("uuuuuu", String.valueOf(this.isloadedlist));
		//this.loaded=true;
	    } catch (ApiException e) {
		// TODO Auto-generated catch block
		  Log.v("xxxxxxxxx", "xxxxxxxxxxxxxxxx");
		e.printStackTrace();
		this.isloadedlist=false;
		//return false;
	    }	   
	}
	
    }


    public Date lastModified() {
	return new Date(); 
    }

    public List<Article> getArticleList() {
	return list; 
    }

    public static long cachedTime = -1;
    public volatile int pageLoaded=0;
    public int everypage=4;
    private volatile boolean isloadedlist=false;
    private int maxlen=20;
    public boolean loadMore() {
	if(this.loaded)
		return false;
	
	initTaobao();
	if(this.isloadedlist==false)
	    return false;
	
	    Log.v("length", String.valueOf(taobaolist.a.length));
	    int len=taobaolist.a.length;
	    
	    System.out.println("dofor@@@@@@@@@@@@@@@@");
	    
	    for (int i = pageLoaded*everypage; i < (pageLoaded+1)*everypage &&i<len&& i< maxlen; i++) {
	    //for (int i = 0; i < 4 ; i++) {
	            Item item = taobaolist.a[i];
	            System.out.println(String.valueOf(taobaolist.b)); 
	            Log.v("@@@@i", String.valueOf(i));
	            Log.v("@@@@name", item.getNick());
	            Log.v("@@@@pic", item.getPicUrl());
	            Log.v("@@@@title", item.getTitle());
	            Log.v("@@@@numiid", String.valueOf(item.getNumIid()));
	            /*System.out.println(item.toString());
	            System.out.println(item.getScore());
	            System.out.println(item.getPostFee());
	            System.out.println(item.getPrice());
	            System.out.println(item.getVolume());*/
	            Article article = new Article();
	            article.setAlreadyLoaded(true);
	            Date date = new Date();
	             StringBuilder thecontent;
	            try {
			Item tempitem=ItemDomainApi.getItem(fields,item.getNumIid());
			
			if (tempitem.getModified()!= null)
		        	date =tempitem.getModified();
		            article.setCreatedDate(date);
			
			Map<String, String> result = new LinkedHashMap<String, String>();
			result.put("price", tempitem.getPrice()); 
			
			result.put("score", item.getScore().toString());
			
			
			result.put("volume", item.getVolume().toString());
			result.put("商品数量", tempitem.getNum().toString() );
	                result.put("运费",tempitem.getPostFee());
	                result.put("type", tempitem.getType());
	                result.put("新旧程度", tempitem.getStuffStatus());     
	                result.put("地点", tempitem.getLocation().getCity());
	                result.put("detail_url", tempitem.getDetailUrl());
	                result.put("介绍", tempitem.getWapDesc());
	                
	                List<String> values =new ArrayList<String>();
	                for(String k:result.keySet()) {
	                   
	                    values.add(result.get(k));
	                }
	                
	                
	                thecontent=new StringBuilder();
	                thecontent.append(String.format("<p> <font  color=\"red\"><b>价格: %s</b></font></p>",result.get("price")));
	                thecontent.append(String.format("<p> 店家声誉:%s </p>",result.get("score")));
	                 
	                thecontent.append(String.format("<p> 商品数量: %s</p>",result.get("商品数量")));
	                thecontent.append(String.format("<p>运费: %s </p>",result.get("运费")));
	                thecontent.append(String.format("<p>type: %s</p>",result.get("type")));
	                thecontent.append(String.format("<p>新旧程度: %s</p>",result.get("新旧程度")));
	                thecontent.append(String.format("<p>地点: %s</p>",result.get("地点")));
	                thecontent.append(String.format("<p> 网址: <a href=\"%s\">%s</a></p>",result.get("detail_url"),result.get("detail_url")));
	                thecontent.append(tempitem.getDesc());
//	                 thecontent=new StringBuilder(String.format("价格: %s\n"+
//	                 
//	                	 
//	                	"店家声誉:%s\n"+
//	                	"30天交易量: %s\n"+"商品数量: %s\n"+
//	                	"运费: %s\n"+"type: %s\n"+
//	                	"新旧程度: %s\n"+"地点: %s\n"+"网址: %s\n"+
//	                	"介绍: %s\n",
//	                	values.toArray()
//	                ));
	                
	               // System.out.println("######################");
	                //System.out.println(thecontent);
	               // System.out.println("######################");
	                
	                
	                //String temptitle=item.getTitle().replace("<[^>]+>|</[^>]+>", "");
	                System.out.println(thecontent.toString());
		        article.setTitle(tempitem.getTitle());
	                article.setContent(thecontent.toString() );
	                
		    } catch (ApiException e1) {
			//thecontent=new StringBuilder("");
			e1.printStackTrace();
		    }catch(Exception e) {
			System.out.println("aaaaaaaaaa!");
			e.printStackTrace();
			
		    }
	            	          
	            System.out.println("xxxxxxxx1111");
	            if (item.getNick() == null || item.getNick().trim().length() == 0)
	                article.setAuthor("");
	            else {
	                article.setAuthor(item.getNick());
	            }
	             
	            System.out.println("xxxxxxxx22222");
	            //System.out.println(item.getDetailUrl());
	            String tempimageurl;
	            try {
	        	if (item.getPicUrl() != null)  {
	        	    tempimageurl=item.getPicUrl();
	        	    /*if(taobaolist.a[0].getPicUrl() != null) 
		        	tempimageurl=taobaolist.a[0].getPicUrl();*/
	        	    
	        		 //article.setPortraitImageUrl(new URL(""));
		                    article.setPortraitImageUrl(null);
		                    article.setImageUrl(new URL(tempimageurl));
		                } 
	            }catch (MalformedURLException e) {
		                }
	        	 
	            
	            //article.setContent(item.getTitle());
	            article.setStatus(Taobao.ITEMURL+String.valueOf(item.getNumIid()));
         
	            System.out.println("xxxxxxxx33333");
	            Log.v("####1", article.getAuthor()) ;
	            Log.v("####2", article.getTitle()) ;
	            Log.v("####3", article.getStatus()) ;

	            article.setSourceType(Constants.TYPE_TAOBAO);
	            list.add(article);
	            if (i==maxlen-1)
	        	//System.out.println("********************haha");
	        	 loaded = true;
	        }
	    pageLoaded+=1;
	    
	    return true;  
    }
    volatile boolean noMoreToLoad;
    public boolean isNoMoreToLoad() {
	if (loaded)
	    return true;
	return false;
    }

    public boolean getForceMagzine() {
	return false;
    }

    public boolean reset() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
