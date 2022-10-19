package ru.skillbranch.skillarticles.extensions

import java.util.*

@kotlin.ExperimentalStdlibApi
fun String?.indexesOf(
    subst: String,
    ignoreCase: Boolean = true
): List<Int> {
    return when {
        this == null || subst.isEmpty() -> listOf()
        else -> Regex(if (ignoreCase) subst.lowercase(Locale.getDefault()) else subst)
            .findAll(
                if (ignoreCase) {
                    this.lowercase(Locale.getDefault())
                } else {
                    this
                }
            )
            .map { it.range.first }
            .toList()
    }
/*
Реализуй функцию расширения fun String.indexesOf(substr: String, ignoreCase: Boolean = true): List,
в качестве аргумента принимает подстроку и флаг - учитывать или нет регистр подстроки при поиске по исходной строке.
Возвращает список позиций вхождений подстроки в исходную строку. Пример: "lorem ipsum sum".indexesOf("sum") // [8, 12]
*/
}