package ru.skillbranch.skillarticles.markdown

import java.util.regex.Pattern

object MarkdownParser { // Парсер markdown разметки

    private val LINE_SEPARATOR = System.getProperty("line.separator") ?: "\n"

    private const val UNORDERED_LIST_ITEM_GROUP = "(^[*+-] .+$)" // Регулярное выражение ненумерованного списка
    private const val HEADER_GROUP = "(^#{1,6} .+?$)"
    private const val QUOTE_GROUP = "(^> .+?$)"
    private const val ITALIC_GROUP = "((?<!\\*)\\*[^*].*?[^*]?\\*(?!\\*)|(?<!_)_[^_].*?[^_]?_(?!_))" // Ищем * после неё второй символ не * далее любое количество символов lazy которые заканчиваются не * и последняя звездочка
    // перед этим ретроспективная проверка что нет * вначала и нет * в конце и опережающая проверка что нет * в конце
    private const val BOLD_GROUP = "((?<!\\*)\\*{2}[^*].*?[^*]?\\*{2}(?!\\*)|(?<!_)_{2}[^_].*?[^_]?_{2}(?!_))"
    private const val STRIKE_GROUP = "()"
    //private const val STRIKE_GROUP = ""

    private const val MARKDOWN_GROUPS = "$UNORDERED_LIST_ITEM_GROUP|$HEADER_GROUP|$QUOTE_GROUP|$ITALIC_GROUP|$BOLD_GROUP|$STRIKE_GROUP" // Строка содержащая все группы

    // Элемент паттерн проинициализирован как Pattern.compile с флагом мультилайн
    private val elementsPattern by lazy { Pattern.compile(MARKDOWN_GROUPS, Pattern.MULTILINE) }


    fun parse(string: String): MarkdownText {
        val elements = mutableListOf<Element>() // Лист в который будем добавлять элементы которые будут возвращаться из метода findElements
        elements.addAll(findElements(string)) // В findElements передаём строку которую необходимо распарсить
        return MarkdownText(elements)
    }

    fun clear(string: String): String? { // Из строки которая содержит маркдаун превращает её в обычную строку без маркдаун символов
        // Нужно чтобы делать поиск по тексту
        return null
    }

    // Их строки будет парсить элементы и возвращать их
    private fun findElements(string: CharSequence): List<Element> {
        val parents = mutableListOf<Element>() // Список с элементами которые найдём в строке
        val matcher = elementsPattern.matcher(string) // Элемент паттерн
        var lastSrartIndex = 0 // Индекс с которого матчер будет искать следующее вхождение

        loop@while (matcher.find(lastSrartIndex)) {
            val startIndex = matcher.start()
            val endIndex = matcher.end()

            // Если последний индекс меньше начального индекса
            if (lastSrartIndex < startIndex) {
                // Значение не в самом начале нашего поиска, значит простой текст
                parents.add(Element.Text(string.subSequence(lastSrartIndex, startIndex)))
            }

            // Found text
            var text: CharSequence

            val groups = 1..6
            var group = -1
            // Вспомогательный цикл для того чтобы итерироваться по группам, каждое выражение будет группа
            // Итерируемся по результатам которые найдёт наш матчер и определим какую группу он нашел
            for (gr in groups) {
                if (matcher.group(gr) != null) {
                    group = gr
                }
            }
            // < API 26 то не может использовать именованные регулярные выражения
            when (group) {
                -1 -> break@loop
                1 -> { // UNORDERED_LIST
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    // Находим нашу строку которая является подстрокой от нашего первого начала вхождения
                    // + 2 символа потому что ненумерованный список начинается с символа _+* и пробел,
                    // чтобы в результирующей строке они не появились
                    val subs = findElements(text)
                    // Проверка есть ли в нашей строке подэлементы если есть то добавляем как сабэлементы
                    // (Второй элемент unordered list item)
                    val element = Element.UnorderedListItem(text, subs)
                    parents.add(element) // После, добавляем созданный элемент

                    // Присваиваем значение которое соответствует концу регулярного выражения. Чтобы не искать ещё раз в этом месте
                    lastSrartIndex = endIndex
                }
                2 -> { // HEADER
                    // Находим символы решётки
                    val reg = "^#{1,6}".toRegex().find(string.subSequence(startIndex, endIndex))
                    val level = reg!!.value.length
                    // Текст без #...
                    text = string.subSequence(startIndex.plus(level.inc()), endIndex)
                    val element = Element.Header(level, text)
                    parents.add(element)
                    lastSrartIndex = endIndex
                }
                3 -> { // QUOTE
                    text = string.subSequence(startIndex.plus(2), endIndex)
                    val subelements = findElements(text)
                    val element = Element.Quote(text, subelements)
                    parents.add(element)
                    lastSrartIndex = endIndex
                }
                4 -> { // ITALIC
                    text = string.subSequence(startIndex.inc(), endIndex.dec())
                    val subelements = findElements(text)
                    val element = Element.Italic(text, subelements)
                    parents.add(element)
                    lastSrartIndex = endIndex
                }
                5 -> { // BOLD
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements = findElements(text)
                    val element = Element.Bold(text, subelements)
                    parents.add(element)
                    lastSrartIndex = endIndex
                }
                /*6 -> { // STRIKE "~~{}~~"
                    text = string.subSequence(startIndex.plus(2), endIndex.plus(-2))
                    val subelements = findElements(text)
                    val element = Element.Strike(text, subelements)
                    parents.add(element)
                    lastSrartIndex = endIndex
                }*/
            }
        }

        // Проверяем что находится после последнего вхождения
        if (lastSrartIndex < string.length) {
            // Всё то что находится после lastSrartIndex это обыкновенный текст
            val text = string.subSequence(lastSrartIndex, string.length)
            parents.add(Element.Text(text))
        }


        return parents
    }
}

// Класс который возвращает список элементов
data class MarkdownText(val elements: List<Element>) {

}

sealed class Element() { // Элемент markdown разметки
    abstract val text: CharSequence
    abstract val elements: List<Element> // Каждый элемент может содержать дочерние элементы

    data class Text( // Элемент текст
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class UnorderedListItem( // Ненумерованный список
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Header( // Заголовок
        val level: Int = 1,
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Quote( // Цитата
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ): Element()

    data class Italic( // Наклонный текст
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Bold( // Жирный текст
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()

    data class Strike( //
        override val text: CharSequence,
        override val elements: List<Element> = emptyList()
    ) : Element()
}