URL url = new URL('http://t.cn/auz7CS')
def conn = url.openConnection() as HttpURLConnection
println conn.getResponseCode()
println conn.getURL().toString()
