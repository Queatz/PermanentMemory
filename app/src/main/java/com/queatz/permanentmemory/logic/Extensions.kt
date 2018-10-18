package com.queatz.permanentmemory.logic

import java.util.*

fun <E> List<E>.randomOrNull(): E? = if (isEmpty()) null else get(Random().nextInt(size))
