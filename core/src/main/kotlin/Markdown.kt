package dev.kord.core

import dev.kord.common.DiscordMarkFlavourDescriptor
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.LeafASTNode
import org.intellij.markdown.ast.visitors.RecursiveVisitor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

/**
 * Parses this String to [Markdown].
 *
 * @see Markdown
 */
public fun String.parseMarkdown(): Markdown {
    val tree = MarkdownParser(DiscordMarkFlavourDescriptor).buildMarkdownTreeFromString(this)
    return Markdown(this, tree)
}

/**
 * Container for Markdown operations.
 *
 * @see parseMarkdown
 */
public class Markdown internal constructor(public val text: String, private val tree: ASTNode) {
    /**
     * Converts this markdown to HTML.
     */
    public fun toHTML(): String = HtmlGenerator(text, tree, DiscordMarkFlavourDescriptor).generateHtml()

    /**
     * Escapes all of this markdown, so Discord will render this as the raw input text.
     */
    public fun escape(): String = buildString { EscapingVisitor(text, this).visitNode(tree) }

    /**
     * Strips this text from all unescaped markdown identifiers.
     */
    public fun strip(): String = buildString { StrippingVisitor(text, this).visitNode(tree) }
}

private class EscapingVisitor(private val source: String, private val escapedString: StringBuilder) :
    RecursiveVisitor() {
    override fun visitNode(node: ASTNode) {
        if (node is LeafASTNode) {
            when (node.type) {
                MarkdownTokenTypes.BACKTICK -> {
                    val ticks = source.substring(
                        node.startOffset, node.endOffset
                    )

                    ticks.forEach {
                        escapedString.append('\\')
                        escapedString.append(it)
                    }
                }
                in DiscordMarkFlavourDescriptor.plainTextTypes, MarkdownTokenTypes.FENCE_LANG, GFMTokenTypes.GFM_AUTOLINK -> escapedString.append(
                    source.substring(
                        node.startOffset, node.endOffset
                    )
                )
                MarkdownTokenTypes.CODE_FENCE_START, MarkdownTokenTypes.CODE_FENCE_END -> {
                    repeat(3) {
                        // according to spec \``` is valid however Discord only accepts \`\`\`
                        escapedString.append(escapeChar).append('`')
                    }
                }
                else -> escapedString.append(escapeChar).append(source.substring(node.startOffset, node.endOffset))
            }
        } else {
            super.visitNode(node)
        }
    }

    companion object {
        const val escapeChar = '\\'
    }
}

private class StrippingVisitor(private val source: String, private val strippedString: StringBuilder) :
    RecursiveVisitor() {
    override fun visitNode(node: ASTNode) {
        if (node is LeafASTNode) {
            if (node.type in DiscordMarkFlavourDescriptor.plainTextTypes) {
                strippedString.append(source.substring(node.startOffset, node.endOffset))
            }
        } else {
            super.visitNode(node)
        }
    }
}
