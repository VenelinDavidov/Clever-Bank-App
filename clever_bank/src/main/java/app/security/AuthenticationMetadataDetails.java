package app.security;

import app.customer.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@AllArgsConstructor
public class AuthenticationMetadataDetails implements UserDetails {

    private UUID customerId;
    private String username;
    private String password;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime accountExpireAt;
    private LocalDateTime isCredentialsExpired;




    @Override
    public Collection <? extends GrantedAuthority> getAuthorities() {

       SimpleGrantedAuthority authority = new SimpleGrantedAuthority ("ROLE_" + role.name());

        return List.of (authority);
    }





    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountExpireAt == null || accountExpireAt.isAfter (LocalDateTime.now ());
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsExpired == null || isCredentialsExpired.isAfter (LocalDateTime.now ());
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
