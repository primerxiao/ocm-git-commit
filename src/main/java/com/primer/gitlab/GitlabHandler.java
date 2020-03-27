package com.primer.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;

public class GitlabHandler {
    public void login() throws GitLabApiException {
        // Log in to the GitLab server using a username and password
        GitLabApi gitLabApi = GitLabApi.oauth2Login("http://your.gitlab.server.com", "xiaojunhui", "123456789");
    }
}
