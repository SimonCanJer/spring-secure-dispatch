package com.secured.api.decor;

import java.util.Set;

public interface ICredentialsToToken {
    String trailUserCredentials(String uName, String password, Set<String> roles);
    String pollToken();
}
