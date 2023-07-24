package fr.nil.backedflow.auth.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 * This class represents a request to refresh a user's authentication token.
 * NOTE: This class is not currently used and needs to be implemented.
 *
 * @param previousToken The previous token to be refreshed.
 */
public class TokenRefreshRequest extends AuthenticationRequest{


    private String previousToken;

}
