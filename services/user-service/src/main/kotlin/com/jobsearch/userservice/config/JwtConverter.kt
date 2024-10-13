package com.jobsearch.userservice.config

import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component
import java.util.stream.Collectors
import java.util.stream.Stream

@Component
@Slf4j
class JwtConverter(
    @Value("\${token.converter.principal-attribute}") private val principalAttribute: String?,
    @Value("\${token.converter.resource-id}") private val resourceId: String
) : Converter<Jwt, AbstractAuthenticationToken> {

    private val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = Stream.concat(
            (jwtGrantedAuthoritiesConverter.convert(jwt) ?: emptyList()).stream(),
            extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet())

        return JwtAuthenticationToken(jwt, authorities, getPrincipleClaimName(jwt))
    }

    private fun getPrincipleClaimName(jwt: Jwt): String {
        val claimName = principalAttribute ?: JwtClaimNames.SUB
        return jwt.getClaim(claimName)
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccess = jwt.getClaim<Map<String, Any>>("resource_access") ?: return emptySet()

        val resource = resourceAccess[resourceId] as? Map<*, *> ?: return emptySet()

        val resourceRoles = resource["roles"] as? Collection<*> ?: return emptySet()

        return resourceRoles.map { role -> SimpleGrantedAuthority(role.toString()) }.toSet()
    }
}
