package org.itstep.projectdeadlinemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("projectDeadlineManagement")
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        UserDetails admin = User.builder()
                .username("admin")
                .password("$2a$10$3uUBBq3ikiB5lXsNbrudeue.NwNkmFcd59t5qeDCuiFp70Af.8H9W") // соответствует строке "admin"
                .roles("ADMIN", "PRODUCT", "EQUIPMENT", "USER")
                .build();
        UserDetails product = User.builder()
                .username("product")
                .roles("PRODUCT", "USER")
                .password("$2a$10$MgLGGRdNXgUtzNJ1W3bt1uyz9G056PzG2uuAoQkBD/Y8jnACCFbwS") // соответствует строке "product"
                .build();
        UserDetails equipment = User.builder()
                .username("equipment")
                .roles("EQUIPMENT", "USER")
                .password("$2a$10$Gm5uICwnx4/ojao3t.YFbOwjkN7VX3ZkZFR6M5So95An1V/pT9SGG") // соответствует строке "equipment"
                .build();
        UserDetails user = User.builder()
                .username("user")
                .roles("USER")
                .password("$2a$10$Djl8U2HDSuCmbCSm1Xxb8eJSbqczMOoLSl0MxWkr.p8kchB75hOXu") // соответствует строке "user"
                .build();
        JdbcUserDetailsManager detailsManager = new JdbcUserDetailsManager(dataSource);
        detailsManager.createUser(admin);
        detailsManager.createUser(product);
        detailsManager.createUser(equipment);
        detailsManager.createUser(user);
        return detailsManager;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/").permitAll()
//                                .requestMatchers("/").hasAnyRole("USER", "EDITOR", "ADMIN")
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                                .requestMatchers(HttpMethod.GET, "/divisions").hasAnyRole("ADMIN", "PRODUCT", "EQUIPMENT", "USER")
                                .requestMatchers(HttpMethod.POST, "/divisions").hasAnyRole("ADMIN", "PRODUCT", "EQUIPMENT", "USER")
                                .requestMatchers(HttpMethod.GET, "/divisions/edit/**").hasAnyRole("ADMIN", "PRODUCT", "EQUIPMENT", "USER")
                                .requestMatchers(HttpMethod.POST, "/divisions/edit/**").hasAnyRole("ADMIN", "PRODUCT", "EQUIPMENT", "USER")
                                .requestMatchers(HttpMethod.GET, "/divisions/delete/**").hasAnyRole("ADMIN", "PRODUCT", "EQUIPMENT", "USER")
                                .requestMatchers(HttpMethod.GET, "/division_types").hasAnyRole("ADMIN", "PRODUCT", "EQUIPMENT", "USER")
                                .requestMatchers(HttpMethod.POST, "/division_types").hasAnyRole("ADMIN", "PRODUCT", "EQUIPMENT", "USER")
                                .requestMatchers(HttpMethod.GET, "/division_types/edit/**").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/division_types/edit/**").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/division_types/delete/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/books").hasAnyRole("USER", "EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/books").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/books/edit/**").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/books/edit/**").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/books/delete/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/authors").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/authors").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/authors/edit/**").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/authors/edit/**").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/authors/delete/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/publishers").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/publishers").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/publishers/edit/**").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/publishers/edit/**").hasAnyRole("EDITOR", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/publishers/delete/**").hasRole("ADMIN")
                                .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .permitAll())
//                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .permitAll())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}



