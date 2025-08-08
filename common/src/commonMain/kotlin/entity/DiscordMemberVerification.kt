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
import dev.kord.ksp.Generate.EntityType.*
import dev.kord.ksp.Generate.Entry
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordMemberVerification(
    val version: Instant?,
    @SerialName("form_fields")
    val formFields: List<DiscordMemberVerificationFormField>,
    val description: String?,
    val guild: DiscordMemberVerificationGuild?,
    val profile: DiscordGuildProfile,
)

@Serializable
public data class DiscordMemberVerificationFormField(
    @SerialName("field_type")
    val fieldType: MemberVerificationFormFieldType,
    val label: String?,
    val choices: Optional<List<String>> = Optional.Missing(),
    val values: Optional<List<String>?> = Optional.Missing(),
    // FIXME This type can be String Int or Boolean, what type to put, any can't be serialized
    val response: Optional<String>? = Optional.Missing(),
    val required: Boolean,
    val description: String?,
    val automations: List<String>?,
    val placeholder: Optional<String>? = Optional.Missing()
)