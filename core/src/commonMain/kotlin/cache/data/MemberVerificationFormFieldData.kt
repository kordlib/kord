package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordMemberVerificationFormField
import dev.kord.common.entity.MemberVerificationFormFieldType
import dev.kord.common.entity.optional.Optional
import io.ktor.util.StringValues
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
public data class MemberVerificationFormFieldData(
    val fieldType: MemberVerificationFormFieldType,
    val label: String? = null,
    val choices: Optional<List<String>> = Optional.Missing(),
    val values: Optional<List<String?>> = Optional.Missing(),
    val response: Optional<JsonPrimitive?> = Optional.Missing(),
    val required: Boolean,
    val description: String? = null,
    val automations: List<String?>,
    val placeholder: Optional<String?> = Optional.Missing()
) {
    public companion object {
        public fun from(entity: DiscordMemberVerificationFormField): MemberVerificationFormFieldData = with(entity) {
            MemberVerificationFormFieldData(
                fieldType = fieldType,
                label = label,
                choices = choices,
                values = values,
                response = response,
                required = required,
                description = description,
                automations = automations,
                placeholder = placeholder
            )
        }
    }
}

public fun DiscordMemberVerificationFormField.toData(): MemberVerificationFormFieldData =
    MemberVerificationFormFieldData.from(this)