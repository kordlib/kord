@file:Generate(STRING_KORD_ENUM, name = "MemberVerificationFormFieldType",
    docUrl = "", entries = [
        Entry("Terms", stringValue = "TERMS", kDoc = "User must agree to the guild rules"),
        Entry("TextInput", stringValue = "TEXT_INPUT", kDoc = "User must respond with a short answer (max 150 characters)"),
        Entry("Paragraph", stringValue = "PARAGRAPH", kDoc = "User must respond with a paragraph (max 1000 characters"),
        Entry("MultipleChoice", stringValue = "MULTIPLE_CHOICE", kDoc = "User must select one of the provided choices"),
        // Crossed out in unofficial docs, here as a placeholder until real docs exist or unofficial updates
        //Entry("Verification", stringValue = "VERIFICATION", kDoc = "User must verify their email or phone number")
    ])

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.STRING_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Instant

/**
 * A representation of a [Discord member verification structure]()
 *
 * @param version Whe the member verification was last modified
 * @param formFields A list of questions for the applicants to answer
 * @param description A description of what the guild is about, can be different to guilds description (max 300 characters)
 * @param guild The guild this member verification is for
 * @param profile The profile of the guild this member verification is for
 */
@Serializable
public data class DiscordMemberVerification(
    val version: Instant?,
    @SerialName("form_fields")
    val formFields: List<DiscordMemberVerificationFormField>,
    val description: String?,
    val guild: DiscordMemberVerificationGuild?,
    val profile: DiscordGuildProfile,
)

/**
 * A representation of the []Discord Member verification form field structure]()
 *
 * @param fieldType The [MemberVerificationFormFieldType] for the quest
 * @param label the label for the field (max 300 characters)
 * @param choices Multiple choice answers
 * @param values The rules the user must agree too
 * @param response The response for this field
 * @param required Whether this field is required for a successful application
 * @param description The subtext of the form field
 * @param automations
 * @param placeholder Placeholder text for the fields response area
 */
@Serializable
public data class DiscordMemberVerificationFormField(
    @SerialName("field_type")
    val fieldType: MemberVerificationFormFieldType,
    val label: String?,
    val choices: Optional<List<String>> = Optional.Missing(),
    val values: Optional<List<String?>> = Optional.Missing(),
    val response: Optional<JsonPrimitive?> = Optional.Missing(),
    val required: Boolean,
    val description: String?,
    val automations: List<String?>,
    val placeholder: Optional<String?> = Optional.Missing()
)