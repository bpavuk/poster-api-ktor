package com.bpavuk.di

import com.bpavuk.data.PostRepository
import com.bpavuk.data.PostRepositoryImpl
import com.bpavuk.data.UserRepository
import com.bpavuk.data.UserRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<PostRepository> { PostRepositoryImpl() }
}