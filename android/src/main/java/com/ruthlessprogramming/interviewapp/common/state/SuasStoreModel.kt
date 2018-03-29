package com.ruthlessprogramming.interviewapp.common.state

import zendesk.suas.Action
import zendesk.suas.Listener
import zendesk.suas.State
import zendesk.suas.Store

abstract class SuasStoreModel<out T> {
    val store: Store by lazy { setStore() }

    abstract fun setStore(): Store
    abstract fun getModelState(): T

    fun dispatch(action: Action<*>) = store.dispatch(action)
    fun addListener(listener: Listener<State>) = store.addListener(listener)
    fun removeListener(listener: Listener<State>) = store.removeListener(listener)
}