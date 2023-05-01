package com.bpavuk.data

import com.bpavuk.di.DatabaseFactory.dbQuery
import com.bpavuk.models.Post
import com.bpavuk.models.Posts
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.math.min

interface PostRepository {
    suspend fun getPost(id: Int): Post
    suspend fun getPosts(start: Int, amount: Int = 5): List<Post>
    suspend fun newPost(photos: List<String>, postDescription: String, userId: Int): String
    suspend fun deletePost(id: Int): String
    suspend fun fakeThePost(postId: Int, userId: Int): String
    suspend fun realThePost(postId: Int, userId: Int): String
}

class PostRepositoryImpl : PostRepository {
    private fun resultRowToPost(row: ResultRow) = Post(
        id = row[Posts.id],
        photosList = row[Posts.photosList].split("\t"),
        description = row[Posts.description],
        authorId = row[Posts.authorId],
        rating = row[Posts.rating],
        fakedBy = row[Posts.fakedBy].split("\t"),
        realedBy = row[Posts.realedBy].split("\t")
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

    override suspend fun fakeThePost(postId: Int, userId: Int): String = dbQuery {
        Posts.update(where = { Posts.id eq postId }) {
            // post to operate with
            val post = Posts.select(id eq postId)
                .map(::resultRowToPost)
                .singleOrNull() ?: throw IllegalArgumentException("Post with ID $postId not found")

            if (post.realedBy.contains("$userId")) {
                it[rating] = ++post.rating
                it[realedBy] = post.realedBy.filter { id -> id != userId.toString() }.joinToString("\t")
            } else if (post.fakedBy.contains("$userId")) {
                it[rating] = --post.rating
                it[fakedBy] = post.fakedBy.filter { id -> id != userId.toString() }.joinToString("\t")
                return@update
            }
            it[rating] = ++post.rating
            it[fakedBy] = (mutableListOf<String>() + post.fakedBy + "$userId").joinToString("\t")
        }
        return@dbQuery "Post #$postId is faked by user #$userId"
    }

    override suspend fun realThePost(postId: Int, userId: Int): String = dbQuery {
        Posts.update(where = { Posts.id eq postId }) {
            // post to operate with
            val post = Posts.select(id eq postId)
                .map(::resultRowToPost)
                .singleOrNull() ?: throw IllegalArgumentException("Post with ID $postId not found")

            if (post.fakedBy.contains("$userId")) {
                it[rating] = --post.rating
                it[fakedBy] = post.fakedBy.filter { id -> id != userId.toString() }.joinToString("\t")
            } else if (post.realedBy.contains("$userId")) {
                it[rating] = ++post.rating
                it[realedBy] = post.realedBy.filter { id -> id != userId.toString() }.joinToString("\t")
                return@update
            }
            it[rating] = --post.rating
            it[realedBy] = (mutableListOf<String>() + post.realedBy + "$userId").joinToString("\t")
        }
        return@dbQuery "Post #$postId is realed by user #$userId"
    }
}
