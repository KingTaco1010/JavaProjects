package org.moonstudio.moontest.posts;

import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.moonstudio.moontest.Config;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.Objects;


public class PostParser {
    private Elements elements;
    private final String workingURL;
    public PostParser(String workingURL) throws Exception {
        Objects.requireNonNull(workingURL);
        if (!workingURL.startsWith("https://vk.com"))
            throw new IllegalArgumentException("Разрешен url только вк группы.");

        this.workingURL = workingURL;

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
            public X509Certificate[] getAcceptedIssuers() { return null; }
        } }, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        refresh();
    }

    public void refresh() throws IOException {
        try {
            Document doc = Jsoup.connect(workingURL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(Config.config.getInt("VK_settings.connection_timeout", 10000))
                    .get();

            elements = doc.select(".wall_post_text");
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException("Нестабильное интернет соединение, соединение было прервано!");
        }
    }

    @Nullable
    public String parseFirst() {
        if (!elements.isEmpty()) {
            return elements.first().text();
        }

        return null;
    }

    @Nullable
    public String parseLast() {
        if (!elements.isEmpty()) {
            return elements.last().text();
        }

        return null;
    }

}
