package fr.nil.backedflow.auth.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a request for Single Sign-On (SSO) login via Google API.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleSSOLoginRequest {

    /**
     * The user's Google ID, which is a unique identifier that Google assigns to each user.
     */
    private String sub;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's full name.
     */
    private String name;

    /**
     * The "authorized party" of the token, which identifies the client that requested the token.
     * This value corresponds to the client ID of the Google API Console project that was used to
     * create the OAuth 2.0 client ID for your Angular application.
     */
    private String azp;

    /**
     * The "audience" of the token, which specifies the intended recipient of the token.
     */
    private String aud;

    /**
     * A unique identifier for the token.
     */
    private String jti;

    /**
     * Gets the first name of the user based on the "name" field.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return this.name.split(" ")[0];
    }

    /**
     * Gets the last name of the user based on the "name" field.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return this.name.split(" ")[1];
    }

}