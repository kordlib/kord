package dev.kord.rest.service

import io.ktor.client.*

public class RestClient(client: HttpClient) {

    // order like in docs:

    // interactions
    public val interaction: InteractionService = InteractionService(client)

    // resources
    public val auditLog: AuditLogService = AuditLogService(client)
    public val autoModeration: AutoModerationService = AutoModerationService(client)
    public val channel: ChannelService = ChannelService(client)
    public val emoji: EmojiService = EmojiService(client)
    public val guild: GuildService = GuildService(client)
    public val template: TemplateService = TemplateService(client)
    public val invite: InviteService = InviteService(client)
    public val stageInstance: StageInstanceService = StageInstanceService(client)
    public val sticker: StickerService = StickerService(client)
    public val user: UserService = UserService(client)
    public val voice: VoiceService = VoiceService(client)
    public val webhook: WebhookService = WebhookService(client)

    // topics
    public val application: ApplicationService = ApplicationService(client)
}
