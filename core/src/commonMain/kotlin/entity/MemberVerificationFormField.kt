package dev.kord.core.entity

import dev.kord.common.entity.MemberVerificationFormFieldType
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.MemberVerificationFormFieldData

public class MemberVerificationFormField(
    public val data: MemberVerificationFormFieldData,
    override val kord: Kord
) : KordObject {
    /**
     * The type of question.
     *
     * @see MemberVerificationFormFieldType
     */
    public val fieldType: MemberVerificationFormFieldType get() = data.fieldType

    /**
     * The label for the form field
     */
    public val label: String? get() = data.label

    /**
     * Multiple choice answers
     */
    public val choices: List<String>? get() = data.choices.value

    /**
     * Rules that the user must agree too
     */
    public val values: List<String?>? get() = data.values.value

    /**
     * The response for this field
     */
    public val response: String? get() = data.response.value

    /**
     * Whether this field is required for a successful application
     */
    public val required: Boolean get() = data.required

    /**
     * The subtext of the form field
     */
    public val description: String? get() = data.description

    // Unknown
    public val automations: List<String?> get() = data.automations

    /**
     * Placeholder text for the fields response area
     */
    public val placeholder: String? get() = data.placeholder.value
}