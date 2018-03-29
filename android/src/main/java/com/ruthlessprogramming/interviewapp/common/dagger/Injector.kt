package com.ruthlessprogramming.interviewapp.common.dagger

import com.ruthlessprogramming.interviewapp.InterviewApplication

class Injector private constructor() {
    companion object {
        fun get(): SingletonComponent =
            InterviewApplication.INSTANCE.component
    }
}