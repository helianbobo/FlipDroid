URL url = new URL("http://www.uutuu.com/api/newzealandapp/coupons/");
URLConnection uc = url.openConnection();
uc.setDoOutput(true);

OutputStream raw = uc.getOutputStream();
OutputStream buf = new BufferedOutputStream(raw);
OutputStreamWriter out = new OutputStreamWriter(buf, "utf-8");
out.write("ver=1.0.1");
out.flush();
out.close();

InputStream inputStream = uc.getInputStream();
inputStream = new BufferedInputStream (inputStream);
Reader r = new InputStreamReader(inputStream);
int c;
System.out.println("==================Beging====================");
while ((c = r.read()) != -1)
    System.out.print((char) c);
inputStream.close ();
System.out.println("===================End======================");
   //{"success":true,"data":{"coupon":[{"name":"Great Sights","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_1.jpg","md5":"362ee1ddcee3caec0b393b7b809be1f5"},{"name":"\u6ce2\u5229\u5c3c\u897f\u4e9a\u6e29\u6cc9","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_2.jpg","md5":"cc3e12a2740b5fd9ebf9aed5264074e2"},{"name":"Q book","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_3.jpg","md5":"ac8ba6f0232335316f5b5ef057709d7c"},{"name":"\u7687\u540e\u9547\u5929\u7a7a\u57ce","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_4.jpg","md5":"6d8fab8c6a779373d9fe46af7f5de24b"},{"name":"Te Puia","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_5.jpg","md5":"594eae6f21a33788bf317a3c815927d2"}],"home_banner":[{"name":"home_ad_1","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_1.jpg","md5":"8ab6d0ef41dc8256baa8404901192125"},{"name":"home_ad_2","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_2.jpg","md5":"277d4b8af43dba4ff46f3830374365bf"},{"name":"home_ad_3","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_3.jpg","md5":"fa52572158e42f59b4a4915858edf237"},{"name":"home_ad_4","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_4.jpg","md5":"f066b11160ae79c1767e5f9b8268b067"},{"name":"home_ad_5","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_5.jpg","md5":"2e353a3aaa886f9b254418498b6ec79c"}],"photo_ad":[{"name":"\u65b0\u897f\u5170\u822a\u7a7a\u673a\u961f","pos":"0","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_1.jpg","md5":"331d31f6d8b91fe7267eee92d9490f60"},{"name":"\u592a\u5e73\u6d0b\u7ecf\u6d4e\u8231","pos":"2","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_2.jpg","md5":"051b321031f742d67d10332b0527e8d5"},{"name":"\u673a\u8231\u5185\u5a31\u4e50","pos":"4","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_3.jpg","md5":"9a687839d8c0fb273678921fab69ac57"},{"name":"\u673a\u8231\u5185\u7f8e\u98df","pos":"6","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_4.jpg","md5":"25292e4f315dce1681a6d6d0b926211b"},{"name":"\u5965\u514b\u5170\u56fd\u9645\u673a\u573a","pos":"8","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_5.jpg","md5":"6fabe9fe963ebf2c033ac7ffffc6f40a"}],"ver":"1.0.11"}}===================End======================
   //{"success":true,"data":{"coupon":[{"name":"Great Sights","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_1.jpg","md5":"362ee1ddcee3caec0b393b7b809be1f5"},{"name":"\u6ce2\u5229\u5c3c\u897f\u4e9a\u6e29\u6cc9","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_2.jpg","md5":"cc3e12a2740b5fd9ebf9aed5264074e2"},{"name":"Q book","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_3.jpg","md5":"ac8ba6f0232335316f5b5ef057709d7c"},{"name":"\u7687\u540e\u9547\u5929\u7a7a\u57ce","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_4.jpg","md5":"6d8fab8c6a779373d9fe46af7f5de24b"},{"name":"Te Puia","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/coupon_5.jpg","md5":"594eae6f21a33788bf317a3c815927d2"}],"home_banner":[{"name":"home_ad_1","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_1.jpg","md5":"8ab6d0ef41dc8256baa8404901192125"},{"name":"home_ad_2","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_2.jpg","md5":"277d4b8af43dba4ff46f3830374365bf"},{"name":"home_ad_3","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_3.jpg","md5":"fa52572158e42f59b4a4915858edf237"},{"name":"home_ad_4","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_4.jpg","md5":"f066b11160ae79c1767e5f9b8268b067"},{"name":"home_ad_5","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/home_ad_5.jpg","md5":"2e353a3aaa886f9b254418498b6ec79c"}],"photo_ad":[{"name":"\u65b0\u897f\u5170\u822a\u7a7a\u673a\u961f","pos":"0","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_1.jpg","md5":"331d31f6d8b91fe7267eee92d9490f60"},{"name":"\u592a\u5e73\u6d0b\u7ecf\u6d4e\u8231","pos":"2","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_2.jpg","md5":"051b321031f742d67d10332b0527e8d5"},{"name":"\u673a\u8231\u5185\u5a31\u4e50","pos":"4","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_3.jpg","md5":"9a687839d8c0fb273678921fab69ac57"},{"name":"\u673a\u8231\u5185\u7f8e\u98df","pos":"6","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_4.jpg","md5":"25292e4f315dce1681a6d6d0b926211b"},{"name":"\u5965\u514b\u5170\u56fd\u9645\u673a\u573a","pos":"8","img":"http:\/\/www.uutuu.com\/newzealandapp\/20111230iphone\/t_5.jpg","md5":"6fabe9fe963ebf2c033ac7ffffc6f40a"}],"ver":"1.0.11"}}===================End======================
