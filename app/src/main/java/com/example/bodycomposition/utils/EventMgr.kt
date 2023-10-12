package com.example.bodycomposition.utils

object EventMgr {
    var events = HashMap<String, Event>()

    fun addEvent(name: String, event: Event) {
        events[name] = event
    }

    fun post(name: String, obj: Any?) {
        if (events.containsKey(name)) {
            events[name]!!.onCallBack(obj)
        }
    }

    interface Event {
        fun onCallBack(obj: Any?)
    }
}
