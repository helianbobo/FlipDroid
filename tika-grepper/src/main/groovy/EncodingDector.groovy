URL url = new URL('http://www.ifanr.com/50811')
def conn = url.openConnection() as HttpURLConnection
println conn.getResponseCode()
println conn.getURL().toString()
