package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.xeroauthorization.CallbackHandler;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import com.xerofinancials.importer.xeroauthorization.Authorization;
import com.xerofinancials.importer.repository.XeroAccountCredentialsRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/xero")
public class XeroAuthorizationController {
    private final TokenStorage tokenStorage;
    private final Authorization authorization;
    private final CallbackHandler callbackHandler;
    private final XeroAccountCredentialsRepository accountCredentialsRepository;

    public XeroAuthorizationController(
            final TokenStorage tokenStorage,
            final Authorization authorization,
            final CallbackHandler callbackHandler,
            final XeroAccountCredentialsRepository accountCredentialsRepository
    ) {
        this.tokenStorage = tokenStorage;
        this.authorization = authorization;
        this.callbackHandler = callbackHandler;
        this.accountCredentialsRepository = accountCredentialsRepository;
    }

    @GetMapping("authorization")
    public String auth(Model model) throws IOException {
        if (!tokenStorage.isAuthentificated()) {
            final String authorizationRedirect = authorization.getAuthorizationRedirect();
            model.addAttribute("redirect", authorizationRedirect);
        }
        model.addAttribute("clientId", accountCredentialsRepository.getClientId());
        model.addAttribute("clientSecret", accountCredentialsRepository.getClientSecret());
        model.addAttribute("redirectURI", accountCredentialsRepository.getRedirectURI());
        return "authorization.html";
    }

    @GetMapping(value = {"/redirect/", "/redirect"})
    public String redirect(@RequestParam Map<String,String> requestParams, Model model) throws IOException {
        callbackHandler.extractTokenInfo(requestParams);
        return "redirect:/xero/authorization";
    }

}
