package org.springframework.social.live.connect;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class LiveOAuth2Template extends OAuth2Template  {

	public LiveOAuth2Template(String clientId, String clientSecret) {
		super(clientId, clientSecret, "https://login.live.com/oauth20_authorize.srf", "https://login.live.com/oauth20_token.srf");
		setUseParametersForClientAuthentication(true);
	}

    /* On POST to /token Microsoft returns JSON as "text/html" (should be application/json), which results in:
    ---
    org.springframework.web.client.RestClientException: Could not extract response: no suitable
    HttpMessageConverter found for response type [interface java.util.Map] and content type
    [text/html;charset=utf-8]
    ---
    Adding support for handling text/html to the MappingJackson2HttpMessageConverter */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate template = super.createRestTemplate();
        for (HttpMessageConverter<?> converter : template.getMessageConverters()){
            if(converter instanceof MappingJackson2HttpMessageConverter){
                List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
                // Add all default
                supportedMediaTypes.addAll(converter.getSupportedMediaTypes());
                // And also handle text/html json returned on POST to /token
                supportedMediaTypes.add(new MediaType("text", "html", Charset.forName("UTF-8")));
                ((MappingJackson2HttpMessageConverter)converter).setSupportedMediaTypes(supportedMediaTypes);
            }
        }
        return template;
    }
}
