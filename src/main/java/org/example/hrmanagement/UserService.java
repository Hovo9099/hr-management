package org.example.hrmanagement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;

@Service
public class UserService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return jdbcTemplate.queryForObject("select id, username, password from hrs where username = ?",
                new Object[]{username},
                ((resultSet, i) -> new HR(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                ))
        );
    }


    public HR register(String username, String password) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into hrs(username, password) values(?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, username);
            ps.setString(2, password);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return new HR(key.intValue(), username, password);
    }

}