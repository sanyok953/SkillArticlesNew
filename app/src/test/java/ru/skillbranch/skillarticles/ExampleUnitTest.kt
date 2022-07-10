package ru.skillbranch.skillarticles

import org.junit.Test

import org.junit.Assert.*
import org.junit.Ignore
import ru.skillbranch.skillarticles.markdown.Element
import ru.skillbranch.skillarticles.markdown.MarkdownParser

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun parse_list_item() { // Парсим ненумерованный список
        val result = MarkdownParser.parse(unorderedListString)
        val actual = prepare<Element.UnorderedListItem>(result.elements)
        assertEquals(expectedUnorderedList, actual)

    }

    @Test
    fun parse_header() {
        val result = MarkdownParser.parse(headerString)
        val actual = prepare<Element.Header>(result.elements)
        // Проверка правильно ли определён level
        val actualLevels = result.elements.spread()
            .filterIsInstance<Element.Header>()
            .map{it.level}
        assertEquals(expectedHeader, actual)
        assertEquals(listOf(1,2,3,4,5,6), actualLevels)

    }

    @Test
    fun parse_quote() {
        val result = MarkdownParser.parse(quoteString)
        val actual = prepare<Element.Quote>(result.elements)
        assertEquals(expectedQuote, actual)

    }

    @Test
    fun parse_italic() {
        val result = MarkdownParser.parse(italicString)
        val actual = prepare<Element.Italic>(result.elements)
        assertEquals(expectedItalic, actual)

        printResults(actual)
        print("")
        printElements(result.elements)
    }
