package weibo4j.examples;

import weibo4j.Status;
import weibo4j.Weibo;
import weibo4j.WeiboException;
import weibo4j.http.AccessToken;
import weibo4j.http.RequestToken;

public class WebOAuth {

	public static RequestToken request(String backUrl) {
		try {
			System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
			System.setProperty("weibo4j.oauth.consumerSecret",
					Weibo.CONSUMER_SECRET);

			Weibo weibo = new Weibo();
			// set callback url, desktop app please set to null
			// http://callback_url?oauth_token=xxx&oauth_verifier=xxx
			RequestToken requestToken = weibo.getOAuthRequestToken(backUrl);

			System.out.println("Got request token.");
			System.out.println("Request token: " + requestToken.getToken());
			System.out.println("Request token secret: "
					+ requestToken.getTokenSecret());
			return requestToken;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static AccessToken requstAccessToken(RequestToken requestToken,
			String verifier) {
		try {
			System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
			System.setProperty("weibo4j.oauth.consumerSecret",
					Weibo.CONSUMER_SECRET);

			Weibo weibo = new Weibo();
			// set callback url, desktop app please set to null
			// http://callback_url?oauth_token=xxx&oauth_verifier=xxx
			AccessToken accessToken = weibo.getOAuthAccessToken(requestToken
					.getToken(), requestToken.getTokenSecret(), verifier);

			System.out.println("Got access token.");
			System.out.println("access token: " + accessToken.getToken());
			System.out.println("access token secret: "
					+ accessToken.getTokenSecret());
			return accessToken;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void update(AccessToken access, String content) {
		try {
			Weibo weibo = new Weibo();

			weibo.setToken(access.getToken(), access.getTokenSecret());

			Status status = weibo.updateStatus(content);
			System.out.println("Successfully updated the status to ["
					+ status.getText() + "].");
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
