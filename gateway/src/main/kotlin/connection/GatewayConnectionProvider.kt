package dev.kord.gateway.connection

public fun interface GatewayConnectionProvider {

    public suspend fun provide(): GatewayConnection

    public companion object {
        private object DefaultGatewayConnectionProvider : GatewayConnectionProvider {
            override suspend fun provide(): GatewayConnection = DefaultGatewayConnection()
        }

        public fun default(): GatewayConnectionProvider = DefaultGatewayConnectionProvider
    }
}