/*
    @Test
    fun parse_ordered_list_item() {
        val result = MarkdownParser.parse(orderedListString)
        val actualOrderedList = prepare<Element.OrderedListItem>(result.elements)
        val actualLevels = result.elements.spread()
            .filterIsInstance<Element.OrderedListItem>()
            .map { it.order }
        assertEquals(listOf("1.", "2."), actualLevels)
        assertEquals(expectedOrderedList, actualOrderedList)
    }






    @Test
    @Ignore
    fun parse_bold() {
        val result = MarkdownParser.parse(boldString)
        val actual = prepare<Element.Bold>(result.elements)
        assertEquals(expectedBold, actual)
    }

    @Test
    @Ignore
    fun parse_strike() {
        val result = MarkdownParser.parse(strikeString)
        val actual = prepare<Element.Strike>(result.elements)
        assertEquals(expectedStrike, actual)
    }

    @Test
    @Ignore
    fun parse_combine() {
        val result = MarkdownParser.parse(combineEmphasisString)
        val actualItalic= prepare<Element.Italic>(result.elements)
        val actualBold= prepare<Element.Bold>(result.elements)
        val actualStrike= prepare<Element.Strike>(result.elements)
        assertEquals(expectedCombine["italic"], actualItalic)
        assertEquals(expectedCombine["bold"], actualBold)
        assertEquals(expectedCombine["strike"], actualStrike)
    }

    @Test
    @Ignore
    fun parse_rule() {
        val result = MarkdownParser.parse(ruleString)
        val actual = prepare<Element.Rule>(result.elements)
        assertEquals(3, actual.size)
    }

    @Test
    @Ignore
    fun parse_inline_code() {
        val result = MarkdownParser.parse(inlineString)
        val actual = prepare<Element.InlineCode>(result.elements)
        assertEquals(expectedInline, actual)
    }

    @Test
    @Ignore
    fun parse_multiline_code() {
        val result = MarkdownParser.parse(multilineCode)
        val actual = prepare<Element.BlockCode>(result.elements) //optionally
        assertEquals(expectedMultilineCode, actual) //optionally
    }

    @Test
    @Ignore
    fun parse_link() {
        val result = MarkdownParser.parse(linkString)
        val actual = prepare<Element.Link>(result.elements)
        val actualLink = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map{it.link}

        assertEquals(expectedLink["titles"], actual)
        assertEquals(expectedLink["links"], actualLink)
    }

    @Test
    @Ignore
    fun parse_images() {
        val result = MarkdownParser.parse(imagesString)
        val actual = prepare<Element.Image>(result.elements)
        val actualLink = result.elements.spread()
            .filterIsInstance<Element.Image>()
            .map{it.url}

        val actualAlts = result.elements.spread()
            .filterIsInstance<Element.Image>()
            .map{it.alt}

        assertEquals(expectedImages["titles"], actual)
        assertEquals(expectedImages["alts"], actualAlts)
        assertEquals(expectedImages["links"], actualLink)
    }

    @Test
    @Ignore
    fun parse_all() {
        val result = MarkdownParser.parse(markdownString)
        val actualUnorderedList = prepare<Element.UnorderedListItem>(result.elements)
        val actualHeaders = prepare<Element.Header>(result.elements)
        val actualQuotes = prepare<Element.Quote>(result.elements)
        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)
        val actualRule = prepare<Element.Rule>(result.elements)
        val actualInline = prepare<Element.InlineCode>(result.elements)
        val actualLinkTitles = prepare<Element.Link>(result.elements)
        val actualLinks = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map { it.link }

        assertEquals(expectedMarkdown["unorderedList"], actualUnorderedList)
        assertEquals(expectedMarkdown["header"], actualHeaders)
        assertEquals(expectedMarkdown["quote"], actualQuotes)
        assertEquals(expectedMarkdown["italic"], actualItalic)
        assertEquals(expectedMarkdown["bold"], actualBold)
        assertEquals(expectedMarkdown["strike"], actualStrike)
        assertEquals(3, actualRule.size)
        assertEquals(expectedMarkdown["inline"], actualInline)
        assertEquals(expectedMarkdown["linkTitles"], actualLinkTitles)
        assertEquals(expectedMarkdown["links"], actualLinks)
    }

    @Test
    @Ignore
    fun clear_all() {
        val result = MarkdownParser.clear(markdownString)
        assertEquals(markdownClearString,  result)
    }


    @Test
    @Ignore
    fun clear_all_with_optionally() {
        val result = MarkdownParser.clear(markdownString)
        assertEquals(markdownOptionallyClearString,  result)
    }

    //optionally (delete @Ignore fo run)
    @Test
    @Ignore
    fun parse_all_with_optionally() {
        val result = MarkdownParser.parse(markdownString)
        val actualUnorderedList = prepare<Element.UnorderedListItem>(result.elements)
        val actualHeaders = prepare<Element.Header>(result.elements)
        val actualQuotes = prepare<Element.Quote>(result.elements)
        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)
        val actualRule = prepare<Element.Rule>(result.elements)
        val actualInline = prepare<Element.InlineCode>(result.elements)
        val actualLinkTitles = prepare<Element.Link>(result.elements)
        val actualLinks = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map { it.link }
        val actualBlockCode = prepare<Element.BlockCode>(result.elements) //optionally
        val actualOrderedList = prepare<Element.OrderedListItem>(result.elements) //optionally

        assertEquals(expectedMarkdown["unorderedList"], actualUnorderedList)
        assertEquals(expectedMarkdown["header"], actualHeaders)
        assertEquals(expectedMarkdown["quote"], actualQuotes)
        assertEquals(expectedMarkdown["italic"], actualItalic)
        assertEquals(expectedMarkdown["bold"], actualBold)
        assertEquals(expectedMarkdown["strike"], actualStrike)
        assertEquals(3, actualRule.size)
        assertEquals(expectedMarkdown["inline"], actualInline)
        assertEquals(expectedMarkdown["linkTitles"], actualLinkTitles)
        assertEquals(expectedMarkdown["links"], actualLinks)
        assertEquals(expectedMarkdown["multiline"], actualBlockCode)
        assertEquals(expectedMarkdown["orderedList"], actualOrderedList)
    }

*/

    private fun printResults(list:List<String>){
        val iterator = list.iterator()
        while (iterator.hasNext()){
            println("find >> ${iterator.next()}")
        }
    }

    private fun printElements(list:List<Element>){
        val iterator = list.iterator()
        while (iterator.hasNext()){
            println("element >> ${iterator.next()}")
        }
    }


    private fun Element.spread():List<Element>{ // Для одного элемента
        val elements = mutableListOf<Element>()
        elements.add(this)
        elements.addAll(this.elements.spread())
        return elements
    }

    // Позволяем взять элемент и развернуть его дочерние элементы (превратить в последовательность из элементов
    private fun List<Element>.spread():List<Element>{
        val elements = mutableListOf<Element>()
        if(this.isNotEmpty()) elements.addAll( // Если список не пустой
            // Добавляем все элементы которые которые развернём с помощью функции fold
            // Первый аргумент аккумулятор вторым наш элемент
            // В каждой итерации добавляем в аккумулятор наш эленент который будет развернут при помощи spread
            this.fold(mutableListOf()){acc, el -> acc.also { it.addAll(el.spread()) }}
        )
        return elements
    }

    // Передаём коллекцию элементов и она будет возвращать коллекцию стрингов
    private inline fun <reified T:Element> prepare(list:List<Element>) : List<String>{ // Дженерик вещественного типа
        // Нужно для удобства, сравнить ожидаемые результаты с фактическими
        return list
            .fold(mutableListOf<Element>()){ acc, el -> //spread inner elements. в качестве аккумулятора пкстая коллекция
                // Будет в пустую коллекцию добавлять последовательно наши элементы к которым мы применяем spread
                acc.also { it.addAll(el.spread()) }
            }
            .filterIsInstance<T>() //filter only expected instance
            .map { it.text.toString() } //transform to element text. с каждого элемента берём поле текста
    }

}