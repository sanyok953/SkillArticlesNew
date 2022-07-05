package ru.skillbranch.skillarticles.data.adapters

import ru.skillbranch.skillarticles.data.local.User

class UserJsonAdapter : JsonAdapter<User> {

    companion object {
        private const val DELIMITER = ";"
    }

    override fun fromJson(json: String): User {
        val parts = json.split(DELIMITER)
        return User(
            id = parts[0],
            avatar = parts[1],
            respect = Integer.parseInt(parts[2]),
            rating = Integer.parseInt(parts[3]),
            about = parts[4],
            name = parts[5]
        )
    }

    override fun toJson(obj: User?): String {
        obj ?: return ""
        return arrayOf(
            obj.id,
            obj.avatar ?: "",
            obj.respect,
            obj.rating,
            obj.about ?: "",
            obj.name
        )
            .joinToString(separator = DELIMITER)
    }
}