package entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private ImNode imNode;
    private String token;
    private String userId;
}
