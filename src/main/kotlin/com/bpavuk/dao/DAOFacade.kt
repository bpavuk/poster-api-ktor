package com.bpavuk.dao

import com.bpavuk.models.Post
import com.bpavuk.models.User
import com.bpavuk.models.UserRegisterForm

interface DAOFacade {
    suspend fun allUsers(): List<User>
    suspend fun addUser(user: UserRegisterForm): User?
    suspend fun getUser(id: Int): User?
    suspend fun searchUser(username: String): List<User>
    suspend fun editUserAvatar(id: Int, imgUrl: String): Boolean
    suspend fun deleteUser(id: Int): Boolean
    suspend fun loginUser(username: String, password: String): Boolean
    suspend fun getPost(id: Int): Post
    suspend fun getPosts(start: Int, amount: Int = 5): List<Post>
    suspend fun newPost(photos: List<String>, postDescription: String, userId: Int): String
}