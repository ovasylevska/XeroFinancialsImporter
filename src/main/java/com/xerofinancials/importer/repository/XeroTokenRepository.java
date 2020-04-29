package com.xerofinancials.importer.repository;

import com.xerofinancials.importer.beans.XeroTokens;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class XeroTokenRepository {
    private final JdbcTemplate jdbc;

    public XeroTokenRepository(@Qualifier("importerJdbcTemplate") JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<XeroTokens> get() {
        final String sql = "SELECT * FROM importer.xero_tokens";
        final List<XeroTokens> results = jdbc.query(sql, (rs, rowNum) -> {
            XeroTokens xeroTokens = new XeroTokens();
            xeroTokens.setJwrToken(rs.getString("jwt_token"));
            xeroTokens.setAccessToken(rs.getString("access_token"));
            xeroTokens.setRefreshToken(rs.getString("refresh_token"));
            xeroTokens.setTenantId(rs.getString("tenant_id"));
            xeroTokens.setExpiresInSeconds(rs.getString("expires_in_seconds"));
            return xeroTokens;
        });
        if (results.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(results.get(0));
        }
    }

    public void update(XeroTokens xeroTokens) {
        delete();
        save(xeroTokens);
    }

    public void delete() {
        final String sql = "DELETE FROM importer.xero_tokens";
        jdbc.update(sql);
    }

    public void save(XeroTokens xeroTokens) {
        final String sql = "INSERT INTO importer.xero_tokens(" +
                "jwt_token, " +
                "access_token, " +
                "refresh_token, " +
                "tenant_id, " +
                "expires_in_seconds" +
                ") VALUES(?, ?, ?, ?, ?)";
        jdbc.update(
                sql,
                xeroTokens.getJwrToken(),
                xeroTokens.getAccessToken(),
                xeroTokens.getRefreshToken(),
                xeroTokens.getTenantId(),
                xeroTokens.getExpiresInSeconds()
        );
    }

}
