package dev.kord.gateway.connection

/**
 * A provider for [GatewayConnection]s.
 */
public fun interface GatewayConnectionProvider {

    /**
     * Provides a [GatewayConnection].
     */
    public suspend fun provide(): GatewayConnection

    public companion object {
        private object DefaultGatewayConnectionProvider : GatewayConnectionProvider {
            override suspend fun provide(): GatewayConnection = DefaultGatewayConnection()
        }

        /**
         * Returns a [GatewayConnectionProvider] that provides [DefaultGatewayConnection]s.
         */
        public fun default(): GatewayConnectionProvider = DefaultGatewayConnectionProvider
    }
}
