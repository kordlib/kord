package dev.kord.core.entity

import dev.kord.common.entity.MemberVerificationFormFieldType
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.MemberVerificationFormFieldData

public class MemberVerificationFormField(
    public val data: MemberVerificationFormFieldData,
    override val kord: Kord
) : KordObject {
    public val fieldType: MemberVerificationFormFieldType get() = data.fieldType

    public val label: String? get() = data.label

    public val choices: List<String>? get() = data.choices.value

    public val values: List<String?>? get() = data.values.value

    public val response: String? get() = data.response.value

    public val required: Boolean get() = data.required

    public val description: String? get() = data.description

    public val automations: List<String?> get() = data.automations

    public val placeholder: String? get() = data.placeholder.value
}