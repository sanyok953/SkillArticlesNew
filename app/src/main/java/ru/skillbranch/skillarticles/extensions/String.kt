package ru.skillbranch.skillarticles.extensions

import java.util.*

fun String?.indexesOf(
    substr: String,
    ignoreCase: Boolean = true
): List<Int> {
    when {
        this == null || substr.isEmpty() -> return listOf()
        else -> return Regex(if (ignoreCase) substr.lowercase(Locale.getDefault()) else substr)
            .findAll(if (ignoreCase) this.lowercase(Locale.getDefault()) else this)
            .map { it.range.first }
            .toList()
    }
/*
Реализуй функцию расширения fun String.indexesOf(substr: String, ignoreCase: Boolean = true): List,
в качестве аргумента принимает подстроку и флаг - учитывать или нет регистр подстроки при поиске по исходной строке.
Возвращает список позиций вхождений подстроки в исходную строку. Пример: "lorem ipsum sum".indexesOf("sum") // [8, 12]
*/
}