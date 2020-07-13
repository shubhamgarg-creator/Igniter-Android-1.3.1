package instagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.text.MessageFormat;

import instagram.model.InstagramUser;
import instagram.utils.SharedPrefUtils;

import static instagram.InstagramHelperConstants.AUTH_URL;
import static instagram.InstagramHelperConstants.CLIENT_ID_DEF;
import static instagram.InstagramHelperConstants.INSTA_AUTH_URL;
import static instagram.InstagramHelperConstants.INSTA_LOGIN;
import static instagram.InstagramHelperConstants.INSTA_REDIRECT_URL;
import static instagram.InstagramHelperConstants.REDIRECT_URI_DEF;
import static instagram.InstagramHelperConstants.RESPONSE_TYPE_DEF;
import static instagram.InstagramHelperConstants.SCOPE_TYPE_DEF;
import static instagram.utils.CommonUtils.checkNotNull;

public class InstagramHelper {

    private String clientId;
    private String redirectUri;
    private String scope;

    private InstagramHelper(String clientId, String redirectUri, String scope) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.scope = scope;
    }

    public void loginFromActivity(Activity context) {
        String authUrl = MessageFormat.format(AUTH_URL
                + CLIENT_ID_DEF + "{0}"
                + REDIRECT_URI_DEF + "{1}"
                + RESPONSE_TYPE_DEF, clientId, redirectUri);

        if (!TextUtils.isEmpty(scope)) {
            authUrl += SCOPE_TYPE_DEF + scope;
        }

        Intent intent = new Intent(context, InstagramLoginActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(INSTA_AUTH_URL, authUrl);
        bundle.putString(INSTA_REDIRECT_URL, redirectUri);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, INSTA_LOGIN);
    }

    public InstagramUser getInstagramUser(Context context) {
        return SharedPrefUtils.getInstagramUser(context);
    }

    public static final class Builder {
        private String clientId;
        private String redirectUrl;
        private String scope;

        public Builder withClientId(String clientId) {
            this.clientId = checkNotNull(clientId, "clientId == null");
            return this;
        }

        public Builder withRedirectUrl(String redirectUrl) {
            this.redirectUrl = checkNotNull(redirectUrl, "redirectUrl == null");
            return this;
        }

        public Builder withScope(String scope) {
            this.scope = checkNotNull(scope, "scope == null");
            return this;
        }

        public InstagramHelper build() {
            return new InstagramHelper(clientId, redirectUrl, scope);
        }
    }
}
