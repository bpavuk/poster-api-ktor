package com.bpavuk.data

import com.bpavuk.di.DatabaseFactory.dbQuery
import com.bpavuk.models.Post
import com.bpavuk.models.Posts
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import kotlin.math.min

interface PostRepository {
    suspend fun getPost(id: Int): Post
    suspend fun getPosts(start: Int, amount: Int = 5): List<Post>
    suspend fun newPost(photos: List<String>, postDescription: String, userId: Int): String
    suspend fun deletePost(id: Int): String
}

class PostRepositoryImpl : PostRepository {
    private fun resultRowToPost(row: ResultRow) = Post(
        id = row[Posts.id],
        photosList = row[Posts.photosList].split("\t"),
        description = row[Posts.description],
        authorId = row[Posts.authorId],
        rating = row[Posts.rating]
    )
    override suspend fun getPost(id: Int): Post = dbQuery {
        Posts.select { Posts.id eq id }
            .map(::resultRowToPost)
            .singleOrNull() ?: throw IllegalArgumentException("No posts with ID $id found")
    }

    override suspend fun getPosts(start: Int, amount: Int): List<Post> = dbQuery {
        val postsList = Posts.select { Posts.id eq start }
            .map(::resultRowToPost)

        return@dbQuery postsList.subList(0, min(postsList.size, amount))
    }

    override suspend fun newPost(
        photos: List<String>,
        postDescription: String,
        userId: Int
    ): String = dbQuery {
        Posts.insert {
            it[photosList] = photos.joinToString("\t")
            it[authorId] = userId
            it[description] = postDescription
        }

        return@dbQuery "Post successfully added"
    }

    override suspend fun deletePost(id: Int): String = dbQuery {
        val deletionResult = Posts.deleteWhere { Posts.id eq id }

        if (deletionResult == 0) throw IllegalArgumentException("No posts with ID $id deleted")
        else return@dbQuery "Post #$id successfully deleted"
    }
}
