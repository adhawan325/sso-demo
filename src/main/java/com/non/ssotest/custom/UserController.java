package com.non.ssotest.custom;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Invitation;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class UserController {
    @Autowired
    Environment environment;
    @GetMapping("/register-user")
    public String addUser(@RequestParam String email){
        GraphServiceClient graphClient = getClient();

        Invitation invitation = new Invitation();
        invitation.invitedUserEmailAddress = email;
        invitation.inviteRedirectUrl = "https://myapps.microsoft.com/";

        graphClient.invitations()
                .buildRequest()
                .post(invitation);
        return "User added";
    }

    public GraphServiceClient getClient(){
        final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(environment.getProperty("spring.security.oauth2.client.registration.az.client-id"))
                .clientSecret(environment.getProperty("spring.security.oauth2.client.registration.az.client-secret"))
                .tenantId(environment.getProperty("az.tenantId"))
                .build();
        java.util.List<String> scopes = new ArrayList<String>();
        scopes.add("https://graph.microsoft.com/.default");
        final TokenCredentialAuthProvider tokenCredentialAuthProvider = new TokenCredentialAuthProvider(scopes, clientSecretCredential);


        final GraphServiceClient graphClient =
                GraphServiceClient
                        .builder()
                        .authenticationProvider(tokenCredentialAuthProvider)
                        .buildClient();
        return graphClient;
    }
}
