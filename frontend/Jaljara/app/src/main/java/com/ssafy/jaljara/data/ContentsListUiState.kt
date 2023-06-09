package com.ssafy.jaljara.data


data class ContentsInfo(
    val contentsId: Long,
    val contentType: String,
    val title: String,
    val description: String,
    val thumbnailImageUrl: String,
    val youtubeUrl: String,
)

data class ContentsListUiState(

    // 컨텐츠 리스트
    val contentsList: List<ContentsInfo> = listOf(),

    val showNavigation : Boolean = true,

    )

data class ContentsUiState(
    val conetents: ContentsInfo? = null,

    val showNavigation: Boolean = true,
)

data class Content(
    val contentsId: Long = 1,
    val contentType: String = "SOUND",
    val title: String = "영상 제목1",
    val description: String = "영상 설명1",
    val youtubeUrl: String = "https://assets.blog.engoo.com/wp-content/uploads/sites/2/2022/09/21164602/cat_idioms_cover-1200x700.jpg",
    val url: String = "https://assets.blog.engoo.com/wp-content/uploads/sites/2/2022/09/21164602/cat_idioms_cover-1200x700.jpg",
    val thumbnailImageUrl: String = "https://assets.blog.engoo.com/wp-content/uploads/sites/2/2022/09/21164602/cat_idioms_cover-1200x700.jpg"
)

data class VideoContent(
    val contentsId: Long = 2,
    val contentType: String = "VIDEO",
    val title: String = "영상 제목2",
    val description: String = "영상 설명2",
    val youtubeUrl: String = "https://assets.blog.engoo.com/wp-content/uploads/sites/2/2022/09/21164602/cat_idioms_cover-1200x700.jpg",
    val thumbnailImageUrl: String = "https://assets.blog.engoo.com/wp-content/uploads/sites/2/2022/09/21164602/cat_idioms_cover-1200x700.jpg"
)