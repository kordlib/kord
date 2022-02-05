package dev.kord.common

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.StrikeThroughParser
import org.intellij.markdown.flavours.gfm.lexer._GFMLexer
import org.intellij.markdown.html.GeneratingProvider
import org.intellij.markdown.html.URI
import org.intellij.markdown.lexer.MarkdownLexer
import org.intellij.markdown.parser.MarkdownParser
import org.intellij.markdown.parser.LinkMap
import org.intellij.markdown.parser.sequentialparsers.SequentialParser
import org.intellij.markdown.parser.sequentialparsers.SequentialParserManager
import org.intellij.markdown.parser.sequentialparsers.impl.AutolinkParser
import org.intellij.markdown.parser.sequentialparsers.impl.BacktickParser
import org.intellij.markdown.parser.sequentialparsers.impl.EmphStrongParser
import org.intellij.markdown.parser.sequentialparsers.impl.InlineLinkParser

/**
 * A [MarkdownFlavourDescriptor] for Discords markdown flavour.
 *
 * Use [MarkdownParser] to use this flavour
 *
 * @see CommonMarkFlavourDescriptor
 * @see MarkdownParser
 */
public object DiscordMarkFlavourDescriptor : CommonMarkFlavourDescriptor() {

    /**
     * Supported [types][IElementType] by this flavour.
     */
    public val supportedElements: List<IElementType>  = listOf(
        MarkdownElementTypes.MARKDOWN_FILE,
        MarkdownElementTypes.BLOCK_QUOTE,
        MarkdownElementTypes.AUTOLINK,
        MarkdownElementTypes.LINK_LABEL,
        MarkdownElementTypes.LINK_TEXT,
        MarkdownElementTypes.LINK_DEFINITION,
        MarkdownElementTypes.CODE_FENCE,
        MarkdownElementTypes.CODE_BLOCK,
        MarkdownElementTypes.EMPH,
        MarkdownElementTypes.STRONG,
        MarkdownElementTypes.CODE_SPAN,
        GFMElementTypes.STRIKETHROUGH
    )

    /**
     * [types][IElementType] used by this flavour, which are considered to be plain text.
     */
    public val plainTextTypes: List<IElementType> = listOf(
        MarkdownTokenTypes.WHITE_SPACE,
        MarkdownTokenTypes.TEXT,
        MarkdownTokenTypes.EOL,
        MarkdownTokenTypes.CODE_LINE,
        MarkdownTokenTypes.CODE_FENCE_CONTENT,
        MarkdownTokenTypes.ESCAPED_BACKTICKS
    )

    override val sequentialParserManager: SequentialParserManager = object : SequentialParserManager() {
        override fun getParserSequence(): List<SequentialParser> {
            return listOf(
                AutolinkParser(listOf(MarkdownTokenTypes.AUTOLINK)),
                BacktickParser(),
                InlineLinkParser(),
                StrikeThroughParser(),
                EmphStrongParser()
            )
        }
    }

    override fun createHtmlGeneratingProviders(linkMap: LinkMap, baseURI: URI?): Map<IElementType, GeneratingProvider> =
        super.createHtmlGeneratingProviders(linkMap, baseURI).filter { (key, _) ->
            key in supportedElements
        }

    // We need to use thr GFM lexer, so we can use the strikethrough parser
    override fun createInlinesLexer(): MarkdownLexer = MarkdownLexer(_GFMLexer())
}
