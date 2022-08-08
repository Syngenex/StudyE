package com.example.studye;

import android.util.Log;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Map;

import okhttp3.*;

import java.io.IOException;


public class EzrevenueClient {
    private final String projectId;
    private final String projectSecret;

    private static final String BASE_URL = "https://revenue.ezfuns.com/api/v1/server";

    public EzrevenueClient(String projectId, String projectSecret) {
        this.projectId = projectId;
        this.projectSecret = projectSecret;
    }

    private SecretKey getJwtKey() {
        return Keys.hmacShaKeyFor(this.projectSecret.getBytes(StandardCharsets.UTF_8));
    }

    private SignatureAlgorithm getAlgorithm() {
        return SignatureAlgorithm.HS256;
    }

    private String generateNonce() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String encodeToken(Map<String, Object> payload) {
        Map<String, Object> claims = new HashMap<>(payload);
        claims.put("nonce", this.generateNonce());
        Instant exp = Instant.now().plusSeconds(30 * 60);
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setHeaderParam("project_id", this.projectId)
                .setClaims(claims)
                .setExpiration(Date.from(exp))
                .signWith(this.getJwtKey(), this.getAlgorithm())
                .compact();
        return token;
    }

    public Map<String, Object> decodeToken(String token) {
        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(this.getJwtKey())
                .build()
                .parseClaimsJws(token);
        Header header = jwt.getHeader();
        Claims payload = jwt.getBody();
        if (!payload.containsKey("exp")) {
            throw new MissingClaimException(header, payload, "claim exp required");
        }
        if (!payload.containsKey("nonce")) {
            throw new MissingClaimException(header, payload, "claim nonce required");
        }
        return payload;
    }

    private String sendRequest(String url, String content) throws IOException {
        MediaType CONTENT_TYPE = MediaType.parse("text/plain");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(content, CONTENT_TYPE))
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) {
                String detail = "Unexpected code " + response;
                if (body != null) {
                    detail += " Body=" + body.string();
                }
                throw new IOException(detail);
            }
            return body.string();
        }
    }

    public Map<String, Object> call(String api, Map<String, Object> params) throws IOException {
        Claims payload = Jwts.claims();
        payload.put("method", api);
        payload.put("params", params);
        String content = this.encodeToken(payload);
        String url = BASE_URL + "/" + api;
        String token = this.sendRequest(url, content);
        Map<String, Object> result = this.decodeToken(token);
        return (Map<String, Object>) result.get("result");
    }

    public static String ezrevenueClient (String user_id,String nickname)  throws IOException {
        adapter.vip_flag=false;
        String projectId = "by2skmvez9tmn";
        String projectSecret = "ttb7ffwvk3k28be0wyua42p8vh2qaxwd";
        String paywallId = "356mwmqhvrc1e";
        String api = "customer.info";
        Map<String, Object> params = Map.of(
                "paywall_id", paywallId,
                "customer", Map.of(
                        "external_id", user_id,
                        "nickname",nickname
                ),
                "include_balance", true
        );
        EzrevenueClient client = new EzrevenueClient(projectId, projectSecret);
        Map<String, Object> result = client.call(api, params);
        Log.e("ezrevenueClient", result.toString());
        String name=result.get("balance_s").toString();
        Log.e("TAG", name );
        String is_balance_usable=name.substring(name.indexOf(", is_balance_usable=")+20,name.indexOf(", has_charged"));//会员
        String is_balance_infinite=name.substring(name.indexOf(", is_balance_infinite=")+22,name.indexOf(", is_balance_usable="));//永久会员
        String balance_text=name.substring(name.indexOf(", balance_text=")+15,name.indexOf(", is_balance_infinite"));//到期时间
        if(is_balance_usable.equals("true")){
            adapter.vip_flag=true;
        }
        if(is_balance_infinite.equals("true")){
            adapter.youngjiu_vip_flag=true;
        }
        adapter.vip_time=balance_text.replaceFirst("-","年").replace("-","月").replace("T","日").replaceFirst(":","时").replace(":","分").replace("Z","秒");
        Log.e("is_balance_usable", adapter.vip_flag.toString() );
        Log.e("is_balance_infinite", adapter.youngjiu_vip_flag.toString() );
        Log.e("balance_text", adapter.vip_time );
        String url=result.toString().substring(result.toString().indexOf("url=")+4,result.toString().indexOf("}, dt_created"));
        Log.e("TAG",url );
        return url;

    }
}