package com.xerofinancials.importer.controllers;

import com.xerofinancials.importer.beans.XeroTokens;
import com.xerofinancials.importer.repository.XeroAccountCredentialsRepository;
import com.xerofinancials.importer.xeroauthorization.Authorization;
import com.xerofinancials.importer.xeroauthorization.CallbackHandler;
import com.xerofinancials.importer.xeroauthorization.TokenStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Controller
@RequestMapping("/xero")
public class XeroAuthorizationController {
    private static final Logger logger = LoggerFactory.getLogger(XeroAuthorizationController.class);
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
        model.addAttribute("isAuthentificated", tokenStorage.isAuthentificated());
        final String authorizationRedirect = authorization.getAuthorizationRedirect();
        model.addAttribute("redirect", authorizationRedirect);
        model.addAttribute("clientId", accountCredentialsRepository.getClientId());
        model.addAttribute("clientSecret", accountCredentialsRepository.getClientSecret());
        model.addAttribute("redirectURI", accountCredentialsRepository.getRedirectURI());
        return "authorization.html";
    }

    @GetMapping("tokens")
    public String getTokens(Model model) {
        model.addAttribute("xeroTokens", tokenStorage.get().orElse(new XeroTokens()));
        return "tokens.html";
    }

    @PostMapping("tokens")
    public String saveTokens(XeroTokens xeroTokens) {
        tokenStorage.save(xeroTokens);
        return "redirect:/xero/tokens";
    }

    @GetMapping(value = {"/redirect/", "/redirect"})
    public String redirect(@RequestParam Map<String, String> requestParams, Model model) throws IOException {
        final String params = requestParams.entrySet()
                .stream()
                .map(p -> urlEncodeUTF8(p.getKey()) + "=" + urlEncodeUTF8(p.getValue()))
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");
        logger.info("Processing Xero API redirect : " + params);
        callbackHandler.extractTokenInfo(requestParams);
        return "redirect:/xero/authorization";
    }

    static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
