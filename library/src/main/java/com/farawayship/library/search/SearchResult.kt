package com.farawayship.library.search

import com.farawayship.library.enum.FileType

data class SearchResult(val path: String, val name: String, val extension: FileType, val capacity: Long)