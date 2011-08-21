HttpURLConnection connection = new URL('http://api.t.sina.com.cn/oauth/request_token').openConnection()
println connection.getResponseCode()